# AI 模块论文补充稿（需求分析 · 总体设计 · 详细设计 · 系统测试）

> **说明**：本文档为「锦食坊」外卖系统 **AI 功能**在毕业论文各章节的补充内容，按论文章节顺序组织：需求分析（对应第 2 章）→ 系统总体设计与数据库设计（对应第 3 章）→ 详细设计（对应第 4 章，即下文"第六章"）→ 系统测试（对应第 5 章）。图注、表注、写作风格均与论文原文保持一致。

---

## 一、AI 功能需求分析（对应论文第 2 章）

### 2.1 AI 功能需求描述

锦食坊外卖平台在传统外卖功能（分类管理、菜品管理、套餐管理、订单管理等）的基础上引入大语言模型 AI 能力。系统 AI 功能涉及用户端与商家端两类角色，具体如下：

**用户端：**

1. AI 智能客服：为用户提供 7×24 小时在线智能客服「小锦」，支持多轮对话，具备智能推荐菜品、订单与配送咨询、售后与帮助、天气查询四大技能，问题超出能力范围时引导转人工客服。
2. AI 菜品推荐：用户输入自然语言偏好（如"想吃辣的""来点清淡的""适合减肥的主食"），系统从可售菜品库中推荐 3~6 道菜品，并以图文卡片 + 个性化理由的形式返回。
3. 评论 AI 扩写：用户输入简短评价（如"好吃"），AI 自动扩展为丰富描述，支持帮写、续写、润色、精简四种模式。

**商家端：**

1. 知识库管理：商家上传知识文档（PDF、TXT、Word 等），系统解析、向量化后存入向量库，供 AI 客服检索增强回答，防止 AI 幻觉。
2. 敏感词管理：商家维护敏感词库，平台对用户评论与回复进行阿里云 Green 云审核 + 本地敏感词词典双重过滤。
3. 人工客服：商家客服人员接收并回复用户的客服会话，实现人工兜底。

### 2.2 AI 功能用例图

根据上述 AI 功能需求，绘制 AI 功能用例图，用户端与商家端 AI 功能用例图如图 2.1 所示。

> 图 2.1 AI 功能用例图（待补充 StarUML 绘制的用例图）

### 2.3 AI 非功能性需求分析

**（1）易使用性：**AI 客服入口醒目，交互采用自然对话形式，无需用户学习成本；推荐结果以卡片化展示，直观明了。

**（2）安全性：**用户会话记忆设置过期时间（TTL 6 小时）自动清理，保护用户隐私；评论与回复内容经过云审核 + 本地敏感词双重过滤，保障平台内容安全。

**（3）可靠性：**大模型调用异常时具备降级机制——推荐失败降级为关键词规则筛选，评论扩写失败降级为随机模板，保证核心功能始终可用；评论自动回复使用独立线程池异步执行，避免阻塞主流程。

**（4）可扩展性：**基于 LangChain4j 框架实现，模型层抽象解耦，可平滑切换或扩展不同大模型；AI 工具（菜品推荐、订单查询）可动态注册，便于扩展新技能。

**（5）性能：**AI 对话采用 SSE 流式输出，逐字返回降低首字延迟；会话记忆与向量库均基于 Redis 内存存储，查询速度快。

---

## 二、AI 系统总体设计（对应论文第 3 章）

### 3.1 AI 系统架构设计

AI 系统采用 LangChain4j 框架统一对接通义千问（Qwen）大模型，整体架构自下而上分为四层：

1. **数据层**：MySQL（业务数据与客服会话等）、Redis（会话记忆、向量库、缓存）、MongoDB（评论文档）、MinIO（知识库文件、图片）；
2. **AI 编排层**（LangChain4j）：模型接入（聊天模型、流式聊天模型、向量嵌入模型）、会话记忆（Redis 存储）、检索增强 RAG（向量检索）、工具调用（菜品推荐、订单工具）；
3. **AI 服务层**：AI 客服服务、菜品推荐服务、评论扩写与自动回复服务、知识库服务、敏感词服务；
4. **接入层**：用户端与商家端通过 SSE（流式对话）、WebSocket（人工客服）、RESTful 接口与后端交互。

AI 系统架构图如图 3.1 所示。

> 图 3.1 AI 系统架构图（待补充）

### 3.2 AI 模块划分

根据第 2 章 AI 功能需求分析，AI 功能模块划分为五大模块，模块结构图如图 3.2 所示。

> 图 3.2 AI 功能模块结构图（待补充）

各模块功能如下：

1. **AI 智能客服模块**：会话管理、SSE 流式对话、Redis 会话记忆、业务工具调用、人工客服 WebSocket 兜底；
2. **AI 菜品推荐模块**：偏好推荐算法（语义理解 + 关键词降级）、推荐工具集成、浏览记录上报；
3. **评论 AI 扩写与自动回复模块**：四种模式扩写、评论发表、AI 自动回复与二级回复；
4. **知识库管理模块**：文档上传与解析、文本切块与向量化、RAG 检索增强；
5. **敏感词过滤模块**：敏感词管理、阿里云 Green 云审核、本地 DFA 词典过滤。

