package org.intellij.sdk.language.nanomessage.tag;

import org.intellij.sdk.language.minimessage.tag.Argument;
import org.intellij.sdk.language.minimessage.tag.MiniMessageTag;

public class LegacyTag extends MiniMessageTag {

  public LegacyTag() {
    super("legacy");

    argument(new Argument("symbol") {
      @Override
      public boolean check(String arg) {
        return arg.matches("([&§])");
      }
    }).completions("&", "§");
  }
}
