# CodeMate 协作入口

## 当前阶段

- v0.13.0：在既有能力上新增 MCP resources 双轨访问、`@` 资源引用、Server 通知处理和运行中任务取消；Java 17、Maven。
- 入口：`com.codemate.cli.Main`；产物：`target/codemate-0.13.0.jar`。
- 版本能力与验证记录见 `CHANGELOG.md`，来源信息见 `NOTICE.md`。
- 参照仓库只用于读取阶段快照，不修改其工作区。

## 迭代规则

- 用户按阶段授权推进；不要一次导入最新版本。
- 每个阶段保留实际可用能力，README、包名、构建入口和 Banner 同步更新。
- 基于指定源提交读取文件，不复制参照仓库未提交的修改或敏感配置。
- 如实记录代码来源；不改写已有提交作者、时间或已有历史。
- 本地提交与推送是两步，用户未要求时不推送远程。
- 不提交 `.env`、密钥、`target/` 或运行生成的数据。

## 验证

- 每阶段至少执行 `mvn clean package`，有测试时检查测试结果。
- 当前包含 CLI、多模型、Plan、Multi-Agent、Memory、RAG、HITL、并行工具、AgentBudget、Web Provider、网络策略、HTML 提取、安全策略与 MCP（传输、配置、工具、资源、通知、取消）测试；不得将单元测试通过描述成真实模型、搜索 Provider、外部网页、Embedding 或真实 MCP 服务联调通过。
- HITL 默认关闭，路径围栏和命令规则也不是沙箱；验证避免真实危险操作。
