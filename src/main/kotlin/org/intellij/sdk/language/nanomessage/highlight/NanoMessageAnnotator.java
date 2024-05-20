package org.intellij.sdk.language.nanomessage.highlight;

import com.intellij.codeInspection.IntentionAndQuickFixAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lang.properties.IProperty;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import org.intellij.sdk.language.Constants;
import org.intellij.sdk.language.nanomessage.psi.LocalStylesFile;
import org.intellij.sdk.language.nanomessage.psi.NanoMessagePsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NanoMessageAnnotator implements Annotator, DumbAware {

  @Override
  public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
    highlightPlaceholders(psiElement, annotationHolder);
    checkForReplacementStyles(psiElement, annotationHolder);
  }

  private void checkForReplacementStyles(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
    if (psiElement instanceof XmlTag tag && !tag.getContainingFile().getName().equals("styles.properties")) {
      PropertiesFile file = LocalStylesFile.getFile(tag.getProject());
      if (!file.findPropertiesByKey(tag.getName()).isEmpty()) {
        return;
      }
      String key = file.getProperties()
      .stream().filter(p -> p.getValue().matches("<" + tag.getName() + ">\\{slot}(</" + tag.getName() + ">)?"))
      .findFirst()
      .map(IProperty::getKey)
      .orElse(null);
      if (key == null) {
        if (Constants.GLOBAL_STYLES.containsKey(tag.getName())) {
          return;
        }
        key = Constants.GLOBAL_STYLES.entrySet().stream()
        .filter(e -> e.getValue().toString().matches(" *<" + tag.getName() + ">(\\{slot}(</" + tag.getName() + ">)?)?"))
        .findFirst()
        .map(e -> e.getKey().toString())
        .orElse(null);
        if (key == null) {
          return;
        }
      }
      String fkey = key;
      annotationHolder.newAnnotation(HighlightSeverity.WARNING, "Style of same value available: " + key)
      .withFix(new IntentionAndQuickFixAction() {
        @Override
        public @IntentionName @NotNull String getName() {
          return "Change <" + tag.getName() + "> to <" + fkey + ">";
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
          return "Change <" + tag.getName() + "> to <" + fkey + ">";
        }

        @Override
        public void applyFix(@NotNull Project project, PsiFile psiFile, @Nullable Editor editor) {
          tag.setName(fkey);
        }
      })
      .needsUpdateOnTyping()
      .create();
    }
  }

  private void highlightPlaceholders(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
    if (psiElement instanceof XmlTag tag) {

      if (!NanoMessagePsiUtils.isPlaceholder(tag)) {
        return;
      }
      boolean isChoice = NanoMessagePsiUtils.isChoice(tag);

      for (PsiElement child : tag.getChildren()) {
        if (child instanceof XmlAttribute) {
          highlightPlaceholder(child.getFirstChild(), annotationHolder, isChoice);
          continue;
        }
        highlightPlaceholder(child, annotationHolder, isChoice);
      }
    }
  }

  private void highlightPlaceholder(PsiElement psiElement, AnnotationHolder annotationHolder, boolean choice) {
    annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
    .range(psiElement)
    .textAttributes(choice ? NanoMessageSyntaxHighlighter.CHOICE : NanoMessageSyntaxHighlighter.PLACEHOLDER)
    .needsUpdateOnTyping()
    .create();
  }
}
