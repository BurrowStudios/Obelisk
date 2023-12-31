package org.burrow_studios.obelisk.api.event.entity.issue.issue;

import org.burrow_studios.obelisk.api.entities.board.Issue;
import org.burrow_studios.obelisk.api.event.entity.EntityUpdateEvent;
import org.jetbrains.annotations.NotNull;

public abstract class IssueUpdateEvent<T> extends IssueEvent implements EntityUpdateEvent<Issue, T> {
    protected final T oldValue;
    protected final T newValue;

    protected IssueUpdateEvent(@NotNull Issue entity, T oldValue, T newValue) {
        super(entity);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public final T getOldValue() {
        return this.oldValue;
    }

    @Override
    public final T getNewValue() {
        return this.newValue;
    }
}
