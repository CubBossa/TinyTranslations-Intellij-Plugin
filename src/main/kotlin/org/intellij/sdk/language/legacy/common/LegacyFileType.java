package org.intellij.sdk.language.legacy.common;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import javax.swing.Icon;
import org.intellij.sdk.language.TinyTranslationsIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class LegacyFileType extends LanguageFileType {

  public static final LegacyFileType INSTANCE = new LegacyFileType();

  protected LegacyFileType() {
    super(LegacyLanguage.INSTANCE);
  }

  @Override
  public @NonNls @NotNull String getName() {
    return "MinecraftLegacyFormatting";
  }

  @Override
  public @NlsContexts.Label @NotNull String getDescription() {
    return "Minecraft legacy formatting";
  }

  @Override
  public @NlsSafe @NotNull String getDefaultExtension() {
    return "leg";
  }

  @Override
  public Icon getIcon() {
    return TinyTranslationsIcons.Logo;
  }
}

