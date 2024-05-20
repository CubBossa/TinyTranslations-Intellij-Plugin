package org.intellij.sdk.language.common.editor;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import de.cubbossa.tinytranslations.nanomessage.NanoMessage;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JTextPane;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class AdventureComponentPreviewComponent {

  public static final Key<Boolean> KEY_MM_PREVIEW_EDITOR = Key.create("KEY_MM_PREVIEW_EDITOR");
  private static final Key<CachedValue<Component>> KEY_MM_PREVIEW_CACHE = Key.create("KEY_MM_PREVIEW_CACHE");

  private final ComponentSerializer<Component, Component, String> deserializer;

  private final JTextPane pane;
  private final JTextPanelComponentRenderer paneRenderer;

  static boolean fontRead = false;

  public AdventureComponentPreviewComponent(ComponentSerializer<Component, Component, String> deserializer, PsiElement miniMessageElement) {
    this.deserializer = deserializer;

    if (!fontRead) {
      try {
        Font font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/MinecraftiaRegular.ttf"));
        font = font.deriveFont(Font.PLAIN, 24f);
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
      } catch (FontFormatException | IOException e) {
        throw new RuntimeException(e);
      }
      fontRead = true;
    }

    pane = new JTextPane();
    pane.setBackground(new Color(0x2B, 0x2B, 0x2B));
    paneRenderer = new JTextPanelComponentRenderer(pane);

    update(miniMessageElement);
  }

  public TagResolver[] getResolvers() {
    return new TagResolver[0];
  }

  public @NotNull JComponent getComponent() {
    return pane;
  }

  private void updateDom(Component component) {
    paneRenderer.render(component);
  }

  public void forceUpdate(PsiElement element) {
    KEY_MM_PREVIEW_CACHE.set(element, null);
    update(element);
  }

  public void update(PsiElement psiElement) {
    updateDom(generate(psiElement));
  }

  private Component generate(PsiElement element) {
    return CachedValuesManager.getCachedValue(element, KEY_MM_PREVIEW_CACHE, new CachedValueProvider<>() {
      @Override
      public @NotNull Result<Component> compute() {
        String text = element.getText();
        if (deserializer instanceof MiniMessage mm) {
          return Result.create(mm.deserialize(text, getResolvers()), element);
        }
        if (deserializer instanceof NanoMessage nm) {
          return Result.create(nm.deserialize(text, getResolvers()), element);
        }
        return Result.create(deserializer.deserialize(text), element);
      }
    });
  }
}
