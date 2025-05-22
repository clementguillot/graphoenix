import type { Workspace } from "~/types/workspace"

export default defineEventHandler(async (event): Promise<Workspace[]> => {
  const { graphoenixServer } = useRuntimeConfig(event)

  const orgId = getRouterParam(event, "orgId")

  return $fetch(
    `${graphoenixServer}/nx-cloud/private/organizations/${orgId}/workspaces`,
  )
})
