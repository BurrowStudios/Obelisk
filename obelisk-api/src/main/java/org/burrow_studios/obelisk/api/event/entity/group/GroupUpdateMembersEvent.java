package org.burrow_studios.obelisk.api.event.entity.group;

import org.burrow_studios.obelisk.api.entities.Group;
import org.burrow_studios.obelisk.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public final class GroupUpdateMembersEvent extends GroupUpdateEvent<Set<? extends User>> {
    private final @NotNull Set<User> joiningMembers;
    private final @NotNull Set<User> leavingMembers;

    public GroupUpdateMembersEvent(long id, @NotNull Group entity, @NotNull Set<? extends User> oldValue, @NotNull Set<? extends User> newValue) {
        super(id, entity, oldValue, newValue);
        this.joiningMembers = newValue.stream()
                .filter(member -> !oldValue.contains(member))
                .collect(Collectors.toUnmodifiableSet());
        this.leavingMembers = oldValue.stream()
                .filter(member -> !newValue.contains(member))
                .collect(Collectors.toUnmodifiableSet());
    }

    public @NotNull Set<User> getJoiningMembers() {
        return this.joiningMembers;
    }

    public @NotNull Set<User> getLeavingMembers() {
        return this.leavingMembers;
    }
}
