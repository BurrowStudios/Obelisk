package org.burrow_studios.obelisk.internal.action;

import com.google.gson.JsonElement;
import org.burrow_studios.obelisk.api.action.DeleteAction;
import org.burrow_studios.obelisk.api.entities.Turtle;
import org.burrow_studios.obelisk.internal.ObeliskImpl;
import org.burrow_studios.obelisk.internal.net.NetworkHandler;
import org.burrow_studios.obelisk.internal.net.http.CompiledRoute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public final class DeleteActionImpl<T extends Turtle> extends ActionImpl<Void> implements DeleteAction<T> {
    private final @NotNull Class<T> type;
    private final long entityId;

    public DeleteActionImpl(@NotNull ObeliskImpl api, @NotNull Class<T> type, long entityId, @NotNull CompiledRoute route) {
        super(api, route, (request, response) -> {
            // TODO: throw exception if response is an error
            return null;
        });
        this.type = type;
        this.entityId = entityId;
    }

    @Override
    public long getId() {
        return this.entityId;
    }

    @Override
    public @NotNull Class<T> getType() {
        return this.type;
    }

    @Override
    public @Nullable JsonElement getContent() {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<Void> submit() {
        final NetworkHandler networkHandler = getAPI().getNetworkHandler();



        // TODO
        return CompletableFuture.completedFuture(null);
    }
}
