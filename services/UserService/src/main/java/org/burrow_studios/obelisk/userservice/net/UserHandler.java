package org.burrow_studios.obelisk.userservice.net;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.burrow_studios.obelisk.commons.rpc.RPCRequest;
import org.burrow_studios.obelisk.commons.rpc.RPCResponse;
import org.burrow_studios.obelisk.commons.rpc.Status;
import org.burrow_studios.obelisk.commons.rpc.exceptions.BadRequestException;
import org.burrow_studios.obelisk.commons.rpc.exceptions.RequestHandlerException;
import org.burrow_studios.obelisk.commons.turtle.TurtleGenerator;
import org.burrow_studios.obelisk.userservice.UserService;
import org.burrow_studios.obelisk.userservice.database.UserDB;
import org.burrow_studios.obelisk.userservice.exceptions.NoSuchEntryException;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public class UserHandler {
    private final TurtleGenerator idGenerator = TurtleGenerator.get("UserService");
    private final UserService service;

    public UserHandler(@NotNull UserService service) {
        this.service = service;
    }

    public void onGetAll(@NotNull RPCRequest request, @NotNull RPCResponse.Builder response) throws RequestHandlerException {
        final JsonArray responseBody = new JsonArray();
        final Set<Long> userIds = getDatabase().getUserIds();

        for (Long userId : userIds)
            responseBody.add(userId);

        response.setStatus(Status.OK);
        response.setBody(responseBody);
    }

    public void onGet(@NotNull RPCRequest request, @NotNull RPCResponse.Builder response) throws RequestHandlerException {
        final String userIdStr = request.getPath().split("/")[1];
        final long   userId    = Long.parseLong(userIdStr);

        try {
            final JsonObject responseBody = getDatabase().getUser(userId);

            response.setStatus(Status.OK);
            response.setBody(responseBody);
        } catch (NoSuchEntryException e) {
            response.setStatus(Status.NOT_FOUND);
        }
    }

    public void onCreate(@NotNull RPCRequest request, @NotNull RPCResponse.Builder response) throws RequestHandlerException {
        if (!(request.getBody() instanceof JsonObject requestBody))
            throw new BadRequestException("Missing request body");

        if (!(requestBody.get("name") instanceof JsonPrimitive nameInfo))
            throw new BadRequestException("Malformed request body: Malformed name info");
        final String name = nameInfo.getAsString();

        final long id = idGenerator.newId();
        getDatabase().createUser(id, name);

        final JsonObject responseBody = getDatabase().getUser(id);

        response.setStatus(Status.CREATED);
        response.setBody(responseBody);
    }

    public void onAddDiscord(@NotNull RPCRequest request, @NotNull RPCResponse.Builder response) throws RequestHandlerException {
        final String[] pathSegments = request.getPath().split("/");
        final long userId    = Long.parseLong(pathSegments[1]);
        final long snowflake = Long.parseLong(pathSegments[3]);

        getDatabase().addUserDiscordId(userId, snowflake);

        response.setStatus(Status.NO_CONTENT);
    }

    public void onDelDiscord(@NotNull RPCRequest request, @NotNull RPCResponse.Builder response) throws RequestHandlerException {
        final String[] pathSegments = request.getPath().split("/");
        final long userId    = Long.parseLong(pathSegments[1]);
        final long snowflake = Long.parseLong(pathSegments[3]);

        getDatabase().removeUserDiscordId(userId, snowflake);

        response.setStatus(Status.NO_CONTENT);
    }

    public void onAddMinecraft(@NotNull RPCRequest request, @NotNull RPCResponse.Builder response) throws RequestHandlerException {
        final String[] pathSegments = request.getPath().split("/");
        final long userId = Long.parseLong(pathSegments[1]);
        final UUID uuid   = UUID.fromString(pathSegments[3]);

        getDatabase().addUserMinecraftId(userId, uuid);

        response.setStatus(Status.NO_CONTENT);
    }

    public void onDelMinecraft(@NotNull RPCRequest request, @NotNull RPCResponse.Builder response) throws RequestHandlerException {
        final String[] pathSegments = request.getPath().split("/");
        final long userId = Long.parseLong(pathSegments[1]);
        final UUID uuid   = UUID.fromString(pathSegments[3]);

        getDatabase().removeUserMinecraftId(userId, uuid);

        response.setStatus(Status.NO_CONTENT);
    }

    public void onDelete(@NotNull RPCRequest request, @NotNull RPCResponse.Builder response) throws RequestHandlerException {
        final String userIdStr = request.getPath().split("/")[1];
        final long   userId    = Long.parseLong(userIdStr);

        getDatabase().deleteUser(userId);

        response.setStatus(Status.NO_CONTENT);
    }

    public void onEdit(@NotNull RPCRequest request, @NotNull RPCResponse.Builder response) throws RequestHandlerException {
        final String userIdStr = request.getPath().split("/")[1];
        final long   userId    = Long.parseLong(userIdStr);

        if (!(request.getBody() instanceof JsonObject requestBody))
            throw new BadRequestException("Missing request body");

        final JsonElement nameInfo = requestBody.get("name");
        if (nameInfo != null) {
            if (!(nameInfo instanceof JsonPrimitive json))
                throw new BadRequestException("Malformed request body: Malformed name info");

            final String name = json.getAsString();

            getDatabase().updateUserName(userId, name);
        }

        final JsonObject responseBody = getDatabase().getUser(userId);

        response.setStatus(Status.OK);
        response.setBody(responseBody);
    }

    public @NotNull UserDB getDatabase() {
        return this.service.getUserDB();
    }
}