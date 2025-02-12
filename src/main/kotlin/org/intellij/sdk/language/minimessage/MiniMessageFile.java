package org.intellij.sdk.language.minimessage;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.intellij.sdk.language.nanomessage.NanoMessageLanguage;
import org.jetbrains.annotations.NotNull;

public class MiniMessageFile extends PsiFileBase {

	public MiniMessageFile(@NotNull FileViewProvider viewProvider) {
		super(viewProvider, MiniMessageLanguage.INSTANCE);
	}

	@Override
	public @NotNull FileType getFileType() {
		return MiniMessageFileType.INSTANCE;
	}
}
