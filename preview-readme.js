(function () {
  const cfg = window.__PREVIEW_README__ || {};
  const content = document.getElementById("content");
  const status = document.getElementById("status");
  const reloadBtn = document.getElementById("reload");

  function setStatus(text) {
    if (status) status.textContent = text;
  }

  async function loadReadme() {
    if (location.protocol === "file:") {
      content.innerHTML =
        '<p class="error">' +
        (cfg.fileProtocolTip ||
          "请使用 HTTP 打开本页，不要用 file://") +
        "</p>";
      setStatus("file:// 不可用");
      return;
    }

    setStatus("加载中…");
    try {
      const res = await fetch(cfg.readmePath || "./README.md", {
        cache: "no-store",
      });
      if (!res.ok) throw new Error("HTTP " + res.status);
      const md = await res.text();
      content.innerHTML = marked.parse(md);
      setStatus("已加载 · " + new Date().toLocaleTimeString());
      document.title = (cfg.title || "README") + " · Preview";
    } catch (err) {
      content.innerHTML =
        '<p class="error">加载 README 失败：' +
        String(err && err.message ? err.message : err) +
        "\n\n请在仓库根执行：python -m http.server " +
        (cfg.portHint || 8091) +
        "</p>";
      setStatus("失败");
    }
  }

  if (reloadBtn) reloadBtn.addEventListener("click", loadReadme);
  loadReadme();
})();
