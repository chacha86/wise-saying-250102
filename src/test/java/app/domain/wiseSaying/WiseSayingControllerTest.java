package app.domain.wiseSaying;

import app.domain.wiseSaying.repository.RepositoryProvider;
import app.domain.wiseSaying.repository.WiseSayingFileRepository;
import app.domain.wiseSaying.repository.WiseSayingRepository;
import app.global.AppConfig;
import app.standard.TestBot;
import app.standard.Util;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

public class WiseSayingControllerTest {

    @BeforeAll
    static void beforeAll() {
        AppConfig.setTestMode();

        WiseSayingRepository repo = RepositoryProvider.provide();
        repo.createTable();
    }

    @BeforeEach
    void before() {

        WiseSayingRepository repo = RepositoryProvider.provide();
        repo.truncateTable();
    }

    @AfterEach
    void after() {

    }

    @Test
    void t1() {
        int rst = 1;
        assertThat(rst).isEqualTo(1);
    }

    @Test
    void t2() {
//        app.App app = new app.App();
//        app.run();

        // aaa가 출력되는가?
        // assertThat(result).isEqualTo("aaa");
    }

    @Test
    void t3() {
        String out = TestBot.run("");
        assertThat(out)
                .contains("명령 )")
                .contains("명언앱을 종료합니다.");

        // 출력값을 체크
    }

    @Test
    @DisplayName("명령을 여러번 입력할 수 있다.")
    void t4() {
        String out = TestBot.run("""
                등록
                현재를 사랑하라.
                작자미상
                등록
                현재를 사랑하라.
                작자미상
                종료
                """);

        // "명령 )"의 출현 횟수를 계산
        long count = out.split("명령 \\)").length - 1;
        // 검증
        assertThat(count).isEqualTo(3); // 기대하는 횟수에 따라 값 수정
    }

    @Test
    @DisplayName("앱 시작시 '== 명언 앱 ==' 출력")
    void t5() {
        String out = TestBot.run("");

        assertThat(out)
                .containsSubsequence("== 명언 앱 ==", "명언앱을 종료합니다.");

    }

    @Test
    @DisplayName("등록 - 명언 1개 입력")
    void t6() {
        String out = TestBot.run("""
                등록
                현재를 사랑하라.
                작자미상
                """);

        assertThat(out)
                .containsSubsequence("명언 : ", "작가 : ");

    }

    @Test
    @DisplayName("등록 - 명언 1개 입력, 명언 번호 출력")
    void t7() {
        String out = TestBot.run("""
                등록
                현재를 사랑하라.
                작자미상
                """);

        assertThat(out)
                .contains("1번 명언이 등록되었습니다.");

    }

    @Test
    @DisplayName("등록 - 명언 2개 입력, 명언 번호가 증가")
    void t8() {
        String out = TestBot.run("""
                등록
                현재를 사랑하라.
                작자미상
                등록
                현재를 사랑하라.
                작자미상
                등록
                현재를 사랑하라.
                작자미상
                """);

        assertThat(out)
                .contains("1번 명언이 등록되었습니다.")
                .contains("2번 명언이 등록되었습니다.")
                .contains("3번 명언이 등록되었습니다.");
    }

    @Test
    @DisplayName("목록 - 명언 2개 입력하면 입력된 명언들이 출력된다.")
    void t9() {
        String out = TestBot.run("""
                등록
                현재를 사랑하라.
                작자미상
                등록
                과거에 집착하지 마라.
                작자미상
                목록
                """);

        assertThat(out)
                .contains("번호 / 작가 / 명언")
                .contains("----------------------")
                .containsSubsequence("2 / 작자미상 / 과거에 집착하지 마라.", "1 / 작자미상 / 현재를 사랑하라.");
    }

    @Test
    @DisplayName("삭제 - id를 이용해서 해당 id의 명언을 삭제할 수 있다. 입력 : 삭제?id=1")
    void t10() {
        String out = TestBot.run("""
                등록
                현재를 사랑하라.
                작자미상
                등록
                과거에 집착하지 마라.
                작자미상
                삭제?id=1
                목록
                """);


        assertThat(out)
                .contains("2 / 작자미상 / 과거에 집착하지 마라.")
                .doesNotContain("1 / 작자미상 / 현재를 사랑하라.");
    }

    @Test
    @DisplayName("삭제 예외 처리 - 없는 id로 삭제를 시도하면 예외처리 메시지가 나온다.")
    void t11() {
        String out = TestBot.run("""
                등록
                현재를 사랑하라.
                작자미상
                등록
                과거에 집착하지 마라.
                작자미상
                삭제?id=1
                삭제?id=1
                """);

        assertThat(out)
                .contains("1번 명언은 존재하지 않습니다.");
    }

