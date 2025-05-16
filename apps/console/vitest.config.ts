/// <reference types='vitest' />
import { defineVitestConfig } from "@nuxt/test-utils/config"

export default defineVitestConfig({
  root: __dirname,
  cacheDir: "../../node_modules/.vite/apps/console",
  // plugins: [vue(), nxViteTsPaths(), nxCopyAssetsPlugin(["*.md"])],
  // Uncomment this if you are using workers.
  // worker: {
  //  plugins: [ nxViteTsPaths() ],
  // },
  test: {
    passWithNoTests: true,
    watch: false,
    globals: true,
    environment: "nuxt",
    // environmentOptions: {
    //   nuxt: {
    //     overrides: {
    //       hooks: {
    //         "app:templates": (app) => {},
    //         ["app:resolve"]: (app) => {
    //           console.log("app:resolve", app)
    //           app.middleware = []
    //           const process = await import("node:process")
    //           if (String(process.env?.TEST) === "true") {
    //             app.middleware = []
    //           }
    //         },
    //       },
    //     },
    //   },
    // },
    // environment: "jsdom",
    include: ["{src,tests}/**/*.{test,spec}.{js,mjs,cjs,ts,mts,cts,jsx,tsx}"],
    reporters: ["default"],
    coverage: {
      reportsDirectory: "../../coverage/apps/console",
      provider: "v8" as const,
    },
  },
})
