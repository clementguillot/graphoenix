import type { Organization } from "~/types/organization"

export default defineEventHandler(async (event): Promise<Organization> => {
  const { graphoenixServer } = useRuntimeConfig(event)

  const id = getRouterParam(event, "orgId")

  return $fetch(`${graphoenixServer}/nx-cloud/private/organizations/${id}`)
})
