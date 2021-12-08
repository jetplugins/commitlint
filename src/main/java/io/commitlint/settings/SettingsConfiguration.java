package io.commitlint.settings;

import com.intellij.openapi.options.Configurable;
import io.commitlint.DefaultConstants;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.Nullable;

public class SettingsConfiguration implements Configurable {

    private SettingsForm form;

    @Nls(capitalization = Capitalization.Title)
    @Override
    public String getDisplayName() {
        return DefaultConstants.NAME;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (form == null) {
            form = new SettingsForm();
        }
        return form.getPanel();
    }

    @Override
    public boolean isModified() {
        Settings originSettings = Settings.getInstance();
        Settings newSettings = form.get();
        return !originSettings.equals(newSettings);
    }

    @Override
    public void apply() {
        Settings settings = form.get();
        Settings.getInstance().loadState(settings);
    }

    @Override
    public void reset() {
        Settings settings = Settings.getInstance();
        form.set(settings);
    }
}
