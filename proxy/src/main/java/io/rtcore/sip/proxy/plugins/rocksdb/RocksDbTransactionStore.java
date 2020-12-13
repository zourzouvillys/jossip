package io.rtcore.sip.proxy.plugins.rocksdb;

import java.io.IOException;

import org.rocksdb.ReadOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RocksDbTransactionStore implements TransactionStore {

  private RocksDB db;
  private final ObjectMapper mapper = new ObjectMapper();

  public RocksDbTransactionStore(RocksDB db) {
    this.db = db;
  }
  
  /**
   * @param key
   * @return
   */

  public ObjectNode get(String key) {
    try {
      byte[] val = db.get(key.getBytes());
      if (val == null) {
        return null;
      }
      return mapper.readValue(val, ObjectNode.class);
    }
    catch (RocksDBException | IOException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }
  }

  public void keys() {
    ReadOptions opts = new ReadOptions();
    RocksIterator it = db.newIterator(opts);
    try {
      it.seekToFirst();
      while (it.isValid()) {
        byte[] key = it.key();
        String strkey = new String(key);
        System.err.println(get(strkey));
        it.next();
      }
    }
    finally {
      it.close();
    }
  }

  @Override
  public void close() {
    try {
      this.db.closeE();
    }
    catch (RocksDBException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }
  }

}
