# 版本记录

## v16.1.0 — 同步最新完整 Agent 能力

- 将完整实现更新到基线提交 `78786aa`，并统一为 CodeMate 包名、配置前缀、命令和产品文案。
- 补齐 Skill、inline TUI、LSP 诊断、Git Side-History、Prompt 分层、Runtime API、图片输入和微信 iLink 文本通道等后续阶段能力。
- 同步多模型目录、MCP 稳定性增强、RAG 增量索引、工具参数校验、长期记忆改进和 Native AgentBench 评测框架。

验证结果：`mvn clean package` 通过；核心模块 257 个测试通过（6 个平台条件测试跳过）；TUI 冒烟 135 个测试全部通过。`mvn test -Pquick` 中 Native AgentBench 的 Windows/POSIX 环境相关用例仍失败，未调用真实模型、未运行 Docker，也未继续正式评测。

## v0.15.0 — CDP 会话复用与登录态浏览器访问

- 新增浏览器会话管理，支持查看状态、连接共享 Chrome、断开并切回隔离模式，以及查看共享会话的标签页。
- 新增 CDP 连通性检查和敏感页面策略；共享模式下只允许关闭 CodeMate 创建的页面。
- 浏览器相关工具默认进入 HITL 审批和审计链，审计增加浏览器模式与页面信息。

验证结果：待本阶段完整构建后补充。

## v0.14.0 — 长上下文与浏览器 MCP

- 新增 `ContextProfile`，根据模型上下文窗口选择 Memory 压缩、RAG Top-K 与 Agent Token 预算策略，并统一格式化 Token 使用量。
- LLM Client 声明上下文窗口与 Prompt Cache 能力；GLM、DeepSeek 适配对应模型窗口。
- 首次启动时可生成 Chrome DevTools MCP 默认配置，浏览器 MCP 工具自动注册并纳入 HITL 审批与审计。
- HITL 增加 MCP Server 维度的会话放行选项；非文本 MCP 返回提供 DOM snapshot 等回退引导。

验证结果：`mvn clean package` 通过，360 个测试全部通过（Windows 环境有 6 个 Stdio Transport 测试按条件跳过）。

## v0.13.0 — MCP 高级能力

- 新增 MCP resources 缓存与双轨访问：自动注册资源列表/读取虚拟工具，也支持 `@server:uri` 在普通任务中内联资源内容。
- 新增 resources、prompts 查看命令，MCP prompts 当前只查看，不自动执行或注入对话。
- 接入 tools/resources 变更通知：工具列表变更后替换动态注册工具，资源变更后使缓存失效并在下次读取时刷新。
- 新增 `/cancel`，ReAct、Plan、Team、工具批次与 Shell 命令在执行边界检查取消 Token。
- 保留 Windows Git Bash 选择、命令进程树终止和原生工作目录输出兼容处理。

验证结果：`mvn clean package` 通过，341 个测试全部通过（其中 6 个 Stdio Transport 测试在 Windows 环境按条件跳过）；资源与通知测试使用内存 MCP Transport，不启动真实 MCP Server 或访问外部服务。

## v0.12.0 — MCP 协议核心

- 支持 stdio 子进程和 Streamable HTTP 两种 MCP Transport，并实现 JSON-RPC 2.0 请求、响应配对和初始化流程。
- 新增 MCP 配置加载：合并用户级 `~/.codemate/mcp.json` 与项目级 `.codemate/mcp.json`，项目配置覆盖同名 Server。
- 将 `tools/list` 返回的工具注册为 `mcp__{server}__{tool}`，自动加入模型可调用工具定义；工具参数 Schema 会过滤不兼容字段。
- 新增 `/mcp`、`/mcp restart`、`/mcp logs`、`/mcp disable`、`/mcp enable` 服务管理命令。
- 所有 MCP 工具默认受 HITL 审批并进入 JSONL 审计，审计中会对常见密钥字段脱敏。
- 保留 Windows Git Bash 选择、命令进程树终止和原生工作目录输出兼容处理。

验证结果：`mvn clean package` 通过，308 个测试全部通过；MCP 单元测试使用内存 Transport 与 MockWebServer，不启动真实 MCP Server 或访问外部服务。

## v0.11.0 — 安全策略围栏与操作审计

- 为 `read_file`、`write_file`、`list_dir` 和 `create_project` 新增项目根路径围栏，拒绝绝对路径外逃、`..` 穿越和符号链接逃逸。
- 为 `execute_command` 新增危险命令快速拒绝，覆盖提权、全盘删除、磁盘格式化、裸设备写入、fork bomb 和远端脚本管道执行等典型风险。
- `write_file` 新增单文件 5MB 上限；命令仍保留 60 秒超时和 8KB 输出截断。
- 新增按天 JSONL 操作审计，记录危险工具的允许、HITL 拒绝、策略拒绝和异常结果。
- 新增 `/policy` 查看安全策略状态，`/audit [N]` 查看今日最近的危险工具审计记录。
- HITL 批准后仍需通过安全策略校验，用户不能批准绕过路径或命令策略。

