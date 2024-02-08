package org.intellij.sdk.language.minimessage.editor;

import com.intellij.formatting.*;
import org.jetbrains.annotations.NotNull;

public class MiniMessageFormattingModelBuilder implements FormattingModelBuilder {

    @Override
    public @NotNull FormattingModel createModel(@NotNull FormattingContext formattingContext) {
        return FormattingModelProvider.createFormattingModelForPsiFile(
                formattingContext.getContainingFile(),
                new MiniMessageBlock(
                        formattingContext.getNode(),
                        Wrap.createWrap(WrapType.NONE, false),
                        Alignment.createAlignment()
                ),
                formattingContext.getCodeStyleSettings()
        );
    }
}
