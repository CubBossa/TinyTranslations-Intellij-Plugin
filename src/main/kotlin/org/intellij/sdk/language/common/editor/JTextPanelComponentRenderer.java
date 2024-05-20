package org.intellij.sdk.language.common.editor;

import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.intellij.sdk.language.Constants;

public class JTextPanelComponentRenderer {

  private JTextPane pane;
  private StyledDocument doc;

  public JTextPanelComponentRenderer(JTextPane pane) {
    this.pane = pane;
    this.doc = pane.getStyledDocument();
  }

  public void render(Component component) {
    this.doc = new DefaultStyledDocument();
    this.pane.setStyledDocument(doc);

    var style = this.doc.addStyle("root", null);
    StyleConstants.setFontFamily(style, "Minecraftia");
    StyleConstants.setFontSize(style, 14);

    renderInternal(component.compact(), style);
  }

  private void renderInternal(Component component, javax.swing.text.Style parent) {

    // apply component style
    if (!component.style().isEmpty()) {
      Style componentStyle = component.style();
      javax.swing.text.Style textStyle = doc.addStyle(null, parent);

      for (TextDecoration type : componentStyle.decorations().keySet()) {
        if (!componentStyle.decoration(TextDecoration.ITALIC).equals(TextDecoration.State.NOT_SET)) {
          boolean val = componentStyle.decoration(type).equals(TextDecoration.State.TRUE);
          switch (type) {
            case ITALIC -> StyleConstants.setItalic(textStyle, val);
            case BOLD -> StyleConstants.setBold(textStyle, val);
            case STRIKETHROUGH -> StyleConstants.setStrikeThrough(textStyle, val);
            case UNDERLINED -> StyleConstants.setUnderline(textStyle, val);
          }
        }
      }

      if (componentStyle.color() != null) {
        StyleConstants.setForeground(textStyle, new Color(componentStyle.color().value()));
      }
      parent = textStyle;
    }

    String text;
    if (component instanceof TextComponent tc) {
      text = tc.content();
    } else if (component instanceof TranslatableComponent tc) {
      String tr = Constants.TRANSLATION_KEY_VALUES.getProperty(tc.key());
      if (tr != null) {
        text = String.format(tr, tc.arguments().stream().map(TranslationArgument::value).toArray());
      } else {
        text = tc.key();
      }
    } else if (component instanceof KeybindComponent tc) {
      String tr = Constants.CONTROL_KEY_NAMES.getProperty(tc.keybind());
      text = tr == null ? "" : tr;
    } else {
      text = "";
    }
    try {
      doc.insertString(doc.getLength(), text, parent);
    } catch (BadLocationException e) {
      throw new RuntimeException(e);
    }

    // render children
    for (Component child : component.children()) {
      renderInternal(child, parent);
    }
  }
}