验证结果：`mvn clean package` 通过，261 个测试全部通过；覆盖路径越界、符号链接逃逸、危险命令、审计落盘、HITL 审批与策略拒绝分支。随后使用隔离用户目录进行 CLI 启动检查，不调用真实模型、搜索 Provider、外部网页或 Embedding 服务。

## v0.10.0 — 联网能力与 Web 工具

- 新增 `web_search` 工具和 `SearchProvider` 接口，内置智谱、SerpAPI、SearXNG 三种实现。
- Provider 可通过 `SEARCH_PROVIDER` 显式选择，也可根据 GLM Key、SerpAPI Key 或 SearXNG URL 自动判断。
- 新增 `web_fetch`，使用 OkHttp、Jsoup 和简化 readability 抽取 Markdown 正文。
- 新增基础网络访问策略：HTTP/HTTPS 白名单、内网地址拦截、每分钟 30 次限流、30 秒超时和 5MB 响应上限。
- ReAct、Plan 和 Worker 提示词加入联网工具选择规则，已知 URL 直接抓取，SPA 或防爬边界不反复重试。
- 新增 `AgentBudget`，使用 Token 预算、重复工具调用检测和硬轮数上限阻止异常循环。
- LLM SSE 默认读取超时放宽并增加整体调用超时配置，适配长时间推理响应。
- 增加搜索结果、Provider 工厂、智谱请求、网络策略、HTML 提取和网页抓取测试。
- 保留多模型和并行工具；Windows 明确优先使用 Git Bash，避免误用不可用的 WSL 启动器，并继续清理超时命令的完整进程树。

验证结果：`mvn clean package` 通过，229 个测试全部通过；`ToolRegistryTest` 修复 Bash 选择后连续运行 3 次通过；使用隔离用户目录和占位 GLM Key完成 v0.10.0 启动检查，未调用真实模型、搜索 Provider、外部网页或 Embedding 服务。

## v0.9.0 — 多模型适配与运行时切换

- 抽出 `LlmClient` 接口，统一消息、工具调用、流式监听和响应结构。
- 新增 OpenAI 兼容客户端基类，集中处理请求构造、SSE 增量解析和工具参数合并。
- 内置 GLM 与 DeepSeek Provider，并允许通过环境变量或 `.env` 指定 Key 和模型名。
- 新增 `CodeMateConfig` 和 `LlmClientFactory`，从 `~/.codemate/config.json` 加载默认 Provider。
- 新增 `/model` 查询与运行时切换命令，切换后保留对话、Memory 和工具状态。
- 新增 `/context` 命令，显示消息角色、轮次、字符量和 Memory Token 状态。
- Agent、Planner、Plan、SubAgent、Orchestrator 和 Memory 全部改为依赖统一模型接口。
- 汇总 ReAct 多轮请求的 Token 用量，并在任务结束时展示耗时。
- 保留并行工具入口以及 Windows Shell 进程树清理增强。

验证结果：`mvn clean package` 通过，183 个测试全部通过；使用隔离用户目录和占位 GLM Key 验证 `/model`、`/context` 及未配置 DeepSeek 时的失败分支，未触发真实 Provider API 或 Embedding 请求。

## v0.8.0 — 异步执行与并行工具调用

- 新增 `ToolRegistry.executeTools()`，同一轮最多并行执行 4 个独立工具调用。
- 批量结果保持原始 `tool_call` 顺序，调用方可安全回灌模型消息历史。
- 新增工具批次超时与取消结果，单个任务异常不会打乱其他结果。
- ReAct、Plan 任务执行器和 Multi-Agent Worker 统一接入批量工具入口。
- Plan 同一 DAG 批次的任务通过独立输出缓冲并行执行，结束后按任务顺序展示。
- 终端增加工具调用摘要，展示工具类型、数量和关键参数。
- 改进流式推理区的迭代衔接，同一次用户任务只展示一次思考标题。
- Maven 测试进程固定使用 UTF-8，增加并行度、顺序、超时和流式渲染测试。
- 保留 Windows Shell 进程树清理与原生项目路径输出增强。

验证结果：`mvn clean package` 通过，183 个测试全部通过；`ToolRegistryTest` 连续运行 3 次通过；使用占位 Key 完成 v0.8.0 启动和退出检查，未触发真实模型或 Embedding 请求。

## v0.7.0 — HITL 人工审批

- 新增危险工具分级，`write_file`、`execute_command`、`create_project` 可在执行前请求人工审批。
- 新增 `HitlToolRegistry`，通过继承并覆写工具执行入口，为 ReAct、Plan 和 Multi-Agent 提供统一透明拦截。
- 终端审批支持批准、会话内同类操作全部放行、拒绝、跳过和修改参数。
- 修改后的参数先进行 JSON 语法校验；输入流异常或连续无效输入时保守拒绝。
- 新增 `/hitl`、`/hitl on`、`/hitl off` 和 `/memory clear` 命令。
- `/clear` 改为只清空短期对话和会话放行记录，长期记忆仅通过显式命令维护。
- 工具调用前主动收尾流式 Markdown 渲染，避免审批提示与模型输出交错。
- 增加审批策略、审批结果、终端交互、注册表拦截和 Plan 工厂测试。