### 3.3 AI 数据库设计

本系统在传统外卖 11 张数据库表的基础上，为 AI 功能新增 5 张数据库表，分别是客服会话表、菜品浏览记录表、知识库信息表、知识库向量关联表、敏感词表。各表结构设计如下。

**表3.12 客服会话表（customer_service_session）**

| 字段名 | 字段描述 | 数据类型 | 长度 | 是否主键 | 约束 | 说明 |
|---|---|---|---|---|---|---|
| id | 主键 | bigint | - | 是 | not null | 唯一约束 |
| user_id | 用户ID | bigint | - | 否 | not null | - |
| session_id | 会话ID | varchar | 64 | 否 | not null | 唯一索引 |
| service_type | 服务类型 | tinyint | - | 否 | not null | 1人工客服 2AI助手 |
| status | 会话状态 | tinyint | - | 否 | not null | 1进行中 2已结束 |
| staff_id | 客服人员ID | bigint | - | 否 | - | - |
| create_time | 创建时间 | datetime | - | 否 | not null | 默认当前时间 |
| end_time | 结束时间 | datetime | - | 否 | - | - |

**表3.13 菜品浏览记录表（item_views）**

| 字段名 | 字段描述 | 数据类型 | 长度 | 是否主键 | 约束 | 说明 |
|---|---|---|---|---|---|---|
| id | 主键 | bigint | - | 是 | not null | 唯一约束 |
| user_id | 用户id | bigint | - | 否 | not null | - |
| item_id | 菜品/套餐ID | bigint | - | 否 | not null | - |
| item_type | 类型 | varchar | 10 | 否 | not null | dish菜品 setmeal套餐 |
| view_count | 浏览次数 | int | - | 否 | - | 默认0 |

**表3.14 知识库信息表（knowledge_base）**

| 字段名 | 字段描述 | 数据类型 | 长度 | 是否主键 | 约束 | 说明 |
|---|---|---|---|---|---|---|
| id | 主键 | int | - | 是 | not null | 唯一约束 |
| file_name | 知识库名称 | varchar | 50 | 否 | not null | - |
| url | 原始文件链接 | varchar | 255 | 否 | not null | MinIO 地址 |
| created_time | 创建时间 | datetime | - | 否 | not null | 默认当前时间 |

**表3.15 知识库向量关联表（knowledge_base_redis）**

| 字段名 | 字段描述 | 数据类型 | 长度 | 是否主键 | 约束 | 说明 |
|---|---|---|---|---|---|---|
| id | 主键 | int | - | 是 | not null | 唯一约束 |
| knowledge_base_id | 知识库主键ID | bigint | - | 否 | not null | - |
| redis_id | 关联的Redis向量ID | varchar | 255 | 否 | not null | - |
| created_time | 创建时间 | datetime | - | 否 | not null | 默认当前时间 |

**表3.16 敏感词表（sensitive）**

| 字段名 | 字段描述 | 数据类型 | 长度 | 是否主键 | 约束 | 说明 |
|---|---|---|---|---|---|---|
| id | 主键 | int | - | 是 | not null | 唯一约束 |
| sensitives | 敏感词 | varchar | 50 | 否 | - | - |
| created_time | 创建时间 | datetime | - | 否 | - | 默认当前时间 |
| updated_time | 更新时间 | datetime | - | 否 | - | 默认当前时间 |

---

# 第六章 AI 功能设计与实现

锦食坊外卖平台在传统外卖系统（分类管理、菜品管理、套餐管理、订单管理）的基础上，引入大语言模型技术，集成 **LangChain4j + 通义千问（Qwen）大模型**，构建了五大 AI 功能模块，使平台从"人工服务 + 规则引擎"升级为"AI 智能交互 + 个性化体验"的新型餐饮服务平台。

系统 AI 能力的技术底座如下表所示。

| 技术项 | 选型 |
|---|---|
| 大模型框架 | LangChain4j 1.10.0-beta18 |
| 大模型服务 | 阿里云百炼平台，通义千问 `qwen-plus-latest`（对话）、`text-embedding-v4`（向量嵌入） |
| 向量数据库 | Redis（key 前缀 `training-embedding:`） |
| 会话记忆 | Redis（key 前缀 `training:session:messages:`，TTL 6 小时） |
| 内容安全 | 阿里云内容安全（Green）+ 本地敏感词库 |

本章依次介绍 AI 智能客服、AI 菜品推荐、评论 AI 扩写与自动回复、知识库管理、敏感词过滤五个模块的设计与实现。

---

## 6.1 AI 智能客服模块

### 6.1.1 功能描述

AI 智能客服模块为平台提供 **7×24 小时在线智能客服「小锦」**，用户在小程序端即可与 AI 进行多轮对话。AI 客服具备四大核心技能：

