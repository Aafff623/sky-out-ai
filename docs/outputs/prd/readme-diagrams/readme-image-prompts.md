# README Image Prompts — 锦食坊 / sky-ai

> Agent 产出 · 用户侧用 GPT Image（优先 **image-to-image + 标杆图**）生成后落盘 `assets/images/readme/`。  
> 视觉标准：`~/.claude/skills/readme-polish/references/visual-standards.md`  
> 标杆目录：`~/.claude/skills/readme-polish/references/`

## §0 全局规范

### 项目定位

培训/毕设外卖平台「锦食坊」：三端（微信小程序 · 商家 Vue · Spring Boot）+ LangChain4j / 通义千问 AI（客服小锦、推荐、评论扩写、知识库 RAG、敏感词）。

### 视觉总调

- 白 / 浅纸色底（`#F7F5F2`），强调色品牌红 `#D81E06`，辅色墨绿 `#2D6A4F`，墨色 `#1A1A1A`，灰色 `#6B6B6B`
- 扁平矢量、统一圆角与线宽；最多 **5** 个强调色
- 标签用真实组件名（JinShiFang / XiaoJin / training-web / MinIO…）
- NO neon purple haze, spider-web arrows, fake UI screenshots, watermarks, other-brand logos

### 给 GPT 的系统指令（可粘贴对话开头）

```text
You are producing GitHub README infographics.
Follow ByteByteGo / clean design-system clarity.
White or near-white background. Flat icons. Thin consistent arrows.
At most 5 accent colors. Rounded rectangles. Legible labels.
NO: isometric clutter, neon glow, tangled lines, unreadable micro-text, fake logos, purple sci-fi backgrounds.
Every labeled box must match the project mapping provided.
```

### 命名契约

落盘：`assets/images/readme/{banner,features,architecture,tech-stack,workflow}.png`  
Showcase / Preview：**截图 only**，禁止文生图冒充产品 UI。

---

## 1. banner.png

```yaml
asset: banner.png
aspect: 3:1 (~1500x500)
readme_section: Header
style_key: brand-system
reference_image: references/banner/brand-system-creative-tim.png
prompt_file: references/prompts/banner.md#brand-system
method: image-to-image
```

**结构拆解（标杆）**：几何标识 + 字标 + 克制色板暗示；大留白；最多一句价值主张。

**English Prompt**

```text
[system instruction from §0]
GitHub README header banner, aspect 3:1 (~1500x500).
Style like a compact brand-system board: geometric mark suggesting a food bowl + chopsticks simplified into abstract geometry (NOT cartoon mascot spam).
Wordmark space: "锦食坊" / "JinShiFang".
One short tagline in English or Chinese: "Takeout platform with AI assistant XiaoJin".
Palette only: #D81E06, #1A1A1A, #F7F5F2, #2D6A4F, #6B6B6B.
Clean white or soft paper background. Generous negative space.
NO capsule-generator clutter, neon grids, photoreal dishes collage, fake app UI inside banner.
```

**Avoid**: purple haze, crowded collage, long paragraphs, watermarks.

---

## 2. features.png

```yaml
asset: features.png
readme_section: 功能
reference_image: references/features/infographic-modules-pack.png
prompt_file: references/prompts/features.md
method: image-to-image
```

**项目模块（5+传统）**

1. AI Customer Service XiaoJin  
2. AI Dish Recommend  
3. Comment AI Expand & Auto-Reply  
4. Knowledge Base RAG  
5. Sensitive Word Filter  
6. Classic Takeout Ops (dish / order / staff)

**English Prompt**

```text
[system instruction]
Infographic of product modules in a clean 2x3 or 3x2 card grid.
Each card: same-stroke icon + short English title + one-line subtitle.
Titles: AI Customer Service · Dish Recommend · Comment AI · Knowledge RAG · Sensitive Filter · Takeout Ops.
Accent #D81E06 and #2D6A4F sparingly; mostly charcoal on paper white.
Unified icon style; no posters of long text inside cards.
```

**Avoid**: >6 colors, mismatched 3D/flat icons, fake screenshots.

---

## 3. architecture.png

```yaml
asset: architecture.png
style_key: bytebytego-client-server
reference_image: references/architecture/bytebytego-client-server.png
prompt_file: references/prompts/architecture.md#2
method: image-to-image
```

