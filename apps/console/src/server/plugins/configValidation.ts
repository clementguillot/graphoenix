import { z } from "zod/v4"

export default defineNitroPlugin(async () => {
  const runtimeConfig = useRuntimeConfig()

  const configResult = GraphoenixConsoleConfig.safeParse(runtimeConfig)
  if (configResult.error) {
    console.error(`Configuration error(s):`)
    console.error(z.prettifyError(configResult.error))
    process.exit(1)
  }

  let oauthConfigResult

  switch (configResult.data.oauthProvider) {
    case "auth0":
      oauthConfigResult = Auth0Config.safeParse(runtimeConfig.oauth.auth0)
      break
    case "github":
      oauthConfigResult = GitHubConfig.safeParse(runtimeConfig.oauth.github)
      break
    case "gitlab":
      oauthConfigResult = GitLabConfig.safeParse(runtimeConfig.oauth.gitlab)
      break
    case "keycloak":
      oauthConfigResult = KeycloakConfig.safeParse(runtimeConfig.oauth.keycloak)
      break
    case "microsoft":
      oauthConfigResult = MicrosoftConfig.safeParse(
        runtimeConfig.oauth.microsoft,
      )
      break
  }

  if (oauthConfigResult.error) {
    console.error(
      `OAuth provider '${configResult.data.oauthProvider}' configuration error(s):`,
    )
    console.error(z.prettifyError(oauthConfigResult.error))
    process.exit(1)
  }
})

const GraphoenixConsoleConfig = z.object({
  graphoenixServer: z.url(),
  oauthProvider: z.enum(["auth0", "github", "gitlab", "keycloak", "microsoft"]),
})

const Auth0Config = z.object({
  clientId: z.string().trim().min(1),
  clientSecret: z.string().trim().min(1),
  domain: z.string().trim().min(1).endsWith(".auth0.com"),
})

const GitHubConfig = z.object({
  clientId: z.string().trim().min(1),
  clientSecret: z.string().trim().min(1),
})

const GitLabConfig = z.object({
  clientId: z.string().trim().min(1),
  clientSecret: z.string().trim().min(1),
})

const KeycloakConfig = z.object({
  clientId: z.string().trim().min(1),
  clientSecret: z.string().trim().min(1),
  serverUrl: z.url(),
  realm: z.string().trim().min(1),
})

const MicrosoftConfig = z.object({
  clientId: z.string().trim().min(1),
  clientSecret: z.string().trim().min(1),
  tenant: z.uuid(),
})
