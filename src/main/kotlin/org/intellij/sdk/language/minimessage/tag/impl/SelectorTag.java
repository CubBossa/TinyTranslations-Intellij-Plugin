package org.intellij.sdk.language.minimessage.tag.impl;

import org.intellij.sdk.language.minimessage.tag.MiniMessageTag;

public class SelectorTag extends MiniMessageTag {

  public SelectorTag() {
    super("selector", "sel");

    argument(selectorArgument().argument(miniMessageArgument()));
  }
}
