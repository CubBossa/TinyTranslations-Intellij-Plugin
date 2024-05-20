package org.intellij.sdk.language.legacy.common;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import java.awt.Color;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.intellij.sdk.language.Constants;
import org.intellij.sdk.language.legacy.common.psi.LegacyFormatter;

public class LegacyElementFactory {

  public static PsiFile createFile(Project project, LegacyLanguage language, String text) {
    String name = "dummy." + language.getAssociatedFileType().getDefaultExtension();
    return PsiFileFactory.getInstance(project).createFileFromText(name, language, text);
  }

  public static LegacyFormatter createHexColorFormatter(Project project, LegacyLanguage language, Color color) {
    String hex = Integer.toHexString(color.getRGB() & 0xffffff);
    String text = language.isHashHexFormat()
    ? language.getAlternateColorCode() + "#" + hex
    : language.getAlternateColorCode() + "x" + Arrays.stream(hex.split("(?!^)"))
    .map(c -> language.getAlternateColorCode() + c)
    .collect(Collectors.joining(""));
    final PsiFile file = createFile(project, language, text);
    return (LegacyFormatter) file.getFirstChild();
  }

  public static LegacyFormatter createColorFormatter(Project project, LegacyLanguage language, Color color) {
    var c = Constants.COLORS.values().stream()
    .filter(namedColor -> namedColor.value() == (color.getRGB() & 0xffffff))
    .findAny();
    if (c.isPresent()) {
      return createColorFormatter(project, language, c.get());
    }
    return createHexColorFormatter(project, language, color);
  }

  public static LegacyFormatter createColorFormatter(Project project, LegacyLanguage language, Constants.NamedColor textColor) {
    final PsiFile file = createFile(project, language, language.getAlternateColorCode() + textColor.symbol());
    return (LegacyFormatter) file.getFirstChild();
  }

}
