package org.burrow_studios.obelisk.api.entities;

import org.burrow_studios.obelisk.api.action.Action;
import org.burrow_studios.obelisk.api.action.DeleteAction;
import org.burrow_studios.obelisk.api.action.entity.project.ProjectModifier;
import org.burrow_studios.obelisk.api.cache.TurtleSetView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

public interface Project extends Turtle {
    @Override
    @NotNull ProjectModifier modify();

    @Override
    @NotNull DeleteAction<Project> delete();

    @NotNull String getTitle();

    @NotNull Timings getTimings();

    @NotNull State getState();

    @NotNull TurtleSetView<? extends User> getMembers();

    @NotNull Action<Project> addMember(@NotNull User user);

    @NotNull Action<Project> removeMember(@NotNull User user);

    record Timings(
            @Nullable Instant release,
            @Nullable Instant apply,
            @Nullable Instant start,
            @Nullable Instant end
    ) { }

    enum State {
        CONCEPT,
        PLANNING,
        APPLICATION,
        RUNNING,
        ENDED,
        STOPPED,
        CANCELED
    }
}
