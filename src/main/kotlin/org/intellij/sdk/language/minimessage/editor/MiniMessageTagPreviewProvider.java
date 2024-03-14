package org.intellij.sdk.language.minimessage.editor;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTokenType;
import de.cubbossa.tinytranslations.MessageStyle;
import de.cubbossa.tinytranslations.MessageTranslator;
import de.cubbossa.tinytranslations.TinyTranslations;
import de.cubbossa.tinytranslations.storage.StyleStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.intellij.sdk.language.Constants;
import org.intellij.sdk.language.nanomessage.NanoMessageLanguage;
import org.intellij.sdk.language.util.HtmlComponentSerializer;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiniMessageTagPreviewProvider extends AbstractDocumentationProvider {

    private final MessageTranslator t;

    public MiniMessageTagPreviewProvider() {
        t = TinyTranslations.application("IntellijPlugin");
        t.setStyleStorage(new StyleStorage() {
            @Override
            public void writeStyles(Map<String, MessageStyle> map) {
            }

            @Override
            public Map<String, MessageStyle> loadStyles() {
                Map<String, MessageStyle> r = new HashMap<>();
                Constants.GLOBAL_STYLES.forEach((key, value) -> r.put(
                        key.toString(),
                        MessageStyle.messageStyle(key.toString(), value.toString())
                ));
                return r;
            }
        });
        t.loadStyles();
    }

    @Override
    public @Nullable PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement, int targetOffset) {
        IElementType tt = contextElement.getNode().getElementType();
        if (tt == XmlTokenType.XML_NAME && contextElement.getParent() instanceof XmlTag tag) {
            return tag;
        }
        return super.getCustomDocumentationElement(editor, file, contextElement, targetOffset);
    }

    @Override
    public @Nullable List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        return null;
    }

    @Override
    public @Nullable @Nls String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        return generateDoc(element, originalElement);
    }

    @Override
    public @Nullable @Nls String generateHoverDoc(@NotNull PsiElement element, @Nullable PsiElement originalElement) {
        return generateDoc(element, originalElement);
    }

    private static final HtmlComponentSerializer S = new HtmlComponentSerializer();

    @Override
    public @Nullable @Nls String generateDoc(@NotNull PsiElement element, @Nullable PsiElement originalElement) {
        if (element instanceof XmlTag) {
            Component c = element.getContainingFile().getLanguage() instanceof NanoMessageLanguage
                    ? t.translate(element.getText())
                    : MiniMessage.miniMessage().deserialize(element.getText());
            return "<head><link rel=\"stylesheet\" media=\"screen\" href=\"https://fontlibrary.org//face/minecraftia\" type=\"text/css\"/></head>" +
                    "<body><hr><span style=\"font-family: \"MinecraftiaRegular\", monospace;\">" + S.serialize(c) + "</span><hr></body>";
        }
        return null;
    }
}
