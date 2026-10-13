package com.codemate.cli;

import org.jline.reader.Candidate;
import org.jline.reader.ParsedLine;
import org.junit.jupiter.api.Test;
import com.codemate.mcp.resources.McpResourceDescriptor;
import com.codemate.skill.Skill;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CodeMateCompleterTest {

    @Test
    void completesManagementProvidersAndNewlyRegisteredModels() {
        var config = new com.codemate.config.CodeMateConfig();
        var settings = new com.codemate.config.CodeMateConfig.ProviderConfig();
        settings.getModels().put("next", com.codemate.llm.ModelProfile.discovered());
        config.getProviders().put("deepseek", settings);
        CodeMateCompleter completer = new CodeMateCompleter(List::of, List::of, () -> config);
        List<Candidate> models = new ArrayList<>();
        completer.complete(null, parsed("/model deepseek/n", "deepseek/n"), models);
        assertTrue(models.stream().anyMatch(c -> c.value().equals("deepseek/next")));
        List<Candidate> providers = new ArrayList<>();
        completer.complete(null, parsed("/model refresh dee", "dee"), providers);
        assertTrue(providers.stream().anyMatch(c -> c.value().equals("deepseek")));
    }

    @Test
    void suggestsDeepSeekFlashModel() {
        CodeMateCompleter completer = new CodeMateCompleter(List::of);
        List<Candidate> candidates = new ArrayList<>();
        completer.complete(null, parsed("/model deep", "deep"), candidates);
        assertTrue(candidates.stream().anyMatch(c -> c.value().equals("deepseek-flash")));
        assertTrue(candidates.stream().anyMatch(c -> c.value().equals("deepseek")));
    }

    @Test
    void suggestsSlashCommandsWhenInputStartsWithSlash() {
        CodeMateCompleter completer = new CodeMateCompleter(List::of);
        List<Candidate> candidates = new ArrayList<>();

        completer.complete(null, parsed("/", "/"), candidates);

        assertTrue(candidates.stream().anyMatch(c -> c.displ().equals("/model")));
        assertTrue(candidates.stream().anyMatch(c -> c.displ().equals("/browser connect")));
        assertTrue(candidates.stream().anyMatch(c -> c.displ().equals("/search <查询>")));
    }

    @Test
    void completesSubCommandWithoutDuplicatingPrefix() {
        CodeMateCompleter completer = new CodeMateCompleter(List::of);
        List<Candidate> candidates = new ArrayList<>();

        completer.complete(null, parsed("/mcp r", "r"), candidates);

        Candidate restart = candidates.stream()
                .filter(c -> c.displ().equals("/mcp restart <name>"))
                .findFirst()
                .orElseThrow();
        assertEquals("restart ", restart.value());
    }

    @Test
    void ignoresNormalWords() {
        CodeMateCompleter completer = new CodeMateCompleter(List::of);
        List<Candidate> candidates = new ArrayList<>();

        completer.complete(null, parsed("hello", "hello"), candidates);

        assertTrue(candidates.isEmpty());
    }

    @Test
    void completesModelProviderNames() {
        CodeMateCompleter completer = new CodeMateCompleter(List::of);
        List<Candidate> candidates = new ArrayList<>();

        completer.complete(null, parsed("/model st", "st"), candidates);

        assertTrue(candidates.stream().anyMatch(c -> c.value().equals("step")));
    }

    @Test
    void completesHunyuanModelNames() {
        CodeMateCompleter completer = new CodeMateCompleter(List::of);
        List<Candidate> providerCandidates = new ArrayList<>();
        List<Candidate> modelCandidates = new ArrayList<>();

        completer.complete(null, parsed("/model hun", "hun"), providerCandidates);
        completer.complete(null, parsed("/model hy", "hy"), modelCandidates);

        assertTrue(providerCandidates.stream().anyMatch(c -> c.value().equals("hunyuan")));
        assertTrue(modelCandidates.stream().anyMatch(c -> c.value().equals("hy4-preview")));
    }

    @Test
    void completesConfigProviderCommand() {
        CodeMateCompleter completer = new CodeMateCompleter(List::of);
        List<Candidate> candidates = new ArrayList<>();

        completer.complete(null, parsed("/config provider fr", "fr"), candidates);

        assertTrue(candidates.stream().anyMatch(c -> c.value().equals("freellmapi ")));
    }

    @Test
    void completesAgnesProviderCommand() {
        CodeMateCompleter completer = new CodeMateCompleter(List::of);
        List<Candidate> candidates = new ArrayList<>();

        completer.complete(null, parsed("/config provider ag", "ag"), candidates);

        assertTrue(candidates.stream().anyMatch(c -> c.value().equals("agnes ")));
    }

    @Test
    void completesXfyunProviderCommand() {
        CodeMateCompleter completer = new CodeMateCompleter(List::of);
        List<Candidate> candidates = new ArrayList<>();

        completer.complete(null, parsed("/config provider xf", "xf"), candidates);

        assertTrue(candidates.stream().anyMatch(c -> c.value().equals("xfyun ")));
    }

    @Test
    void completesHunyuanProviderCommand() {
        CodeMateCompleter completer = new CodeMateCompleter(List::of);
        List<Candidate> candidates = new ArrayList<>();

        completer.complete(null, parsed("/config provider hu", "hu"), candidates);

        assertTrue(candidates.stream().anyMatch(c -> c.value().equals("hunyuan ")));
    }

    @Test
    void completesMcpServerNamesFromResources() {
        CodeMateCompleter completer = new CodeMateCompleter(() -> List.of(
                new McpResourceDescriptor("chrome-devtools", "file:///a", "a", "", "", "text/plain", null),
                new McpResourceDescriptor("filesystem", "file:///b", "b", "", "", "text/plain", null)
        ));
        List<Candidate> candidates = new ArrayList<>();

        completer.complete(null, parsed("/mcp logs ch", "ch"), candidates);

        Candidate candidate = candidates.stream()
                .filter(c -> c.value().equals("chrome-devtools"))
                .findFirst()
                .orElseThrow();
        assertEquals("MCP server", candidate.group());
    }

    @Test
    void completesSkillNames() {
        CodeMateCompleter completer = new CodeMateCompleter(List::of, () -> List.of(
                skill("web-access", "浏览器和联网策略"),
                skill("ai-article", "文章写作")
        ));
        List<Candidate> candidates = new ArrayList<>();

        completer.complete(null, parsed("/skill show web", "web"), candidates);

        Candidate candidate = candidates.stream()
                .filter(c -> c.value().equals("web-access"))
                .findFirst()
                .orElseThrow();
        assertEquals("浏览器和联网策略", candidate.descr());
    }

    @Test
    void completesSkillSubCommands() {
        CodeMateCompleter completer = new CodeMateCompleter(List::of);
        List<Candidate> candidates = new ArrayList<>();

        completer.complete(null, parsed("/skill sh", "sh"), candidates);

        assertTrue(candidates.stream().anyMatch(c -> c.value().equals("show ")));
    }

    @Test
    void completesTaskSubCommands() {
        CodeMateCompleter completer = new CodeMateCompleter(List::of);
        List<Candidate> candidates = new ArrayList<>();

        completer.complete(null, parsed("/task ca", "ca"), candidates);

        assertTrue(candidates.stream().anyMatch(c -> c.value().equals("cancel ")));
    }

    @Test
    void completesLocalPathMentions() {
        CodeMateCompleter completer = new CodeMateCompleter(List::of);
        List<Candidate> candidates = new ArrayList<>();

        completer.complete(null, parsed("@pom", "@pom"), candidates);

        assertTrue(candidates.stream().anyMatch(c -> c.value().equals("@pom.xml")));
    }

    @Test
    void completesImagePathMentionsWithTokenPrefix() {
        CodeMateCompleter completer = new CodeMateCompleter(List::of);
        List<Candidate> candidates = new ArrayList<>();

        completer.complete(null, parsed("@image:pom", "@image:pom"), candidates);

        assertTrue(candidates.stream().anyMatch(c -> c.value().equals("@image:pom.xml")));
    }

    private static Skill skill(String name, String description) {
        return new Skill(name, description, "1.0.0", null, List.of(), Skill.Source.USER, "body", null, null);
    }

    private static ParsedLine parsed(String line, String word) {
        return new ParsedLine() {
            @Override public String word() { return word; }
            @Override public int wordCursor() { return word.length(); }
            @Override public int wordIndex() { return 0; }
            @Override public List<String> words() { return List.of(word); }
            @Override public String line() { return line; }
            @Override public int cursor() { return line.length(); }
        };
    }
}
