package org.intellij.sdk.language.nanomessage.psi;

import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import java.util.Arrays;
import org.jetbrains.annotations.Nullable;

public class ChoiceWrapper {

  private final XmlTag delegate;
  private final int start;
  private final XmlAttribute[] options;

  public ChoiceWrapper(XmlTag choice) {
    this.delegate = choice;
    int i = 0;
    while (true) {
      if (i >= choice.getAttributes().length) {
        throw new IllegalArgumentException("Provided choice does not contain choice symbol '?'.");
      }
      if (choice.getAttributes()[i].getFirstChild().getText().equals("?")) {
        break;
      }
      i++;
    }
    start = i;
    options = Arrays.copyOfRange(choice.getAttributes(), start, choice.getAttributes().length);
  }

  public boolean hasOptions() {
    return delegate.getAttributes().length > start;
  }

  public @Nullable XmlAttribute getOption(int index) {
    return delegate.getAttributes()[start + index];
  }

  public XmlAttribute[] getOptions() {
    return options;
  }
}
