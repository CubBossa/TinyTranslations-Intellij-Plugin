package org.intellij.sdk.language.legacy.common.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import java.awt.Color;
import org.intellij.lang.annotations.RegExp;
import org.intellij.sdk.language.Constants;
import org.intellij.sdk.language.legacy.common.LegacyElementFactory;
import org.intellij.sdk.language.legacy.common.LegacyLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FormatterStub extends ASTWrapperPsiElement implements LegacyFormatting {

  private LegacyLanguage language = null;

  public FormatterStub(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public boolean isTypeEquals(LegacyFormatting other) {
    return isColor() && other.isColor() || isColor() && other.isReset()
    || isDeco() && other.isDeco() || isDeco() && other.isReset()
    || isReset() && other.isReset();
  }

  public boolean isColor() {
    return formatterMatches("[0-9a-f]") || isHexColor();
  }

  @Override
  public boolean isHexColor() {
    return (language().isHashHexFormat() && formatterMatches("#[0-9a-f]{6}"))
    || (!language().isHashHexFormat() && formatterMatches("x(p[0-9a-f]){6}".replace("p", language().getAlternateColorCode())));
  }

  public @Nullable Color getColor() {
    if (!isColor()) {
      return null;
    }
    if (!isHexColor()) {
      String symbol = formatterContent();
      Constants.NamedColor c = Constants.COLORS.values().stream()
      .filter(namedColor -> namedColor.symbol().equals(symbol))
      .findFirst().orElse(null);
      if (c == null) {
        throw new IllegalStateException("Missing color in constants");
      }
      return new Color(c.value());
    }
    String hex;
    if (language().isHashHexFormat()) {
      hex = formatterContent().substring(1);
    } else {
      hex = formatterContent().replace(language().getAlternateColorCode(), "");
      hex = hex.substring(1);
    }
    return new Color(Integer.parseInt(hex, 16));
  }

  @Override
  public void setColor(Color color) {
    LegacyFormatter stub = LegacyElementFactory.createColorFormatter(getNode().getPsi().getProject(), language(), color);
    deleteChildRange(getFirstChild().getNextSibling(), getLastChild());
    addRange(stub.getFirstChild().getNextSibling(), stub.getLastChild());
  }

  public boolean isDeco() {
    return formatterMatches("[klmno]");
  }

  @Override
  public @Nullable TextDecoration getDeco() {
    return null;
  }

  @Override
  public void setDeco(TextDecoration deco) {

  }

  public boolean isReset() {
    return formatterMatches("r");
  }

  @Override
  public void setReset() {

  }

  private String formatterContent() {
    if (children().length < 2) {
      return "";
    }
    return getText().substring(language().getAlternateColorCode().length());
  }

  private boolean formatterMatches(@RegExp String regex) {
    var s = formatterContent();
    return formatterContent().matches(regex);
  }

  private ASTNode[] children() {
    return getNode().getChildren(TokenSet.ANY);
  }

  private LegacyLanguage language() {
    if (language != null) {
      return language;
    }
    if (getNode().getPsi().getContainingFile().getLanguage() instanceof LegacyLanguage leg) {
      language = leg;
    } else {
      throw new IllegalStateException("LegacyFormatting can only be used with LegacyLanguage");
    }
    return language;
  }
}
