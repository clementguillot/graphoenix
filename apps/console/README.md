# apps/console

The **console** is a web UI for accessing data from the [`server`](../server).

So far, only **authentication** (`authn`) is implemented, using OAuth with a selection of providers.

**Authorization** (`authz`) is **not yet** implemented.

This project uses Nuxt (a Vue.js metaframework).

If you want to learn more about Nuxt, please visit its [official website](https://nuxt.com/).

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
nx serve apps/console
```

The default listening port is `4200`: [http://localhost:4200](http://localhost:4200).

Please note that both the server and the console can be accessed through the Docker Compose reverse proxy at [http://localhost:10000](http://localhost:10000).

## Set up OAuth authentication

Authentication is implemented with [nuxt-auth-utils](https://github.com/atinux/nuxt-auth-utils/) module.

You must set the OAuth provider via environment variable:
```ini
NUXT_OAUTH_PROVIDER=auth0|github|gitlab|keycloak|microsoft
```

Supported OAuth providers and required parameters:
* **Auth0** (see [auth0.ts](https://github.com/atinux/nuxt-auth-utils/blob/main/src/runtime/server/lib/oauth/auth0.ts))
  * `clientId`
  * `clientSecret`
  * `domain` (must ends with `.auth0.com`)
* **GitHub** (see [github.ts](https://github.com/atinux/nuxt-auth-utils/blob/main/src/runtime/server/lib/oauth/github.ts))
  * `clientId`
  * `clientSecret`
* **GitLab** (see [gitlab.ts](https://github.com/atinux/nuxt-auth-utils/blob/main/src/runtime/server/lib/oauth/gitlab.ts))
  * `clientId`
  * `clientSecret`
* **Keycloak** (see [keycloak.ts](https://github.com/atinux/nuxt-auth-utils/blob/main/src/runtime/server/lib/oauth/keycloak.ts))
  * `clientId`
  * `clientSecret`
  * `serverUrl`
  * `realm`
* **Microsoft (including Azure AD)** (see [microsoft.ts](https://github.com/atinux/nuxt-auth-utils/blob/main/src/runtime/server/lib/oauth/microsoft.ts))
  * `clientId`
  * `clientSecret`
  * `tenant` (must be a valid UUID)

All settings can be defined at runtime via environment variables using the pattern:
```ini
NUXT_OAUTH_<PROVIDER>_<SETTING>
```

For example, to set the client secret (`clientSecret`) for the Microsoft provider, use:
```ini
NUXT_OAUTH_MICROSOFT_CLIENT_SECRET=your_secret_here
```