1. **智能推荐菜品**：当用户描述口味偏好（如"想吃辣的""适合减肥"）、用餐场景（如"一人食""朋友聚餐"）或食材要求时，从当前可售菜品库中推荐 3~6 道最匹配的菜品，并给出个性化推荐理由。
2. **订单与配送咨询**：查询订单状态（1 待付款、2 待接单、3 已接单、4 派送中、5 已完成、6 已取消、7 退款），配送范围限定广州市天河区正佳广场周边 3 公里。
3. **售后与帮助**：仅对"待付款、待接单"状态的订单支持取消（需用户提供取消原因），其余状态引导转人工客服。
4. **天气查询**：可查询指定地区天气，仅广州天河区可同时返回推荐菜品。

当问题超出 AI 能力范围（用户情绪激动、涉及食品安全、连续两次无法回答等）时，AI 主动引导用户转接人工客服，形成"AI 为主、人工兜底"的服务闭环。

### 6.1.2 页面效果展示

AI 客服入口位于小程序"消息"页面，用户可点击"AI 智能助手"进入 AI 客服会话页，也可点击"人工客服"进入人工客服会话。AI 客服界面由顶部会话栏、中间对话消息区、底部输入栏三部分组成：对话消息区以气泡形式展示用户消息与 AI 回复，AI 消息支持 Markdown 渲染与图文推荐卡片展示。页面效果示意如图 6.1 所示。

> 图 6.1 AI 智能客服页面效果（待系统运行后截图补入）

### 6.1.3 详细设计和实现

（1）**会话建立**

用户首次进入 AI 客服时，小程序调用 `POST /client/customer-service/session/ai` 接口，后端在 `CustomerServiceServiceImpl.createOrGetSession` 中先查询该用户是否已有进行中的会话，若存在则复用，否则生成 UUID 作为 `sessionId`，写入 MySQL 数据库 `customer_service_session` 表（service_type 为 2 表示 AI 会话，1 表示人工会话），返回会话信息给前端。

（2）**流式聊天**

用户发送消息后，小程序通过 **SSE（Server-Sent Events）** 调用 `POST /client/ai-assistant/chat` 接口，请求体为 `AiChatDTO{sessionId, message, messageType}`。后端 `AiAssistantController.chat` 从 JWT 中解析用户 ID，调用 `AiAssistantServiceImpl.processChat` 处理消息，最终通过 `ConsultantService.chat(sessionId, message)` 触发 LangChain4j 流式聊天。

流式响应通过 `Flux<ChatEventVO>` 下发，事件类型定义于 `ChatEventTypeEnum`：

- **DATA（1001）**：AI 逐字回复的文本内容，前端实时追加到气泡；
- **PARAM（1003）**：AI 调用工具产生的结构化参数（如推荐菜品列表），前端据此渲染图文卡片；
- **STOP（1002）**：本轮对话结束标记。

（3）**系统提示词与工具调用**

`ConsultantService` 接口标注 `@SystemMessage(fromResource = "Consultant.txt")`，系统提示词定义了 AI 客服的"小锦"人设、四大技能、限制与安全准则。其装配由 `LangChain4jConfiguration.consultantService` 使用 `AiServices.builder` 显式完成，注入流式聊天模型、会话记忆提供者、RAG 检索器与业务工具（`DishRecommendTool` 菜品推荐工具、`OrderTool` 订单工具），使 AI 能够在对话中主动查询真实业务数据，做到"不胡言乱造"。

（4）**Redis 会话记忆**

AI 会话记忆采用 `MessageWindowChatMemory`（窗口 20 条），通过自定义 `RedisChatMemoryStore` 持久化到 Redis，key 为 `training:session:messages:{sessionId}`，TTL 6 小时，既保证多轮对话的上下文连续性，又兼顾性能与隐私。

（5）**人工客服兜底**

当用户选择转人工客服时，调用 `POST /client/customer-service/session/human` 建立人工会话；商家端通过 `PUT /store/customer-service/session/{sessionId}/assign/{staffId}` 分配客服人员。用户与客服人员通过 **WebSocket** 端点 `/ws/customer-service/{sessionId}/{userType}` 建立长连接，`CustomerServiceWebSocketServer` 根据 `sessionId_userType` 维护连接映射，消息经 `sendStaffMessage`/`sendMessage` 处理后双向转发，实现低延迟实时沟通。

### 6.1.4 关键代码

（1）流式聊天核心实现（`AiAssistantServiceImpl.handleGeneralQuestion`）

```java
return Flux.create(sink -> {
    consultantService.chat(sessionId, message)
        .onPartialResponse(content -> sink.next(new ChatEventVO(content, DATA.getValue())))
        .onCompleteResponse(chatResponse -> {
            Map map = ToolResultHolder.get(requestId);
            if (CollUtil.isNotEmpty(map)) {
                ToolResultHolder.remove(requestId);
                sink.next(ChatEventVO.builder().eventData(map).eventType(PARAM.getValue()).build());
            }
            sink.next(STOP_EVENT);
            ToolContext.clear();
        })
        .onError(error -> log.error("AI处理失败", error))
        .start();
});
```

（2）AI 客服装配（`LangChain4jConfiguration`）

