package io.rtcore.sip.proxy.plugins.rocksdb;

/**
 * store handles state for work currently in progress or that is recently completed, e.g to absorb
 * retransmits.
 *
 * the store should ideally keep state between restarts, as this allows for small upgrades without
 * disruption of service. however - this is not required. in general transactions are fairly short
 * lived (exception being INVITEs), and even the longer ones are only measured in minutes.
 * 
 * @author theo
 *
 */

public interface TransactionStore {

  void close();

}
