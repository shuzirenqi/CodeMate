# 版本记录

## v0.1.0 — 基础 ReAct 与工具调用

### 阶段映射

- 代码基线：PaiCLI `e2b8df4`（`1.0.0`）。
- 第一阶段截止：`0dfb87a`；中间提交仅补充 README、演示素材和路线图，未增加生产代码功能。
- CodeMate 重新编写阶段 README，不导入上游演示产物、示例生成项目和完整未来路线图。
- 后续 Plan 阶段从 `a3101c4` 开始；本次只完成第一阶段。
- 整体迭代目标：`2cbff45`（`Add StepFun support and overhaul roadmap/docs`），尚未导入。

### 本阶段整理

- 导入 ReAct、GLM 客户端及 5 个基础工具。
- 包名改为 `com.codemate`，Maven 坐标为 `com.codemate:codemate:0.1.0`。
- 终端展示改为 CodeMate v0.1.0，更新运行说明及实际能力边界。
- 保留基础版本的执行行为，不提前引入后续阶段能力。

### 验证结果

- `mvn clean package` 通过：4 个生产类编译成功，可执行 Jar 打包成功；本阶段没有测试用例。Shade 插件提示依赖资源和模块描述重叠，未阻止打包及启动。
- 使用临时占位 Key 执行 `clear`、`exit`，启动、清空历史及退出检查通过，未触发模型请求。
- 真实模型响应和工具端到端执行需配置可用 Key 后另行验证。
