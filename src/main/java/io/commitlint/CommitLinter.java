package io.commitlint;

import com.intellij.openapi.vcs.checkin.CheckinHandler.ReturnResult;
import io.commitlint.LintRule.RuleType;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * Lint commit
 *
 * @see #lint(Commit)
 */
public class CommitLinter {

    private final List<LintRule> rules;

    public CommitLinter(List<LintRule> rules) {
        this.rules = rules;
    }

    /**
     * lint commit
     */
    public Result lint(Commit commit) {
        Result result = lintText(commit, commit.branchName, getRulesByType(RuleType.BRANCH_NAME));
        if (result.returnResult == ReturnResult.COMMIT) {
            result = lintText(commit, commit.userName, getRulesByType(RuleType.USER_NAME));
        }
        if (result.returnResult == ReturnResult.COMMIT) {
            result = lintText(commit, commit.userEmail, getRulesByType(RuleType.USER_EMAIL));
        }
        if (result.returnResult == ReturnResult.COMMIT) {
            result = lintText(commit, commit.message, getRulesByType(RuleType.MESSAGE));
        }
        return result;
    }

    private Result lintText(Commit commit, String text, List<LintRule> rules) {
        if (text == null) {
            text = "";
        }
        Result result = new Result();
        result.returnResult = ReturnResult.COMMIT;
        for (LintRule rule : rules) {
            String regular = handleRuleMatch(commit, rule.match);
            if (!text.matches(regular)) {
                result.returnResult = ReturnResult.CANCEL;
                result.tips = rule.tips;
            }
        }
        return result;
    }

    private List<LintRule> getRulesByType(RuleType type) {
        return rules.stream()
                .filter(rule -> rule.type == type && StringUtils.isNotEmpty(rule.match))
                .collect(Collectors.toList());
    }

    private String handleRuleMatch(Commit commit, String regular) {
        if (regular == null) {
            return "";
        }
        String userNameExpression = "${USER_NAME}";
        String userName = commit.userName != null ? Pattern.quote(commit.userName) : "";
        regular = regular.replace(userNameExpression, userName);
        return regular;
    }

    /**
     * 提交相关信息
     */
    public static class Commit {

        public String message;
        public String userName;
        public String userEmail;
        public String branchName;
    }

    /**
     * 校验结果
     */
    public static class Result {

        public ReturnResult returnResult;
        public String tips;
    }
}
