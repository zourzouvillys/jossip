package io.rtcore.sip.proxy.plugins.aws.kinesis;

import java.time.Duration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.kinesis.producer.KinesisProducer;
import com.amazonaws.services.kinesis.producer.KinesisProducerConfiguration;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;

public class KinesisWriter {

  private final String streamName;
  private final KinesisProducer producer;

  private static class CredentialsProviderSupplier implements AWSCredentialsProvider {

    private AwsCredentialsProvider credentialsProvider;

    public CredentialsProviderSupplier(AwsCredentialsProvider credentialsProvider) {
      this.credentialsProvider = credentialsProvider;
    }

    @Override
    public void refresh() {
      // background handled in aws-sdk 2.0.
    }

    @Override
    public AWSCredentials getCredentials() {

      AwsCredentials creds = credentialsProvider.resolveCredentials();

      if (creds instanceof AwsSessionCredentials) {
        AwsSessionCredentials sessionCreds = (AwsSessionCredentials) creds;
        return new BasicSessionCredentials(sessionCreds.accessKeyId(), sessionCreds.secretAccessKey(), sessionCreds.sessionToken());
      }

      return new BasicAWSCredentials(creds.accessKeyId(), creds.secretAccessKey());

    }

  }

  public KinesisWriter(String streamName, AwsCredentialsProvider credentialsProvider) {

    this.streamName = streamName;

    final KinesisProducerConfiguration config = new KinesisProducerConfiguration();

    config.setAggregationEnabled(true);
    // config.setAggregationMaxCount(4294967295L);
    // config.setAggregationMaxSize(51200);
    config.setRateLimit(150);
    config.setMaxConnections(24);

    // how long a record can be bufferd without submitting before we give up.
    // note that when we give up we need to resynchronize! but also don't want
    // stale data around forever.
    config.setRecordTtl(Duration.ofSeconds(30).toMillis());

    // config.setThreadPoolSize(settings.threadPoolSize().orElse(0));
    config.setThreadingModel(KinesisProducerConfiguration.ThreadingModel.POOLED);
    config.setRecordMaxBufferedTime(Duration.ofMillis(100).toMillis());
    config.setRequestTimeout(Duration.ofSeconds(6).toMillis());

    // region based on the stream ARN.
    config.setRegion("us-west-2");

    // todo: use AssumeRole if different account?
    config.setCredentialsProvider(new CredentialsProviderSupplier(credentialsProvider));

    this.producer = new KinesisProducer(config);

  }

}
