import type { Organization } from "~/types/organization"

export default defineEventHandler(async (event): Promise<Organization[]> => {
  const { graphoenixServer } = useRuntimeConfig(event)

  return $fetch(`${graphoenixServer}/nx-cloud/private/organizations`)
})
