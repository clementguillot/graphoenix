import type { OAuthConfig, User } from "#auth-utils"
import { defineOAuthMicrosoftEventHandler } from "#imports"

const { oauthProvider } = useRuntimeConfig()

const baseConfig: OAuthConfig<
  | OAuthAuth0Config
  | OAuthGitHubConfig
  | OAuthGitLabConfig
  | OAuthKeycloakConfig
  | OAuthMicrosoftConfig,
  { user: User; tokens?: { access_token: string } }
> = {
  async onSuccess(event, { user, tokens }) {
    if (oauthProvider === "github" && tokens) {
      const emails = await $fetch<{ email: string; primary: boolean }[]>(
        "https://api.github.com/user/emails",
        {
          headers: {
            authorization: `bearer ${tokens.access_token}`,
          },
        },
      )
      await setUserSession(event, {
        user: { ...user, email: emails.find((e) => e.primary)?.email },
      })
    } else {
      await setUserSession(event, { user })
    }
    const redirectUrl = getCookie(event, "redirectURL")
    deleteCookie(event, "redirectURL")
    return sendRedirect(event, redirectUrl ?? "/")
  },
  // Optional, will return a json error and 401 status code by default
  onError(event, error) {
    console.error("OAuth error:", error)
    return sendRedirect(event, "/")
  },
}

function buildAuthEventHandler(provider: string) {
  switch (provider) {
    case "auth0":
      return defineOAuthAuth0EventHandler(baseConfig)
    case "github":
      return defineOAuthGitHubEventHandler(baseConfig)
    case "gitlab":
      return defineOAuthGitLabEventHandler(baseConfig)
    case "keycloak":
      return defineOAuthKeycloakEventHandler(baseConfig)
    case "microsoft":
      return defineOAuthMicrosoftEventHandler(baseConfig)
    default:
      throw new Error(`Unknown OAuth provider: ${provider}`)
  }
}

export default buildAuthEventHandler(oauthProvider)
