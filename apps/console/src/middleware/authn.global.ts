export default defineNuxtRouteMiddleware((to) => {
  const { loggedIn } = useUserSession()
  const redirectUrl = useCookie("redirectURL")

  if (!loggedIn.value) {
    redirectUrl.value = to.path
    return navigateTo("/auth/login", { external: true })
  }
})
