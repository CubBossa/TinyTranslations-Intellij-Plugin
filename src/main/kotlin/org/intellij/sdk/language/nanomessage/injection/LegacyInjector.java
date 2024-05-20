package org.intellij.sdk.language.nanomessage.injection;

import com.intellij.json.JsonLanguage;
import com.intellij.lang.Language;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.impl.source.xml.XmlTextImpl;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import org.intellij.sdk.language.legacy.ampersand.AmpersandLanguage;
import org.intellij.sdk.language.legacy.common.LegacyLanguage;
import org.intellij.sdk.language.nanomessage.NanoMessageFileType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LegacyInjector implements MultiHostInjector {

  @Override
  public void getLanguagesToInject(@NotNull MultiHostRegistrar multiHostRegistrar, @NotNull PsiElement context) {
    if (!context.getContainingFile().getFileType().equals(NanoMessageFileType.INSTANCE)) {
      return;
    }
    if (context instanceof XmlTag tag) {
      if (tag.getValue().getChildren().length >= 1 && tag.getValue().getChildren()[0] instanceof PsiLanguageInjectionHost txt) {
        Language language = switch (tag.getName()) {
          case "legacy" -> {
            boolean amp = tag.getAttributes().length > 0 && Objects.equals(tag.getAttributes()[0].getValue(), "&");
            yield amp ? AmpersandLanguage.INSTANCE : LegacyLanguage.INSTANCE;
          }
          case "json", "nbt", "gson" -> JsonLanguage.INSTANCE;
          default -> null;
        };
        if (language != null) {
          multiHostRegistrar
          .startInjecting(language)
          .addPlace(null, null, txt, TextRange.create(0, txt.getTextLength()))
          .doneInjecting();
        }
      }
    }
  }

  @Override
  public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
    return List.of(XmlTag.class);
  }
}
