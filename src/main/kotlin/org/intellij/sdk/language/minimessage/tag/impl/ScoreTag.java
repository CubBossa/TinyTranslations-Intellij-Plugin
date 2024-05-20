package org.intellij.sdk.language.minimessage.tag.impl;

import org.intellij.sdk.language.minimessage.tag.MiniMessageTag;

public class ScoreTag extends MiniMessageTag {

  public ScoreTag() {
    super("score");

    argument(textArgument().argument(textArgument()));
    argument(selectorArgument().argument(textArgument()));
  }
}
