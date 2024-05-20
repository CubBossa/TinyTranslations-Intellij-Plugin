package org.intellij.sdk.language.legacy.ampersand;

import org.intellij.sdk.language.legacy.common.LegacyLanguage;

public class AmpersandLanguage extends LegacyLanguage {

  public static final AmpersandLanguage INSTANCE = new AmpersandLanguage();

  protected AmpersandLanguage() {
    super("MinecraftAmpersandFormatting", "&", true);
  }
}
