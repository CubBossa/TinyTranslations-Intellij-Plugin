package org.intellij.sdk.language.minimessage.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import org.intellij.sdk.language.minimessage.MiniMessageFile;
import org.intellij.sdk.language.minimessage.MiniMessageLanguage;

public class MiniMessagePsiFactory {

  public static MiniMessageFile createFile(Project project, String text) {
    String name = "dummy.mm";
    return (MiniMessageFile) PsiFileFactory.getInstance(project).createFileFromText(name, MiniMessageLanguage.INSTANCE, text);
  }

  public static XmlAttributeValue createAttributeValue(XmlTag tag, String value) {
    return createAttributeValue(tag, value, "'");
  }

  public static XmlAttributeValue createAttributeValue(XmlTag tag, String value, String quote) {
    MiniMessageFile file = createFile(tag.getProject(), "<dummy:" + quote + value + quote + "/>");
    XmlTag t = (XmlTag) file.getFirstChild();
    return t.getAttributes()[0].getValueElement();
  }

  public static void renameTag(XmlTag tag, String name) {
    PsiElement o = tag.getChildren()[1];
    PsiElement c = null;
    if (tag.getChildren().length >= 6) {
      c = tag.getChildren()[tag.getChildren().length - 2];
      if (!o.getText().equals(c.getText())) {
        c = null;
      }
    }
    o.replace(createTagName(tag.getProject(), name));
    if (c != null) {
      c.replace(createTagName(tag.getProject(), name));
    }
  }

  public static PsiElement createTagName(Project project, String name) {
    MiniMessageFile file = createFile(project, "<" + name + "/>");
    return file.getFirstChild().getChildren()[1];
  }
}
