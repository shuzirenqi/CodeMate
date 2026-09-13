# CodeMate

基于 Java 的终端 AI 编程助手，通过自然语言驱动文件操作、命令执行和项目创建。

当前版本：**v0.1.0 — 基础 ReAct 与工具调用**。

## 当前能力

- ReAct 执行循环：模型选择工具，程序执行并回传结果，模型继续判断下一步。
- 会话内对话历史：保留用户消息、模型回复与工具结果；支持手动清空。
- 5 个内置工具：读取文件、写入文件、列出目录、执行 Shell 命令、创建项目骨架。
- GLM 模型接入：使用 OkHttp 发送同步请求，Jackson 处理消息与工具调用参数。
- 每次用户任务最多执行 10 轮模型调用，避免循环无限持续。

## 快速开始

需要 Java 17+、Maven，以及可访问代码中配置接口的 GLM API Key。执行 Shell 工具还需要 PATH 中存在可用的 `bash`；Windows 可使用 Git Bash 环境。

在项目根目录执行：

```powershell
Copy-Item .env.example .env
# 编辑 .env，填写自己的 GLM_API_KEY
mvn clean package
java -jar target/codemate-0.1.0.jar
```

也可以通过环境变量提供 `GLM_API_KEY`。当前实现先检查工作目录的 `.env`，不存在时再检查用户主目录的 `.env`，两者均不存在才读取环境变量。若已存在的 `.env` 没有有效 Key，需要补齐配置；本版本不会继续向后回退。

当前模型固定为 `glm-5.1`，接口固定为 `https://open.bigmodel.cn/api/coding/paas/v4/chat/completions`，配置位置在 `GLMClient`。`.env` 已加入 Git 忽略规则。

## 使用示例

```text
列出当前目录的文件
读取 pom.xml，解释项目依赖
创建一个名为 hello-demo 的 Java 项目骨架
```

交互命令：

| 输入 | 行为 |
| --- | --- |
| `clear` | 清空当前对话，保留系统提示词 |
| `exit` / `quit` | 退出程序 |

文件操作和命令默认基于启动时的工作目录；工具也支持模型传入的绝对文件路径。

## 执行过程

用户输入进入对话历史后，Agent 将历史消息和工具定义发送给模型。如果响应包含工具调用，程序依次执行工具，并通过 `tool_call_id` 将结果与调用关联后回传；如果没有工具调用，则输出回复并结束本次任务。

本版本使用同步请求和串行工具执行，不提供流式输出。

## 代码结构

```text
src/main/java/com/codemate/
├── cli/Main.java           终端入口、配置加载、交互循环
├── agent/Agent.java        ReAct 循环与消息历史
├── llm/GLMClient.java      模型请求、响应解析、消息类型
└── tool/ToolRegistry.java  工具定义、参数解析、执行分发
```

技术栈：Java 17、Maven、Jackson、OkHttp、SLF4J。

## 当前边界

- 对话只保存在进程内，退出后不会恢复；尚未实现长期记忆与摘要压缩。
- 尚未加入 Plan、RAG、Multi-Agent、MCP 和人工审批。
- 文件写入和命令执行直接生效，没有沙箱和命令级超时，适合在独立的练习目录中运行短时任务。
- 工具参数与返回片段会显示在终端；模型配置目前固定在代码中。
- 本阶段尚无自动化测试用例，构建成功不代表真实模型与工具联调已通过。

## 版本记录

各阶段的来源映射、范围和验证方式见 [CHANGELOG.md](CHANGELOG.md)。下一阶段加入 Plan-and-Execute 与 DAG 任务依赖。

## 来源说明

本阶段代码基于 [PaiCLI](https://gitcode.com/javabetter/paicli) 的基础 ReAct 实现整理，源提交为 `e2b8df4`（原提交作者：沉默王二）。CodeMate 在此基础上统一项目命名、包结构和版本说明，后续改动通过独立提交记录。

当前导入的源快照未包含许可证文件，本仓库不额外声明对原代码的许可授权；公开分发前需确认对应授权。
