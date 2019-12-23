# Java SIP Stuff


## RFC 3261 Message & Parser Library

Library which provides an extensible SIP message API and RFC 3261 parser.  It does not include any transports or transaction manager.

The models are immutable.  Helper message builders are provided for creating or modifying messages.

## Signaling Engine

Provides a high level API and serializable state machines for handling of SIP INVITE based dialogs and the negotiation needed to process calls and offer/answers.

 