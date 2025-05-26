import { nxViteTsPaths } from "@nx/vite/plugins/nx-tsconfig-paths.plugin"
import { defineNuxtConfig } from "nuxt/config"

// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  modules: [
    "@compodium/nuxt",
    "@nuxt/test-utils/module",
    "@nuxt/ui",
    "@vueuse/nuxt",
    "nuxt-auth-utils",
  ],

  imports: {
    autoImport: true,
  },
  devtools: { enabled: true },

  app: {
    head: {
      link: [
        { rel: "icon", type: "image/x-icon", href: "/favicon.ico" },
        { rel: "icon", type: "image/svg+xml", href: "/favicon.svg" },
      ],
    },
  },

  css: ["~/assets/css/main.css"],

  runtimeConfig: {
    // Graphoenix Server endpoint
    graphoenixServer: "",
    // Required OAuth provider to set
    oauthProvider: "",
    // Default OAuth provider settings to ensure integration with Graphoenix
    oauth: {
      auth0: {
        scope: ["openid", "email"],
      },
      github: {
        scope: ["user:email"],
      },
      microsoft: {
        scope: ["openid", "email"],
      },
    },
  },
  srcDir: "src",
  workspaceDir: "../../",

  devServer: {
    host: "localhost",
    port: 4200,
  },

  compatibilityDate: "2025-04-03",

  vite: {
    plugins: [nxViteTsPaths()],
    server: {
      allowedHosts: true,
    },
  },

  typescript: {
    typeCheck: true,
    tsConfig: {
      extends: "../tsconfig.app.json", // Nuxt copies this string as-is to the `./.nuxt/tsconfig.json`, therefore it needs to be relative to that directory
    },
  },
})
