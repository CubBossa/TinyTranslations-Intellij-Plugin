package org.intellij.sdk.language.minimessage.tag.impl;

import org.intellij.sdk.language.minimessage.tag.Argument;
import org.intellij.sdk.language.minimessage.tag.MiniMessageTag;

public class RainbowTag extends MiniMessageTag {

    public RainbowTag() {
        super("rainbow");

        argument(new Argument("[!]<phase>") {
            @Override
            public boolean check(String arg) {
                return arg.matches("!?[0-9]*");
            }
        }).optional();
    }
}
