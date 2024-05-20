package org.intellij.sdk.language;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.LayeredIcon;
import javax.swing.Icon;

public interface TinyTranslationsIcons {

  Icon Logo = IconLoader.getIcon("/icons/logo.svg", TinyTranslationsIcons.class);
  Icon LogoOverlay = IconLoader.getIcon("/icons/logo_overlay.svg", TinyTranslationsIcons.class);
  Icon Tag = IconLoader.getIcon("/icons/tag.svg", TinyTranslationsIcons.class);
  Icon Choice = IconLoader.getIcon("/icons/choice.svg", TinyTranslationsIcons.class);
  Icon Placeholder = IconLoader.getIcon("/icons/placeholder.svg", TinyTranslationsIcons.class);
  Icon Directory = createDirectoryIcon();

  static Icon createDirectoryIcon() {
    LayeredIcon icon = new LayeredIcon(2);
    icon.setIcon(AllIcons.Nodes.Folder, 0);
    icon.setIcon(LogoOverlay, 1);
    return icon;
  }
}
