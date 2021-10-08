package io.commitlint;

/**
 * Lint rules.
 */
public class LintRule {

    /** 校验目标 */
    public RuleType type;

    /** 匹配正则 */
    public String match;

    /** 消息提示 */
    public String tips;

    /**
     * Lint rule type.
     */
    public enum RuleType {
        MESSAGE, USER_NAME, USER_EMAIL, BRANCH_NAME
    }
}