```java
@Bean
public ConsultantService consultantService(OpenAiChatModel openAiChatModel,
                                           OpenAiStreamingChatModel openAiStreamingChatModel,
                                           ChatMemoryProvider chatMemoryProvider,
                                           DishRecommendTool dishRecommendTool,
                                           OrderTool orderTool,
                                           ContentRetriever contentRetriever) {
    return AiServices.builder(ConsultantService.class)
            .chatModel(openAiChatModel)                      // 聊天模型
            .streamingChatModel(openAiStreamingChatModel)    // 流式聊天模型
            .chatMemoryProvider(chatMemoryProvider)          // 会话记忆
            .contentRetriever(contentRetriever)              // 检索增强（RAG）
            .tools(dishRecommendTool, orderTool)             // 业务工具
            .build();
}
```

（3）客服会话接口

```java
// AiAssistantController
@PostMapping("/client/ai-assistant/chat")
public Flux<ChatEventVO> chat(@RequestBody AiChatDTO dto) { ... }

// CustomerServiceController（客户端）
@PostMapping("/client/customer-service/session/ai")      // 建立 AI 会话
@PostMapping("/client/customer-service/session/human")   // 建立人工会话
@GetMapping("/client/customer-service/messages/{sessionId}") // 历史消息
```

---

## 6.2 AI 菜品推荐模块

### 6.2.1 功能描述

AI 菜品推荐模块基于**自然语言语义理解**，为用户提供个性化菜品推荐。用户可在 AI 客服中直接输入口味偏好（如"想吃辣的""来点清淡不油腻的""适合减肥的主食"），或点击推荐入口，系统通过语义理解解析用户偏好，从当前可售菜品库中筛选 3~6 道最匹配的菜品，并以"图片 + 名称 + 价格 + 个性化理由"的图文卡片形式返回，实现"点餐更懂你"。同时系统采集用户浏览记录（`item_views` 表），为个性化推荐沉淀用户行为数据。

### 6.2.2 页面效果展示

推荐结果以菜品卡片形式展示，每张卡片包含菜品图片、名称、价格与推荐理由（30 字以内）；用户进入菜品详情页时系统自动上报浏览记录。页面效果示意如图 6.2 所示。

> 图 6.2 AI 菜品推荐页面效果（待系统运行后截图补入）

### 6.2.3 详细设计和实现

（1）**偏好推荐算法**

`AiAssistantServiceImpl.getSmartRecommendations(userId, preference, shopId)` 是推荐核心，处理步骤如下：

1. 通过 `dishMapper.list(null)` 获取全部启售菜品；
2. 截取前 20 道菜品，与用户偏好拼接成提示语（要求模型"推荐 10 道最符合用户偏好的菜品，至少包含一个套餐，并以 JSON 数组返回"）；
3. 调用 `RecommenAssistantService.chat(userPrompt)`，由通义千问返回结构化 JSON（字段含 id、name、price、reason、categoryId、type）；
4. 用 fastjson 解析 JSON 得到推荐结果；
5. 若解析失败，降级为 `getTraditionalRecommendations` 关键词规则筛选（如"辣""清淡""汤粉"等），保证功能可用性。

（2）**工具调用集成**

`DishRecommendTool` 向 AI 客服暴露两个工具方法：

- `getRecommendDishes()`：随机打乱可售菜品，截取前 6 道返回；
- `getDishesByPreference(preference)`：从 `ToolContext` 取用户 ID，调用 `getSmartRecommendations` 得到智能推荐，再还原为含口味的 `DishVO` 列表。

工具结果通过 `ToolResultHolder` 以 requestId 为维度暂存，流式对话完成时由 `handleGeneralQuestion` 取出，封装为 PARAM(1003) 事件下发给前端渲染推荐卡片。

（3）**浏览记录上报**

用户进入菜品详情页时，前端 `onMounted` 调用 `POST /client/view/increment/dish/{dishId}`。`ViewServiceImpl.incrementViewCount` 先按（用户、菜品、类型）查询 `item_views` 表：不存在则插入一条 `view_count=1` 记录，存在则自增计数。浏览数据沉淀于 `item_views` 表，作为用户行为数据积累，供个性化推荐算法分析与调用（当前版本推荐以语义偏好为核心，浏览记录主要用于行为数据采集）。

（4）**独立推荐接口**

小程序"AI 推荐"独立入口调用 `POST /client/ai-assistant/recommend`（入参 `RecommendRequestDTO{preference, shopId}`），返回 `Result<List<Map>>`（字段含 id、name、price、image、flavors、reason），与 AI 客服工具调用共享同一推荐算法。

### 6.2.4 关键代码

（1）偏好推荐核心（`AiAssistantServiceImpl`）

