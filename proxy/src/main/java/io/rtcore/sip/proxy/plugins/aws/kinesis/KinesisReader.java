package io.rtcore.sip.proxy.plugins.aws.kinesis;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import picocli.CommandLine.Option;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.kinesis.common.ConfigsBuilder;
import software.amazon.kinesis.common.KinesisClientUtil;
import software.amazon.kinesis.coordinator.Scheduler;
import software.amazon.kinesis.exceptions.InvalidStateException;
import software.amazon.kinesis.exceptions.ShutdownException;
import software.amazon.kinesis.lifecycle.events.InitializationInput;
import software.amazon.kinesis.lifecycle.events.LeaseLostInput;
import software.amazon.kinesis.lifecycle.events.ProcessRecordsInput;
import software.amazon.kinesis.lifecycle.events.ShardEndedInput;
import software.amazon.kinesis.lifecycle.events.ShutdownRequestedInput;
import software.amazon.kinesis.metrics.MetricsLevel;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;
import software.amazon.kinesis.retrieval.polling.PollingConfig;

public class KinesisReader {

  @picocli.CommandLine.Command(name = "kinesis:read", description = "read kinesis stream")
  public static class Command implements Callable<Integer> {

    @Option(names = "streamName")
    String streamName;

    @Option(names = "region")
    String region;

    @Override
    public Integer call() throws Exception {
      new KinesisReader(streamName, DefaultCredentialsProvider.create(), region).run();
      return 0;
    }

  }

  private static final Logger log = LoggerFactory.getLogger(KinesisReader.class);

  private final String streamName;
  private final Region region;
  private final KinesisAsyncClient kinesisClient;

  /**
   * Constructor sets streamName and region. It also creates a KinesisClient object to send data to
   * Kinesis. This KinesisClient is used to send dummy data so that the consumer has something to
   * read; it is also used indirectly by the KCL to handle the consumption of the data.
   */

  private KinesisReader(String streamName, AwsCredentialsProvider credentialsProvider, String region) {
    this.streamName = streamName;
    this.region = Region.of(ObjectUtils.firstNonNull(region, "us-west-2"));
    this.kinesisClient = KinesisClientUtil.createKinesisAsyncClient(KinesisAsyncClient.builder().region(this.region).credentialsProvider(credentialsProvider));
  }

  public void run() {

    String workerIdentifier = UUID.randomUUID().toString();

    DynamoDbAsyncClient dynamoClient = DynamoDbAsyncClient.builder().region(region).build();

    CloudWatchAsyncClient cloudWatchClient = CloudWatchAsyncClient.builder().region(region).build();

    ConfigsBuilder configsBuilder =
      new ConfigsBuilder(
        streamName,
        streamName,
        kinesisClient,
        dynamoClient,
        cloudWatchClient,
        workerIdentifier,
        new SampleRecordProcessorFactory());

    /**
     * The Scheduler (also called Worker in earlier versions of the KCL) is the entry point to the
     * KCL. This instance is configured with defaults provided by the ConfigsBuilder.
     */

    Scheduler scheduler =
      new Scheduler(
        configsBuilder.checkpointConfig(),
        configsBuilder.coordinatorConfig(),
        configsBuilder.leaseManagementConfig(),
        configsBuilder.lifecycleConfig(),
        configsBuilder.metricsConfig().metricsLevel(MetricsLevel.NONE),
        configsBuilder.processorConfig(),
        configsBuilder
          .retrievalConfig()
          .retrievalSpecificConfig(new PollingConfig(streamName, kinesisClient).idleTimeBetweenReadsInMillis(50)));

    /**
     * Kickoff the Scheduler. Record processing of the stream of dummy data will continue
     * indefinitely until an exit is triggered.
     */

    Thread schedulerThread = new Thread(scheduler);
    schedulerThread.setDaemon(true);
    schedulerThread.start();

    boolean running = true;

    while (running) {
      try {
        Thread.sleep(1000);
      }
      catch (InterruptedException e) {
        // TODO Auto-generated catch block
        throw new RuntimeException(e);
      }
    }

    /**
     * Stops consuming data. Finishes processing the current batch of data already received from
     * Kinesis before shutting down.
     */

    Future<Boolean> gracefulShutdownFuture = scheduler.startGracefulShutdown();

    log.info("Waiting up to 20 seconds for shutdown to complete.");

    try {
      gracefulShutdownFuture.get(20, TimeUnit.SECONDS);
    }
    catch (InterruptedException e) {
      log.info("Interrupted while waiting for graceful shutdown. Continuing.");
    }
    catch (ExecutionException e) {
      log.error("Exception while executing graceful shutdown.", e);
    }
    catch (TimeoutException e) {
      log.error("Timeout while waiting for shutdown.  Scheduler may not have exited.");
    }

    log.info("Completed, shutting down now.");

  }

