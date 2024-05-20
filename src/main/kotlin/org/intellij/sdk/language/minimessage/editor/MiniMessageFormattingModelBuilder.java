package org.intellij.sdk.language.minimessage.editor;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.FormattingContext;
import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.formatting.FormattingModelProvider;
import com.intellij.formatting.Wrap;
import com.intellij.formatting.WrapType;
import org.jetbrains.annotations.NotNull;

public class MiniMessageFormattingModelBuilder implements FormattingModelBuilder {

  @Override
  public @NotNull FormattingModel createModel(@NotNull FormattingContext formattingContext) {
    return FormattingModelProvider.createFormattingModelForPsiFile(
    formattingContext.getContainingFile(),
    new MiniMessageBlock(
    formattingContext.getNode(),
    Wrap.createWrap(WrapType.NONE, false),
    Alignment.createAlignment(false, Alignment.Anchor.LEFT)
    ),
    formattingContext.getCodeStyleSettings()
    );
  }
}
