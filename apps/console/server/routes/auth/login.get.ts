import type { OAuthConfig, User } from "#auth-utils"
import type { H3Event } from "h3"

const { oauthProvider } = useRuntimeConfig()

const baseConfig: OAuthConfig<
  | OAuthAuth0Config
  | OAuthGitHubConfig
  | OAuthGitLabConfig
  | OAuthKeycloakConfig
  | OAuthMicrosoftConfig,
  { user: User; tokens?: { access_token: string } }
> = {
  async onSuccess(
    event: H3Event,
    { user, tokens }: { user: User; tokens?: { access_token: string } },
  ) {
    switch (oauthProvider) {
      case "github": {
        const emails = await $fetch<{ email: string; primary: boolean }[]>(
          "https://api.github.com/user/emails",
          {
            headers: {
              authorization: `bearer ${tokens?.access_token}`,
            },
          },
        )
        await setUserSession(event, {
          user: { ...user, email: emails.find((e) => e.primary)?.email ?? "" },
        })
        break
      }
      case "microsoft":
        await setUserSession(event, {
          user: { ...user, email: (user as User & { mail: string }).mail },
        })
        break
      default:
        await setUserSession(event, { user })
        break
    }
    const redirectUrl = getCookie(event, "redirectURL")
    deleteCookie(event, "redirectURL")
    return sendRedirect(event, redirectUrl ?? "/")
  },
  onError(event: H3Event, error: Error) {
    console.error("OAuth error:", error)
    return sendRedirect(event, "/")
  },
}

function buildAuthEventHandler(provider: string) {
  switch (provider) {
    case "auth0":
      return defineOAuthAuth0EventHandler(baseConfig)
    case "github":
      return defineOAuthGitHubEventHandler(baseConfig as never)
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
