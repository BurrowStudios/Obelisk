package org.burrow_studios.obelisk.shelly.net;

import com.google.gson.JsonObject;
import org.burrow_studios.obelisk.commons.rpc.RPCRequest;
import org.burrow_studios.obelisk.commons.rpc.RPCResponse;
import org.burrow_studios.obelisk.commons.rpc.Status;
import org.burrow_studios.obelisk.commons.rpc.exceptions.RequestHandlerException;
import org.burrow_studios.obelisk.shelly.Shelly;
import org.burrow_studios.obelisk.shelly.crypto.TokenManager;
import org.burrow_studios.obelisk.shelly.database.DatabaseException;
import org.jetbrains.annotations.NotNull;

public class SessionHandler {
    private final @NotNull Shelly shelly;

    public SessionHandler(@NotNull Shelly shelly) {
        this.shelly = shelly;
    }

    public void onLogin(@NotNull RPCRequest request, @NotNull RPCResponse.Builder response) throws RequestHandlerException {
        final TokenManager tokenManager = getTokenManager();

        final long subject = request.getPathSegmentAsLong(1);

        final long identity = request.bodyHelper().requireElementAsLong("identity");

        final String sessionToken = tokenManager.newSessionToken(identity, subject);

        final JsonObject responseJson = new JsonObject();
        responseJson.addProperty("session", sessionToken);

        response.setBody(responseJson);
        response.setStatus(Status.OK);
    }

    public void onLogout(@NotNull RPCRequest request, @NotNull RPCResponse.Builder response) throws RequestHandlerException, DatabaseException {
        final long subject = request.getPathSegmentAsLong(1);
        final long session = request.getPathSegmentAsLong(2);

        final long identity = request.bodyHelper().requireElementAsLong("identity");

        this.shelly.getDatabase().invalidateSession(session, identity);

        response.setStatus(Status.NO_CONTENT);
    }

    public void onLogoutAll(@NotNull RPCRequest request, @NotNull RPCResponse.Builder response) throws RequestHandlerException {
        final long subject = request.getPathSegmentAsLong(1);

        final long identity = request.bodyHelper().requireElementAsLong("identity");

        this.shelly.getDatabase().invalidateAllSessions(identity);

        response.setStatus(Status.NO_CONTENT);
    }

    public void onGetSocket(@NotNull RPCRequest request, @NotNull RPCResponse.Builder response) throws RequestHandlerException {
        JsonObject body = new JsonObject();
        body.addProperty("host", "api.burrow-studios.org");
        body.addProperty("port", 8346);

        response.setStatus(Status.OK);
        response.setBody(body);
    }

    private @NotNull TokenManager getTokenManager() {
        return this.shelly.getTokenManager();
    }
}
