package org.burrow_studios.obelisk.server.net.http.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.burrow_studios.obelisk.api.entities.board.Issue;
import org.burrow_studios.obelisk.core.ObeliskImpl;
import org.burrow_studios.obelisk.core.entities.action.board.issue.IssueBuilderImpl;
import org.burrow_studios.obelisk.core.entities.action.board.issue.IssueModifierImpl;
import org.burrow_studios.obelisk.core.entities.impl.UserImpl;
import org.burrow_studios.obelisk.core.entities.impl.board.BoardImpl;
import org.burrow_studios.obelisk.core.entities.impl.board.IssueImpl;
import org.burrow_studios.obelisk.core.entities.impl.board.TagImpl;
import org.burrow_studios.obelisk.server.net.NetworkHandler;
import org.burrow_studios.obelisk.server.net.http.Request;
import org.burrow_studios.obelisk.server.net.http.ResponseBuilder;
import org.burrow_studios.obelisk.server.net.http.exceptions.APIException;
import org.burrow_studios.obelisk.server.net.http.exceptions.BadRequestException;
import org.burrow_studios.obelisk.server.net.http.exceptions.InternalServerErrorException;
import org.burrow_studios.obelisk.server.net.http.exceptions.NotFoundException;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.StreamSupport;

public class IssueHandler {
    private final @NotNull NetworkHandler networkHandler;

    public IssueHandler(@NotNull NetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
    }

    public void onGetAll(@NotNull Request request, @NotNull ResponseBuilder response) throws APIException {
        JsonArray json = new JsonArray();
        for (long id : getAPI().getIssues().getIdsAsImmutaleSet())
            json.add(id);

        response.setCode(200);
        response.setBody(json);
    }

    public void onGet(@NotNull Request request, @NotNull ResponseBuilder response) throws APIException {
        final long issueId = request.getSegmentLong(3);

        final IssueImpl issue = getAPI().getIssue(issueId);
        if (issue == null)
            throw new NotFoundException();

        response.setCode(200);
        response.setBody(issue.toJson());
    }

    public void onCreate(@NotNull Request request, @NotNull ResponseBuilder response) throws APIException {
        Optional<JsonObject> body = request.optionalBody()
                .map(JsonElement::getAsJsonObject);

        final long boardId = request.getSegmentLong(1);
        final BoardImpl board = getAPI().getBoard(boardId);
        if (board == null)
            throw new NotFoundException();

        if (body.isEmpty())
            throw new BadRequestException("No content");

        final JsonObject json = body.get();
        final JsonObject result;

        final IssueBuilderImpl builder;

        try {
            final long   authorId = json.get("author").getAsLong();
            final String title    = json.get("title").getAsString();

            final String stateStr = json.get("state").getAsString();
            final Issue.State state = Issue.State.valueOf(stateStr);

            UserImpl author = getAPI().getUser(authorId);
            if (author == null)
                throw new IllegalArgumentException("Invalid user id: " + authorId);

            builder = board.createIssue()
                    .setAuthor(author)
                    .setTitle(title)
                    .setState(state);

            JsonArray assignees = json.getAsJsonArray("assignees");
            StreamSupport.stream(assignees.spliterator(), false)
                    .map(JsonElement::getAsLong)
                    .map(id -> {
                        UserImpl user = getAPI().getUser(id);
                        if (user == null)
                            throw new IllegalArgumentException("Invalid user id: " + id);
                        return user;
                    })
                    .forEach(builder::addAssignees);

            JsonArray tags = json.getAsJsonArray("tags");
            StreamSupport.stream(tags.spliterator(), false)
                    .map(JsonElement::getAsLong)
                    .map(id -> {
                        TagImpl tag = getAPI().getTag(id);
                        if (tag == null)
                            throw new IllegalArgumentException("Invalid tag id: " + id);
                        return tag;
                    })
                    .forEach(builder::addTags);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            throw new BadRequestException("Malformed body");
        }

        try {
            result = ((IssueImpl) builder.await()).toJson();
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }

        response.setCode(200);
        response.setBody(result);
    }

