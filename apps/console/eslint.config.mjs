import { createConfigForNuxt } from "@nuxt/eslint-config"
import baseConfig from "../../eslint.config.mjs"

export default createConfigForNuxt({
  features: {
    typescript: true,
  },
})
  .prepend(...baseConfig)
  .append(
    {
      files: ["**/*.ts", "**/*.tsx", "**/*.js", "**/*.jsx", "**/*.vue"],
      rules: {
        "vue/multi-word-component-names": "off",
      },
    },
    {
      ignores: [
        ".nuxt/**",
        ".output/**",
        "node_modules",
        "**/*.d.ts",
        "**/*.vue.js",
      ],
    },
  )
