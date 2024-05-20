package org.intellij.sdk.language.minimessage.tag.impl;

import org.intellij.sdk.language.minimessage.tag.MiniMessageTag;

public class FontTag extends MiniMessageTag {

  public FontTag() {
    super("font");

    argument(keyArgument());
  }
}
