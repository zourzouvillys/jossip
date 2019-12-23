
/**
 * @formatter:off
 
   UAC-II:   While an INVITE transaction is incomplete or ACK
             transaction associated with an offer/answer is incomplete,
             a UA must not send another INVITE request.

   UAC-UU:   While an UPDATE transaction is incomplete, a UA must not
             send another UPDATE request.

   UAC-UI:   While an UPDATE transaction is incomplete, a UA should not
             send a re-INVITE request.

   UAC-IU:   While an INVITE transaction is incomplete, and an ACK or a
             PRACK transaction associated with an offer/answer is
             incomplete, a UA should not send an UPDATE request.
             
             
             

   UAS-IcI:  While an INVITE client transaction is incomplete or ACK
             transaction associated with an offer/answer is incomplete,
             a UA must reject another INVITE request with a 491
             response.

   UAS-IsI:  While an INVITE server transaction is incomplete or ACK
             transaction associated with an offer/answer is incomplete,
             a UA must reject another INVITE request with a 500
             response.

   UAS-UcU:  While an UPDATE client transaction is incomplete, a UA must
             reject another UPDATE request with a 491 response.

   UAS-UsU:  While an UPDATE server transaction is incomplete, a UA must
             reject another UPDATE request with a 500 response.

   UAS-UcI:  While an UPDATE client transaction is incomplete, a UA
             should reject a re-INVITE request with a 491 response.

   UAS-UsI:  While an UPDATE server transaction is incomplete, a UA
             should reject a re-INVITE request with a 500 response.

   UAS-IcU:  While an INVITE client transaction is incomplete, and an
             ACK or a PRACK transaction associated with an offer/answer
             is incomplete, a UA should reject an UPDATE request with a
             491 response.

   UAS-IsU:  While an INVITE server transaction is incomplete, and an
             ACK or a PRACK transaction associated with an offer/answer
             is incomplete, a UA should reject an UPDATE request with a
             500 response.




        Offer                Rejection
     ------------------------------------------------------------------
     1. INVITE Req. (*)      488 INVITE Response
     2. 2xx INVITE Resp.     Answer in ACK Req. followed by new offer
                             OR termination of dialog
     3. INVITE Req.          488 INVITE Response (same as Pattern 1)
     4. 1xx-rel INVITE Resp. Answer in PRACK Req. followed by new offer
     5. PRACK Req. (**)      200 PRACK Resp. followed by new offer
                             OR termination of dialog
     6. UPDATE Req.          488 UPDATE Response

   (*) If this was a re-INVITE, a failure response should not be sent if
   media has already been exchanged using the new offer.

   (**) A UA should only use PRACK to send an offer when it has strong
   reasons to expect the receiver will accept the offer.

                      Table 2: Rejection of an Offer
  

         Offer                Answer             RFC    Ini Est Early
   -------------------------------------------------------------------
   1. INVITE Req.          2xx INVITE Resp.     RFC 3261  Y   Y    N
   2. 2xx INVITE Resp.     ACK Req.             RFC 3261  Y   Y    N
   3. INVITE Req.          1xx-rel INVITE Resp. RFC 3262  Y   Y    N
   4. 1xx-rel INVITE Resp. PRACK Req.           RFC 3262  Y   Y    N
   5. PRACK Req.           200 PRACK Resp.      RFC 3262  N   Y    Y
   6. UPDATE Req.          2xx UPDATE Resp.     RFC 3311  N   Y    Y
  

 * @formatter:on
 */
package io.rtcore.sip.signaling.call;
