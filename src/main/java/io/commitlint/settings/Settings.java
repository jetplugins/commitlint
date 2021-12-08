package io.commitlint.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import io.commitlint.InternalUtils;
import io.commitlint.LintRule;
import io.commitlint.LintRule.RuleType;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Yapix应用程序级别配置.
 */
@State(name = "CommitLintSettings", storages = @Storage("CommitLintSettings.xml"))
public class Settings implements PersistentStateComponent<Settings> {

    public boolean forceLint = true;

    public boolean allowCustomRules = false;

    public List<LintRule> rules;

    public static Settings getInstance() {
        Settings settings = ServiceManager.getService(Settings.class);
        if (settings.rules == null || settings.rules.isEmpty()) {
            settings.rules = InternalUtils.getLintRules();
        }
        return settings;
    }

    /**
     * 获取内置默认设置
     */
    public static Settings getDefaultSettings() {
        Settings settings = new Settings();
        settings.forceLint = true;
        settings.allowCustomRules = false;
        settings.rules = InternalUtils.getLintRules();
        return settings;
    }

    @Nullable
    @Override
    public Settings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull Settings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public LintRule getRuleByType(RuleType type) {
        if (rules == null || rules.isEmpty()) {
            return null;
        }
        return rules.stream().filter(o -> type == o.type).findFirst().orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Settings)) {
            return false;
        }

        Settings settings = (Settings) o;

        if (forceLint != settings.forceLint) {
            return false;
        }
        if (allowCustomRules != settings.allowCustomRules) {
            return false;
        }
        return rules != null ? rules.equals(settings.rules) : settings.rules == null;
    }

    @Override
    public int hashCode() {
        int result = (forceLint ? 1 : 0);
        result = 31 * result + (allowCustomRules ? 1 : 0);
        result = 31 * result + (rules != null ? rules.hashCode() : 0);
        return result;
    }
}
