package org.intellij.sdk.language.nanomessage.highlight;

import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.sdk.language.nanomessage.highlight.NanoMessageSyntaxHighlighter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NanoMessageSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
	@Override
	public @NotNull SyntaxHighlighter getSyntaxHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile) {
		return new NanoMessageSyntaxHighlighter();
	}
}
