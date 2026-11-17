package com.javaweb.nnland.repository;

import com.javaweb.nnland.model.Row;
import java.sql.*;
import java.time.*;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class Store {
  private final JdbcTemplate jdbc;

  public Store(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  private final RowMapper<Row> mapper =
      (rs, index) -> {
        Row row = new Row();
        ResultSetMetaData m = rs.getMetaData();
        for (int i = 1; i <= m.getColumnCount(); i++)
          row.put(m.getColumnLabel(i).toLowerCase(Locale.ROOT), rs.getObject(i));
        return row;
      };

  public List<Row> list(String sql, Object... args) {
    return jdbc.query(sql, mapper, args);
  }

  public Row one(String sql, Object... args) {
    List<Row> rows = list(sql, args);
    return rows.isEmpty() ? null : rows.get(0);
  }

  public long count(String sql, Object... args) {
    Long n = jdbc.queryForObject(sql, Long.class, args);
    return n == null ? 0 : n;
  }

  public int update(String sql, Object... args) {
    return jdbc.update(sql, args);
  }

  public long insert(String sql, Object... args) {
    KeyHolder keys = new GeneratedKeyHolder();
    jdbc.update(
        c -> {
          PreparedStatement s = c.prepareStatement(sql, new String[] {"id"});
          for (int i = 0; i < args.length; i++) {
            Object a = args[i];
            s.setObject(i + 1, a);
          }
          return s;
        },
        keys);
    if (keys.getKey() == null) throw new IllegalStateException("Missing generated ID");
    return keys.getKey().longValue();
  }
}
