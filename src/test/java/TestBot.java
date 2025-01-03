import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class TestBot {

    // 자바 시간에 객체는 static 지양.

    public static String run(String input) {
        Scanner sc = new Scanner(input + "\n");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        TestApp app = new TestApp();
        app.run();

        return out.toString();
    }
}
