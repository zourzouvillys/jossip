package io.rtcore.sip.message.message.api.headers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.api.SipParameterDefinition;
import io.rtcore.sip.message.parameters.api.TokenParameterValue;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.parameters.tools.ParameterUtils;
import io.rtcore.sip.message.uri.Uri;

/**
 * First stab at a History-Info header.
 *
 * 
 *
 */

public class HistoryInfo {

  public static final SipParameterDefinition<Token> P_RC = ParameterUtils.createFlagParameterDefinition(Token.from("c"));
  public static final SipParameterDefinition<Token> P_MP = ParameterUtils.createFlagParameterDefinition(Token.from("mp"));
  public static final SipParameterDefinition<Token> P_NP = ParameterUtils.createFlagParameterDefinition(Token.from("np"));

  public enum ChangeType {
    // o "rc": The Request-URI has changed while the target user associated
    // with the original Request-URI prior to retargeting has been
    // retained.
    RC,
    // o "mp": The target was determined based on a mapping to a user other
    // than the target user associated with the Request-URI being
    // retargeted.
    MP,
    // o "np": The target hasn't changed, and the associated Request-URI
    // remained the same.
    NP,
    // unknown (none specified?
    Unknown

  }

  @Value.Immutable(builder = false)
  @Value.Style(
      jdkOnly = true,
      allowedClasspathAnnotations = { Override.class })
  public static abstract class Entry {

    @Value.Parameter
    public abstract Uri uri();

    @Value.Parameter
    public abstract int[] index();

    @Value.Parameter
    public abstract ChangeType type();

    @Value.Parameter
    public abstract int[] prev();

    public NameAddr toNameAddr() {
      final List<RawParameter> raw = new ArrayList<>();
      switch (this.type()) {
        case MP:
          raw.add(new RawParameter("mp", new TokenParameterValue(Token.from(buildIndex(prev())))));
          break;
        case NP:
          raw.add(new RawParameter("np", new TokenParameterValue(Token.from(buildIndex(prev())))));
          break;
        case RC:
          raw.add(new RawParameter("rc", new TokenParameterValue(Token.from(buildIndex(prev())))));
          break;
        default:
          break;
      }

      if ((this.index() != null) && (this.index().length > 0)) {
        raw.add(new RawParameter("index", new TokenParameterValue(Token.from(buildIndex(this.index())))));
      }

      return new NameAddr(this.uri(), DefaultParameters.from(raw));

    }

    public ChangeType changeType() {
      return this.type();
    }

    public static Entry of(Uri address, int[] index, ChangeType type, int[] prev) {
      return ImmutableEntry.of(address, index, type, prev);
    }

  }

  public static final HistoryInfo EMPTY = new HistoryInfo(List.of());
  private static final int[] INITIAL_INDEX =
    { 1 };
  private final List<Entry> entries;

  private static String buildIndex(int[] index) {
    StringBuilder sb = new StringBuilder();
    int i = 0;
    for (int id : index) {
      if (i++ > 0) {
        sb.append('.');
      }
      sb.append(id);
    }
    return sb.toString();
  }

  public HistoryInfo(final List<Entry> entries) {
    this.entries = entries;
  }

  public List<Entry> entries() {
    // TODO: immutable?
    return this.entries;
  }

  public Optional<Entry> last() {
    if (this.entries.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(this.entries.get(this.entries.size() - 1));
  }

  public static HistoryInfo build(final List<NameAddr> nas) {
    final List<Entry> entries = new ArrayList<>();
    for (final NameAddr na : nas) {
      int[] prev = new int[] { 1 };
      entries.add(Entry.of(na.address(), extractIndex(na), extractType(na), prev));
    }
    return new HistoryInfo(entries);
  }

  private static int[] extractIndex(final NameAddr na) {
    return new int[] { 0 };
  }

  private static ChangeType extractType(final NameAddr na) {
    if (na.getParameter(P_RC).isPresent()) {
      return ChangeType.RC;
    }
    else if (na.getParameter(P_MP).isPresent()) {
      return ChangeType.MP;
    }
    else if (na.getParameter(P_NP).isPresent()) {
      return ChangeType.NP;
    }
    return ChangeType.Unknown;
  }

  public HistoryInfo withAppended(final Uri target) {
    final List<Entry> entries = new ArrayList<>();
    entries.addAll(this.entries);
    entries.add(Entry.of(target, INITIAL_INDEX, ChangeType.MP, new int[] { 1 }));
    return new HistoryInfo(entries);
  }

  public HistoryInfo withRecursion(final Uri target) {
    final List<Entry> entries = new ArrayList<>();
    entries.addAll(this.entries);
    entries.add(Entry.of(target, INITIAL_INDEX, ChangeType.RC, new int[] { 1 }));
    return new HistoryInfo(entries);
  }

  public HistoryInfo withRetarget(final Uri target) {
    final List<Entry> entries = new ArrayList<>();
    entries.addAll(this.entries);
    entries.add(Entry.of(target, INITIAL_INDEX, ChangeType.MP, new int[] { 1 }));
    return new HistoryInfo(entries);
  }

  public HistoryInfo withNoChange(final Uri target) {
    final List<Entry> entries = new ArrayList<>();
    entries.addAll(this.entries);
    entries.add(Entry.of(target, INITIAL_INDEX, ChangeType.NP, new int[] { 1 }));
    return new HistoryInfo(entries);
  }

  public static HistoryInfo fromUnknownRequest(Uri target) {
    final List<Entry> entries = List.of(Entry.of(target, INITIAL_INDEX, ChangeType.Unknown, new int[] {}));
    return new HistoryInfo(entries);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    int i = 0;
    for (Entry e : entries) {
      if (i++ > 0) {
        sb.append(", ");
      }
      sb.append(e.toNameAddr());
    }
    return sb.toString();
  }

  public boolean isEmpty() {
    return this.entries.isEmpty();
  }

}
