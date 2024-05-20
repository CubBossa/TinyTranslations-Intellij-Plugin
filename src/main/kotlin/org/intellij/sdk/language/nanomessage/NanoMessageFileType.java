package org.intellij.sdk.language.nanomessage;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import org.intellij.sdk.language.TinyTranslationsIcons;
import org.intellij.sdk.language.TinyTranslationsIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class NanoMessageFileType extends LanguageFileType {

  public static final NanoMessageFileType INSTANCE = new NanoMessageFileType();

  protected NanoMessageFileType() {
    super(NanoMessageLanguage.INSTANCE);
  }

  @Override
  public @NonNls
  @NotNull String getName() {
    return "NanoMessage";
  }

  @Override
  public @NlsContexts.Label
  @NotNull String getDescription() {
    return "Minecraft NanoMessage";
  }

  @Override
  public @NlsSafe
  @NotNull String getDefaultExtension() {
    return "nm";
  }

  @Override
  public Icon getIcon() {
    return TinyTranslationsIcons.Logo;
  }
}
