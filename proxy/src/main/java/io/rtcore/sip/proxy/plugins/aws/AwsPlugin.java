package io.rtcore.sip.proxy.plugins.aws;

/**
 * note: implemented in the same project right now, will pull out once there are multiple plugins.
 * 
 * @author theo
 *
 */

public class AwsPlugin {

  public AwsWorkQueue createWorkSource(String queueName) {
    return AwsWorkQueue.fromQueue(queueName);
  }

}
