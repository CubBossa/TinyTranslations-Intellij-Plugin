package org.intellij.sdk.language.nanomessage.psi;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.xml.XmlTag;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

public class NanoMessagePsiUtils {

  private static final Key<CachedValue<Boolean>> KEY_IS_PLACEHOLDER = new Key<>("NM_KEY_IS_PLACEHOLDER");
  private static final Key<CachedValue<Boolean>> KEY_IS_CHOICE = new Key<>("NM_KEY_IS_CHOICE");

  public static boolean isPlaceholder(XmlTag tag) {
    return CachedValuesManager.getCachedValue(tag, KEY_IS_PLACEHOLDER, () -> {
      return CachedValueProvider.Result.create(tag.getFirstChild().getText() == "{", tag);
    });
  }

  public static boolean isChoice(XmlTag tag) {
    return CachedValuesManager.getCachedValue(tag, KEY_IS_CHOICE, () -> {
      return CachedValueProvider.Result.create(Arrays.stream(tag.getAttributes()).anyMatch(xmlAttribute -> xmlAttribute.getFirstChild().getText() == "?"), tag);
    });
  }

  public static boolean beforeIs(@NotNull PsiElement token, IElementType... types) {
    for (IElementType type : types) {
      token = token.getPrevSibling();
      if (token == null) {
        return false;
      }
      if (!token.getNode().getElementType().equals(type)) {
        return false;
      }
    }
    return true;
  }

  public static boolean nextIs(@NotNull PsiElement token, IElementType... types) {
    for (IElementType type : types) {
      token = token.getNextSibling();
      if (token == null) {
        return false;
      }
      if (!token.getNode().getElementType().equals(type)) {
        return false;
      }
    }
    return true;
  }
}
