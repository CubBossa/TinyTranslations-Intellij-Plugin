package org.intellij.sdk.language.legacy.common.editor;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.XmlHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import org.intellij.sdk.language.legacy.common.psi.LegacyFormatter;
import org.jetbrains.annotations.NotNull;

public class LegacyAnnotator implements Annotator {

  public static final TextAttributesKey COLOR =
  createTextAttributesKey("LEGACY_FORMAT", XmlHighlighterColors.XML_TAG_NAME);

  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (element instanceof LegacyFormatter) {
      holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
      .range(element)
      .textAttributes(COLOR)
      .create();
    }
    if (element instanceof LegacyFormatter a) {
      if (element.getNextSibling() instanceof LegacyFormatter b) {
        if (a.isTypeEquals(b)) {
          holder.newAnnotation(HighlightSeverity.WARNING, "Redundant color sign " + a.getText())
          .range(a)
          .highlightType(ProblemHighlightType.WARNING)
          .tooltip("Redundant color sign, overwritten by " + b.getText())
          .create();
        }
      }
    }
  }
}
