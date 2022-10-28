package io.rtcore.sip.channels.netty.udp;

public class DatagramICT {

  enum Action {
    
  }
  
  
  enum Event {
    Provisional,
    Success,
    Failure,
    TransportError,
  }
  
  enum State {
    Calling,
    Proceeding,
    Completed,
    Accepted,
    Terminated,
  }

  enum Timer {
    TimerA,
    TimerB,
    TimerD,
    TimerM,
  }
  
}
