package org.intellij.sdk.language.minimessage.tag.impl;

import java.util.List;
import org.intellij.sdk.language.Constants;
import org.intellij.sdk.language.minimessage.editor.MiniMessageCompletionContributor;
import org.intellij.sdk.language.minimessage.tag.Argument;
import org.intellij.sdk.language.minimessage.tag.MiniMessageTag;

public class TransitionTag extends MiniMessageTag {

  public TransitionTag() {
    super("transition");
    argument(repeatingArg());
  }

  private Argument repeatingArg() {
    return new Argument("<color...>") {
      @Override
      public boolean check(String arg) {
        return Constants.COLORS.containsKey(arg) || arg.matches("#[0-9a-f]{6}");
      }

      @Override
      public List<Argument> getChildren() {
        return List.of(
        repeatingArg(),
        GradientTag.intArgument(0).optional()
        );
      }
    }.completions(MiniMessageCompletionContributor.colorCompletions());
  }
}
