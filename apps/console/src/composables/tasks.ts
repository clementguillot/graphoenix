import type { Task } from "~/types/task"
import type { AsyncDataRequestStatus } from "nuxt/app"

export const useTasks = async (
  linkId: string,
  workspaceId: string,
  pageIndex: Ref<number>,
  pageSize: Ref<number>,
) => {
  const { data, status } = await useLazyFetch(
    `/api/runs/${linkId}/${workspaceId}/tasks`,
    {
      key: `run-${linkId}-${workspaceId}-tasks-${pageIndex.value}-${pageSize.value}`,
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
    },
  )
  return { data, status } as {
    data: Ref<{ items: Task[]; totalCount: number }>
    status: Ref<AsyncDataRequestStatus>
  }
}
