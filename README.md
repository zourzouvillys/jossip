# Java SIP Stuff

## Quick Start

```
implementation 'io.zrz.rtcore.sip:sip-parser:x.x.x'
```


## RFC 3261 Message & Parser Library

Library which provides an extensible SIP message API and RFC 3261 parser.  It does not include any transports or transaction manager.

The models are immutable.  Helper message builders are provided for creating or modifying messages.

## SIP Channels

the sip-channels module provides an API for interacting with other SIP endpoints, both as a UAC and UAS.  The APIs are all async, reactive, and with flow control.

the sip-channels-netty module provides a netty based implementation of UDP, TCP, TLS, and WS(S) transports.


## Client SIP Exchange

```

// the channel opener.
SipTlsConnectionProvider opener = SipTlsConnectionProvider.createProvider(...);

// open the underlying SIP channel.
SipConnection conn = opener.requestConnection(
  ImmutableSipRoute.builder()
    .transportProtocol(StandardSipTransportName.TLS)
    .localAddress(new InetSocketAddress(0))
    // the names of the target for X509 verification 
    .addRemoteServerNames(new SNIHostName("proxy.example.invalid"))
    // the remote address to connect to
    .remoteAddress(new InetSocketAddress(InetAddresses.forString("1.2.3.4"), 5061))
    // the local socks5a proxy to use to connect to
    .addProxyChain(new InetSocketAddress(InetAddresses.forString("10.0.0.1"), 1080))
    .build());

//
System.err.println("[connection established]");

// exchange a message on this connection.
conn
  .exchange(createRequest())
  .responses()
  .blockingSubscribe(
    res -> SipChannelUtils.dumpFrame(System.err, res.response()),
    err -> err.printStackTrace(),
    () -> System.err.println("[txn completed]")
  );

System.err.println("[exchange completed]");

// close the connection...
conn.close();

````

