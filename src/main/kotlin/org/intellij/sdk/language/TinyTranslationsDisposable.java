package org.intellij.sdk.language;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

@Service({Service.Level.APP, Service.Level.PROJECT})
public final class TinyTranslationsDisposable implements Disposable {

    public static @NotNull Disposable getInstance() {
        return ApplicationManager.getApplication().getService(TinyTranslationsDisposable.class);
    }

    public static @NotNull Disposable getInstance(Project project) {
        return project.getService(TinyTranslationsDisposable.class);
    }


    @Override
    public void dispose() {
    }
}
