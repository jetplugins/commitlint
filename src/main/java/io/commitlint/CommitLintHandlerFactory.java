package io.commitlint;

import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.CommitContext;
import com.intellij.openapi.vcs.changes.CommitExecutor;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.NonFocusableCheckBox;
import com.intellij.util.PairConsumer;
import com.intellij.util.ui.JBUI;
import com.intellij.vcs.log.VcsUser;
import git4idea.GitUserRegistry;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
import io.commitlint.CommitLinter.Commit;
import io.commitlint.CommitLinter.Result;
import java.util.List;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nullable;

public class CommitLintHandlerFactory extends CheckinHandlerFactory {

    @Override
    public CheckinHandler createHandler(CheckinProjectPanel panel, CommitContext commitContext) {
        return new CheckinHandler() {

            private final Project project = panel.getProject();

            @Override
            public RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
                final NonFocusableCheckBox checkItem = new NonFocusableCheckBox("Lint commit");
                checkItem.setSelected(true);
                checkItem.setEnabled(false);

                return new RefreshableOnComponent() {
                    public JComponent getComponent() {
                        return JBUI.Panels.simplePanel().addToLeft(checkItem);
                    }

                    @Override
                    public void refresh() {
                    }

                    @Override
                    public void saveState() {
                    }

                    @Override
                    public void restoreState() {
                    }
                };
            }

            @Override
            public ReturnResult beforeCheckin(@Nullable CommitExecutor executor,
                    PairConsumer<Object, Object> additionalDataConsumer) {
                // 校验提交信息
                try {
                    CommitLinter linter = getCommitLinter();
                    List<Commit> commitList = getCommit();
                    for (Commit commit : commitList) {
                        Result result = linter.lint(commit);
                        if (result.returnResult != ReturnResult.COMMIT) {
                            Messages.showErrorDialog(project, result.tips, DefaultConstants.NAME);
                            return result.returnResult;
                        }
                    }
                    return ReturnResult.COMMIT;
                } catch (VcsException e) {
                    throw new RuntimeException(e);
                }
            }

            private List<Commit> getCommit() throws VcsException {
                GitUserRegistry gitUserRegistry = GitUserRegistry.getInstance(project);
                // 获取选中的项目
                List<VirtualFile> choiceRoots = Lists.newArrayList();
                for (VirtualFile root : panel.getRoots()) {
                    for (VirtualFile file : panel.getVirtualFiles()) {
                        if (file.getPath().startsWith(root.getPath())) {
                            choiceRoots.add(root);
                            break;
                        }
                    }
                }

                List<Commit> commits = Lists.newArrayListWithExpectedSize(choiceRoots.size());
                for (VirtualFile root : choiceRoots) {
                    Commit commit = new Commit();
                    commit.message = panel.getCommitMessage();
                    VcsUser user = gitUserRegistry.getUser(root);
                    if (user != null) {
                        commit.userName = user.getName();
                        commit.userEmail = user.getEmail();
                    }
                    GitRepository gitRepository = GitBranchUtil.getRepositoryOrGuess(project, root);
                    if (gitRepository != null) {
                        commit.branchName = gitRepository.getCurrentBranchName();
                    }
                    commits.add(commit);
                }
                return commits;
            }

            private CommitLinter getCommitLinter() {
                List<LintRule> rules = InternalUtils.getLintRules();
                return new CommitLinter(rules);
            }
        };
    }
}
