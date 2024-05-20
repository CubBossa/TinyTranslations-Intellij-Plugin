package org.intellij.sdk.language.nanomessage;

import com.intellij.lang.Language;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import org.intellij.sdk.language.minimessage.MiniMessageLanguage;
import org.intellij.sdk.language.nanomessage.tag.*;

import java.util.List;

public class NanoMessageLanguage extends MiniMessageLanguage {

  public static final NanoMessageLanguage INSTANCE = new NanoMessageLanguage();

  protected NanoMessageLanguage(String name) {
    super(name);

    tags.addAll(List.of(
    new BrighterTag(), new DarkerTag(), new LegacyTag(), new MessageTag(), new NbtTag(), new PreTag(), new RepeatTag()
    ));
  }

  protected NanoMessageLanguage() {
    this("NanoMessage");
  }
}
