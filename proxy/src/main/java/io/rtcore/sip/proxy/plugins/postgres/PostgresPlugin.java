package io.rtcore.sip.proxy.plugins.postgres;

import java.net.URI;
import java.util.concurrent.Callable;

import org.rocksdb.ComparatorOptions;
import org.rocksdb.CompressionType;
import org.rocksdb.Env;
import org.rocksdb.EnvOptions;
import org.rocksdb.Options;
import org.rocksdb.RocksDBException;
import org.rocksdb.Slice;
import org.rocksdb.SstFileWriter;
import org.rocksdb.util.BytewiseComparator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.net.HostAndPort;

import io.reactivex.rxjava3.core.Flowable;
import io.rtcore.sip.proxy.plugins.aws.RdsSigner;
import io.zrz.jpgsql.client.PgSession;
import io.zrz.jpgsql.client.PostgresConnectionProperties;
import io.zrz.jpgsql.client.PostgresUtils;
import io.zrz.jpgsql.client.opj.PgThreadPooledClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "pgprime", description = "prime from postgres")
public class PostgresPlugin implements Callable<Integer> {

  @Parameters(arity = "1")
  public URI uri;

  private final ObjectMapper mapper = new ObjectMapper();

  public void initialize() {

    String username = uri.getUserInfo();
    String dbname = uri.getPath().substring(1);
    HostAndPort target = HostAndPort.fromString(uri.getHost());

    PostgresConnectionProperties config =
      PostgresConnectionProperties.builder()
        .dbname(dbname)
        .username(username)
        .password(() -> RdsSigner.getAuthToken(target, username))
        .hostname(target.getHost())
        .build();

    PgThreadPooledClient pool = PgThreadPooledClient.create(config);

    try (PgSession s = pool.openSession()) {

      Options options = new Options();
      EnvOptions envOptions = new EnvOptions();
      ComparatorOptions comparatorOptions = new ComparatorOptions();

      options =
        options
          .setCreateIfMissing(true)
          .setEnv(Env.getDefault())
          .setCompressionType(CompressionType.ZSTD_COMPRESSION)
          .setComparator(new BytewiseComparator(comparatorOptions));

      try (SstFileWriter sst = new SstFileWriter(envOptions, options)) {

        sst.open("pgprime_data");

        String query =
          "select username, passwords, tenant, primary, secondary from accounts"
        //
        ;

        System.err.println(query);

        Flowable.fromPublisher(
          s.submit(query))
          .flatMap(PostgresUtils.rowMapper())
          .blockingForEach(row -> {

            ObjectNode node = JsonNodeFactory.instance.objectNode();

            node.put("username", row.strval(0));
            node.set("passwords", mapper.readTree(row.strval(1)));
            node.put("tenant", row.strval(2));
            node.put("primary_server", row.strval(3));
            node.put("secondary_server", row.strval(4));

            System.err.println(row.strval("username") + ": " + node.toString());

            sst.put(new Slice(row.strval("username")), new Slice(node.toString()));

          });

        sst.finish();

        System.err.println(query);

      }
      catch (RocksDBException e) {
        // TODO Auto-generated catch block
        throw new RuntimeException(e);
      }

    }

  }

  @Override
  public Integer call() throws Exception {
    initialize();
    return 0;
  }

}
