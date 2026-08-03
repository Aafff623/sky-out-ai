module.exports = {
  // 继承推荐规范配置
  extends: [
    "stylelint-config-standard",
    "stylelint-config-recommended-scss",
    "stylelint-config-recommended-vue/scss",
    "stylelint-config-html/vue",
    "stylelint-config-recess-order",
  ],
  // 指定不同文件对应的解析器，overrides允许你为特定类型的文件指定特定的规则或者处理方式
  overrides: [
    // 对.vue和.html文件采用postcss-html的自定义语法
    {
      files: ["**/*.{vue,html}"],
      customSyntax: "postcss-html",
    },
    // 对.css和.scss文件采用postcss-scss的自定义语法
    {
      files: ["**/*.{css,scss}"],
      customSyntax: "postcss-scss",
    },
  ],
  // 自定义规则
  rules: {
    // 指定导入CSS文件的方式("string"|"url")
    "import-notation": "string",
    // 选择器类名命名规则
    "selector-class-pattern": null,
    // 自定义属性命名规则
    "custom-property-pattern": null,
    // 动画帧节点样式命名规则
    "keyframes-name-pattern": null,
    // 允许无降序特异性
    "no-descending-specificity": null,
    // 允许空style标签存在
    "no-empty-source": null,
    // 允许使用!important
    "declaration-no-important": null,
    // 允许 :global 、:export 、:deep伪类
    "selector-pseudo-class-no-unknown": [
      true,
      {
        ignorePseudoClasses: ["global", "export", "deep"],
      },
    ],
    // 允许未知属性（也就是自定义属性）
    "property-no-unknown": [
      true,
      {
        // 指定我们自定义的属性名
        ignoreProperties: ["bgColor"],
      },
    ],
  },
};