```java
// 拼接推荐提示语
String userPrompt = String.format(
    "用户偏好：%s\n\n可选菜品列表：\n%s\n\n" +
    "请推荐10道最符合用户偏好的菜品，至少返回一个套餐，并以JSON格式返回，格式为" +
    "[{\"id\":菜品id,\"name\":菜品名,\"price\":价格,\"reason\":推荐理由(30字以内)," +
    "\"categoryId\":分类id,\"type\":1菜品/2套餐}]",
    preference, dishJson);

// 调用推荐 AI
String aiResult = recommendationService.chat(userPrompt);
// fastjson 解析
List<Map> recommendations = parseAiRecommendations(aiResult);
// 解析失败降级为关键词规则筛选
if (recommendations == null || recommendations.isEmpty()) {
    recommendations = getTraditionalRecommendations(allDishes, preference);
}
```

（2）偏好推荐工具（`DishRecommendTool`）

```java
@Tool("根据用户偏好推荐菜品")
public List<DishVO> getDishesByPreference(@P(value = "用户偏好") String preference) {
    String requestId = ToolContext.get(ToolContext.REQUEST_ID).toString();
    Long userId = (Long) ToolContext.get(ToolContext.USER_ID);
    List<Map<String, Object>> smartRecommendations =
        aiAssistantService.getSmartRecommendations(userId, preference, 1L);
    List<DishVO> dishVOS = parseListMapToDishVOs(smartRecommendations, 6);
    ToolResultHolder.put(requestId, Dish.class.getSimpleName(), dishVOS);
    return dishVOS;
}
```

（3）浏览记录上报（`ViewServiceImpl`）

```java
@Override
public void incrementViewCount(Long userId, Long dishId) {
    ItemView itemView = itemViewMapper.selectByItemIdAndUserIdAndType(userId, dishId);
    if (itemView == null) {
        itemViewMapper.insert(ItemView.builder().userId(userId).itemId(dishId)
            .itemType("dish").viewCount(1).build());
    } else {
        itemViewMapper.updateViewCount(itemView.getId());
    }
}
```

---

## 6.3 评论 AI 扩写与自动回复模块

### 6.3.1 功能描述

用户完成订单后，可对订单中的菜品/套餐发表文字评价。为提升评论质量与数量、反哺运营分析，系统提供两大 AI 能力：

1. **AI 评论扩写**：用户输入简短评价（如"好吃"），AI 自动扩展为丰富描述（如"口感细腻，酱汁浓郁，回味无穷"）。支持四种模式：1 帮写、2 续写、3 润色、4 精简。
2. **AI 自动回复**：用户发表评论后，系统异步调用大模型生成商家回复，模拟"用户评论 - 商家响应"的良性互动；用户可继续回复 AI 触发二级回复，商家也可人工回复。

### 6.3.2 页面效果展示

评价页面提供"AI 扩写"按钮，点击后返回扩写文案，用户在编辑框中确认或修改后提交；评论列表展示用户评论与 AI/商家回复。页面效果示意如图 6.3 所示。

> 图 6.3 评论 AI 扩写页面效果（待系统运行后截图补入）

### 6.3.3 详细设计和实现

（1）**发表评论**

用户调用 `POST /client/comment/save` 提交评论，入参 `CommentSaveDTO{orderId, orderNumber, dishIds, setmealIds, content, originalComment, type}`。`CommentServiceImpl.comment` 的处理流程：

1. 校验内容非空且不超过 500 字；
2. 依次通过阿里云 Green 内容审核与本地 `SensitiveWordUtil` 敏感词词典双重过滤，违规即拦截；
3. 构建 `Comment` 对象（authorId、authorName、dishId、moreDishName 拼接商品名、content、createdTime 等），通过 `MongoTemplate.save` 写入 MongoDB `comment` 集合；
4. 更新订单为已评论（`isComment=1`），删除 Redis 用户订单缓存；
5. 异步触发 `commentRepayService.aiCommentRepay(commentId)` 生成 AI 自动回复。

（2）**AI 评论扩写**

用户点击"AI 扩写"后调用 `POST /client/ai-comment/expand`。`AiCommentServiceImpl.expand` 按是否携带订单菜品 ID 分两种处理：

- **订单评论**（携带 dishIds/setmealIds）：查询菜品/套餐实体，`JSON.toJSONString` 拼接后，调用 `AiCommentService.chat(content, type, jsonStr)`，系统提示词取 `comment-chat-message.txt`（美食内容创作者人设，技能 1 帮写/2 续写/3 润色/4 精简，输出不超过 500 字）；若调用异常则降级为随机模板评价。
- **普通评论**（未携带）：调用 `AiCommentExpandServiceAgent.chat(content, type, originalComment)`，系统提示词取 `repay-comment-chat-message.txt`。

（3）**AI 自动回复**

发评后异步执行 `CommentRepayServiceImpl.aiCommentRepay`：拼接提示语"用户购买了菜品：%s，评论了：%s，请你对此进行回复"，调用 `SystemChatServer.chat`（知识问答小助手），以系统身份 `SYSTEM_USER_ID=4L` 调用 `uniformReply` 将回复写入 MongoDB `comment_repay` 集合，并累加主评论回复数。用户回复 AI 评论时触发 `aiCommentRepayTwo` 二级回复。异步任务由专用线程池 `aiReplyExecutor`（核心 5 / 最大 10 / 队列 100）执行，避免阻塞主流程。

