package app.domain.wiseSaying.repository;

import app.domain.wiseSaying.WiseSaying;
import app.standard.simpleDb.SimpleDb;
import app.standard.simpleDb.Sql;

import java.util.Optional;

public class WiseSayingDbRepository {

    private final SimpleDb simpleDb;

    public WiseSayingDbRepository() {
        this.simpleDb = new SimpleDb("localhost", "root", "lldj123414", "wiseSaying__test");
    }

    public void createWiseSayingTable() {
        simpleDb.run("DROP TABLE IF EXISTS wise_saying");

        simpleDb.run("""
                CREATE TABLE wise_saying (
                    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
                    content VARCHAR(100) NOT NULL,
                    author VARCHAR(100) NOT NULL
                )
                """);
    }

    public void truncateWiseSayingTable() {
        simpleDb.run(
                "TRUNCATE wise_saying"
        );
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
}
