package org.intellij.sdk.language.minimessage.tag.impl;

import org.intellij.sdk.language.minimessage.tag.MiniMessageTag;

public class ColorTag extends MiniMessageTag {

  public static final ColorTag INSTANCE = new ColorTag();

  public ColorTag() {
    super("color");
    argument(colorArgument());
  }
}
