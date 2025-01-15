package app.domain.wiseSaying.repository;

import app.global.AppConfig;

public class RepositoryProvider {

    public static WiseSayingRepository provide() {
        if(AppConfig.isFileDb()) {
            return new WiseSayingFileRepository();
        }
        else if(AppConfig.isMysqlDb()) {
            return new WiseSayingDbRepository();
        }

        throw new RuntimeException("지원하지 않는 DB 타입");
    }
}
