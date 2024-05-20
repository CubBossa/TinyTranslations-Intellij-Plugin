package org.intellij.sdk.language.minimessage.tag.impl;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import org.intellij.sdk.language.Constants;
import org.intellij.sdk.language.minimessage.tag.MiniMessageTag;

public class KeybindTag extends MiniMessageTag {

  public KeybindTag() {
    super("key");
    argument(keyArgument()
    .completions(s -> Constants.CONTROL_KEYS.getCompletions(s).stream().map(LookupElementBuilder::create).toList()));
  }
}
