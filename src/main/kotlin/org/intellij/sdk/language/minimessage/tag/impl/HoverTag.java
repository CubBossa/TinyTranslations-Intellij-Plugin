package org.intellij.sdk.language.minimessage.tag.impl;

import org.intellij.sdk.language.minimessage.tag.MiniMessageTag;

public class HoverTag extends MiniMessageTag {

  public HoverTag() {
    super("hover");

    argument(literalArgument("show_text").argument(miniMessageArgument()));
    argument(literalArgument("show_item").argument(itemArgument()));
    argument(literalArgument("show_entity").argument(entityArgument()));
  }
}
