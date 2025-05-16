import { nxViteTsPaths } from "@nx/vite/plugins/nx-tsconfig-paths.plugin"
import { defineNuxtConfig } from "nuxt/config"

// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  workspaceDir: "../../",
  srcDir: "src",
  devtools: { enabled: true },

  devServer: {
    host: "localhost",
    port: 4200,
  },

  typescript: {
    typeCheck: true,
    tsConfig: {
      extends: "../tsconfig.app.json", // Nuxt copies this string as-is to the `./.nuxt/tsconfig.json`, therefore it needs to be relative to that directory
    },
  },

  modules: [
    "@nuxt/test-utils/module",
    "@nuxt/ui",
    "@compodium/nuxt",
    "nuxt-auth-utils",
  ],

  imports: {
    autoImport: true,
  },

  css: ["~/assets/css/main.css"],

  vite: {
    plugins: [nxViteTsPaths()],
  },

  compatibilityDate: "2025-04-03",

  app: {
    head: {
      link: [
        { rel: "icon", type: "image/x-icon", href: "/favicon.ico" },
        { rel: "icon", type: "image/svg+xml", href: "/favicon.svg" },
      ],
    },
  },
})
