import olefile, re, os, sys

src = sys.argv[1]
dst = sys.argv[2]

ole = olefile.OleFileIO(src)
raw = ole.openstream('WordDocument').read()  # 只读正文 stream，避免 1Table/0Table 噪音
ole.close()

text = raw.decode('utf-16-le', errors='ignore')
# 保留中文/日文/韩文、ASCII 可见字符、常用标点，丢弃其余
text = re.sub(r'[^一-鿿　-〿＀-￯\w\s.,;:!?()\[\]{}\-+/\\@#$%^&*"\'<>~·×—…《》【】（）]', '', text)
# 合并连续空白
text = re.sub(r'[ \t]{2,}', ' ', text)
lines = []
for ln in text.splitlines():
    s = ln.strip()
    if not s:
        continue
    # 跳过明显的二进制残留（<30% 中文/ascii 的可打印行）
    printable = sum(1 for c in s if c.isprintable() and not c.isspace())
    if printable and (printable / max(len(s), 1)) < 0.4:
        continue
    lines.append(s)

out = '\n'.join(lines)
os.makedirs(os.path.dirname(dst), exist_ok=True)
with open(dst, 'w', encoding='utf-8') as f:
    f.write(out)
print('lines:', len(lines), 'chars:', len(out))
