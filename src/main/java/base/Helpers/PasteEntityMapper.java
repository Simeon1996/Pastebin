package base.Helpers;

import base.PasteEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PasteEntityMapper implements RowMapper<PasteEntity> {
    @Override
    public PasteEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return PasteEntityFactory.create(
                rs.getLong("id"),
                rs.getString("pasteId"),
                rs.getString("content"),
                rs.getString("ip"),
                rs.getString("title"),
                null
        );
    }
}
