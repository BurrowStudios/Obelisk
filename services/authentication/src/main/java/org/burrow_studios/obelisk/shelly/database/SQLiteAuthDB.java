package org.burrow_studios.obelisk.shelly.database;

import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteConfig;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLiteAuthDB implements AuthDB {
    private static final String STMT_CREATE_TABLE_IDENTITIES   = "CREATE TABLE IF NOT EXISTS `identities` (`subject` BIGINT(20) NOT NULL, `token_family` INT NOT NULL, `token_id` BIGINT(20) NOT NULL, PRIMARY KEY (`token_id`));";
    private static final String STMT_CREATE_TABLE_EXP_FAMILIES = "CREATE TABLE IF NOT EXISTS `expired_families` (`subject` BIGINT(20) NOT NULL, `family` INT NOT NULL, PRIMARY KEY (`subject`, `family`), FOREIGN KEY (`subject`, `family`) REFERENCES `identities`(`subject`, `token_family`));";
    private static final String STMT_CREATE_TABLE_SESSIONS     = "CREATE TABLE IF NOT EXISTS `sessions` (`id` BIGINT(20) NOT NULL, `identity` BIGINT(20) NOT NULL, `token` TEXT NOT NULL, `expired` BOOLEAN NOT NULL DEFAULT FALSE, PRIMARY KEY (`id`), FOREIGN KEY (`identity`) REFERENCES `identities`(`token_id`), UNIQUE (`token`));"; // TODO: don't store token?

    private static final String STMT_INDEX_CREATE_IDENTITIES_SUBJECT      = "CREATE INDEX IF NOT EXISTS `subject` ON `identities` (`subject`);";
    private static final String STMT_INDEX_CREATE_IDENTITIES_TOKEN_FAMILY = "CREATE INDEX IF NOT EXISTS `token_family` ON `identities` (`token_family`);";

    private static final String STMT_SELECT_SESSIONS   = "SELECT * FROM `sessions` WHERE `identity` = ? AND `expired` = 0;";
    private static final String STMT_SELECT_FAMILY     = "SELECT `token_family` AS `family` FROM `identities` WHERE `subject` = ? ORDER BY `family` DESC LIMIT 1;";
    private static final String STMT_SELECT_EXP_FAMILY = "SELECT * FROM `expired_families` WHERE `subject` = ? AND `family` = ?;";
    private static final String STMT_SELECT_SESSION    = "SELECT * FROM `sessions` WHERE `id` = ? LIMIT 1;";

    private static final String STMT_INSERT_IDENTITY   = "INSERT INTO `identities` (`subject`, `token_family`, `token_id`) VALUES (?, ?, ?);";
    private static final String STMT_INSERT_EXP_FAMILY = "INSERT OR IGNORE INTO `expired_families` (`subject`, `family`) VALUES (?, ?);";
    private static final String STMT_INSERT_SESSION    = "INSERT INTO `sessions` (`id`, `identity`, `token`) VALUES (?, ?, ?);";

    private static final String STMT_UPDATE_SESSION_EXPIRE     = "UPDATE `sessions` SET `expired` = 1 WHERE `id` = ? AND `identity` = ?;";
    private static final String STMT_UPDATE_SESSION_EXPIRE_ALL = "UPDATE `sessions` SET `expired` = 1 WHERE `identity` = ?;";

    private static final Logger LOG = Logger.getLogger(SQLiteAuthDB.class.getSimpleName());

    private final Connection connection;

    public SQLiteAuthDB(@NotNull File file) throws SQLException {
        String url = String.format("jdbc:sqlite:%s", file.getAbsolutePath());

        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);

        LOG.log(Level.INFO, "Initiating database connection to " + url);
        this.connection = DriverManager.getConnection(url, config.toProperties());

        this.init();

        LOG.log(Level.INFO, "Database is online");
    }

    private void init() {
        LOG.log(Level.INFO, "Creating tables");
        try {
            final PreparedStatement createIdentities  = connection.prepareStatement(STMT_CREATE_TABLE_IDENTITIES);
            final PreparedStatement createExpFamilies = connection.prepareStatement(STMT_CREATE_TABLE_EXP_FAMILIES);
            final PreparedStatement createSessions    = connection.prepareStatement(STMT_CREATE_TABLE_SESSIONS);

            createIdentities.execute();
            createExpFamilies.execute();
            createSessions.execute();


            final PreparedStatement indexIdentitiesSubject     = connection.prepareStatement(STMT_INDEX_CREATE_IDENTITIES_SUBJECT);
            final PreparedStatement indexIdentitiesTokenFamily = connection.prepareStatement(STMT_INDEX_CREATE_IDENTITIES_TOKEN_FAMILY);

            indexIdentitiesSubject.execute();
            indexIdentitiesTokenFamily.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Could not create tables", e);
        }
    }

    @Override
    public long[] getActiveSessions(long identity) throws DatabaseException {
        try (PreparedStatement stmt = connection.prepareStatement(STMT_SELECT_SESSIONS)) {
            stmt.setLong(1, identity);

            ResultSet result = stmt.executeQuery();

            ArrayList<Long> sessionIds = new ArrayList<>();

            while (result.next())
                sessionIds.add(result.getLong("id"));

            return sessionIds.stream()
                    .mapToLong(Long::longValue)
                    .toArray();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public @NotNull String getSocketKey(long session) throws DatabaseException {
        try (PreparedStatement stmt = connection.prepareStatement(STMT_SELECT_SESSION)) {
            stmt.setLong(1, session);

            ResultSet result = stmt.executeQuery();

            if (!result.next())
                throw new NoSuchEntryException();

            return result.getString("token");
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void createSession(long id, long identity, String token) throws DatabaseException {
        try (PreparedStatement stmt = connection.prepareStatement(STMT_INSERT_SESSION)) {
            stmt.setLong(1, id);
            stmt.setLong(2, identity);
            stmt.setString(3, token);

            stmt.execute();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void invalidateSession(long id, long identity) throws DatabaseException {
        try (PreparedStatement stmt = connection.prepareStatement(STMT_UPDATE_SESSION_EXPIRE)) {
            stmt.setLong(1, id);
            stmt.setLong(2, identity);

            stmt.execute();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void invalidateAllSessions(long identity) throws DatabaseException {
        try (PreparedStatement stmt = connection.prepareStatement(STMT_UPDATE_SESSION_EXPIRE_ALL)) {
            stmt.setLong(1, identity);

            stmt.execute();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void invalidateIdentityTokenFamily(long subject) throws DatabaseException {
        final int family = getCurrentFamily(subject, false);

        try (PreparedStatement stmt = connection.prepareStatement(STMT_INSERT_EXP_FAMILY)) {
            stmt.setLong(1, subject);
            stmt.setInt(2, family);

            stmt.execute();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void createIdentity(long id, long subject) throws DatabaseException {
        final int family = getCurrentFamily(subject, true);

        try (PreparedStatement stmt = connection.prepareStatement(STMT_INSERT_IDENTITY)) {
            stmt.setLong(1, subject);
            stmt.setInt(2, family);
            stmt.setLong(3, id);

            stmt.execute();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private int getCurrentFamily(long subject, boolean createNew) throws DatabaseException {
        int family = 0;

        try (PreparedStatement stmt = connection.prepareStatement(STMT_SELECT_FAMILY)) {
            stmt.setLong(1, subject);

            ResultSet result = stmt.executeQuery();

            if (result.next())
                family = result.getInt("family");
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        // check if family is expired
        try (PreparedStatement stmt = connection.prepareStatement(STMT_SELECT_EXP_FAMILY)) {
            ResultSet result = stmt.executeQuery();

            if (result.next() && createNew)
                family++;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return family;
    }
}
