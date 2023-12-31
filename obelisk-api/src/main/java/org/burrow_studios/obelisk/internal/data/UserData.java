package org.burrow_studios.obelisk.internal.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.burrow_studios.obelisk.api.entities.User;
import org.burrow_studios.obelisk.internal.EntityBuilder;
import org.burrow_studios.obelisk.internal.entities.UserImpl;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class UserData extends Data<User, UserImpl> {
    public UserData() {
        super();
    }

    public UserData(long id) {
        super(id);
    }

    @Override
    public @NotNull UserImpl build(@NotNull EntityBuilder builder) {
        return builder.buildUser(toJson());
    }

    @Override
    public void update(@NotNull UserImpl user) {
        final JsonObject json = toJson();

        handleUpdate(json, "name", JsonElement::getAsString, user::setName);
        handleUpdateArray(json, "discord", JsonElement::getAsLong, user.getDiscordIdsMutable());
        handleUpdateArray(json, "minecraft", j -> UUID.fromString(j.getAsString()), user.getMinecraftIdsMutable());
    }

    public void setName(@NotNull String name) {
        this.set("name", new JsonPrimitive(name));
    }

    public void addDiscordIds(long... ids) {
        JsonArray arr = new JsonArray();
        for (long id : ids)
            arr.add(id);
        this.addToArray("discord", arr);
    }

    public void removeDiscordIds(long... ids) {
        JsonArray arr = new JsonArray();
        for (long id : ids)
            arr.add(id);
        this.removeFromArray("discord", arr);
    }

    public void addMinecraftIds(@NotNull UUID... ids) {
        JsonArray arr = new JsonArray();
        for (UUID id : ids)
            arr.add(id.toString());
        this.addToArray("minecraft", arr);
    }

    public void removeMinecraftIds(@NotNull UUID... ids) {
        JsonArray arr = new JsonArray();
        for (UUID id : ids)
            arr.add(id.toString());
        this.removeFromArray("minecraft", arr);
    }
}
