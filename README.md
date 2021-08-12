# Java SIP Stuff


## RFC 3261 Message & Parser Library

Library which provides an extensible SIP message API and RFC 3261 parser.  It does not include any transports or transaction manager.

The models are immutable.  Helper message builders are provided for creating or modifying messages.

## SIP Channels

the sip-channels module provides an API for interacting with other SIP endpoints, both as a UAC and UAS.  The APIs are all async, reactive, and with flow control.

the sip-channels-netty module provides a netty based implementation of UDP, TCP, TLS, and WS(S) transports.

