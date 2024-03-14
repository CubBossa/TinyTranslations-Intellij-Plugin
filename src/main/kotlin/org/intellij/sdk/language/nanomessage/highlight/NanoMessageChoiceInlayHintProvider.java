package org.intellij.sdk.language.nanomessage.highlight;

import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.codeInsight.hints.InlayParameterHintsProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import org.intellij.sdk.language.nanomessage.psi.ChoiceWrapper;
import org.intellij.sdk.language.nanomessage.psi.NanoMessagePsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class NanoMessageChoiceInlayHintProvider implements InlayParameterHintsProvider {

	@Override
	public @NotNull List<InlayInfo> getParameterHints(@NotNull PsiElement element) {
		if (!(element instanceof XmlTag choiceTag)) {
			return Collections.emptyList();
		}
		if (!NanoMessagePsiUtils.isChoice(choiceTag) || choiceTag.getAttributes().length == 0) {
			return Collections.emptyList();
		}
		ChoiceWrapper choice = new ChoiceWrapper(choiceTag);
		if (choice.getOptions().length == 1) {
			if (choice.getOption(0).getValue().isEmpty()) {
				return Collections.emptyList();
			}
			return List.of(new InlayInfo("1|true", choice.getOption(0).getValueElement().getTextOffset()));
		}
		if (choice.getOptions().length == 2) {
			List<InlayInfo> infos = new ArrayList<>();
			boolean aEmpty = choice.getOption(0).getValue().isEmpty();
			if (!aEmpty) {
				infos.add(new InlayInfo("1|true", choice.getOption(0).getValueElement().getTextOffset() - 1));
			}
			boolean bEmpty = choice.getOption(1).getValue().isEmpty();
			if (!bEmpty) {
				infos.add(new InlayInfo("0|false|many", choice.getOption(1).getValueElement().getTextOffset() - 1));
			}
			return infos;
		}
		List<InlayInfo> infos = new ArrayList<>();
		int i = 0;
		for (XmlAttribute option : choice.getOptions()) {
			if (option.getValue().isEmpty()) {
			} else if (i == 0) {
				infos.add(new InlayInfo("0|false", option.getValueElement().getTextOffset() - 1));
			} else if (i == 1) {
				infos.add(new InlayInfo("1|true", option.getValueElement().getTextOffset() - 1));
			} else if (i == choice.getOptions().length - 1) {
				infos.add(new InlayInfo("many", option.getValueElement().getTextOffset() - 1));
			} else {
				infos.add(new InlayInfo("" + i, option.getValueElement().getTextOffset() - 1));
			}
			i++;
		}
		return infos;
	}

	@Override
	public @NotNull Set<String> getDefaultBlackList() {
		return Collections.emptySet();
	}
}
