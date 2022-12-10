package io.rtcore.gateway;

import com.google.common.util.concurrent.ServiceManager;

import dagger.Component;
import io.rtcore.gateway.engine.SipEngineModule;

@Component(modules = {

  RestModule.class,

  SipEngineModule.class

})
interface Server {

  /**
   * the service manager for controlling startup/shutdown.
   */

  ServiceManager serviceManager();

}
