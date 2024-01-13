package org.burrow_studios.obelisk.api.event.entity.board.issue;

import org.burrow_studios.obelisk.api.entities.board.Issue;
import org.burrow_studios.obelisk.api.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

public abstract class IssueEvent extends EntityEvent<Issue> {
    protected IssueEvent(long id, @NotNull Issue entity) {
        super(id, entity);
    }
}
