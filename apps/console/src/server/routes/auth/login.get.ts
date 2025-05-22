export default defineOAuthKeycloakEventHandler({
  config: {
    clientId: "graphoenix-console",
    clientSecret: "HKrT2ehfkpR4uv8r2qwfg10tDK1A9XdR",
    serverUrl: "http://localhost:8090",
    realm: "graphoenix",
  },
  async onSuccess(event, { user }) {
    await setUserSession(event, { user })
    const redirectUrl = getCookie(event, "redirectURL")
    deleteCookie(event, "redirectURL")
    return sendRedirect(event, redirectUrl ?? "/")
  },
  // Optional, will return a json error and 401 status code by default
  onError(event, error) {
    console.error("OAuth error:", error)
    return sendRedirect(event, "/")
  },
})
