package org.burrow_studios.obelisk.internal;

import org.burrow_studios.obelisk.api.Obelisk;
import org.burrow_studios.obelisk.internal.action.entity.board.BoardBuilderImpl;
import org.burrow_studios.obelisk.internal.action.entity.group.GroupBuilderImpl;
import org.burrow_studios.obelisk.internal.action.entity.project.ProjectBuilderImpl;
import org.burrow_studios.obelisk.internal.action.entity.ticket.TicketBuilderImpl;
import org.burrow_studios.obelisk.internal.action.entity.user.UserBuilderImpl;
import org.burrow_studios.obelisk.internal.cache.TurtleCache;
import org.burrow_studios.obelisk.internal.entities.GroupImpl;
import org.burrow_studios.obelisk.internal.entities.ProjectImpl;
import org.burrow_studios.obelisk.internal.entities.TicketImpl;
import org.burrow_studios.obelisk.internal.entities.UserImpl;
import org.burrow_studios.obelisk.internal.entities.board.BoardImpl;
import org.burrow_studios.obelisk.internal.entities.board.IssueImpl;
import org.burrow_studios.obelisk.internal.entities.board.TagImpl;
import org.burrow_studios.obelisk.internal.event.EventHandlerImpl;
import org.burrow_studios.obelisk.internal.net.NetworkHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ObeliskImpl implements Obelisk {
    private final EventHandlerImpl eventHandler;
    private final NetworkHandler   networkHandler;

    private final TurtleCache<GroupImpl>     groupCache;
    private final TurtleCache<ProjectImpl> projectCache;
    private final TurtleCache<TicketImpl>   ticketCache;
    private final TurtleCache<UserImpl>       userCache;
    private final TurtleCache<BoardImpl>     boardCache;
    private final TurtleCache<IssueImpl>     issueCache;
    private final TurtleCache<TagImpl>         tagCache;

    private final String token;

    public ObeliskImpl() {
        this.eventHandler   = new EventHandlerImpl(this);
        this.networkHandler = new NetworkHandler(this);

        this.groupCache   = new TurtleCache<>(this);
        this.projectCache = new TurtleCache<>(this);
        this.ticketCache  = new TurtleCache<>(this);
        this.userCache    = new TurtleCache<>(this);
        this.boardCache   = new TurtleCache<>(this);
        this.issueCache   = new TurtleCache<>(this);
        this.tagCache     = new TurtleCache<>(this);

        this.token = "null"; // TODO
    }

    public @NotNull EventHandlerImpl getEventHandler() {
        return eventHandler;
    }

    public @NotNull NetworkHandler getNetworkHandler() {
        return networkHandler;
    }

    public @NotNull String getToken() {
        return token;
    }

    /* - ENTITIES - */

    @Override
    public @NotNull TurtleCache<GroupImpl> getGroups() {
        return this.groupCache;
    }

    @Override
    public @Nullable GroupImpl getGroup(long id) {
        return this.groupCache.get(id);
    }

    @Override
    public @NotNull TurtleCache<ProjectImpl> getProjects() {
        return this.projectCache;
    }

    @Override
    public @Nullable ProjectImpl getProject(long id) {
        return this.projectCache.get(id);
    }

    @Override
    public @NotNull TurtleCache<TicketImpl> getTickets() {
        return ticketCache;
    }

    @Override
    public @Nullable TicketImpl getTicket(long id) {
        return this.ticketCache.get(id);
    }

    @Override
    public @NotNull TurtleCache<UserImpl> getUsers() {
        return this.userCache;
    }

    @Override
    public @Nullable UserImpl getUser(long id) {
        return this.userCache.get(id);
    }

    @Override
    public @NotNull TurtleCache<BoardImpl> getBoards() {
        return this.boardCache;
    }

    @Override
    public @Nullable BoardImpl getBoard(long id) {
        return this.boardCache.get(id);
    }

    @Override
    public @NotNull TurtleCache<IssueImpl> getIssues() {
        return this.issueCache;
    }

    @Override
    public @Nullable IssueImpl getIssue(long id) {
        return issueCache.get(id);
    }

    @Override
    public @NotNull TurtleCache<TagImpl> getTags() {
        return this.tagCache;
    }

    @Override
    public @Nullable TagImpl getTag(long id) {
        return this.tagCache.get(id);
    }

    @Override
    public @NotNull GroupBuilderImpl createGroup() {
        return new GroupBuilderImpl(this);
    }

    @Override
    public @NotNull ProjectBuilderImpl createProject() {
        return new ProjectBuilderImpl(this);
    }

    @Override
    public @NotNull TicketBuilderImpl createTicket() {
        return new TicketBuilderImpl(this);
    }

    @Override
    public @NotNull UserBuilderImpl createUser() {
        return new UserBuilderImpl(this);
    }

    @Override
    public @NotNull BoardBuilderImpl createBoard() {
        return new BoardBuilderImpl(this);
    }
}
