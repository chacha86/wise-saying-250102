package app.standard.simpleDb;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Sql {

    private final SimpleDb simpleDb;
    private final List<Object> params;
    private final StringBuilder sqlBuilder;

    public Sql(SimpleDb simpleDb) {
        this.sqlBuilder = new StringBuilder();
        this.simpleDb = simpleDb;
        this.params = new ArrayList<>();
    }

    public Sql append(String sqlLine) {
        this.sqlBuilder.append(sqlLine);
        this.sqlBuilder.append(" ");
        return this;
    }

    public Sql append(String sqlLine, Object... args) {
        this.params.addAll(Arrays.stream(args).toList());
        this.sqlBuilder.append(sqlLine);
        this.sqlBuilder.append(" ");
        return this;
    }

    public long insert() {
        return simpleDb.insert(sqlBuilder.toString(), params);
    }

    public int update() {
        return simpleDb.update(sqlBuilder.toString(), params);
    }

    public int delete() {
        return simpleDb.delete(sqlBuilder.toString(), params);
    }

    public <T> List<T> selectRows(Class<T> cls) {
        return simpleDb.selectRows(sqlBuilder.toString(), params, cls);
    }

    public List<Map<String, Object>> selectRows() {
        return simpleDb.selectRows(sqlBuilder.toString(), params);
    }

    public Map<String, Object> selectRow() {
        return simpleDb.selectRow(sqlBuilder.toString(), params);
    }

    public <T> T selectRow(Class<T> cls) {
        return simpleDb.selectRow(sqlBuilder.toString(), params, cls);
    }

    public LocalDateTime selectDatetime() {
        return simpleDb.selectDatetime(sqlBuilder.toString(), params);
    }

    public Long selectLong() {
        return simpleDb.selectLong(sqlBuilder.toString(), params);
    }

    public String selectString() {
        return simpleDb.selectString(sqlBuilder.toString(), params);
    }

    public Boolean selectBoolean() {
        return simpleDb.selectBoolean(sqlBuilder.toString(), params);
    }

    public Sql appendIn(String sql, Object... args) {
        String inCluase = Arrays.stream(args)
                .map(o -> "?")
                .collect(Collectors.joining(", "));

        String replacedSql = sql.replaceAll("\\?", inCluase);
        this.params.addAll(Arrays.stream(args).toList());
        this.sqlBuilder.append(replacedSql);

        return this;
    }

    public List<Long> selectLongs() {
        return simpleDb.selectLongs(sqlBuilder.toString(), params);
    }
}
