## DNS Watcher

```

DnsWatcher watcher = DnsWatcher.create(DnsClient.createDOHClient("https://dns.google/resolve", ClientSubnet.forAddress("1.2.3.4")));

var status = watcher.srv(DnsWatcher.SIP_TLS, "some.domain");

    
```