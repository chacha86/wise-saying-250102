package app.standard;

import app.domain.wiseSaying.WiseSaying;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonTest {


    @Test
    @DisplayName("Map을 Json으로 변환1 - 속성이 1개")
    void t1() {

        Map<String, Object> map = Map.of("name", "홍길동");

        String jsonStr = Util.Json.mapToJson(map);

        assertThat(jsonStr)
                .isEqualTo("""
                        {
                            "name" : "홍길동"
                        }
                        """.stripIndent().trim());
    }

    @Test
    @DisplayName("Map을 Json으로 변환2 - 속성이 2개")
    void t2() {

        // Map은 순서를 보장하지 않는다.
        // 순서 보장 -> LinkedHashMap
//        Map<String, Object> map = Map.of("name", "홍길동", "home", "서울");

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "홍길동");
        map.put("home", "서울");

        String jsonStr = Util.Json.mapToJson(map);

        assertThat(jsonStr)
                .isEqualTo("""
                        {
                            "name" : "홍길동",
                            "home" : "서울"
                        }
                        """.stripIndent().trim());
    }

    @Test
    @DisplayName("Map을 Json으로 변환3 - 속성이 3개, 문자와 숫자 혼합")
    void t3() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "홍길동");
        map.put("home", "서울");
        map.put("age", 20);

        String jsonStr = Util.Json.mapToJson(map);

        assertThat(jsonStr)
                .isEqualTo("""
                        {
                            "name" : "홍길동",
                            "home" : "서울",
                            "age" : 20
                        }
                        """.stripIndent().trim());
    }

    @Test
    @DisplayName("WiseSaying을 Map으로 변환 -> Json으로 변환")
    void t4() {

        WiseSaying wiseSaying = new WiseSaying(1, "aaa", "bbb");
        Map<String, Object> wiseSayingMap = wiseSaying.toMap();

        String jsonStr = Util.Json.mapToJson(wiseSayingMap);

        assertThat(jsonStr)
                .isEqualTo("""
                        {
                            "id" : 1,
                            "content" : "aaa",
                            "author" : "bbb"
                        }
                        """.stripIndent().trim());
    }

    @Test
    @DisplayName("Map을 넘기면 Json 파일로 저장하기")
    void t5() {

        WiseSaying wiseSaying = new WiseSaying(1, "aaa", "bbb");
        Map<String, Object> wiseSayingMap = wiseSaying.toMap();

        String filePath = "test/%d.json".formatted(wiseSaying.getId());
        Util.Json.writeAsMap(filePath, wiseSayingMap);

        boolean rst = Files.exists(Path.of(filePath));
        assertThat(rst).isTrue();

        String content = Util.File.readAsString(filePath);
        assertThat(content)
                .isEqualTo("""
                        {
                            "id" : 1,
                            "content" : "aaa",
                            "author" : "bbb"
                        }
                        """.stripIndent().trim()
                );
    }

    @Test
    @DisplayName("Json 문자열을 Map으로 변환하기")
    void t6() {

        String jsonStr = """
                {
                    "id" : 1,
                    "content" : "aaa",
                    "author" : "bbb"
                }
                """;

        Map<String, Object> map = Util.Json.jsonToMap(jsonStr);

        assertThat(map)
                .hasSize(3)
                .containsEntry("id", 1)
                .containsEntry("content", "aaa")
                .containsEntry("author", "bbb");

    }

    @Test
    @DisplayName("파일명을 넘기면 Map으로 읽어오기")
    void t7() {

        String filePath = "test/%d.json".formatted(1);

        Map<String, Object> map = Util.Json.readAsMap(filePath);

        assertThat(map)
                .hasSize(3)
                .containsEntry("id", 1)
                .containsEntry("content", "aaa")
                .containsEntry("author", "bbb");
    }
}
