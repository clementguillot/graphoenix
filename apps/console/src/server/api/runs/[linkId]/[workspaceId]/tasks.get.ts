export default defineEventHandler(
  async (
    event,
  ): Promise<{
    items: {
      taskId: string
      projectName: string
      target: string
      startTime: string
      endTime?: string
      cacheStatus?: string
      params: string
    }[]
    totalCount: number
  }> => {
    const { graphoenixServer } = useRuntimeConfig(event)

    const linkId = getRouterParam(event, "linkId")
    const workspaceId = getRouterParam(event, "workspaceId")

    const query = getQuery(event)
    const pageIndex = parseInt(query.pageIndex) ?? 0
    const pageSize = parseInt(query.pageSize) ?? 20

    return $fetch(
      `${graphoenixServer}/nx-cloud/private/runs/${linkId}/${workspaceId}/tasks?pageIndex=${pageIndex}&pageSize=${pageSize}`,
    )
  },
)