### 6.3.4 关键代码

（1）AI 扩写两分支（`AiCommentServiceImpl.expand`）

```java
if (dishIds != null || setmealIds != null) {
    // 查询菜品/套餐实体，JSON 拼接
    List<Dish> dishes = dishMapper.getDishByIds(dishIds);
    String jsonStr = JSON.toJSONString(dishes);
    try {
        string = aiCommentService.chat(dto.getContent(), dto.getType(), jsonStr);
    } catch (Exception e) {
        string = getTraditionalRecommendations(dto); // 降级
    }
} else {
    // 普通评论走独立 Agent
    string = aiCommentExpandServiceAgent.chat(dto.getContent(),
        dto.getType(), dto.getOriginalComment());
}
map.put("msg", string);
```

（2）发表评论与触发自动回复（`CommentServiceImpl.comment`）

```java
comment = Comment.builder().authorId(userId).authorName(userById.getName())
    .dishId(dishId).moreDishName(moreDishName.toString()).content(dto.getContent())
    .image(userById.getAvatar()).flag((short) 0).orderNumber(dto.getOrderNumber())
    .createdTime(new Date(System.currentTimeMillis() + 8 * 60 * 60 * 1000))
    .likes(0).reply(0).build();
mongoTemplate.save(comment);
orders.setIsComment((short) 1);
orderMapper.update(orders);
commentRepayService.aiCommentRepay(comment.getId()); // 异步 AI 自动回复
```

（3）AI 自动回复（`CommentRepayServiceImpl.aiCommentRepay`）

```java
String userPrompt = String.format("用户购买了菜品：%s,\n评论了：%s,\n请你对这个进行回复",
    comment.getMoreDishName(), comment.getContent());
String aiChat = sysChatServer.chat(userPrompt);
repaySaveDTO.setRootCommentId(comment.getId());
repaySaveDTO.setContent(aiChat);
repaySaveDTO.setReplyAuthorId(comment.getAuthorId());
this.uniformReply(repaySaveDTO, SYSTEM_USER_ID); // SYSTEM_USER_ID = 4L
```

---

## 6.4 知识库管理模块（RAG 检索增强）

### 6.4.1 功能描述

知识库管理模块为 AI 客服提供**基于真实业务知识的检索增强生成（RAG）能力**。商家在管理端上传知识文档（PDF、TXT、Word 等），系统自动完成文档解析、文本切块、向量化并存入 Redis 向量库；用户向 AI 客服提问时，系统先从知识库检索最相关的知识片段，再交给大模型组织回答，从而保证 AI"有据可答、不胡编乱造"，有效缓解大模型幻觉问题。

### 6.4.2 页面效果展示

商家管理端"知识库"页面提供文档分页列表、上传按钮与删除操作，上传后展示文档名称、上传时间等元信息。页面效果示意如图 6.4 所示。

> 图 6.4 知识库管理页面效果（待系统运行后截图补入）

### 6.4.3 详细设计和实现

（1）**知识库上传与向量化**

商家调用 `POST /store/knowledgeBase/embed`（multipart 文件）上传文档，`KnowledgeBaseServiceImpl.addKnowledgeBase` 的处理流程：

1. 通过 `fileService.uploadMinio(file)` 将文档上传至 MinIO 对象存储，得到文件 URL；
2. 按文件后缀选择解析器：`.pdf` 使用 `ApachePdfBoxDocumentParser`，`.txt` 使用 `TextDocumentParser`，其余格式使用 `ApacheTikaDocumentParser`，将文档内容解析为 LangChain4j 的 `Document` 对象；
3. 通过 `DocumentSplitters.recursive(300, 100)` 将长文本按 300 字符切块、100 字符重叠，避免切断语义；
4. 使用自定义 `MyEmbeddingStoreIngestor`（注入 `EmbeddingModel`（text-embedding-v4）与 `RedisEmbeddingStore`）对文本块执行向量化，`ingest(document)` 返回 Redis 中的向量 ID 集合；
5. 落库：`knowledge_base` 表保存文件元信息（文件名、MinIO URL、创建时间），`knowledge_base_redis` 表保存知识库与向量 ID 的关联；向量本体存储在 Redis（key 前缀 `training-embedding:`）。

（2）**RAG 检索**

`LangChain4jConfiguration.contentRetriever` 构建检索器：`EmbeddingStoreContentRetriever` 使用 RedisEmbeddingStore 与 embeddingModel，参数 `maxResults=2`、`minScore=0.6`（仅返回相似度不低于 0.6 的片段）。该检索器注入 `ConsultantService` 装配，AI 客服回答用户问题时自动完成"向量检索 → 注入上下文 → 生成回答"的 RAG 流程。

（3）**知识库管理**

分页查询 `GET /store/knowledgeBase/page`（入参 `KnowledgeBasePageQueryDTO`）返回 `PageResult`；批量删除 `DELETE /store/knowledgeBase/delete` 同时删除 Redis 向量（`redisEmbeddingStore.remove(id)`）、MinIO 文件与两表记录，保证数据一致性。

