package org.intellij.sdk.language.nanomessage.completion;

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import org.jetbrains.annotations.NotNull;

public class NanoMessageClickTemplateContextType extends TemplateContextType {

  NanoMessageClickTemplateContextType() {
    super("NanoMessage");
  }

  @Override
  public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
    return true;
  }
}
