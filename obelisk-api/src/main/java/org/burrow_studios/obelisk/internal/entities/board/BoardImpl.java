package org.burrow_studios.obelisk.internal.entities.board;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.burrow_studios.obelisk.api.action.DeleteAction;
import org.burrow_studios.obelisk.api.entities.Group;
import org.burrow_studios.obelisk.api.entities.board.Board;
import org.burrow_studios.obelisk.internal.ObeliskImpl;
import org.burrow_studios.obelisk.internal.action.DeleteActionImpl;
import org.burrow_studios.obelisk.internal.action.entity.board.BoardModifierImpl;
import org.burrow_studios.obelisk.internal.action.entity.board.issue.IssueBuilderImpl;
import org.burrow_studios.obelisk.internal.action.entity.board.tag.TagBuilderImpl;
import org.burrow_studios.obelisk.internal.cache.DelegatingTurtleCacheView;
import org.burrow_studios.obelisk.internal.entities.GroupImpl;
import org.burrow_studios.obelisk.internal.entities.TurtleImpl;
import org.burrow_studios.obelisk.internal.net.http.Route;
import org.jetbrains.annotations.NotNull;

public final class BoardImpl extends TurtleImpl<Board> implements Board {
    private @NotNull String title;
    private @NotNull GroupImpl group;
    private final @NotNull DelegatingTurtleCacheView<TagImpl> availableTags;
    private final @NotNull DelegatingTurtleCacheView<IssueImpl> issues;

    public BoardImpl(
            @NotNull ObeliskImpl api,
            long id,
            @NotNull String title,
            @NotNull GroupImpl group,
            @NotNull DelegatingTurtleCacheView<TagImpl> availableTags,
            @NotNull DelegatingTurtleCacheView<IssueImpl> issues
    ) {
        super(api, id);
        this.title = title;
        this.group = group;
        this.availableTags = availableTags;
        this.issues = issues;
    }

    @Override
    public @NotNull JsonObject toJson() {
        JsonObject json = super.toJson();
        json.addProperty("title", title);
        json.addProperty("group", group.getId());

        JsonArray tagIds = new JsonArray();
        for (long tagId : this.availableTags.getIdsAsImmutaleSet())
            tagIds.add(tagId);
        json.add("tags", tagIds);

        JsonArray issueIds = new JsonArray();
        for (long issueId : this.issues.getIdsAsImmutaleSet())
            issueIds.add(issueId);
        json.add("issues", issueIds);

        return json;
    }

    @Override
    public @NotNull BoardModifierImpl modify() {
        return new BoardModifierImpl(this);
    }

    @Override
    public @NotNull DeleteAction<Board> delete() {
        return new DeleteActionImpl<>(
                this.getAPI(),
                Board.class,
                this.getId(),
                Route.Board.DELETE.builder()
                        .withArg(getId())
                        .compile()
        );
    }

    @Override
    public @NotNull TagBuilderImpl createTag() {
        return new TagBuilderImpl(this);
    }

    @Override
    public @NotNull IssueBuilderImpl createIssue() {
        return new IssueBuilderImpl(this);
    }

    @Override
    public @NotNull String getTitle() {
        return this.title;
    }

    public void setTitle(@NotNull String title) {
        this.title = title;
    }

    @Override
    public @NotNull Group getGroup() {
        return this.group;
    }

    public void setGroup(@NotNull GroupImpl group) {
        this.group = group;
    }

    @Override
    public @NotNull DelegatingTurtleCacheView<TagImpl> getAvailableTags() {
        return this.availableTags;
    }

    @Override
    public @NotNull DelegatingTurtleCacheView<IssueImpl> getIssues() {
        return this.issues;
    }
}
