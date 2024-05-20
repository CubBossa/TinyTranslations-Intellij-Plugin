package org.intellij.sdk.language.minimessage.editor;

import com.intellij.openapi.editor.ElementColorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTokenType;
import java.awt.Color;
import org.intellij.sdk.language.Constants;
import org.intellij.sdk.language.minimessage.psi.MiniMessagePsiFactory;
import org.intellij.sdk.language.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MiniMessageElementColorProvider implements ElementColorProvider, DumbAware {

  private static final Key<CachedValue<Color>> KEY_TAG_COLOR = new Key<>("MM_KEY_TAG_COLOR");

  @Override
  public @Nullable Color getColorFrom(@NotNull PsiElement psiElement) {
    return CachedValuesManager.getCachedValue(psiElement, KEY_TAG_COLOR, () -> {
      return CachedValueProvider.Result.create(getColorUncached(psiElement), psiElement);
    });
  }

  private Color getColorUncached(PsiElement psiElement) {
    // the name is the color information
    if (psiElement.getParent() instanceof XmlTag tag && tag.getChildren().length > 1 && tag.getChildren()[1].equals(psiElement)) {
      return getColorFrom(tag.getName());
    }
    if (!(psiElement.getNode().getElementType() == XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN)) {
      return null;
    }
    if (!(psiElement.getParent() instanceof XmlAttributeValue value)) {
      return null;
    }
    if (!(value.getParent() instanceof XmlAttribute attr)) {
      return null;
    }
    if (attr.getParent() == null) {
      return null;
    }
    return getColorFrom(value.getText());
  }

  private Color getColorFrom(String string) {
    string = StringUtil.unquote(string);
    Constants.NamedColor color = Constants.COLORS.get(string.toLowerCase());
    if (color != null) {
      return new Color(color.value());
    }
    if (string.matches("#[0-9a-f]{6}")) {
      return new Color(Integer.parseInt(string.substring(1), 16));
    }
    return null;
  }

  @Override
  public void setColorTo(@NotNull PsiElement psiElement, @NotNull Color color) {
    if (psiElement.getParent() instanceof XmlTag tag && tag.getChildren()[1].equals(psiElement)) {
      tag.setName(toString(color));
      return;
    }
    if (!(psiElement.getNode().getElementType() == XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN)) {
      return;
    }
    if (!(psiElement.getParent() instanceof XmlAttributeValue value)) {
      return;
    }
    if (!(value.getParent() instanceof XmlAttribute attr)) {
      return;
    }
    value.deleteChildRange(value.getFirstChild(), value.getLastChild());
    XmlAttributeValue newValue = MiniMessagePsiFactory.createAttributeValue(attr.getParent(), toString(color));
    value.addRange(newValue.getFirstChild(), newValue.getLastChild());
  }

  private String toString(Color color) {
    return "#" + Integer.toHexString(color.getRGB() & 0xffffff);
  }
}
