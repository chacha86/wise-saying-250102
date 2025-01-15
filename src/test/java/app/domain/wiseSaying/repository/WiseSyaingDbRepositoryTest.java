package app.domain.wiseSaying.repository;

import app.domain.wiseSaying.WiseSaying;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class WiseSyaingDbRepositoryTest {

    private static WiseSayingDbRepository wiseSayingDbRepository = new WiseSayingDbRepository();

    @BeforeAll
    static void dropTable() {
        wiseSayingDbRepository.createWiseSayingTable();
    }

    @BeforeEach
    void truncateArticleTable() {
        wiseSayingDbRepository.truncateWiseSayingTable();
    }

    @Test
    @DisplayName("save, findById 테스트")
    void t1() {
        WiseSaying wiseSaying = new WiseSaying("현재를 사랑하라", "작자미상");
        wiseSaying = wiseSayingDbRepository.save(wiseSaying);

        Optional<WiseSaying> opWiseSaying = wiseSayingDbRepository.findById(wiseSaying.getId());

        WiseSaying found = opWiseSaying.orElse(null);

        assertThat(wiseSaying.getId()).isEqualTo(1);
        assertThat(found).isEqualTo(wiseSaying);

    }

    @Test
    @DisplayName("명언 삭제")
    void t2() {

        WiseSaying wiseSaying = new WiseSaying(1, "aaa", "bbb");
        wiseSaying = wiseSayingDbRepository.save(wiseSaying);

        boolean delRst = wiseSayingDbRepository.deleteById(wiseSaying.getId());
        assertThat(delRst).isTrue();
    }
}
