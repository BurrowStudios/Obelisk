package org.burrow_studios.obelisk.internal.net;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.http.HttpResponse;

public final class Response {
    private final @NotNull NetworkHandler networkHandler;
    private final long id;

    private final int code;
    private final @Nullable JsonElement content;

    Response(
            @NotNull NetworkHandler networkHandler,
            long id,
            int code,
            @Nullable JsonElement content
    ) {
        this.networkHandler = networkHandler;
        this.id = id;

        this.code = code;
        this.content = content;
    }

    public static @NotNull Response ofHttpResponse(@NotNull HttpResponse<String> httpResponse, @NotNull Request request) {
        final NetworkHandler networkHandler = request.getNetworkHandler();

        final String bodyStr = httpResponse.body();
        final JsonElement body = bodyStr == null ? null : networkHandler.gson.fromJson(bodyStr, JsonElement.class);

        return new Response(networkHandler, request.getId(), httpResponse.statusCode(), body);
    }

    public @NotNull NetworkHandler getNetworkHandler() {
        return networkHandler;
    }

    public long getId() {
        return id;
    }

    public int getCode() {
        return code;
    }

    public @Nullable JsonElement getContent() {
        return content;
    }
}
