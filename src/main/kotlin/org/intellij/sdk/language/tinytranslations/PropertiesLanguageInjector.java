package org.intellij.sdk.language.tinytranslations;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.lang.properties.psi.Property;
import com.intellij.lang.properties.psi.impl.PropertyImpl;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.intellij.sdk.language.nanomessage.NanoMessageLanguage;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PropertiesLanguageInjector implements MultiHostInjector, DumbAware {

//	@Override
//	public @Nullable Injection getInjection(@NotNull PsiElement psiElement) {
//		String path = psiElement.getContainingFile().getOriginalFile().getVirtualFile().getPath();
//		if (!path.contains("/lang/")) {
//			return null;
//		}
//		if (psiElement instanceof Property p) {
//			PropertyValueImpl
//			return new SimpleInjection(NanoMessageLanguage.INSTANCE, "", "", null);
//		}
//		return null;
//	}

	@Override
	public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
		String path = context.getContainingFile().getOriginalFile().getVirtualFile().getPath();
		if (!path.contains("/lang/")) {
			return;
		}
		if (!(context instanceof PropertyImpl property)) {
			return;
		}
		if (property.getValue() == null || property.getValue().isEmpty()) {
			return;
		}
		int len = property.getTextLength();
		registrar
				.startInjecting(NanoMessageLanguage.INSTANCE)
				.addPlace(null, null, property, TextRange.create(len - property.getValue().length(), len))
				.doneInjecting();
	}

	@Override
	public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
		return List.of(Property.class);
	}
}
