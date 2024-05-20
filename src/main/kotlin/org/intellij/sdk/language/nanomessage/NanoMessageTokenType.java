package org.intellij.sdk.language.nanomessage;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.xml.IXmlLeafElementType;
import org.intellij.sdk.language.minimessage.MiniMessageTokenType;

public interface NanoMessageTokenType extends MiniMessageTokenType {

  IElementType NM_PLACEHOLDER_START = new IXmlLeafElementType("NM_PLACEHOLDER_START");
  IElementType NM_PLACEHOLDER_END = new IXmlLeafElementType("NM_PLACEHOLDER_END");
  IElementType NM_CHOICE_MARKER = new IXmlLeafElementType("NM_CHOICE");
}
