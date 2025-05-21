import type { AsyncDataRequestStatus } from "nuxt/app"
import type { Run } from "~/types/run"

export const useRuns = async (
  orgId: string,
  workspaceId: string,
  pageIndex: Ref<number>,
  pageSize: Ref<number>,
) => {
  const { data, status } = await useLazyFetch<{
    items: {
      id: string
      linkId: string
      status: number
      command: string
      branch?: string
      startTime: string
      endTime: string
    }[]
    totalCount: number
  }>(`/api/orgs/${orgId}/workspaces/${workspaceId}/runs`, {
    key: `org-${orgId}-workspace-${workspaceId}-runs-${pageIndex.value}-${pageSize.value}`,
    query: {
      pageIndex,
      pageSize,
    },
    transform: (data) => ({
      totalCount: data.totalCount,
      items: data.items.map((item) => ({
        ...item,
        startTime: new Date(item.startTime),
        endTime: new Date(item.endTime),
      })),
    }),
  })

  return { data, status } as {
    data: Ref<{ items: Run[]; totalCount: number }>
    status: Ref<AsyncDataRequestStatus>
  }
}

export const useRun = async (linkId: string) => {
  const { data: run } = await useFetch<Run>(`/api/runs/${linkId}`, {
    key: `run-${linkId}`,
  })

  if (run.value === null) {
    throw createError(`Failed to fetch run ${linkId}`)
  }

  return run as Ref<Run>
}
