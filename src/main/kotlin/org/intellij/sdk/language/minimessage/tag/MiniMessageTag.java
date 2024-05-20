package org.intellij.sdk.language.minimessage.tag;

import static org.intellij.sdk.language.TinyTranslationsIcons.Tag;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MiniMessageTag extends Argument {

  protected String[] alias;

  public MiniMessageTag(String name, String... alias) {
    super(name, new ArrayList<>());
    this.alias = alias;
  }

  @Override
  public List<? extends LookupElement> getCompletions(String in) {
    if (alias.length == 0) {
      return List.of(LookupElementBuilder.create(name).withIcon(Tag));
    }
    List<String> list = new ArrayList<>(this.alias.length + 1);
    list.add(name);
    list.addAll(Arrays.stream(alias).toList());
    return List.of(LookupElementBuilder.create(list).withIcon(Tag));
  }

  @Override
  public boolean check(String arg) {
    if (arg.equals(name)) {
      return true;
    }
    for (String s : alias) {
      if (arg.equals(s)) {
        return true;
      }
    }
    return false;
  }
}
