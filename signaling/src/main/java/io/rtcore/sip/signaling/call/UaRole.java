package io.rtcore.sip.signaling.call;

public enum UaRole {

  UAS,

  UAC;

  public UaRole swap() {
    switch (this) {
      case UAC:
        return UAS;
      case UAS:
        return UAC;
    }
    throw new IllegalArgumentException();
  }

}
