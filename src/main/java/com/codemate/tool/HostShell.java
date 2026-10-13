package com.codemate.tool;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

/** 根据宿主操作系统构造 execute_command 的命令行。 */
final class HostShell {
    private static final String POWERSHELL_UTF8_PREFIX =
            "[Console]::OutputEncoding=[System.Text.UTF8Encoding]::new($false);"
                    + "$OutputEncoding=[Console]::OutputEncoding;";

    private HostShell() {
    }

    static Invocation current(String command) {
        return forOs(System.getProperty("os.name", ""), command);
    }

    static Invocation forOs(String osName, String command) {
        if (isWindows(osName)) {
            return new Invocation(
                    List.of("powershell.exe", "-NoLogo", "-NoProfile", "-NonInteractive", "-Command",
                            POWERSHELL_UTF8_PREFIX + command),
                    StandardCharsets.UTF_8,
                    "Windows PowerShell");
        }
        return new Invocation(List.of("bash", "-c", command), Charset.defaultCharset(), "Bash");
    }

    static boolean isWindows(String osName) {
        return osName != null && osName.toLowerCase(Locale.ROOT).startsWith("windows");
    }

    static String toolDescription() {
        String shell = isWindows(System.getProperty("os.name", "")) ? "Windows PowerShell" : "Bash";
        return "在当前项目目录中执行短时 " + shell
                + " 命令（默认 60 秒超时，不允许全盘扫描；必须使用当前 Shell 的语法）";
    }

    record Invocation(List<String> arguments, Charset outputCharset, String displayName) {
    }
}