    @Test
    @DisplayName("수정 - id를 이용해서 해당 id의 명언을 수정할 수 있다. 이때 기존의 명언과 작가가 나와야 함. 입력값 - 수정?id=1")
    void t12() {
        String out = TestBot.run("""
                등록
                현재를 사랑하라.
                작자미상
                등록
                과거에 집착하지 마라.
                작자미상
                수정?id=1
                새 명언 내용
                새 작가
                목록
                """);

        assertThat(out)
                .doesNotContain("1 / 작자미상 / 현재를 사랑하라.")
                .contains("1 / 새 작가 / 새 명언 내용");
    }

    @Test
    @DisplayName("목록 - 명언이 하나도 등록되지 않았을 때")
    void t13() {
        String out = TestBot.run("""
                목록
                """);

        assertThat(out)
                .contains("등록된 명언이 없습니다.");
    }

    @Test
    @DisplayName("빌드")
    void t14() {
        String out = TestBot.run("""
                등록
                현재를 사랑하라.
                작자미상
                등록
                과거에 집착하지 마라.
                작자미상
                빌드
                """);


        boolean rst = Util.File.exists(WiseSayingFileRepository.getBuildPath());
        assertThat(rst).isTrue();

    }

    @Test
    @DisplayName("검색 - 검색 타입과 키워드를 입력받아 키워드를 포함하는 명언을 출력한다.")
    void t15() {
        String out = TestBot.run("""
                등록
                현재를 사랑하라.
                작자미상
                등록
                과거에 집착하지 마라.
                작자미상
                목록?keywordType=content&keyword=과거
                """);

        assertThat(out)
                .contains("2 / 작자미상 / 과거에 집착하지 마라.")
                .doesNotContain("1 / 작자미상 / 현재를 사랑하라.");
    }

    @Test
    @DisplayName("페이징 - 샘플데이터 생성")
    void t16() {

        TestBot.makeSample(10);

        String out = TestBot.run("""
                목록
                """);

        assertThat(out)
                .contains("10 / 작가10 / 명언10");
    }

    @Test
    @DisplayName("페이징 - 페이징 UI 출력")
    void t17() {

        TestBot.makeSample(10);

        String out = TestBot.run("""
                목록?page=2
                """);

        assertThat(out)
                .contains("1 / [2]");

    }

    @Test
    @DisplayName("페이징 - 페이징 UI 출력, 샘플 개수에 맞는 페이지 출력")
    void t18() {

        TestBot.makeSample(30);

        String out = TestBot.run("""
                목록?page=4
                """);

        assertThat(out)
                .contains("1 / 2 / 3 / [4] / 5 / 6");

    }

    @Test
    @DisplayName("페이징 - 실제 페이제 맞는 데이터 가져오기1")
    void t19() {
        TestBot.makeSample(15);

        // 1 / 작가1 / 명언1
        // 2 / 작가2 / 명언2
        // 3 / 작가3 / 명언3
        // ....
        // 15 / 작가15 / 명언15

        // 1, 10, 11, 12, 13, 14, 15

        // 15, 14, 13, 12, 11  - 1 페이지
        // 10, 1 - 2 페이지


        String out = TestBot.run("""
                목록?keywordType=content&keyword=1
                """);

        assertThat(out)
                .containsSubsequence("15 / 작가15 / 명언15", "14 / 작가14 / 명언14")
                .doesNotContain("10 / 작가10 / 명언10");

        assertThat(out)
                .contains("[1] / 2");

    }

    @Test
    @DisplayName("페이징 - 실제 페이제 맞는 데이터 가져오기2")
    void t20() {
        TestBot.makeSample(15);
        String out = TestBot.run("""
                목록?keywordType=content&keyword=1&page=2
                """);

        assertThat(out)
                .containsSubsequence("10 / 작가10 / 명언10", "1 / 작가1 / 명언1")
                .doesNotContain("11 / 작가11 / 명언11");

        assertThat(out)
                .contains("1 / [2]");
    }

    @Test
    @DisplayName("페이징 - 실제 페이제 맞는 데이터 가져오기3")
    void t21() {
        TestBot.makeSample(30);
        String out = TestBot.run("""
                목록?page=3
                """);

        assertThat(out)
                .contains("18 / 작가18 / 명언18")
                .contains("1 / 2 / [3] / 4 / 5 / 6");
    }

    @Test
    @DisplayName("검색 UI 출력")
    void t22() {

        String out = TestBot.run("""
                등록
                현재를 사랑하라.
                작자미상
                등록
                과거에 집착하지 마라.
                작자미상
                목록?keywordType=content&keyword=과거
                """);

        assertThat(out)
                .contains("검색타입 : content")
                .contains("검색어 : 과거");
    }
}
