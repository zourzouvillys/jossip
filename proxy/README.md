# RESTish SIP API

# Flows

## Client Connection

To request a flow is opened to a specific destination, POST with
the desired remote target and policy spec which is the same as 
an incoming connection:

```
POST /flows/
Content-Type: application/json

{
  remote: {
    address: "1.2.3.4",
    port: 5060,
  },
  policy: {
    "max-message-size": 8192
  },
  route: [
    { 
      @type: "switch",
      range: [ { field: "method" }, { exists: "to-tag" } ],
      cases: [
        { 
          pattern: [ { "_": {} }, true ], 
          result: "https://my-api/in-dialog/${dialog-hash}"
        },
        { 
          pattern: [ "INVITE", false ], 
          result: "https://my-api/new-call"
        },
        {
          result: { @type: "error", status: 404, reason: "method not supported" },
        }
      ]
    }
  ]
}



```



# Stateless Flows

## Transmission

to send a request statelessly, using a specific flow:

```
POST /flows/fre4hj9jff
Content-Type: message/sip
Authorization: Bearer ...


INVITE sip:xyz SIP/2.0
Via: SIP/2.0/UDP localhost;branch=z9hG4bK29342a
Max-Forwards: 70
From: sip:user1@domain.com;tag=49583
To: sip:user2@domain.com
Call-ID: asd88asd77a@1.2.3.4
CSeq: 1 INVITE
Content-Type: application/sdp
Content-Length: ...

...

```

if the specified flow is no longer valid (e.g, connection closed), a 4xx will be returned.  otherwise, as long as
there is buffer space it will be accepted for delivery.  Note that there is no guaruntee that the message will actually
be delivered as the flow could fail before the packet is received by the other side.

If there is currently insufficient buffer space, a 4xx will be returned indicating this.  The client may retry after 
a while, or try a different flow if one is available.

If the message is too large for the specified flow, it will be rejected immediately. the total max size for a UDP message
depends on the underlying flow, however is will always support at least 1200 bytes and will never be larger than 64k.

No transport will support messages larger than 64k.

Otherwise, a 200 OK will be returned to indicate the message has been submitted for transmission.

### Validation

- the transport in the Via must match the transport the flow represents
- the host in Via sent-by must be authorized for use by the sender
- unless the caller is authorized to send invalid messages:
  - the message must be syntactically correct
  - all mandatory headers must be present

## Reception

All packets received over flows are dispatched to their configured handler in the same format as used for sending.

An invalid packet will be submitted to the invalid packet queue, and not dispatched.

The handler may include variables:

- type: 'request' or 'response'
- method: INVITE, ACK, CANCEL, BYE, etc ... from header or CSeq
- transactionId: branch value from top via header. note this will be the same for INVITE and the CANCEL

- path(request.uri) 
  - sip/example.com/theo ... sip:${user}@example.com
  - sip/example.com
  - tel/-/441752223399
  - tel/example.com/1001
  - urn/service/emergency





# Transactions

Although the transports only support stateless messaging,  



