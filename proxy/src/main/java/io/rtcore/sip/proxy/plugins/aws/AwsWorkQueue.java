package io.rtcore.sip.proxy.plugins.aws;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequest.Builder;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchResponse;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

public class AwsWorkQueue {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AwsWorkQueue.class);

  private SqsAsyncClient sqsClient;

  private String queueUrl;

  private AwsWorkQueue(SqsAsyncClient sqsClient, String queueUrl) {
    this.sqsClient = sqsClient;
    this.queueUrl = queueUrl;
  }

  public CompletableFuture<ReceiveMessageResponse> receiveMessages(int maxMessages, int waitTimeSeconds) {
    ReceiveMessageRequest receiveMessageRequest =
      ReceiveMessageRequest.builder()
        .queueUrl(this.queueUrl + "#XYZ")
        .waitTimeSeconds(waitTimeSeconds)
        .maxNumberOfMessages(maxMessages)
        .attributeNames(QueueAttributeName.ALL)
        .messageAttributeNames("All")
        .build();
    return sqsClient.receiveMessage(receiveMessageRequest);
  }

  public static AwsWorkQueue fromQueue(String queueName) {

    try {

      SqsAsyncClient sqsClient =
        SqsAsyncClient.builder()
          .region(Region.US_WEST_2)
          .build();

      CompletableFuture<GetQueueUrlResponse> getQueueUrlResponse =
        sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build());

      String queueUrl = getQueueUrlResponse.get().queueUrl();

      return new AwsWorkQueue(sqsClient, queueUrl);

    }
    catch (ExecutionException | InterruptedException ex) {

      throw new RuntimeException(ex);

    }

  }

  public CompletableFuture<DeleteMessageBatchResponse> removeMessages(Collection<DeleteMessageBatchRequestEntry> entries) {
    Builder rb = DeleteMessageBatchRequest.builder();
    rb.queueUrl(this.queueUrl);
    rb.entries(entries);
    log.info("deleting {} message handles", entries.size());
    return sqsClient.deleteMessageBatch(rb.build());
  }

}
