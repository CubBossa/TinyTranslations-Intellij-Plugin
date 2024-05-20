package org.intellij.sdk.language.minimessage.tag.impl;

import org.intellij.sdk.language.minimessage.tag.MiniMessageTag;

public class ClickTag extends MiniMessageTag {

  public static final ClickTag INSTANCE = new ClickTag();

  public ClickTag() {
    super("click");

    argument(literalArgument("change_page").argument(numberArgument()));
    argument(literalArgument("copy_to_clipboard").argument(textArgument()));
    argument(literalArgument("open_file").argument(fileArgument()));
    argument(literalArgument("open_url").argument(urlArgument()));
    argument(literalArgument("run_command").argument(commandArgument()));
    argument(literalArgument("suggest_command").argument(commandArgument()));
  }
}
