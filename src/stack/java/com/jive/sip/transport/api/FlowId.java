package com.jive.sip.transport.api;

import java.io.Serializable;

import com.google.common.net.HostAndPort;
import com.jive.sip.transport.udp.ListenerId;

/**
 * Base for representing a transport layer flow between two endpoints - one local, and one remote.
 * 
 * @author theo
 * 
 */
public interface FlowId extends Serializable
{

  ListenerId getListenerId();

  HostAndPort getRemote();

}
