package io.rtcore.sip.proxy.plugins.rocksdb;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

class RocksDbTransactionStoreTest {

  @Test
  void test() {

    RocksDbPlugin db = new RocksDbPlugin();

    RocksDbTransactionStore store = db.createTransactionStore();

    ObjectNode res = store.get("9523-SumoMaya");

    System.err.println(res.at("/username"));
    System.err.println(res.findPath("passwords"));
    System.err.println(res.at("/tenant"));
    System.err.println(res.at("/primary_server"));
    System.err.println(res.at("/secondary_server"));

  }

}
