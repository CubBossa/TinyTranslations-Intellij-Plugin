package org.intellij.sdk.language;

import com.github.weisj.jsvg.R;
import com.intellij.codeInspection.IntentionAndQuickFixAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import net.kyori.adventure.text.format.NamedTextColor;
import org.intellij.lang.annotations.RegExp;
import org.intellij.sdk.language.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TranslationsAnnotator implements Annotator, DumbAware {

	private static final String[] ACTIONS = {
			"change_page", "copy_to_clipboard", "open_file", "open_url", "run_command", "suggest_command"
	};

	private record Rule(String key, @RegExp String[] patterns) {

	}
	private static final Rule[] RULES = {
			new Rule("click", new String[]{ String.join("|", ACTIONS) }),
			new Rule("color", new String[]{ String.join("|", NamedTextColor.NAMES.keys()) + "|(#[0-9a-f]{6})"}),
			new Rule("repeat", new String[]{ "[0-9]+" }),
	};


	@Override
	public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
		if (psiElement instanceof TranslationsSelfClosingTag contentTag) {
			annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
					.range(contentTag)
					.textAttributes(TranslationsSyntaxHighlighter.TAG)
					.create();
		}
		else if (psiElement instanceof TranslationsOpenTag || psiElement instanceof TranslationsCloseTag) {
			annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
					.range(psiElement)
					.textAttributes(TranslationsSyntaxHighlighter.TAG)
					.create();
		}
		else if (psiElement instanceof TranslationsPlaceholder contentTag) {
			annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
					.range(contentTag)
					.textAttributes(TranslationsSyntaxHighlighter.PLACEHOLDER)
					.create();
		}
		else if (psiElement.getParent() instanceof TranslationsChoicePlaceholder) {
			if (!(psiElement instanceof TranslationsChoiceOption)) {
				annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
						.range(psiElement)
						.textAttributes(TranslationsSyntaxHighlighter.CHOICE)
						.create();
			} else {
				if (psiElement.getFirstChild() instanceof TranslationsTextElement) {
					annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
							.range(psiElement)
							.textAttributes(TranslationsSyntaxHighlighter.ATTRIBUTE)
							.create();
				}
			}
		}
		else if (psiElement instanceof TranslationsAttribute) {
			annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
					.range(psiElement)
					.textAttributes(TranslationsSyntaxHighlighter.ATTRIBUTE)
					.create();
		}
		if (psiElement instanceof TranslationsSelfClosingTag selfClosingTag) {
			annotationHolder.newAnnotation(HighlightSeverity.WARNING, "Prefer placeholder to self closing tags")
					.range(selfClosingTag)
					.withFix(new IntentionAndQuickFixAction() {
						@Override
						public @IntentionName @NotNull String getName() {
							return "Replace self-closing tag with placeholder tag";
						}

						@Override
						public @IntentionFamilyName @NotNull String getFamilyName() {
							return "Translations code style";
						}

						@Override
						public void applyFix(@NotNull Project project, PsiFile psiFile, @Nullable Editor editor) {
							String t = selfClosingTag.getText();
							t = t.substring(1, t.length() - 2);
							selfClosingTag.replace(TranslationsElementFactory.createPlaceholder(project, t));
						}
					})
					.create();
		}
		else if (psiElement instanceof TranslationsContentTag contentTag) {
			for (Rule rule : RULES) {
				if (!contentTag.getOpenTag().getKey().getText().equalsIgnoreCase(rule.key)) {
					continue;
				}
				List<TranslationsAttribute> attributes = contentTag.getOpenTag().getAttributes().getAttributeList();
				int max = Math.min(rule.patterns.length, attributes.size());
				for (int i = 0; i < max; i++) {
					String pattern = rule.patterns[i];
					TranslationsAttribute attribute = attributes.get(i);
					if (!attribute.getText().matches(pattern)) {
						annotationHolder.newAnnotation(HighlightSeverity.WARNING, "Attribute must follow pattern '" + pattern + "'.")
								.range(attribute)
								.create();
					}
				}
			}
		}
	}
}
