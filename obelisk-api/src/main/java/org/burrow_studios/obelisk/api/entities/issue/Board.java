package org.burrow_studios.obelisk.api.entities.issue;

import org.burrow_studios.obelisk.api.cache.TurtleSetView;
import org.burrow_studios.obelisk.api.entities.Group;
import org.burrow_studios.obelisk.api.entities.Turtle;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface Board extends Turtle {
    @NotNull String getTitle();

    long getGroupId();

    @NotNull Group getGroup();

    @NotNull Set<Long> getAvailableTagIds();

    @NotNull TurtleSetView<? extends Tag> getAvailableTags();

    @NotNull Set<Long> getIssueIds();

    @NotNull TurtleSetView<? extends Issue> getIssues();
}