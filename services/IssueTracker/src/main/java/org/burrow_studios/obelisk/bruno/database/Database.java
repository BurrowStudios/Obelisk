package org.burrow_studios.obelisk.bruno.database;

import com.google.gson.JsonObject;
import org.burrow_studios.obelisk.bruno.exceptions.DatabaseException;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.util.Set;

public interface Database extends Closeable {
    // BOARDS

    @NotNull Set<Long> getBoardIds() throws DatabaseException;

    @NotNull JsonObject getBoard(long id) throws DatabaseException;

    void createBoard(long id, @NotNull String title, long group) throws DatabaseException;

    void updateBoardTitle(long id, @NotNull String title) throws DatabaseException;

    void updateBoardGroup(long board, long group) throws DatabaseException;

    void deleteBoard(long id) throws DatabaseException;

    // ISSUES

    @NotNull Set<Long> getIssueIds(long board) throws DatabaseException;

    @NotNull JsonObject getIssue(long board, long id) throws DatabaseException;

    void createIssue(long board, long id, long author, @NotNull String title, @NotNull IssueState state) throws DatabaseException;

    void updateIssueTitle(long board, long id, @NotNull String title) throws DatabaseException;

    void updateIssueState(long board, long id, @NotNull IssueState state) throws DatabaseException;

    void addIssueAssignee(long board, long id, long assignee) throws DatabaseException;

    void removeIssueAssignee(long board, long id, long assignee) throws DatabaseException;

    void addIssueTag(long board, long id, long tag) throws DatabaseException;

    void removeIssueTag(long board, long id, long tag) throws DatabaseException;

    void deleteIssue(long board, long is) throws DatabaseException;

    // TAGS

    @NotNull Set<Long> getTagIds(long board) throws DatabaseException;

    @NotNull JsonObject getTag(long board, long id) throws DatabaseException;

    void createTag(long board, long id, @NotNull String name) throws DatabaseException;

    void updateTagTitle(long board, long id, @NotNull String name) throws DatabaseException;

    void deleteTag(long board, long id) throws DatabaseException;
}
