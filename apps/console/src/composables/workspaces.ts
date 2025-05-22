import type { Workspace } from "~/types/workspace"

export const useWorkspaces = async (orgId: string) => {
  const { data: workspaces } = await useFetch<Workspace[]>(
    `/api/orgs/${orgId}/workspaces`,
    {
      key: `org-${orgId}-workspaces`,
    },
  )

  if (workspaces.value === null) {
    throw createError("Failed to fetch workspaces")
  }

  return workspaces as Ref<Workspace[]>
}

export const useWorkspace = async (orgId: string, workspaceId: string) => {
  const { data: workspaces } = useNuxtData<Workspace[]>(
    `org-${orgId}-workspaces`,
  )

  const { data: workspace } = await useFetch<Workspace>(
    `/api/orgs/${orgId}/workspaces/${workspaceId}`,
    {
      key: `org-${orgId}-workspace-${workspaceId}`,
      default() {
        return workspaces.value?.find(
          (workspace) =>
            workspace.id === workspaceId && workspace.orgId === orgId,
        )
      },
    },
  )

  if (workspace.value === null) {
    throw createError(`Failed to fetch workspace ${workspaceId}`)
  }

  return workspace as Ref<Workspace>
}
