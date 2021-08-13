package base;

import base.Helpers.PasteEntityMapper;
import base.Helpers.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Repository
public class RootRepository {

    private static final String TABLE_NAME = "pastes";

    @PostConstruct
    public void init() {

        // @TODO use ipv4 and ipv6 for ip address and use another type in the table
        String createTableQuery = "CREATE TABLE " + TABLE_NAME +
                " (id INTEGER PRIMARY KEY AUTO_INCREMENT, content TEXT NOT NULL, paste_id CHAR(5) NOT NULL UNIQUE, title VARCHAR(255), ip VARCHAR(60) NOT NULL," +
                " created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, expire_at TIMESTAMP) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin";

        jdbcTemplate.execute("DROP TABLE IF EXISTS " + TABLE_NAME);
        jdbcTemplate.execute(createTableQuery);
        jdbcTemplate.execute("CREATE INDEX paste_id_idx ON " + TABLE_NAME + "(ip)");
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean save(PasteEntity entity) {
        int result = jdbcTemplate.update(
                "INSERT INTO " + TABLE_NAME + " (content, paste_id, ip, title, expire_at) VALUES (?, ?, ?, ?, ?)",
                entity.getContent(), entity.getPasteId(), entity.getIp(), entity.getTitle(), Utils.convertToTimestamp(entity.getExpireAt().toEpochMilli())
        );

        return result != 0;
    }

    public boolean checkPasteIdExists(String pasteId) {
        Integer objectsCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(id) FROM " + TABLE_NAME + " WHERE paste_id = ?",
                (rs, rowNum) -> rs.getInt(1), pasteId
        );

        if (objectsCount == null) {
            return false;
        }

        return objectsCount > 0;
    }

    public int getPastesCountByIp(String ip) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(id) FROM " + TABLE_NAME + " WHERE ip = ?",
                (rs, rowNum) -> rs.getInt(1), ip
        );

        if (count == null) {
            return 0;
        }

        return count;
    }

    public boolean delete(long id) {
        return jdbcTemplate.update("DELETE FROM " + TABLE_NAME + " WHERE id = ?", id) > 0;
    }

    public Optional<PasteEntity> getById(long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM " + TABLE_NAME + " WHERE id = ?", new PasteEntityMapper(), id));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<PasteEntity> getByIp(String ip) {
        return jdbcTemplate.query("SELECT id, content, pasteId, ip, title FROM " + TABLE_NAME + " WHERE ip = ?", new PasteEntityMapper(), ip);
    }

    public Optional<PasteEntity> getByPasteId(String pasteId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM " + TABLE_NAME + " WHERE pasteId = ?", new PasteEntityMapper(), pasteId));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public int deleteExpiredPastes() {
        return jdbcTemplate.update(
                "DELETE FROM " + TABLE_NAME + " WHERE expire_at IS NOT NULL AND CURRENT_TIMESTAMP >= expire_at"
        );
    }
}
