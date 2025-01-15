package app.standard.simpleDb;

import app.standard.Util;
import lombok.Setter;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class SimpleDb {
    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private Map<String, Connection> connections;

    @Setter
    private boolean devMode = false;

    // 생성자: 데이터베이스 연결 정보 초기화
    public SimpleDb(String host, String user, String password, String dbName) {
        this.dbUrl = "jdbc:mysql://" + host + ":3306/" + dbName; // JDBC URL
        this.dbUser = user;                                    // 사용자 이름
        this.dbPassword = password;                            // 비밀번호

        connections = new HashMap<>();

    }

    private Connection getCurrentThreadConnection() {

        try {
            Connection conn = connections.get(Thread.currentThread().getName());

            if (conn == null) {
                Connection currentThreadConn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                connections.put(Thread.currentThread().getName(), currentThreadConn);

                return currentThreadConn;
            }

            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public long insert(String sql, List<Object> params) {
        return _run(sql, Long.class, params);
    }

    public int update(String sql, List<Object> params) {
        return _run(sql, Integer.class, params);
    }

    public int delete(String sql, List<Object> params) {
        return _run(sql, Integer.class, params);
    }

    public Map<String, Object> selectRow(String sql, List<Object> params) {
        return _run(sql, Map.class, params);
    }

    public <T> T selectRow(String sql, List<Object> params, Class<T> cls) {
        List<T> rows = selectRows(sql, params, cls);
        if(rows.isEmpty()) {
            return null;
        }
        return rows.getFirst();
    }

    public List<Map<String, Object>> selectRows(String sql, List<Object> params) {
        return _run(sql, List.class, params);
    }

    public <T> List<T> selectRows(String sql, List<Object> params, Class<T> cls) {

        return selectRows(sql, params).stream()
                .map(map -> Util.Mapper.mapToObj(map, cls))
                .toList();
    }

    public String selectString(String sql, List<Object> params) {
        return _run(sql, String.class, params);
    }

    public Long selectLong(String sql, List<Object> params) {
        return _run(sql, Long.class, params);
    }

    public boolean selectBoolean(String sql, List<Object> params) {
        return _run(sql, Boolean.class, params);
    }

    public LocalDateTime selectDatetime(String sql, List<Object> params) {
        return _run(sql, LocalDateTime.class, params);
    }

    public int run(String sql, Object... params) {
        return _run(sql, Integer.class, Arrays.stream(params).toList());
    }

    public Sql genSql() {
        return new Sql(this);
    }

    private <T> T _run(String sql, Class<T> cls, List<Object> params) {
        System.out.println("sql : " + sql);

        Connection connection = getCurrentThreadConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParams(stmt, params);

            if (sql.startsWith("SELECT")) {
                ResultSet rs = stmt.executeQuery(); // 실제 반영된 로우 수. insert, update, delete
                return parseResultSet(rs, cls);
            }

            if (sql.startsWith("INSERT")) {
                if (cls == Long.class) {

                    stmt.executeUpdate();
                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        return cls.cast(rs.getLong(1));
                    }
                }
            }

            return cls.cast(stmt.executeUpdate());

        } catch (SQLException e) {
            throw new RuntimeException("SQL 실행 실패: " + e.getMessage());
        }
    }

    private <T> T parseResultSet(ResultSet rs, Class<T> cls) throws SQLException {
        if (cls == Boolean.class) {
            rs.next();
            return cls.cast((rs.getBoolean(1)));
        } else if (cls == String.class) {
            rs.next();
            return cls.cast(rs.getString(1));
        } else if (cls == Long.class) {
            rs.next();
            return cls.cast(rs.getLong(1));
        } else if (cls == LocalDateTime.class) {
            rs.next();
            return cls.cast(rs.getTimestamp(1).toLocalDateTime());
        } else if (cls == Map.class) {
            rs.next();
            return cls.cast(rsRowToMap(rs));

        } else if (cls == List.class) {
            List<Map<String, Object>> rows = new ArrayList<>();

            while (rs.next()) {
                rows.add(rsRowToMap(rs));
            }

            return cls.cast(rows);
        }

        throw new RuntimeException();
    }

    private Map<String, Object> rsRowToMap(ResultSet rs) throws SQLException {

        Map<String, Object> row = new HashMap<>();

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            String cname = metaData.getColumnName(i);
            row.put(cname, rs.getObject(i));
        }

        return row;
    }

    private void setParams(PreparedStatement stmt, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            stmt.setObject(i + 1, params.get(i)); // '?' 위치에 값 설정
        }
    }

    public List<Long> selectLongs(String sql, List<Object> params) {
        List<Map<String, Object>> maps = selectRows(sql, params);

        return maps.stream()
                .map(map -> (Long) map.values().iterator().next())
                .toList();
    }

    public void close() {

        try {
            Connection conn = getCurrentThreadConnection();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void startTransaction() {
        try {
            getCurrentThreadConnection().setAutoCommit(false); // auto commit 끄기
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void rollback() {
        try {
            getCurrentThreadConnection().rollback();
            getCurrentThreadConnection().setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void commit() {
        try {
            getCurrentThreadConnection().commit();
            getCurrentThreadConnection().setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}