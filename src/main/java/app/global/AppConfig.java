package app.global;

public class AppConfig {

    private static String mode;
    private static String dbMode;

    static {
        setDevMode();
        setMySQLDbMode();
    }

    public static void setDevMode() {
        mode = "dev";
    }
    public static void setTestMode() {
        mode = "test";
    }
    public static void setFileDbMode() {
        dbMode = "file";
    }
    public static void setMySQLDbMode() {
        dbMode = "mysql";
    }
    public static boolean isFileDb() {
        return dbMode.equals("file");
    }
    public static boolean isMysqlDb() {
        return dbMode.equals("mysql");
    }
    public static String getDbPath() {
        return "db/" + mode;
    }

}
