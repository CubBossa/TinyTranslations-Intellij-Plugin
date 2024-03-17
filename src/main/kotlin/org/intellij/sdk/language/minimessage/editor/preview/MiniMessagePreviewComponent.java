package org.intellij.sdk.language.minimessage.editor.preview;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefClient;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.intellij.lang.annotations.Language;
import org.intellij.sdk.language.TinyTranslationsDisposable;
import org.intellij.sdk.language.util.HtmlComponentSerializer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.html.HTML;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class MiniMessagePreviewComponent extends JBCefBrowser {

    private static final JBCefClient ourCefClient = JBCefApp.getInstance().createClient();

    static {
        Disposer.register(TinyTranslationsDisposable.getInstance(), ourCefClient);
    }

    private static final @Language("HTML") String HTML_FRAME = """
            <head>
                <meta charset="UTF-8"/>
                <link rel="stylesheet" media="screen" href="https://fontlibrary.org//face/minecraftia" type="text/css"/>
                <style>
                        #chat {
                            color: white;
                            background-color: rgba(0, 0, 0, 0.4);
                            text-rendering: optimizeLegibility;
                            text-shadow: 3px 3px hsl(0, 0%, 16%);
                            font-family: "MinecraftiaRegular", monospace;
                            font-weight: normal;
                            font-style: normal;
                            padding: 10px;
                        }
                </style>
            </head>
            <body>
            <div id='chat'>
            </div>
            </body>
            """;

    public static final Key<Boolean> KEY_MM_PREVIEW_EDITOR = Key.create("KEY_MM_PREVIEW_EDITOR");
    private static final Key<CachedValue<String>> KEY_MM_PREVIEW_CACHE = Key.create("KEY_MM_PREVIEW_CACHE");

    private static final MiniMessage miniMessage = MiniMessage.builder()
            .strict(false)
            .build();
    private static final HtmlComponentSerializer htmlSerializer = new HtmlComponentSerializer();

    private String previousRenderClosure = "";

    public MiniMessagePreviewComponent(PsiElement miniMessageElement) {
        super(JBCefBrowser.createBuilder().setOffScreenRendering(true).setClient(ourCefClient));

        loadHTML(HTML_FRAME);
        update(miniMessageElement);
    }

    public TagResolver[] getResolvers() {
        return new TagResolver[0];
    }

    private void updateDom(String renderClosure) {
        previousRenderClosure = renderClosure;

        String escapedRenderClosure = renderClosure.replace("\"", "\\\"");
        String code = "document.getElementById('chat').innerHTML = \"" + escapedRenderClosure + "\";";
        myCefBrowser.executeJavaScript(code, myCefBrowser.getURL(), 0);
    }

    public void update(PsiElement psiElement) {
        updateDom(generateHtml(psiElement));
    }

    private String generateHtml(PsiElement element) {
        return CachedValuesManager.getCachedValue(element, KEY_MM_PREVIEW_CACHE, new CachedValueProvider<>() {
            @Override
            public @NotNull Result<String> compute() {
                String text = element.getText();
                Component c = miniMessage.deserialize(text, getResolvers());
                String html = htmlSerializer.serialize(c);
                return Result.create(html, element);
            }
        });
    }
}
