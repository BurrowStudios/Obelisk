package org.burrow_studios.obelisk.core.entities.action.board.issue;

import com.google.gson.JsonPrimitive;
import org.burrow_studios.obelisk.api.action.entity.board.issue.IssueModifier;
import org.burrow_studios.obelisk.api.entities.board.Issue;
import org.burrow_studios.obelisk.core.action.ModifierImpl;
import org.burrow_studios.obelisk.core.entities.EntityUpdater;
import org.burrow_studios.obelisk.core.entities.checks.board.IssueChecks;
import org.burrow_studios.obelisk.core.entities.impl.board.IssueImpl;
import org.burrow_studios.obelisk.commons.rpc.Endpoints;
import org.jetbrains.annotations.NotNull;

public class IssueModifierImpl extends ModifierImpl<Issue, IssueImpl> implements IssueModifier {
    public IssueModifierImpl(@NotNull IssueImpl issue) {
        super(
                issue,
                Endpoints.Board.Issue.EDIT.builder(issue.getBoard().getId(), issue.getId()).getPath(),
                EntityUpdater::updateIssue
        );
    }

    @Override
    public @NotNull IssueModifierImpl setTitle(@NotNull String title) throws IllegalArgumentException {
        IssueChecks.checkTitle(title);
        data.set("title", new JsonPrimitive(title));
        return this;
    }

    @Override
    public @NotNull IssueModifier setState(@NotNull Issue.State state) {
        data.set("state", new JsonPrimitive(state.name()));
        return this;
    }
}
