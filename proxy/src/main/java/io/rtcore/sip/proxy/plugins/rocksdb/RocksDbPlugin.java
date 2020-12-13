package io.rtcore.sip.proxy.plugins.rocksdb;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.DBOptions;
import org.rocksdb.IngestExternalFileOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import picocli.CommandLine.Command;

@Command(name = "rocks:dump", description = "dump RocksDB")
public class RocksDbPlugin implements Callable<Integer> {

  public RocksDbTransactionStore createTransactionStore() {

    try {

      DBOptions opts = new DBOptions();

      final ColumnFamilyOptions cfo = new ColumnFamilyOptions();
      final List<ColumnFamilyHandle> cfh = new ArrayList<>();
      final List<ColumnFamilyDescriptor> cfds = new ArrayList<>();

      cfds.add(new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, cfo));

      opts.setCreateIfMissing(true);
      opts.setCreateMissingColumnFamilies(true);
      opts.setAllowIngestBehind(true);

      String path = "/tmp/rocksdb.txns";

      RocksDB db = RocksDB.open(opts, path, cfds, cfh);

      if (Files.exists(Paths.get("/tmp/db/sst_upload_01"))) {
        IngestExternalFileOptions ingestExternalFileOptions = new IngestExternalFileOptions();
        ingestExternalFileOptions.setAllowBlockingFlush(true);
        ingestExternalFileOptions.setAllowGlobalSeqNo(true);
        ingestExternalFileOptions.setIngestBehind(true);
        ingestExternalFileOptions.setMoveFiles(true);
        ingestExternalFileOptions.setSnapshotConsistency(true);
        ingestExternalFileOptions.setWriteGlobalSeqno(false);
        db.ingestExternalFile(Arrays.asList("/tmp/db/sst_upload_01"), ingestExternalFileOptions);
      }

      return new RocksDbTransactionStore(db);

    }
    catch (RocksDBException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }
  }

  @Override
  public Integer call() throws Exception {
    RocksDbTransactionStore store = this.createTransactionStore();
    store.keys();
    store.close();
    return 0;
  }

}
