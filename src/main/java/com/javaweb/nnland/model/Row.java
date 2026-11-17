package com.javaweb.nnland.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

/** A detached, explicitly selected database record; never binds HTTP input to SQL identifiers. */
public class Row extends LinkedHashMap<String, Object> {
  public String str(String key) {
    Object v = get(key);
    return v == null ? "" : String.valueOf(v);
  }

  public long num(String key) {
    Object v = get(key);
    return v == null ? 0 : ((Number) v).longValue();
  }

  public Long id() {
    return num("id");
  }

  public BigDecimal decimal(String key) {
    Object v = get(key);
    return v == null ? BigDecimal.ZERO : new BigDecimal(v.toString());
  }

  public boolean bool(String key) {
    Object v = get(key);
    return Boolean.TRUE.equals(v) || (v instanceof Number && ((Number) v).intValue() != 0);
  }

  public LocalDate date(String key) {
    Object v = get(key);
    if (v == null) return null;
    if (v instanceof Date) return ((Date) v).toLocalDate();
    if (v instanceof LocalDate) return (LocalDate) v;
    return LocalDate.parse(v.toString().substring(0, 10));
  }

  public LocalDateTime time(String key) {
    Object v = get(key);
    if (v == null) return null;
    if (v instanceof Timestamp) return ((Timestamp) v).toLocalDateTime();
    if (v instanceof LocalDateTime) return (LocalDateTime) v;
    return LocalDateTime.parse(v.toString().replace(' ', 'T'));
  }

  public Row with(String key, Object value) {
    put(key, value);
    return this;
  }
}
