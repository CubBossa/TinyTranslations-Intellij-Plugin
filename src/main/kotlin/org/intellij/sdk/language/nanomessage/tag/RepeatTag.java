package org.intellij.sdk.language.nanomessage.tag;

import org.intellij.sdk.language.minimessage.tag.MiniMessageTag;

public class RepeatTag extends MiniMessageTag {

  public RepeatTag() {
    super("repeat");
    argument(intArgument(0));
  }
}