验证结果：`mvn clean package` 通过，177 个测试全部通过；使用占位 Key 验证 `/hitl` 状态查询和开关命令，未触发真实模型、Embedding 或危险工具调用。

## v0.6.0 — Multi-Agent 协作

- 新增 Planner、Worker、Reviewer 三类子 Agent 和角色化系统提示词。
- 新增 Orchestrator，解析计划并根据依赖调度步骤。
- 默认通过 2 个 Worker 并行执行同批独立任务，并按步骤顺序展示缓冲输出。
- Reviewer 审查执行结果，不通过时携带反馈重新执行，每步最多重试 2 次。
- Worker 可以使用完整工具集；Planner 和 Reviewer 不接收工具定义。
- Multi-Agent 复用 ReAct 的 ToolRegistry 和 MemoryManager，并将最终结果写回记忆。
- 增加 Agent 消息、角色、子 Agent、编排器和工具行为测试。
- 保留 Windows RAG 项目路径规范化修复。

验证结果：`mvn clean package` 通过，123 个测试全部通过；`ToolRegistryTest` 连续运行 3 次通过；使用占位 Key 完成启动和退出检查，未触发真实模型或 Embedding 请求。

## v0.5.0 — 流式输出、终端渲染与日志

- GLM 客户端切换为 SSE 流式读取，增量合并回复、推理内容和工具调用参数。
- ReAct、Plan 规划和计划任务执行过程支持流式展示。
- 新增轻量终端 Markdown 渲染器，处理标题、列表、引用、表格和代码块。
- 默认隐藏工具参数、结果片段和 Token 统计，运行细节改为写入日志。
- 新增 Logback 文件日志，支持按天和大小滚动、历史压缩及总容量限制。
- 复用 Jieba 分词器，补充 Planner、流式展示和终端渲染测试。
- 保留 Windows RAG 项目路径规范化修复。

验证结果：`mvn clean package` 通过，89 个测试全部通过；使用占位 Key 完成启动和退出检查，未触发真实模型或 Embedding 请求。日志目录和滚动文件配置能够正常初始化。

## v0.4.0 — RAG 代码库索引与检索

- 新增代码扫描及文件、Java 类、方法三级分块。
- 使用 JavaParser 提取代码结构和关系图谱。
- 支持 Ollama 与 OpenAI 兼容 Embedding 接口，并使用 SQLite 持久化向量和关系。
- 融合余弦语义检索与关键词检索，加入命中及代码类型加权和单文件数量限制。
- 新增 `/index`、`/search`、`/graph` 命令和 `search_code` Agent 工具。
- 统一 SQLite 中的项目路径表示，修复 Windows 下写入索引后无法通过等价路径检索的问题。
- 增加 RAG 核心模块测试。

验证结果：`mvn clean package` 通过，72 个测试全部通过；使用占位 Key 完成启动和退出检查，未调用真实模型或 Embedding 服务。

## v0.3.0 — Memory 与上下文管理

- 新增短期记忆、长期记忆和 4 种记忆条目类型。
- 长期事实使用 JSON 持久化，支持相关性排序和 Token 限额注入。
- 新增 Token 预算、FIFO 淘汰与 Map-Reduce 摘要压缩。
- ReAct 和 Plan 两条执行路径写入用户消息、助手回复及工具结果。
- 新增 `/memory`、`/mem` 和 `/save` 命令，并在清理会话时提取关键事实。
- 增加 Memory 核心模块及 Plan 集成测试。

验证结果：`mvn clean package` 通过，56 个测试全部通过；使用占位 Key 完成启动和退出检查，未触发模型请求。测试输出中的部分中文受终端编码影响显示异常，但未影响断言和构建结果。

## v0.2.0 — Plan-and-Execute 与 DAG 调度

- 新增 Planner，将复杂目标拆解成带依赖关系的任务计划。
- 新增 ExecutionPlan，通过拓扑关系选择当前可执行任务。
- 支持同一依赖批次最多 4 个任务并行执行，并按计划顺序展示结果。
- 新增 `/plan` 命令与计划审阅交互，支持确认、展开、取消和补充要求后重新规划。
- 增加计划结构、命令解析和输入处理测试。

验证结果：`mvn clean package` 通过，19 个测试全部通过；使用占位 Key 完成启动和退出检查，未触发模型请求。Shade 插件提示依赖资源重叠，但未影响打包和启动。

## v0.1.0 — 基础 ReAct 与工具调用

- 实现 ReAct 循环及会话内消息历史。
- 接入 GLM Chat Completions 接口。
- 实现读取、写入、目录浏览、Shell 命令和项目创建 5 个工具。
- 统一 CodeMate 包名、构建产物和终端展示。
- Maven 构建和基础 CLI 冒烟检查通过；本阶段没有自动化测试。
