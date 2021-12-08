package io.commitlint;

/**
 * Lint rules.
 */
public class LintRule {

    /** 校验目标 */
    public RuleType type;

    /** 匹配正则 */
    public String match;

    /**
     * 消息提示
     */
    public String tips;

    /**
     * Lint rule type.
     */
    public enum RuleType {
        MESSAGE, USER_NAME, USER_EMAIL, BRANCH_NAME
    }

    public LintRule() {
    }

    public LintRule(RuleType type, String match, String tips) {
        this.type = type;
        this.match = match;
        this.tips = tips;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LintRule)) {
            return false;
        }

        LintRule lintRule = (LintRule) o;

        if (type != lintRule.type) {
            return false;
        }
        if (match != null ? !match.equals(lintRule.match) : lintRule.match != null) {
            return false;
        }
        return tips != null ? tips.equals(lintRule.tips) : lintRule.tips == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (match != null ? match.hashCode() : 0);
        result = 31 * result + (tips != null ? tips.hashCode() : 0);
        return result;
    }
}
