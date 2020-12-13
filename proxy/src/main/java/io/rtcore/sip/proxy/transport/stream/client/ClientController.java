package io.rtcore.sip.proxy.transport.stream.client;

import java.util.Collection;

import javax.inject.Inject;

import com.google.common.base.Stopwatch;
import com.google.common.eventbus.EventBus;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.rtcore.sip.message.base.api.RawMessage;
import io.rtcore.sip.message.processor.rfc3261.parsing.DefaultRfcMessageParser;
import io.rtcore.sip.proxy.actions.OpenStream;

@Controller("/")
public class ClientController {

  private static DefaultRfcMessageParser parser = new DefaultRfcMessageParser();
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ClientController.class);

  @Inject
  private EventBus bus;

  @Inject
  private ClientStreamRegistry registry;

  // X-Twilio-Error: 32201 Authentication failure - source IP Address not in ACL.

  @Get(value = "/flows", produces = MediaType.APPLICATION_JSON)
  public Collection<Flow> listFlows() {
    log.info("listning flows");
    return registry.flows();
  }

  @Post(value = "/flows", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
  public String openFlow(OpenStream open) {
    log.info("processing flow open request: {}", open);
    bus.post(open);
    return open.toString();
  }

  @Post(value = "/flows/{flowId}", consumes = "message/sip", produces = MediaType.APPLICATION_JSON)
  public HttpResponse<?> sendRawMessage(@PathVariable String flowId, byte[] body) {
    Stopwatch timer = Stopwatch.createStarted();
    try {
      RawMessage raw = parser.parse(body, body.length);
      log.debug("processing flow send request: {}, body: {}", flowId, raw.getInitialLine());
      Flow flow = registry.get(flowId);
      if (flow == null) {
        return HttpResponse.notFound();
      }
      flow.txmit(raw);
      return HttpResponse.ok();
    }
    finally {
      timer.stop();
      log.debug("processed send in {}", timer);
    }
  }

  @Delete(value = "/flows/{flowId}")
  public String closeFlow(@PathVariable String flowId) {
    log.info("closing flow {}", flowId);
    return flowId;
  }

}