### 6.4.4 关键代码

（1）文档向量化（`KnowledgeBaseServiceImpl.addKnowledgeBase`）

```java
// 解析文档
Document document = parseBySuffix(fileName, fileUrl); // pdfbox / tika / text 解析器
// 切块并向量化
MyEmbeddingStoreIngestor ingestor = MyEmbeddingStoreIngestor.builder()
        .embeddingModel(embeddingModel)          // text-embedding-v4
        .embeddingStore(redisEmbeddingStore)     // Redis 向量库
        .documentSplitter(DocumentSplitters.recursive(300, 100))
        .build();
EmbeddingStoreIngestor.IngestionResult result = ingestor.ingest(document);
// 记录知识库与向量 ID 关联
for (String redisId : result.getIds()) {
    knowledgeBaseRedisMapper.insertBatch(...);
}
```

（2）RAG 检索器配置（`LangChain4jConfiguration`）

```java
@Bean
public ContentRetriever contentRetriever() {
    return EmbeddingStoreContentRetriever.builder()
            .embeddingStore(redisEmbeddingStore)
            .embeddingModel(embeddingModel)
            .maxResults(2)      // 检索最相关的 2 个片段
            .minScore(0.6)      // 相似度阈值
            .build();
}
```

---

## 6.5 敏感词过滤模块

### 6.5.1 功能描述

为保障平台内容安全，系统对用户发表的评论与回复进行**双重内容审核**：一是接入阿里云内容安全（Green）进行云审核，二是内置基于 DFA 算法的本地敏感词词典过滤。商家可在管理端维护敏感词库，实现"可配置、可扩展"的内容管控。

### 6.5.2 页面效果展示

商家管理端"敏感词"页面支持敏感词的增删改查与分页展示。页面效果示意如图 6.5 所示。

> 图 6.5 敏感词管理页面效果（待系统运行后截图补入）

### 6.5.3 详细设计和实现

（1）**敏感词管理**

商家通过 `POST /sensitive`（新增）、`PUT /sensitive/{id}`（修改）、`GET /sensitive`（分页）、`DELETE /sensitive`（批量删除）管理敏感词，数据存于 MySQL `sensitive` 表（id、sensitives、created_time、updated_time）。新增/修改/删除后，`SensitiveServiceImpl` 均调用 `commentService.initSensitive()` 重建内存中的敏感词词典，保证即时生效。

（2）**双重内容审核**

用户发表评论或回复时，`CommentServiceImpl.comment` 与 `CommentRepayServiceImpl.commentRepay` 依次执行两道审核：

1. **阿里云 Green 云审核**：`GreenTextScan.greenTextScan(content)` 调用阿里云 `textModerationPlus` 接口（service 为 `comment_detection_pro`），返回结果中的 label 与 `RiskLabel.textRiskLabels`（色情、政治、暴恐、违禁品等 30 项）对照，命中即抛出违规异常；
2. **本地敏感词过滤**：`SensitiveWordUtil.matchWords(content)` 基于 DFA（确定性有限自动机）算法在词典中匹配敏感词，命中即拦截。

（3）**展示端处理**

加载评论时，`processCommentContent` 将命中敏感词的违规内容替换为"该评论存在违规内容"，实现"审核拦截 + 展示脱敏"的双重防护。

### 6.5.4 关键代码

（1）评论发布双重审核（`CommentServiceImpl.comment`）

```java
// 1. 阿里云 Green 云审核
Map<String, Object> greenResult = greenTextScan.greenTextScan(content);
String label = (String) greenResult.get("label");
if (textRiskLabels.contains(label)) {
    throw new CommentException(VIOLATION_CONTENT); // 命中云审核违规
}
// 2. 本地 DFA 敏感词过滤
Map<String, Integer> matchMap = SensitiveWordUtil.matchWords(content);
if (!matchMap.isEmpty()) {
    throw new CommentException(VIOLATION_CONTENT); // 命中本地敏感词
}
```

（2）敏感词更新重建词典（`SensitiveServiceImpl`）

```java
@Override
public void initSensitive() {
    List<Sensitive> list = sensitiveMapper.list();
    List<String> words = list.stream().map(Sensitive::getSensitives).toList();
    SensitiveWordUtil.initWordMap(words); // 重建 DFA 词典
}
```

---

## 6.6 本章小结

本章介绍了锦食坊外卖平台基于 LangChain4j 与通义千问大模型实现的五大 AI 功能模块：

| 模块 | 核心能力 | 技术要点 |
|---|---|---|
| AI 智能客服 | 7×24 智能问答、推荐、订单、天气 | SSE 流式 + Redis 记忆 + RAG + 工具调用 |
| AI 菜品推荐 | 个性化菜品推荐 | 语义理解 + 用户行为 + 关键词降级 |
| 评论 AI 扩写与自动回复 | 帮写/续写/润色/精简 + 商家互动 | 双提示词 + 异步线程池 |
| 知识库管理 | RAG 检索增强，防幻觉 | 文档解析 + 向量化 + Redis 向量检索 |
| 敏感词过滤 | 内容安全双重防护 | 阿里云 Green + DFA 词典 |

