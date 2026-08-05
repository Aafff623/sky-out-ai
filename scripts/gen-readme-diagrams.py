# -*- coding: utf-8 -*-
"""Generate readable README diagrams for JinShiFang (sky-ai)."""
from __future__ import annotations

from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

OUT = Path(__file__).resolve().parents[1] / "assets" / "images" / "readme"
PAPER = (247, 245, 242)
WHITE = (255, 255, 255)
INK = (26, 26, 26)
MUTE = (107, 107, 107)
BRAND = (216, 30, 6)
FRESH = (45, 106, 79)
LINE = (220, 216, 210)


def font(size: int, bold: bool = False) -> ImageFont.FreeTypeFont | ImageFont.ImageFont:
    candidates = [
        r"C:\Windows\Fonts\msyhbd.ttc" if bold else r"C:\Windows\Fonts\msyh.ttc",
        r"C:\Windows\Fonts\msyh.ttc",
        r"C:\Windows\Fonts\segoeui.ttf",
        r"C:\Windows\Fonts\arial.ttf",
    ]
    for path in candidates:
        try:
            return ImageFont.truetype(path, size=size)
        except OSError:
            continue
    return ImageFont.load_default()


def rounded(draw: ImageDraw.ImageDraw, box, fill, outline=LINE, radius=16, width=2):
    draw.rounded_rectangle(box, radius=radius, fill=fill, outline=outline, width=width)


def center_text(draw, xy, text, fnt, fill=INK):
    x, y = xy
    bbox = draw.textbbox((0, 0), text, font=fnt)
    tw, th = bbox[2] - bbox[0], bbox[3] - bbox[1]
    draw.text((x - tw / 2, y - th / 2), text, font=fnt, fill=fill)


