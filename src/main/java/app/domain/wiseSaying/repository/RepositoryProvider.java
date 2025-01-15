package app.domain.wiseSaying.repository;

import app.global.AppConfig;

public class RepositoryProvider {

    public static WiseSayingRepository provide() {
//        if(AppConfig.isFileDb()) {
//            return new WiseSayingFileRepository();
//        }
//        else {
//            return new WiseSayingMemRepository();
//        }

        return new WiseSayingDbRepository();
    }
}
