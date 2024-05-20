package org.intellij.sdk.language.minimessage.editor;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class MiniMessageRemoveUnexpectedArgumentsFix extends BaseIntentionAction {

  private XmlTag tag;
  private int firstInvalid;

  public MiniMessageRemoveUnexpectedArgumentsFix(XmlTag tag, int firstInvalid) {
    this.tag = tag;
    this.firstInvalid = firstInvalid;
  }

  @Override
  public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile psiFile) {
    return true;
  }

  @Override
  public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {
    // 0 = <, 1 = tagname
    tag.deleteChildRange(tag.getAttributes()[firstInvalid], tag.getAttributes()[tag.getAttributes().length - 1]);
  }

  @Override
  public @NotNull @IntentionName String getText() {
    return "Remove unexpected arguments " + Arrays.stream(Arrays.copyOfRange(tag.getAttributes(), firstInvalid, tag.getAttributes().length))
    .map(XmlAttribute::getValueElement).filter(Objects::nonNull)
    .map(PsiElement::getText)
    .collect(Collectors.joining(":"));
  }

  @Override
  public @NotNull @IntentionFamilyName String getFamilyName() {
    return "Remove property";
  }
}