  private static class SampleRecordProcessorFactory implements ShardRecordProcessorFactory {

    @Override
    public ShardRecordProcessor shardRecordProcessor() {
      return new SampleRecordProcessor();
    }

  }

  /**
   * The implementation of the ShardRecordProcessor interface is where the heart of the record
   * processing logic lives. In this example all we do to 'process' is log info about the records.
   */

  private static class SampleRecordProcessor implements ShardRecordProcessor {

    private static final String SHARD_ID_MDC_KEY = "ShardId";

    private static final Logger log = LoggerFactory.getLogger(SampleRecordProcessor.class);

    private String shardId;

    /**
     * Invoked by the KCL before data records are delivered to the ShardRecordProcessor instance
     * (via processRecords). In this example we do nothing except some logging.
     *
     * @param initializationInput
     *          Provides information related to initialization.
     */
    @Override
    public void initialize(InitializationInput initializationInput) {
      shardId = initializationInput.shardId();
      MDC.put(SHARD_ID_MDC_KEY, shardId);
      try {
        log.info("Initializing @ Sequence: {}", initializationInput.extendedSequenceNumber());
      }
      finally {
        MDC.remove(SHARD_ID_MDC_KEY);
      }
    }

    /**
     * Handles record processing logic. The Amazon Kinesis Client Library will invoke this method to
     * deliver data records to the application. In this example we simply log our records.
     *
     * @param processRecordsInput
     *          Provides the records to be processed as well as information and capabilities related
     *          to them (e.g. checkpointing).
     */
    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
      MDC.put(SHARD_ID_MDC_KEY, shardId);
      try {
        log.info("Processing {} record(s)", processRecordsInput.records().size());
        processRecordsInput.records().forEach(r -> log.info("Processing record pk: {} -- Seq: {}", r.partitionKey(), r.sequenceNumber()));
      }
      catch (Throwable t) {
        log.error("Caught throwable while processing records. Aborting.");
        Runtime.getRuntime().halt(1);
      }
      finally {
        MDC.remove(SHARD_ID_MDC_KEY);
      }
    }

    /**
     * Called when the lease tied to this record processor has been lost. Once the lease has been
     * lost, the record processor can no longer checkpoint.
     *
     * @param leaseLostInput
     *          Provides access to functions and data related to the loss of the lease.
     */
    @Override
    public void leaseLost(LeaseLostInput leaseLostInput) {
      MDC.put(SHARD_ID_MDC_KEY, shardId);
      try {
        log.info("Lost lease, so terminating.");
      }
      finally {
        MDC.remove(SHARD_ID_MDC_KEY);
      }
    }

    /**
     * Called when all data on this shard has been processed. Checkpointing must occur in the method
     * for record processing to be considered complete; an exception will be thrown otherwise.
     *
     * @param shardEndedInput
     *          Provides access to a checkpointer method for completing processing of the shard.
     */
    @Override
    public void shardEnded(ShardEndedInput shardEndedInput) {
      MDC.put(SHARD_ID_MDC_KEY, shardId);
      try {
        log.info("Reached shard end checkpointing.");
        shardEndedInput.checkpointer().checkpoint();
      }
      catch (ShutdownException | InvalidStateException e) {
        log.error("Exception while checkpointing at shard end. Giving up.", e);
      }
      finally {
        MDC.remove(SHARD_ID_MDC_KEY);
      }
    }

    /**
     * Invoked when Scheduler has been requested to shut down (i.e. we decide to stop running the
     * app by pressing Enter). Checkpoints and logs the data a final time.
     *
     * @param shutdownRequestedInput
     *          Provides access to a checkpointer, allowing a record processor to checkpoint before
     *          the shutdown is completed.
     */
    @Override
    public void shutdownRequested(ShutdownRequestedInput shutdownRequestedInput) {
      MDC.put(SHARD_ID_MDC_KEY, shardId);
      try {
        log.info("Scheduler is shutting down, checkpointing.");
        shutdownRequestedInput.checkpointer().checkpoint();
      }
      catch (ShutdownException | InvalidStateException e) {
        log.error("Exception while checkpointing at requested shutdown. Giving up.", e);
      }
      finally {
        MDC.remove(SHARD_ID_MDC_KEY);
      }
    }
  }

}
