package io.commitlint.settings;

import com.google.common.collect.Lists;
import io.commitlint.LintRule;
import io.commitlint.LintRule.RuleType;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SettingsForm {

    private JCheckBox forceCheckBox;
    private JPanel panel;
    private JTextField branchRuleField;
    private JTextField messageRuleField;
    private JTextField authorRuleField;
    private JTextField emailRuleField;
    private JButton resetDefaultButton;
    private JCheckBox customRulesCheckBox;
    private JPanel rulesPanel;
    private JTextArea branchTipsField;
    private JTextArea messageTipsField;
    private JTextArea authorTipsField;
    private JTextArea emailTipsField;

    private AtomicBoolean listenerSet = new AtomicBoolean(false);

    public JPanel getPanel() {
        return panel;
    }

    public Settings get() {
        LintRule branchRule = new LintRule(RuleType.BRANCH_NAME, branchRuleField.getText().trim(),
                branchTipsField.getText().trim());
        LintRule messageRule = new LintRule(RuleType.MESSAGE, messageRuleField.getText().trim(),
                messageTipsField.getText().trim());
        LintRule authorRule = new LintRule(RuleType.USER_NAME, authorRuleField.getText().trim(),
                authorTipsField.getText().trim());
        LintRule emailRule = new LintRule(RuleType.USER_EMAIL, emailRuleField.getText().trim(),
                emailTipsField.getText().trim());
        List<LintRule> rules = Lists.newArrayList(branchRule, messageRule, authorRule, emailRule);

        Settings settings = new Settings();
        settings.forceLint = forceCheckBox.isSelected();
        settings.allowCustomRules = customRulesCheckBox.isSelected();
        settings.rules = rules;
        return settings;
    }

    public void set(Settings settings) {
        // 事件添加
        if (listenerSet.compareAndSet(false, true)) {
            resetDefaultButton.addActionListener(e -> {
                set(Settings.getDefaultSettings());
            });
            customRulesCheckBox.addChangeListener(e -> {
                boolean allowEdit = customRulesCheckBox.isSelected();
                rulesPanel.setVisible(allowEdit);
            });
        }

        forceCheckBox.setSelected(settings.forceLint);
        customRulesCheckBox.setSelected(settings.allowCustomRules);
        LintRule branchRule = settings.getRuleByType(RuleType.BRANCH_NAME);
        if (branchRule != null) {
            branchRuleField.setText(branchRule.match);
            branchTipsField.setText(branchRule.tips);
        }
        LintRule messageRule = settings.getRuleByType(RuleType.MESSAGE);
        if (messageRule != null) {
            messageRuleField.setText(messageRule.match);
            messageTipsField.setText(messageRule.tips);
        }
        LintRule authorRule = settings.getRuleByType(RuleType.USER_NAME);
        if (authorRule != null) {
            authorRuleField.setText(authorRule.match);
            authorTipsField.setText(authorRule.tips);
        }
        LintRule emailRule = settings.getRuleByType(RuleType.USER_EMAIL);
        if (emailRule != null) {
            emailRuleField.setText(emailRule.match);
            emailTipsField.setText(emailRule.tips);
        }

        boolean allowEdit = customRulesCheckBox.isSelected();
        rulesPanel.setVisible(allowEdit);
    }
}
