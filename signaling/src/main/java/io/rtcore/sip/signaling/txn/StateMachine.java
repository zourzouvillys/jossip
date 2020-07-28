package io.rtcore.sip.signaling.txn;

public class StateMachine {

  enum TxnRole {
    Client,
    Server,
  }

  enum TxnType {
    NICT,
    NIST,
    IST,
    ICT,
  }

  /**
   * 
   */

  /**
   * the event types which can fire within a txn.
   */

  enum EventType {
    FromTU,
    FromNet,
    Message,
    TimerExpired,
    TransportError,
  }

  enum ResponseType {
    Trying,
    Provisional,
    Success,
    Failure,
  }

  private static int message_transitions[][] =
    {
    // { NICT, Client, },
    // {},
    // {}
    };

  private enum TStateMachine {

    // @formatter:off
    //
    //    /*Message type  Handshake type       Writer S2N_SERVER                S2N_CLIENT                   handshake.state              */
    //    {TLS_HANDSHAKE, TLS_CLIENT_HELLO,      'C', {s2n_client_hello_recv,    s2n_client_hello_send}},    /* CLIENT_HELLO              */
    //
    //    /* message_type_t           = {Record type, Message type, Writer, {Server handler, client handler} }  */
    //    [CLIENT_HELLO]              = {TLS_HANDSHAKE, TLS_CLIENT_HELLO, 'C', {s2n_establish_session, s2n_client_hello_send}},
    //    
    // @formatter:on

    IST {

    },

    ICT {

    },

    NICT {

    },

    NIST {

    }

  }

  enum State {

    // C: No Response Received, S: Initial Request received - No Response Sent.
    Trying,

    // C: Received Provisional, S: Sent Provisional
    Proceding,

    // C: Received Failure
    Completed,

    // C: n/a, S[INVITE ONLY]: Received ACK.
    Confirmed,

    // C: Received 2xx, S: Sent 2XX
    Accepted,

    // Expired
    Terminated
  }

  // ----

  enum InviteClient {
    Calling,
    Proceeding,
    Completed,
    Accepted,
    Terminated,
  }

  enum InviteServer {
    Proceeding,
    Completed,
    Accepted,
    Confirmed,
    Terminated,
  }

  enum NonInvite {
    NIT_Trying,
    Proceeding,
    NIT_Completed,
    Terminated,
  }

  // @formatter:off
  //
  //  static struct s2n_handshake_action state_machine[] = {
  //    /*Message type  Handshake type       Writer S2N_SERVER                S2N_CLIENT                   handshake.state              */
  //    {TLS_HANDSHAKE, TLS_CLIENT_HELLO,      'C', {s2n_client_hello_recv,    s2n_client_hello_send}},    /* CLIENT_HELLO              */
  //    {TLS_HANDSHAKE, TLS_SERVER_HELLO,      'S', {s2n_server_hello_send,    s2n_server_hello_recv}},    /* SERVER_HELLO              */
  //    {TLS_HANDSHAKE, TLS_SERVER_CERT,       'S', {s2n_server_cert_send,     s2n_server_cert_recv}},     /* SERVER_CERT               */
  //    {TLS_HANDSHAKE, TLS_SERVER_KEY,        'S', {s2n_server_key_send,      s2n_server_key_recv}},      /* SERVER_KEY                */
  //    {TLS_HANDSHAKE, TLS_SERVER_CERT_REQ,   'S', {NULL,                     NULL}},                     /* SERVER_CERT_REQ           */
  //    {TLS_HANDSHAKE, TLS_SERVER_HELLO_DONE, 'S', {s2n_server_done_send,     s2n_server_done_recv}},     /* SERVER_HELLO_DONE         */
  //    {TLS_HANDSHAKE, TLS_CLIENT_CERT,       'C', {NULL,                     NULL}},                     /* CLIENT_CERT               */
  //    {TLS_HANDSHAKE, TLS_CLIENT_KEY,        'C', {s2n_client_key_recv,      s2n_client_key_send}},      /* CLIENT_KEY                */
  //    {TLS_HANDSHAKE, TLS_CLIENT_CERT_VERIFY,'C', {NULL,                     NULL}},                     /* CLIENT_CERT_VERIFY        */
  //    {TLS_CHANGE_CIPHER_SPEC, 0,            'C', {s2n_client_ccs_recv,      s2n_client_ccs_send}},      /* CLIENT_CHANGE_CIPHER_SPEC */
  //    {TLS_HANDSHAKE, TLS_CLIENT_FINISHED,   'C', {s2n_client_finished_recv, s2n_client_finished_send}}, /* CLIENT_FINISHED           */
  //    {TLS_CHANGE_CIPHER_SPEC, 0,            'S', {s2n_server_ccs_send,      s2n_server_ccs_recv}},      /* SERVER_CHANGE_CIPHER_SPEC */
  //    {TLS_HANDSHAKE, TLS_SERVER_FINISHED,   'S', {s2n_server_finished_send, s2n_server_finished_recv}}, /* SERVER_FINISHED           */
  //    {TLS_APPLICATION_DATA, 0,              'B', {NULL, NULL}}    /* HANDSHAKE_OVER            */
  //};
  //
  // @formatter:on

}
