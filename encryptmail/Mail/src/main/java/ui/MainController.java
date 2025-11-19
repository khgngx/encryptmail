package ui;

public class MainController {
    private static String email;
    private static String password;
    private static String smtpHost;
    private static String imapHost;
    private static int smtpPort;
    private static int imapPort;

    public static void setSession(String e, String p,
                                  String sHost, String iHost,
                                  int sPort, int iPort) {
        email = e;
        password = p;
        smtpHost = sHost;
        imapHost = iHost;
        smtpPort = sPort;
        imapPort = iPort;
    }

    public static String getEmail() { return email; }
    public static String getPassword() { return password; }
    public static String getSmtpHost() { return smtpHost; }
    public static String getImapHost() { return imapHost; }
    public static int getSmtpPort() { return smtpPort; }
    public static int getImapPort() { return imapPort; }
}
