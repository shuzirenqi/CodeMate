package com.codemate.tool;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HostShellTest {
    @Test
    void windowsUsesNativePowerShellAndUtf8() {
        HostShell.Invocation invocation = HostShell.forOs("Windows 11", "Get-Location");

        assertEquals("powershell.exe", invocation.arguments().get(0));
        assertEquals("-NoProfile", invocation.arguments().get(2));
        assertEquals("-Command", invocation.arguments().get(4));
        assertTrue(invocation.arguments().get(5).endsWith("Get-Location"));
        assertTrue(invocation.arguments().get(5).contains("OutputEncoding"));
        assertEquals(StandardCharsets.UTF_8, invocation.outputCharset());
        assertEquals("Windows PowerShell", invocation.displayName());
    }

    @Test
    void unixUsesBashWithoutReparsingCommand() {
        HostShell.Invocation invocation = HostShell.forOs("Linux", "printf 'a b'");

        assertEquals(List.of("bash", "-c", "printf 'a b'"), invocation.arguments());
        assertEquals("Bash", invocation.displayName());
        assertFalse(HostShell.isWindows("Darwin"));
    }
}
