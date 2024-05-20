package org.intellij.sdk.language.minimessage.tag.impl;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.intellij.sdk.language.Constants;
import org.intellij.sdk.language.minimessage.tag.Argument;
import org.intellij.sdk.language.minimessage.tag.MiniMessageTag;

public class TranslatableTag extends MiniMessageTag {

  public TranslatableTag() {
    super("lang", "translate", "tr");

    Constants.TRANSLATION_KEY_VALUES.forEach((k, v) -> {
      Argument a = literalArgument((String) k);
      argument(a);
      int count = 0;
      Matcher matcher = Pattern.compile("(?<!%)%(?!%)").matcher(v.toString());
      while (matcher.find()) count++;

      for (int i = 0; i < count; i++) {
        a = a.argument(miniMessageArgument());
      }
    });

    argument(keyArgument()
    .completions(s -> Constants.TRANSLATION_KEYS.getCompletions(s).stream().map(LookupElementBuilder::create).toList())
    );
  }
}