这些 AI 模块与平台原有的员工、分类、菜品、套餐、订单等管理模块深度融合，使平台在"高效服务"的基础上进一步实现"智能交互"与"个性化体验"，体现了新一代餐饮服务平台的发展方向。

---

## 四、AI 系统测试（对应论文第 5 章）

### 5.1 测试方法

AI 功能模块的测试采用功能测试为主、结合接口联调测试的方法：先在小程序端与管理端对各 AI 功能进行黑盒测试，验证功能是否符合预期；再通过后端接口对关键调用链（流式对话、工具调用、向量检索、敏感词审核）进行验证。由于 AI 功能依赖大模型与外部服务，测试时需在部署好虚拟机与阿里云百炼环境的前提下进行。

> 说明：下表各测试用例的"测试结果"待系统部署完成后补充实测值。

### 5.2 AI 智能客服测试

测试流程：用户从小程序"消息"页进入 AI 智能客服，依次测试多轮对话、菜品推荐、订单查询、人工客服转接等功能，测试结果如表 5.5 所示。

表5.5 AI 智能客服测试结果表

| 测试用例 | 预期结果 | 测试结果 |
|---|---|---|
| 用户输入"你好"，进行多轮对话 | AI 返回问候语，上下文连续 | 待验证 |
| 用户输入"想吃辣的"，触发菜品推荐 | AI 返回 3~6 道菜品卡片，含推荐理由 | 待验证 |
| 用户输入"我的订单什么时候到" | AI 查询订单状态并返回，展示订单号 | 待验证 |
| 用户输入超范围问题（如"你们老板是谁"） | AI 引导转人工客服 | 待验证 |
| 用户点击"人工客服" | 建立人工会话，客服实时回复 | 待验证 |

### 5.3 AI 菜品推荐测试

测试流程：用户在小程序菜品详情页/首页触发 AI 推荐，或输入偏好文字，验证推荐结果的准确性与合理性，测试结果如表 5.6 所示。

表5.6 AI 菜品推荐测试结果表

| 测试用例 | 预期结果 | 测试结果 |
|---|---|---|
| 输入"来点清淡不油腻的" | 返回清淡类菜品，理由合理 | 待验证 |
| 输入"适合减肥的主食" | 返回低脂高蛋白菜品/套餐 | 待验证 |
| 输入"想吃辣的" | 返回辣味菜品，理由个性化 | 待验证 |
| 用户进入菜品详情页 | 浏览记录自动上报 item_views | 待验证 |
| 大模型接口异常时 | 降级为关键词规则推荐，仍返回菜品 | 待验证 |

### 5.4 评论 AI 扩写与自动回复测试

测试流程：用户完成订单后进入评价页，测试 AI 扩写四种模式及自动回复功能，测试结果如表 5.7 所示。

表5.7 评论 AI 扩写测试结果表

| 测试用例 | 预期结果 | 测试结果 |
|---|---|---|
| 输入"好吃"，选择"润色" | 返回丰富化评价文案，可编辑提交 | 待验证 |
| 输入简短评价，选择"续写" | AI 基于原文续写内容 | 待验证 |
| 选择"帮写"且未输入内容 | AI 根据菜品自动生成评价 | 待验证 |
| 提交含敏感词的评论 | 审核拦截，提示违规 | 待验证 |
| 评论发表成功后 | 系统异步生成 AI 商家回复 | 待验证 |

### 5.5 知识库管理测试

测试流程：商家在管理端上传知识文档，验证向量化与 AI 客服 RAG 回答效果，测试结果如表 5.8 所示。

表5.8 知识库管理测试结果表

| 测试用例 | 预期结果 | 测试结果 |
|---|---|---|
| 上传 PDF/TXT 知识文档 | 上传成功，展示在知识库列表 | 待验证 |
| 用户询问知识库内的问题 | AI 客服依据知识库内容回答，不编造 | 待验证 |
| 用户询问知识库外的问题 | AI 回复"建议联系人工客服"，不胡编 | 待验证 |
| 批量删除知识文档 | 同步删除向量、MinIO 文件与记录 | 待验证 |

### 5.6 敏感词过滤测试

测试流程：商家维护敏感词库，用户提交含敏感词的评论/回复，验证双重过滤效果，测试结果如表 5.9 所示。

表5.9 敏感词过滤测试结果表

| 测试用例 | 预期结果 | 测试结果 |
|---|---|---|
| 商家新增敏感词 | 词典即时重建，新增词立即生效 | 待验证 |
| 提交含本地敏感词的评论 | 拦截，提示"评论存在违规内容" | 待验证 |
| 提交命中阿里云 Green 的内容 | 云审核拦截，抛出违规异常 | 待验证 |
| 加载历史违规评论 | 违规内容替换为占位文案展示 | 待验证 |