def features():
    w, h = 1600, 900
    img = Image.new("RGB", (w, h), PAPER)
    d = ImageDraw.Draw(img)
    title = font(44, True)
    sub = font(22)
    card_t = font(28, True)
    card_s = font(20)
    center_text(d, (w / 2, 70), "JinShiFang · Core Capabilities", title)
    center_text(d, (w / 2, 120), "AI modules + classic takeout operations", sub, MUTE)

    cards = [
        ("AI Customer Service", "XiaoJin · SSE · RAG · Tools"),
        ("Dish Recommend", "Preference → JSON → degrade"),
        ("Comment AI", "Expand · polish · auto-reply"),
        ("Knowledge RAG", "Embed docs · Redis vectors"),
        ("Sensitive Filter", "Green + local DFA"),
        ("Takeout Ops", "Dish · order · staff · report"),
    ]
    cols, rows = 3, 2
    margin_x, top = 80, 180
    gap = 28
    cw = (w - margin_x * 2 - gap * (cols - 1)) // cols
    ch = 260
    for i, (t, s) in enumerate(cards):
        r, c = divmod(i, cols) if False else (i // cols, i % cols)
        x0 = margin_x + c * (cw + gap)
        y0 = top + r * (ch + gap)
        rounded(d, (x0, y0, x0 + cw, y0 + ch), WHITE, radius=20)
        # accent bar
        d.rounded_rectangle((x0, y0, x0 + 12, y0 + ch), radius=6, fill=BRAND if i < 5 else FRESH)
        # icon circle
        cx, cy = x0 + cw // 2, y0 + 78
        d.ellipse((cx - 36, cy - 36, cx + 36, cy + 36), outline=FRESH if i == 5 else BRAND, width=3)
        center_text(d, (cx, cy), str(i + 1), font(28, True), BRAND if i < 5 else FRESH)
        center_text(d, (x0 + cw / 2, y0 + 150), t, card_t)
        center_text(d, (x0 + cw / 2, y0 + 195), s, card_s, MUTE)
    img.save(OUT / "features.png", optimize=True)


def architecture():
    w, h = 1600, 900
    img = Image.new("RGB", (w, h), PAPER)
    d = ImageDraw.Draw(img)
    title = font(40, True)
    label = font(22, True)
    small = font(18)
    center_text(d, (w / 2, 48), "JinShiFang Architecture", title)

    # clients
    clients = [(120, 160, "WeChat Mini Program"), (120, 280, "Merchant Vue Admin")]
    for x, y, t in clients:
        rounded(d, (x, y, x + 280, y + 80), WHITE)
        center_text(d, (x + 140, y + 40), t, label)

    # access
    rounded(d, (480, 200, 760, 320), WHITE)
    center_text(d, (620, 240), "Access Layer", label)
    center_text(d, (620, 280), "JWT · SSE · WebSocket", small, MUTE)

    # server
    rounded(d, (860, 140, 1240, 380), WHITE)
    center_text(d, (1050, 175), "Spring Boot :8848", label)
    center_text(d, (1050, 215), "training-web", small, MUTE)
    rounded(d, (900, 250, 1200, 350), (255, 245, 243), BRAND)
    center_text(d, (1050, 285), "AI Layer", label, BRAND)
    center_text(d, (1050, 320), "LangChain4j + Qwen", small, MUTE)

    # data
    stores = [("MySQL", 420), ("Redis", 700), ("MongoDB", 980), ("MinIO", 1260)]
    for name, x in stores:
        rounded(d, (x, 520, x + 200, 620), WHITE, FRESH)
        center_text(d, (x + 100, 570), name, label, FRESH)

    # external
    rounded(d, (1320, 160, 1540, 360), WHITE)
    center_text(d, (1430, 200), "External", label)
    for i, t in enumerate(["Bailian Qwen", "Green Scan", "WeChat Login"]):
        center_text(d, (1430, 250 + i * 30), t, small, MUTE)

    # arrows
    d.line((400, 200, 480, 240), fill=INK, width=3)
    d.line((400, 320, 480, 280), fill=INK, width=3)
    d.line((760, 260, 860, 260), fill=INK, width=3)
    d.line((1240, 260, 1320, 260), fill=MUTE, width=2)
    for _name, sx in stores:
        d.line((1050, 380, sx + 100, 520), fill=LINE, width=2)

    center_text(d, (w / 2, 700), "Data plane", small, MUTE)
    center_text(
        d,
        (w / 2, 820),
        "Clients → Access → training-web (+ AI) → MySQL / Redis / MongoDB / MinIO",
        small,
        MUTE,
    )
    img.save(OUT / "architecture.png", optimize=True)


def tech_stack():
    w, h = 1600, 900
    img = Image.new("RGB", (w, h), PAPER)
    d = ImageDraw.Draw(img)
    center_text(d, (w / 2, 50), "JinShiFang · Tech Stack", font(40, True))
    rows = [
        ("Backend", ["Java 17", "Spring Boot 3.4", "MyBatis", "LangChain4j"]),
        ("Frontend", ["Vue 3", "Vite", "Element Plus", "uni-app", "Pinia"]),
        ("Data", ["MySQL", "Redis", "MongoDB", "MinIO"]),
        ("AI / Infra", ["Qwen", "SSE", "WebSocket", "Aliyun Green"]),
    ]
    y = 130
    for name, items in rows:
        rounded(d, (80, y, 1520, y + 140), WHITE)
        d.rounded_rectangle((80, y, 100, y + 140), radius=4, fill=BRAND if name != "Data" else FRESH)
        center_text(d, (220, y + 70), name, font(26, True))
        x = 380
        for item in items:
            bw = 200
            rounded(d, (x, y + 35, x + bw, y + 105), PAPER, LINE, radius=12)
            center_text(d, (x + bw / 2, y + 70), item, font(20, True))
            x += bw + 24
        y += 170
    img.save(OUT / "tech-stack.png", optimize=True)


def workflow():
    w, h = 1600, 900
    img = Image.new("RGB", (w, h), PAPER)
    d = ImageDraw.Draw(img)
    center_text(d, (w / 2, 40), "JinShiFang · User Journey", font(40, True))
    steps = ["Browse", "Cart", "Order", "Pay", "Comment\n(+ AI expand)"]
    y = 160
    x = 80
    boxes = []
    for i, s in enumerate(steps):
        box = (x, y, x + 220, y + 110)
        boxes.append(box)
        rounded(d, box, WHITE, BRAND if i == 4 else LINE, radius=14)
        lines = s.split("\n")
        if len(lines) == 1:
            center_text(d, (x + 110, y + 55), s, font(24, True))
        else:
            center_text(d, (x + 110, y + 40), lines[0], font(24, True))
            center_text(d, (x + 110, y + 75), lines[1], font(18), MUTE)
        if i < len(steps) - 1:
            d.line((x + 220, y + 55, x + 260, y + 55), fill=INK, width=3)
            d.polygon(
                [(x + 260, y + 55), (x + 248, y + 48), (x + 248, y + 62)],
                fill=INK,
            )
        x += 280

    # AI path
    rounded(d, (200, 380, 520, 500), WHITE, BRAND)
    center_text(d, (360, 420), "Message · AI XiaoJin", font(24, True), BRAND)
    center_text(d, (360, 460), "Recommend / Order Tools / RAG", font(18), MUTE)

    # diamond
    cx, cy = 760, 440
    diamond = [(cx, cy - 55), (cx + 90, cy), (cx, cy + 55), (cx - 90, cy)]
    d.polygon(diamond, fill=WHITE, outline=BRAND)
    d.line(diamond + [diamond[0]], fill=BRAND, width=2)
    center_text(d, (cx, cy), "Need human?", font(18, True), BRAND)

    rounded(d, (980, 360, 1320, 440), WHITE, FRESH)
    center_text(d, (1150, 400), "Human CS · WebSocket", font(22, True), FRESH)
    rounded(d, (980, 470, 1320, 550), WHITE)
    center_text(d, (1150, 510), "Continue AI chat", font(22, True))

    d.line((520, 440, cx - 90, 440), fill=INK, width=3)
    d.line((cx + 90, 415, 980, 400), fill=FRESH, width=3)
    d.line((cx + 90, 465, 980, 510), fill=INK, width=3)

    # merchant lane
    rounded(d, (120, 650, 1480, 820), WHITE)
    center_text(d, (w / 2, 690), "Merchant Admin lane", font(24, True))
    items = ["Orders", "Knowledge embed", "Sensitive words", "Staff CS"]
    ix = 220
    for t in items:
        rounded(d, (ix, 730, ix + 240, 790), PAPER, FRESH, radius=12)
        center_text(d, (ix + 120, 760), t, font(20, True), FRESH)
        ix += 280

    img.save(OUT / "workflow.png", optimize=True)


def main():
    OUT.mkdir(parents=True, exist_ok=True)
    features()
    architecture()
    tech_stack()
    workflow()
    print("wrote:", ", ".join(p.name for p in OUT.glob("*.png")))


if __name__ == "__main__":
    main()
