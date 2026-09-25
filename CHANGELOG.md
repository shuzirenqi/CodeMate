# 版本记录

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
