package app.domain.wiseSaying.repository;
import app.standard.simpleDb.SimpleDb;

public class WiseSayingDbRepository {

    private final SimpleDb simpleDb;

    public WiseSayingDbRepository() {
        this.simpleDb = new SimpleDb("localhost", "root", "lldj123414", "wiseSaying__test");
    }
}
