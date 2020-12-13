package io.rtcore.sip.proxy.cmd.gateway;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.eventbus.EventBus;

import io.rtcore.sip.proxy.chronicle.ChronicleMessageWriter;
import io.rtcore.sip.proxy.chronicle.Payloads;
import io.rtcore.sip.proxy.chronicle.Payloads.Frame;
import io.rtcore.sip.proxy.client.SipClientManager;
import io.rtcore.sip.proxy.plugins.aws.AwsPlugin;
import io.rtcore.sip.proxy.plugins.aws.AwsWorkQueue;
import io.rtcore.sip.proxy.plugins.rocksdb.RocksDbPlugin;
import io.rtcore.sip.proxy.plugins.rocksdb.TransactionStore;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

@Command(name = "gateway", description = "provide gateway behavior with a plugin source")
public class GatewayCommand implements Runnable {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GatewayCommand.class);

  @Option(names = { "--queueName" }, required = true)
  private String queueName;

  @Option(names = { "--txpath" }, required = true)
  private String txpath;

  @Override
  public void run() {
    try {

      run(new AwsPlugin(), new RocksDbPlugin());
      
    }
    catch (InterruptedException | ExecutionException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }
  }

  private void run(AwsPlugin plugin, RocksDbPlugin db) throws InterruptedException, ExecutionException {

    AwsWorkQueue workQueue = plugin.createWorkSource(queueName);

    ChronicleMessageWriter writer = new ChronicleMessageWriter(Paths.get(txpath));

    TransactionStore txns = db.createTransactionStore();

    EventBus bus = new EventBus();

    SipClientManager client = new SipClientManager(writer);

    while (true) {

      ReceiveMessageResponse res = workQueue.receiveMessages(1, 5).get();

      HashSet<DeleteMessageBatchRequestEntry> removeHandles = new HashSet<>();

      for (Message msg : res.messages()) {

        String messageId = msg.messageId();

        log.debug("sending from via SQS messageId={}", messageId);
        log.debug("attributes: {}", msg.attributes());
        log.debug("message attributes: {}", msg.messageAttributes());

        try {

          Frame f = Payloads.read(msg.body());

          // writer.write(f);

          client.send(f.remote(), f.payload());

          removeHandles.add(DeleteMessageBatchRequestEntry.builder()
            .id(msg.messageId())
            .receiptHandle(msg.receiptHandle())
            .build());

        }
        catch (JsonProcessingException e) {
          log.warn("error processing frame", e.getMessage(), e);
        }

      }

      if (!removeHandles.isEmpty()) {
        workQueue.removeMessages(removeHandles).get();
      }

    }

  }

}
