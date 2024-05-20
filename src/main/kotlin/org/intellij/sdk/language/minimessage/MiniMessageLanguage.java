package org.intellij.sdk.language.minimessage;

import com.intellij.lang.Language;
import com.intellij.lang.xml.XMLLanguage;
import java.util.ArrayList;
import java.util.List;
import org.intellij.sdk.language.minimessage.tag.MiniMessageTag;
import org.intellij.sdk.language.minimessage.tag.impl.ClickTag;
import org.intellij.sdk.language.minimessage.tag.impl.ColorTag;
import org.intellij.sdk.language.minimessage.tag.impl.DecorationTag;
import org.intellij.sdk.language.minimessage.tag.impl.FontTag;
import org.intellij.sdk.language.minimessage.tag.impl.GradientTag;
import org.intellij.sdk.language.minimessage.tag.impl.HoverTag;
import org.intellij.sdk.language.minimessage.tag.impl.KeybindTag;
import org.intellij.sdk.language.minimessage.tag.impl.NegatedDecorationTag;
import org.intellij.sdk.language.minimessage.tag.impl.NewlineTag;
import org.intellij.sdk.language.minimessage.tag.impl.RainbowTag;
import org.intellij.sdk.language.minimessage.tag.impl.ResetTag;
import org.intellij.sdk.language.minimessage.tag.impl.ScoreTag;
import org.intellij.sdk.language.minimessage.tag.impl.SelectorTag;
import org.intellij.sdk.language.minimessage.tag.impl.TransitionTag;
import org.intellij.sdk.language.minimessage.tag.impl.TranslatableTag;

public class MiniMessageLanguage extends Language {

  public static final MiniMessageLanguage INSTANCE = new MiniMessageLanguage();
  protected final List<MiniMessageTag> tags;

  private MiniMessageLanguage(Language parent, String name) {
    super(parent, name);

    tags = new ArrayList<>();
    tags.addAll(List.of(
    new ClickTag(), new ColorTag(), new FontTag(), new HoverTag(), new GradientTag(), new KeybindTag(),
    new NewlineTag(), new RainbowTag(), new ResetTag(), new ScoreTag(), new SelectorTag(), new TranslatableTag(),
    new DecorationTag(), new NegatedDecorationTag(), new TransitionTag()
    ));
  }

  protected MiniMessageLanguage(String name) {
    this(MiniMessageLanguage.INSTANCE, name);
  }

  private MiniMessageLanguage() {
    this(XMLLanguage.INSTANCE, "MiniMessage");
  }

  public List<MiniMessageTag> getTags() {
    return new ArrayList<>(tags);
  }
}
