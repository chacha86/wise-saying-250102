package app.domain.wiseSaying.repository;

import app.domain.wiseSaying.Page;
import app.domain.wiseSaying.WiseSaying;
import app.global.AppConfig;
import app.standard.Util;
import app.standard.simpleDb.SimpleDb;
import app.standard.simpleDb.Sql;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WiseSayingDbRepository implements WiseSayingRepository {

    private static final String DB_PATH = AppConfig.getDbPath() + "/wiseSaying";
    private static final String BUILD_PATH = DB_PATH + "/build/data.json";
    private final SimpleDb simpleDb;

    public WiseSayingDbRepository() {
        this.simpleDb = new SimpleDb("localhost", "root", "lldj123414", "wiseSaying__test");
    }

    public WiseSaying save(WiseSaying wiseSaying) {
        Sql sql = simpleDb.genSql();
        sql.append("INSERT INTO wise_saying")
                .append("SET content = ?,", wiseSaying.getContent())
                .append("author = ?", wiseSaying.getAuthor());

        long generatedId = sql.insert();
        wiseSaying.setId((int) generatedId);

        return wiseSaying;
    }

    public Optional<WiseSaying> findById(int id) {

        Sql sql = simpleDb.genSql();
        sql.append("SELECT *")
                .append("FROM wise_saying")
                .append("WHERE id = ?", id);

        WiseSaying wiseSaying = sql.selectRow(WiseSaying.class);

        if (wiseSaying == null) {
            return Optional.empty();
        }

        return Optional.of(wiseSaying);
    }

    public boolean deleteById(int id) {

        int rst = simpleDb.genSql().append("DELETE FROM wise_saying")
                .append("WHERE id = ?", id)
                .delete();

        return rst > 0;
    }

    public Page<WiseSaying> findAll(int itemsPerPage, int page) {

        long totalItems = count();
        List<WiseSaying> content = simpleDb.genSql()
                .append("SELECT *")
                .append("FROM wise_saying")
                .append("LIMIT ?, ?", (long) (page - 1) * itemsPerPage, itemsPerPage)
                .selectRows(WiseSaying.class);

        return new Page<>(content, (int) totalItems, itemsPerPage, page);
    }

    public List<WiseSaying> findAll() {
        return simpleDb.genSql().append("SELECT *")
                .append("FROM wise_saying")
                .selectRows(WiseSaying.class);
    }

    public void build() {
        List<Map<String, Object>> mapList = findAll().stream()
                .map(WiseSaying::toMap)
                .toList();

        String jsonStr = Util.Json.listToJson(mapList);
        Util.File.write(BUILD_PATH, jsonStr);
    }

    public static String getBuildPath() {
        return BUILD_PATH;
    }

    public int count() {
        long cnt = simpleDb.genSql()
                .append("SELECT COUNT(*)")
                .append("FROM wise_saying")
                .selectLong();

        return (int) cnt;
    }

    public int count(String ktype, String kw) {
        long cnt = simpleDb.genSql()
                .append("SELECT COUNT(*)")
                .append("FROM wise_saying")
                .append("WHERE content LIKE CONCAT('%', ?, '%')", kw)
                .selectLong();

        return (int) cnt;
    }

    @Override
    public void makeSampleData(int cnt) {
        for(int i = 1; i <= cnt; i++) {
            save(new WiseSaying("명언" + i, "작가" + i));
        }
    }

    @Override
    public Page<WiseSaying> findByKeyword(String ktype, String kw, int itemsPerPage, int page) {

        int totalItems = count(ktype, kw); // 검색 결과 수

        List<WiseSaying> content = simpleDb.genSql()
                .append("SELECT *")
                .append("FROM wise_saying")
                .append("WHERE content LIKE CONCAT('%', ?, '%')", kw)
                .append("ORDER BY id DESC")
                .append("LIMIT ?, ?", (long) (page - 1) * itemsPerPage, itemsPerPage)
                .selectRows(WiseSaying.class);

        return new Page(content, totalItems, itemsPerPage, page);
    }

    @Override
    public void createTable() {
        simpleDb.run("DROP TABLE IF EXISTS wise_saying");

        simpleDb.run("""
                CREATE TABLE wise_saying (
                    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
                    content VARCHAR(100) NOT NULL,
                    author VARCHAR(100) NOT NULL
                )
                """);
    }

    @Override
    public void truncateTable() {
        simpleDb.run(
                "TRUNCATE wise_saying"
        );
    }

}
