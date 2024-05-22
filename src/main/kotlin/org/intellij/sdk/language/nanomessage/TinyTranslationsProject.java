package org.intellij.sdk.language.nanomessage;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.AsyncFileListener;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import de.cubbossa.tinytranslations.Message;
import de.cubbossa.tinytranslations.MessageTranslator;
import de.cubbossa.tinytranslations.TinyTranslations;
import de.cubbossa.tinytranslations.annotation.KeyPattern;
import de.cubbossa.tinytranslations.storage.properties.PropertiesMessageStorage;
import de.cubbossa.tinytranslations.storage.properties.PropertiesStyleStorage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.intellij.lang.annotations.Language;
import org.intellij.sdk.language.TinyTranslationsDisposable;
import org.intellij.sdk.language.common.editor.AdventureComponentPreviewEditor;
import org.intellij.sdk.language.common.editor.AdventureComponentSplitViewEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TinyTranslationsProject {

  private static Key<MessageTranslator> moduleTranslator = Key.create("TINY_TRANSLATIONS_TRANSLATOR");

  private static File tempFileForTranslationConfigs = null;
  private static MessageTranslator globalTranslator = null;

  public static MessageTranslator getTranslator(Module module) {
    if (tempFileForTranslationConfigs == null) {
      try {
        tempFileForTranslationConfigs = Files.createTempDirectory("temp_tiny_translations").toFile();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    globalTranslator = TinyTranslations.globalTranslator(tempFileForTranslationConfigs);

    MessageTranslator translator = module.getUserData(moduleTranslator);
    if (translator == null) {
      translator = globalTranslator.fork("module_translator_" + UUID.randomUUID().toString().toLowerCase());
      module.putUserData(moduleTranslator, translator);

      VirtualFile file = Arrays.stream(ModuleRootManager.getInstance(module).getModifiableModel().getSourceRoots())
          .filter(f -> f.getPath().endsWith("src/main/resources"))
          .findFirst().orElse(null);

      if (file != null) {
        File langDir = new File(file.getPath() + "/lang/");
        if (!langDir.exists()) {
          try {
            langDir.createNewFile();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
        File stylesFile = new File(langDir, module.getName() + "_styles.properties");
        try {
          stylesFile.createNewFile();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        translator.setStyleStorage(new PropertiesStyleStorage(stylesFile));
        translator.setMessageStorage(new PropertiesMessageStorage(langDir, module.getName() + "_", ""));

        VirtualFile vStylesFile = LocalFileSystem.getInstance().findFileByIoFile(stylesFile);

        MessageTranslator finalTranslator = translator;
        VirtualFileManager.getInstance().addAsyncFileListener(new AsyncFileListener() {
          @Nullable
          @Override
          public ChangeApplier prepareChange(@NotNull List<? extends @NotNull VFileEvent> events) {
            return new ChangeApplier() {
              @Override
              public void afterVfsChange() {
                for (VFileEvent event : events) {
                  if (event.getFile() != null && event.getFile().equals(vStylesFile)) {
                    finalTranslator.loadStyles();
                    Arrays.stream(FileEditorManager.getInstance(module.getProject()).getAllEditors())
                        .filter(fileEditor -> fileEditor instanceof AdventureComponentSplitViewEditor)
                        .map(fileEditor -> ((AdventureComponentSplitViewEditor) fileEditor).getPreviewEditor())
                        .filter(fileEditor -> fileEditor instanceof AdventureComponentPreviewEditor)
                        .map(fileEditor -> (AdventureComponentPreviewEditor) fileEditor)
                        .forEach(AdventureComponentPreviewEditor::updateView);
                  }
                }
              }
            };
          }
        }, TinyTranslationsDisposable.getInstance());
      }
    }
    translator.loadStyles();
    translator.loadLocales();
    return translator;
  }

  public static void createStyle(Module module, String key, @Language("NanoMessage") String style) {
    MessageTranslator translator = getTranslator(module);
    translator.getStyleSet().put(key, style);
    translator.saveStyles();
  }

  public static void createTranslation(Module module, Locale lang, @KeyPattern String key, @Language("NanoMessage") String translation) {
    MessageTranslator translator = getTranslator(module);
    translator.addMessage(Message.builder(key).withTranslation(lang, translation).build());
    translator.saveLocale(lang);
  }

  public static MessageTranslator getTranslator(PsiFile file) {
    PsiFile baseFile = InjectedLanguageManager.getInstance(file.getProject()).getTopLevelFile(file);
    if (baseFile == null) {
      return null;
    }
    Module module = ProjectRootManager.getInstance(baseFile.getProject()).getFileIndex().getModuleForFile(baseFile.getVirtualFile());
    return getTranslator(module);
  }
}
