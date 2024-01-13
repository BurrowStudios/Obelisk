package org.burrow_studios.obelisk.api.event;

import org.burrow_studios.obelisk.api.Obelisk;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractEvent implements Event {
    protected final @NotNull Obelisk api;
    protected final long id;

    protected AbstractEvent(@NotNull Obelisk api, long id) {
        this.api = api;
        this.id = id;
    }

    @Override
    public final long getId() {
        return this.id;
    }

    @Override
    public final @NotNull Obelisk getAPI() {
        return this.api;
    }
}
