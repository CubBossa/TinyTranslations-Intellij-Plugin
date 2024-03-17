package org.intellij.sdk.language.util;

import net.kyori.adventure.text.*;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.intellij.sdk.language.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class HtmlComponentSerializer implements ComponentSerializer<Component, Component, String> {

    @Override
    public @NotNull Component deserialize(@NotNull String input) {
        throw new IllegalStateException("Don't use to deserialize");
    }

    @Override
    public @NotNull String serialize(@NotNull Component c) {

        StringBuilder sb = new StringBuilder("<span");

        if (!c.style().isEmpty()) {
            sb.append(" style=\"");
            Style s = c.style();

            if (s.color() != null) {
                sb.append("color: ").append(s.color().asHexString()).append("; ");
            }
            s.decorations().forEach((textDecoration, state) -> {
                if (state == TextDecoration.State.NOT_SET) {
                    return;
                }
                boolean set = state == TextDecoration.State.TRUE;
                List<String> decoration = new ArrayList<>(2);
                String key = switch (textDecoration) {
                    case OBFUSCATED -> "background-color: " + (set ? "11000000" : "00000000");
                    case BOLD -> "font-weight: " + (set ? "bold" : "normal");
                    case STRIKETHROUGH -> { decoration.add("line-through"); yield ""; }
                    case UNDERLINED -> { decoration.add("underline"); yield ""; }
                    case ITALIC -> "font-style: " + (set ? "italic" : "normal");
                };
                if (!decoration.isEmpty()) {
                    sb.append(key).append(" text-decoration: ").append(String.join(" ", decoration));
                }
            });
            sb.append("\"");
        }
        sb.append(">");
        if (c instanceof TextComponent tc) {
            sb.append(tc.content().replace("<", "&#60;").replace("\n", "<br>"));
        } else if (c instanceof TranslatableComponent tc) {
            String tr = Constants.TRANSLATION_KEY_VALUES.getProperty(tc.key());
            if (tr == null) {
                return "";
            }
            sb.append(String.format(tr, tc.arguments().stream().map(TranslationArgument::value).toArray()));
        } else if (c instanceof KeybindComponent tc) {
            String tr = Constants.CONTROL_KEY_NAMES.getProperty(tc.keybind());
            return tr == null ? "" : tr;
        }
        for (Component child : c.children()) {
            sb.append(serialize(child));
        }
        sb.append("</span>");
        return sb.toString();
    }
}
