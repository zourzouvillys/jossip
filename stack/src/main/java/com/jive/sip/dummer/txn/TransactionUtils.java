package com.jive.sip.dummer.txn;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.google.common.collect.Lists;
import com.jive.sip.message.api.BranchId;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.Via;
import com.jive.sip.message.api.ViaProtocol;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.api.TokenParameterValue;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.transport.api.FlowId;

public class TransactionUtils
{

  private static final char[] symbols;

  static
  {
    final StringBuilder tmp = new StringBuilder();
    for (char ch = '0'; ch <= '9'; ++ch)
    {
      tmp.append(ch);
    }
    for (char ch = 'a'; ch <= 'z'; ++ch)
    {
      tmp.append(ch);
    }
    for (char ch = 'A'; ch <= 'Z'; ++ch)
    {
      tmp.append(ch);
    }
    symbols = tmp.toString().toCharArray();
  }

  private static boolean sendRport = System.getProperty("asterisk.is.broken.and.i.hate.it", null) == null;

  public static String randomAlphanumeric(final int len)
  {

    final char[] buf = new char[len];

    for (int idx = 0; idx < buf.length; ++idx)
    {
      buf[idx] = symbols[ThreadLocalRandom.current().nextInt(symbols.length)];
    }

    return new String(buf);
  }

  /**
   * Adds a via header.
   *
   * @param req
   * @param branch
   * @param runtime
   * @param protocol
   * @return
   */

  public static SipRequest addVia(final SipRequest req, final BranchId branch, final TransactionRuntime runtime,
      final ViaProtocol protocol, final FlowId flowId)
  {

    final List<RawParameter> params = Lists.newLinkedList();

    params.add(new RawParameter("branch", new TokenParameterValue(branch.getValue())));

    if (sendRport)
    {
      params.add(new RawParameter("rport"));
    }

    final Via via = new Via(protocol, runtime.getSelf(flowId), DefaultParameters.from(params));

    return req.withPrepended("Via", via);

  }
  //
  // public static Pair<SipUri, SipRequest> popRoute(SipRequest req, final HostAndPort me)
  // {
  //
  // if (req.getRoute().isEmpty())
  // {
  // throw new RuntimeException("No Route headers in request");
  // }
  //
  // final List<NameAddr> routes = Lists.newLinkedList(req.getRoute());
  //
  // req = req.withoutHeaders("Route");
  //
  // final SipUri self = routes.remove(0).getAddress().apply(SipUriExtractor.getInstance());
  //
  // if (!self.getHost().equals(me))
  // {
  // throw new RuntimeException(String.format("Top route header wasn't %s, it was %s", me, self));
  // }
  //
  // if (!routes.isEmpty())
  // {
  // req = req.withParsed("Route", routes);
  // }
  //
  // return xPair.of(self, req);
  //
  // }

}
