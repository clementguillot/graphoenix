export default defineEventHandler(
  async (
    event,
  ): Promise<{
    id: string
    linkId: string
    status: number
    command: string
    branch?: string
    startTime: string
    endTime: string
    workspaceId: string
  }> => {
    const { graphoenixServer } = useRuntimeConfig(event)

    const linkId = getRouterParam(event, "linkId")

    return $fetch(`${graphoenixServer}/nx-cloud/private/runs/${linkId}`)
  },
)
