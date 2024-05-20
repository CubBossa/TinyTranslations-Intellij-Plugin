package org.intellij.sdk.language.nanomessage.tag;

import org.intellij.sdk.language.minimessage.tag.MiniMessageTag;

public class MessageTag extends MiniMessageTag {
  public MessageTag() {
    super("msg", "message");

    argument(keyArgument());
  }
}
