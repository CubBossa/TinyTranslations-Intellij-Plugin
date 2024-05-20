package org.intellij.sdk.language.util;

public class StringUtil {

  public static String unquote(String string) {
    if (string.startsWith("\"") && string.endsWith("\"")) {
      return string.substring(1, string.length() - 1);
    }
    if (string.startsWith("'") && string.endsWith("'")) {
      return string.substring(1, string.length() - 1);
    }
    return string;
  }

}
