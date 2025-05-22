export default defineEventHandler(
  async (
    event,
  ): Promise<{
    items: {
      id: string
      linkId: string
      status: number
      command: string
      branch?: string
      startTime: string
      endTime: string
      workspaceId: string
    }[]
    totalCount: number
  }> => {
    const { graphoenixServer } = useRuntimeConfig(event)

    const workspaceId = getRouterParam(event, "workspaceId")

    const query = getQuery(event)
    const pageIndex = parseInt(query.pageIndex) ?? 0
    const pageSize = parseInt(query.pageSize) ?? 20

    return $fetch(
      `${graphoenixServer}/nx-cloud/private/workspaces/${workspaceId}/runs?pageIndex=${pageIndex}&pageSize=${pageSize}`,
    )
  },
)
