import { createConfigForNuxt } from "@nuxt/eslint-config"
import baseConfig from "../../eslint.config.mjs"

export default createConfigForNuxt({
  features: {
    nuxt: {
      sortConfigKeys: true,
    },
  },
  dirs: {
    src: ["src"],
  },
}).prepend(...baseConfig)
