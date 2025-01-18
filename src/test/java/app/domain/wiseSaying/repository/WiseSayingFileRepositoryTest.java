package app.domain.wiseSaying.repository;

import app.domain.wiseSaying.Page;
import app.domain.wiseSaying.WiseSaying;
import app.global.AppConfig;
import app.standard.Util;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class WiseSayingFileRepositoryTest {

    private WiseSayingFileRepository wiseSayingRepository = new WiseSayingFileRepository();

    @BeforeAll
    static void beforeAll() {
        AppConfig.setTestMode();
    }

    @BeforeEach
    void beforeEach() {
        wiseSayingRepository.createTable();
    }

    @AfterEach
    void afterEach() {
        wiseSayingRepository.truncateTable();
    }

    @Test
    @DisplayName("명언 저장")
    void t1() {

        WiseSaying wiseSaying = new WiseSaying(1, "aaa", "bbb");

        wiseSayingRepository.save(wiseSaying);

        String filePath = WiseSayingFileRepository.getFilePath(wiseSaying.getId());

        boolean rst = Files.exists(Path.of(filePath));
        assertThat(rst).isTrue();

        Map<String, Object> map = Util.Json.readAsMap(filePath);
        WiseSaying restoredWiseSaying = WiseSaying.fromMap(map);

        System.out.println(wiseSaying);
        System.out.println(restoredWiseSaying);

        assertThat(wiseSaying).isEqualTo(restoredWiseSaying);

    }

    @Test
    @DisplayName("명언 삭제")
    void t2() {

        WiseSaying wiseSaying = new WiseSaying(1, "aaa", "bbb");

        wiseSayingRepository.save(wiseSaying);
        String filePath = WiseSayingFileRepository.getFilePath(wiseSaying.getId());

        boolean delRst = wiseSayingRepository.deleteById(1);

        boolean rst = Files.exists(Path.of(filePath));
        assertThat(rst).isFalse();
        assertThat(delRst).isTrue();
    }

    @Test
    @DisplayName("아이디로 해당 명언 가져오기")
    void t3() {

        WiseSaying wiseSaying = new WiseSaying(1, "aaa", "bbb");
        wiseSayingRepository.save(wiseSaying);

        String filePath = WiseSayingFileRepository.getFilePath(wiseSaying.getId());
        assertThat(Files.exists(Path.of(filePath))).isTrue();

        Optional<WiseSaying> opWiseSaying = wiseSayingRepository.findById(1);
        WiseSaying foundWiseSaying = opWiseSaying.orElse(null);

        assertThat(foundWiseSaying).isNotNull();
        assertThat(foundWiseSaying).isEqualTo(wiseSaying);

    }

    @Test
    @DisplayName("모든 명언 가져오기")
    void t4() {

        WiseSaying wiseSaying1 = new WiseSaying(1, "aaa1", "bbb1");
        WiseSaying wiseSaying2 = new WiseSaying(2, "aaa2", "bbb2");
        WiseSaying wiseSaying3 = new WiseSaying(3, "aaa3", "bbb3");

        wiseSayingRepository.save(wiseSaying1);
        wiseSayingRepository.save(wiseSaying2);
        wiseSayingRepository.save(wiseSaying3);

        List<WiseSaying> wiseSayings = wiseSayingRepository.findAll();

        assertThat(wiseSayings).hasSize(3);
        assertThat(wiseSayings).contains(wiseSaying1, wiseSaying2, wiseSaying3);

    }

    @Test
    @DisplayName("lastId 가져오기")
    void t5() {

        WiseSaying wiseSaying1 = new WiseSaying("aaa1", "bbb1");
        wiseSayingRepository.save(wiseSaying1);

        WiseSaying wiseSaying2 = new WiseSaying("aaa1", "bbb1");
        wiseSayingRepository.save(wiseSaying2);


        int lastId = wiseSayingRepository.getLastId();

        assertThat(lastId).isEqualTo(wiseSaying2.getId());

    }

    @Test
    @DisplayName("build 하면 모든 명언을 모아 하나의 파일로 저장")
    void t6() {

        WiseSaying wiseSaying1 = new WiseSaying("aaa", "bbb");
        wiseSayingRepository.save(wiseSaying1);

        WiseSaying wiseSaying2 = new WiseSaying("ccc", "ddd");
        wiseSayingRepository.save(wiseSaying2);

        wiseSayingRepository.build();

        String jsonStr = Util.File.readAsString(WiseSayingFileRepository.getBuildPath());

        assertThat(jsonStr)
                .isEqualTo("""
                        [
                            {
                                "id" : 1,
                                "content" : "aaa",
                                "author" : "bbb"
                            },
                            {
                                "id" : 2,
                                "content" : "ccc",
                                "author" : "ddd"
                            }
                        ]
                        """.stripIndent().trim());

    }

    @Test
    @DisplayName("현재 저장된 명언의 개수를 가져오는 count")
    void t7() {

        WiseSaying wiseSaying1 = new WiseSaying("aaa", "bbb");
        wiseSayingRepository.save(wiseSaying1);

        WiseSaying wiseSaying2 = new WiseSaying("ccc", "ddd");
        wiseSayingRepository.save(wiseSaying2);


        int count = wiseSayingRepository.count();

        assertThat(count)
                .isEqualTo(2);

    }

    @Test
    @DisplayName("페이지 정보와 결과 가져오기")
    void t8() {

        WiseSaying wiseSaying1 = new WiseSaying("aaa", "bbb");
        wiseSayingRepository.save(wiseSaying1);

        WiseSaying wiseSaying2 = new WiseSaying("ccc", "ddd");
        wiseSayingRepository.save(wiseSaying2);

        WiseSaying wiseSaying3 = new WiseSaying("eee", "fff");
        wiseSayingRepository.save(wiseSaying3);

        int itemsPerPage = 5;
        int page = 1;
        Page<WiseSaying> pageContent = wiseSayingRepository.findAll(itemsPerPage, page);

        List<WiseSaying> wiseSayings = pageContent.getContent();
        int totalItems = pageContent.getTotalItems();
        int totalPages = pageContent.getTotalPages();

        assertThat(totalItems)
                .isEqualTo(3);

        assertThat(totalPages)
                .isEqualTo(1);
    }
}
