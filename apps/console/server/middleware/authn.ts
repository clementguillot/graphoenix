export default defineEventHandler(async (event) => {
  const pathname = getRequestURL(event).pathname

  if (!pathname.startsWith("/api/") || whiteList.includes(pathname)) {
    return
  }

  const session = await getUserSession(event)
  if (!session.user) {
    return createError({
      statusCode: 401,
      message: "Unauthorized",
    })
  }
})

const whiteList = [
  "/auth/login",
  "/auth/logout",
  "/api/_auth/session",
  "/api/_nuxt_icon/lucide.json",
]
