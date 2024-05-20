package org.intellij.sdk.language.tinytranslations;

import com.intellij.lang.injection.general.Injection;
import com.intellij.lang.injection.general.LanguageInjectionContributor;
import com.intellij.lang.injection.general.SimpleInjection;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import org.intellij.sdk.language.minimessage.MiniMessageLanguage;
import org.intellij.sdk.language.nanomessage.NanoMessageLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLElementType;

import static org.jetbrains.yaml.YAMLElementTypes.*;

public class YmlLanguageInjector implements LanguageInjectionContributor, DumbAware {

  public YmlLanguageInjector() {
  }

  private static final YAMLElementType[] INJECT_TYPES = {
  SCALAR_PLAIN_VALUE, SCALAR_TEXT_VALUE, SCALAR_QUOTED_STRING, SCALAR_LIST_VALUE
  };

  @Override
  public @Nullable Injection getInjection(@NotNull PsiElement psiElement) {
    String path = psiElement.getContainingFile().getOriginalFile().getVirtualFile().getPath();
    if (!path.contains("/lang/")) {
      return null;
    }
    for (YAMLElementType injectType : INJECT_TYPES) {
      if (psiElement.getNode().getElementType().equals(injectType)) {
        return new SimpleInjection(MiniMessageLanguage.INSTANCE, "", "", null);
      }
    }
    return null;
  }
}
