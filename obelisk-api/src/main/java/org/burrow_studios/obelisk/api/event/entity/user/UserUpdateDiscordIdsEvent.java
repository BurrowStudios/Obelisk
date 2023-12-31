package org.burrow_studios.obelisk.api.event.entity.user;

import org.burrow_studios.obelisk.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public final class UserUpdateDiscordIdsEvent extends UserUpdateEvent<Set<Long>> {
    private final @NotNull Set<Long>   addedIds;
    private final @NotNull Set<Long> removedIds;

    public UserUpdateDiscordIdsEvent(@NotNull User entity, @NotNull Set<Long> oldValue, @NotNull Set<Long> newValue) {
        super(entity, oldValue, newValue);
        this.addedIds = newValue.stream()
                .filter(discordId -> !oldValue.contains(discordId))
                .collect(Collectors.toUnmodifiableSet());
        this.removedIds = oldValue.stream()
                .filter(discordId -> !newValue.contains(discordId))
                .collect(Collectors.toUnmodifiableSet());
    }

    public @NotNull Set<Long> getAddedIds() {
        return this.addedIds;
    }

    public @NotNull Set<Long> getRemovedIds() {
        return this.removedIds;
    }
}
