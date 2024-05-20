package org.intellij.sdk.language.minimessage.highlight;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.XmlHighlighterColors;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTokenType;
import org.jetbrains.annotations.NotNull;

public class MiniMessageHighlighting implements Annotator, DumbAware {

  @Override
  public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
    if (psiElement instanceof XmlTag tag) {

      for (XmlAttribute attribute : tag.getAttributes()) {
        annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
        .range(attribute.getValueElement())
        .textAttributes(XmlHighlighterColors.XML_ATTRIBUTE_VALUE)
        .needsUpdateOnTyping()
        .create();
      }

      if (tag.getFirstChild().getText() != "<") {
        return;
      }

      int i = 0;
      for (PsiElement child : tag.getChildren()) {
        if (child.getNode().getElementType() == XmlTokenType.XML_END_TAG_START) {
          i++;
        }
        if (i % 2 == 0) {
          highlightTag(child, annotationHolder);
        }
        if (child.getNode().getElementType() == XmlTokenType.XML_TAG_END) {
          i++;
        }
      }
    }
  }

  private void highlightTag(PsiElement psiElement, AnnotationHolder annotationHolder) {
    annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
    .range(psiElement)
    .textAttributes(XmlHighlighterColors.XML_TAG)
    .needsUpdateOnTyping()
    .create();
  }
}