    public void onAddAssignee(@NotNull Request request, @NotNull ResponseBuilder response) throws APIException {
        final long issueId = request.getSegmentLong(3);
        final long  userId = request.getSegmentLong(5);

        final IssueImpl issue = getAPI().getIssue(issueId);
        final UserImpl  user  = getAPI().getUser(userId);

        if (issue == null || user == null)
            throw new NotFoundException();

        try {
            issue.addAssignee(user).await();
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }

        response.setCode(204);
    }

    public void onDeleteAssignee(@NotNull Request request, @NotNull ResponseBuilder response) throws APIException {
        final long issueId = request.getSegmentLong(3);
        final long  userId = request.getSegmentLong(5);

        final IssueImpl issue = getAPI().getIssue(issueId);
        final UserImpl  user  = getAPI().getUser(userId);

        if (issue == null)
            throw new NotFoundException();

        if (user != null && issue.getAssignees().containsId(userId)) {
            try {
                issue.removeAssignee(user).await();
            } catch (Exception e) {
                throw new InternalServerErrorException();
            }
        }

        response.setCode(204);
    }

    public void onAddTag(@NotNull Request request, @NotNull ResponseBuilder response) throws APIException {
        final long issueId = request.getSegmentLong(3);
        final long   tagId = request.getSegmentLong(5);

        final IssueImpl issue = getAPI().getIssue(issueId);
        final TagImpl   tag   = getAPI().getTag(tagId);

        if (issue == null || tag == null)
            throw new NotFoundException();

        try {
            issue.addTag(tag).await();
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }

        response.setCode(204);
    }

    public void onDeleteTag(@NotNull Request request, @NotNull ResponseBuilder response) throws APIException {
        final long issueId = request.getSegmentLong(3);
        final long   tagId = request.getSegmentLong(5);

        final IssueImpl issue = getAPI().getIssue(issueId);
        final TagImpl   tag   = getAPI().getTag(tagId);

        if (issue == null)
            throw new NotFoundException();

        if (tag != null && issue.getAssignees().containsId(tagId)) {
            try {
                issue.removeTag(tag).await();
            } catch (Exception e) {
                throw new InternalServerErrorException();
            }
        }

        response.setCode(204);
    }

    public void onDelete(@NotNull Request request, @NotNull ResponseBuilder response) throws APIException {
        final long issueId = request.getSegmentLong(3);
        final IssueImpl issue = getAPI().getIssue(issueId);

        if (issue != null) {
            try {
                issue.delete().await();
            } catch (Exception e) {
                throw new InternalServerErrorException();
            }
        }

        response.setCode(204);
    }

    public void onEdit(@NotNull Request request, @NotNull ResponseBuilder response) throws APIException {
        final long issueId = request.getSegmentLong(3);

        Optional<JsonObject> body = request.optionalBody()
                .map(JsonElement::getAsJsonObject);

        if (body.isEmpty())
            throw new BadRequestException("No content");

        final JsonObject json = body.get();

        IssueImpl issue = getAPI().getIssue(issueId);
        if (issue == null)
            throw new NotFoundException();
        final IssueModifierImpl modifier = issue.modify();

        try {
            Optional.ofNullable(json.get("title"))
                    .map(JsonElement::getAsString)
                    .ifPresent(modifier::setTitle);

            Optional.ofNullable(json.get("state"))
                    .map(JsonElement::getAsString)
                    .map(Issue.State::valueOf)
                    .ifPresent(modifier::setState);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            throw new BadRequestException("Malformed body");
        }

        try {
            issue = ((IssueImpl) modifier.await());
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }

        response.setCode(200);
        response.setBody(issue.toJson());
    }

    private @NotNull ObeliskImpl getAPI() {
        return this.networkHandler.getServer().getAPI();
    }
}
