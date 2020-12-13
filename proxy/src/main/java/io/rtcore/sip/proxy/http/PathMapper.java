package io.rtcore.sip.proxy.http;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.re2j.Matcher;
import com.google.re2j.Pattern;

final class PathMapper {

  //

  private static final ImmutableMap<String, PathMatch> patterns =
    ImmutableMap.<String, PathMatch>builder()
      .put("flow", compile("/flows/{flowId}"))
      .build();

  static Map.Entry<String, Map<String, String>> parse(CharSequence path) {

    for (Entry<String, PathMatch> e : patterns.entrySet()) {

      LinkedHashMap<String, String> res = e.getValue().match(path);

      if (res != null) {
        return Maps.immutableEntry(e.getKey(), res);
      }

    }

    return null;

  }

  private final static class PathMatch {

    private final Pattern pattern;
    private final ImmutableList<String> variables;

    PathMatch(Pattern pattern, ArrayList<String> vars) {
      this.pattern = pattern;
      this.variables = ImmutableList.copyOf(vars);
    }

    public LinkedHashMap<String, String> match(CharSequence input) {

      Matcher m = pattern.matcher(input);

      if (!m.matches()) {
        return null;
      }

      LinkedHashMap<String, String> values = new LinkedHashMap<>(m.groupCount());

      for (int i = 0; i < m.groupCount(); ++i) {
        values.put(variables.get(i), m.group(i + 1));
      }

      return values;

    }

  }

  private static PathMatch compile(String template) {

    final Matcher m = Pattern.compile("(?:([^{]+)|\\{([a-zA-Z0-9-_]+)(?::([^}]+))?\\}|(\\*{1,2}))").matcher(template);

    final StringBuilder sb = new StringBuilder();
    final ArrayList<String> vars = new ArrayList<>();

    sb.append("^");

    while (m.find()) {

      if (m.group(1) != null) {

        sb.append(Pattern.quote(m.group(1)));

      }
      else if (m.group(2) != null) {
        vars.add(m.group(2));
        String regex = m.group(3);
        if (regex == null) {
          regex = "[^/]+";
        }
        sb.append("(");
        sb.append(regex);
        sb.append(")");
      }
      else if (m.group(4) != null) {
        switch (m.group(4)) {
          case "*":
            sb.append("[^/]+");
            break;
          case "**":
            sb.append(".+");
            break;
        }
      }
      else {
        throw new IllegalArgumentException();
      }

    }

    sb.append("$");

    return new PathMatch(Pattern.compile(sb.toString()), vars);

  }

}