**Project mapping**

- Clients: WeChat Mini Program · Merchant Vue Admin · (optional browser)
- Edge: HTTP/JSON · JWT (`authentication` / `token`) · SSE · WebSocket
- App Server: Spring Boot training-web :8848 (client/store controllers + LangChain4j AI layer)
- Data: MySQL cq_take_out · Redis (cache/memory/vectors) · MongoDB comments · MinIO media
- External: Alibaba Bailian Qwen · Green content moderation · WeChat login

**English Prompt**

```text
[system instruction]
ByteByteGo-style client-server diagram on white background.
Title pill: "JinShiFang Architecture".
Horizontal primary path:
  Clients (Mini Program + Merchant Admin) → API Gateway/HTTP → Spring Boot training-web → Data stores.
Show Spring Boot as central server with a dashed subgroup "AI Layer (LangChain4j + Qwen)".
Bidirectional Cache/Memory: Redis above server.
Data cylinders below: MySQL · MongoDB · MinIO.
Side callout: Bailian / Green / WeChat as external services with thin arrows.
Sparse pastel accents (#D81E06, #2D6A4F); black thin arrows; no spider-web.
Labels must be legible at 1200px width.
```

**Avoid**: microservices explosion, neon, fake AWS logos.

---

## 4. tech-stack.png

```yaml
asset: tech-stack.png
reference_image: references/tech-stack/icon-wall-slidestack.png
prompt_file: references/prompts/tech-stack.md
method: image-to-image
```

**Labels（真实栈）**

Java 17 · Spring Boot 3.4 · MyBatis · LangChain4j · Vue 3 · Vite · Element Plus · uni-app · Pinia · MySQL · Redis · MongoDB · MinIO · Qwen · SSE/WebSocket

**English Prompt**

```text
[system instruction]
Unified icon wall of real tech logos/labels for a takeout+AI stack.
Group lightly into rows: Backend · Frontend · Data · AI/Infra.
Exact names: Java 17, Spring Boot, MyBatis, LangChain4j, Vue 3, Vite, Element Plus, uni-app, MySQL, Redis, MongoDB, MinIO, Qwen.
Same icon size, calm paper background, charcoal labels.
Do NOT duplicate the architecture topology; this is a stack wall only.
```

---

## 5. workflow.png

```yaml
asset: workflow.png
style_key: stage-decision
reference_image: references/workflow/stage-decision-sales.png
prompt_file: references/prompts/workflow.md#stage-decision
method: image-to-image
```

**主路径**

Browse → Cart → Order/Pay → (optional Comment AI expand) → Comment  
Side path from Message: AI XiaoJin chat (recommend/order tools) → escalate Human CS  

**English Prompt**

```text
[system instruction]
Stage + decision flowchart for user journey.
Main path left-to-right:
  Browse dishes → Cart → Submit order → Pay → Comment (optional AI expand).
One decision diamond after AI chat: "Need human?" → Yes to Human CS WebSocket / No continue AI.
Secondary short lane: Merchant Admin (orders, knowledge embed, sensitive words).
Clear stage labels, ≤2 branches, black thin arrows, accents #D81E06/#2D6A4F only.
```

---

## 6. Showcase / Preview（截图，不生图）

```yaml
method: screenshot
assets:
  - showcase-weapp-order.png      # 点餐
  - showcase-weapp-ai.png         # AI 小锦
  - showcase-weapp-comment.png    # 评论扩写
  - showcase-admin-kb.png         # 知识库
  - showcase-admin-cs.png         # 客服
  - showcase-admin-order.png      # 订单
```

气质参考：`references/showcase/` · `references/preview/`（仅质量条，勿当本产品图发布）。

截图前：起后端 8848 + 商家端 9527 + 小程序开发者工具；勿用说明图冒充。

---

## 7. 用户出图清单

1. 将本文件 §0 系统指令贴到对话开头  
2. 对 §1–§5 逐张：附上 skill 内 `reference_image` 做 img2img  
3. 导出 PNG → `assets/images/readme/`  
4. 刷新根 `README.md` 引用（已预留路径）  
5. Showcase 另开真机截图任务  
