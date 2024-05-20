package org.intellij.sdk.language.legacy.common;

import com.intellij.lang.Language;

public class LegacyLanguage extends Language {

  public static final LegacyLanguage INSTANCE = new LegacyLanguage();

  private final String alternateColorCode;
  private final boolean hashHexFormat;

  protected LegacyLanguage() {
    this("MinecraftLegacyFormatting", "\u00A7", false);
  }

  protected LegacyLanguage(String name, String alternateColorCode, boolean hashHexFormat) {
    super(name);
    this.alternateColorCode = alternateColorCode;
    this.hashHexFormat = hashHexFormat;
  }

  public boolean isHashHexFormat() {
    return hashHexFormat;
  }

  public String getAlternateColorCode() {
    return alternateColorCode;
  }
}
