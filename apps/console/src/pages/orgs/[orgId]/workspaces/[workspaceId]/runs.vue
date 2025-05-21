<script setup lang="ts">
import type { TableColumn } from "@nuxt/ui"
import type { Run } from "~/types/run"

const NuxtLink = resolveComponent("NuxtLink")
const StatusBadge = resolveComponent("StatusBadge")

const route = useRoute()
const orgId = route.params.orgId
const workspaceId = route.params.workspaceId

const workspace = await useWorkspace(orgId, workspaceId)

const pageIndex = ref(0)
const pageSize = ref(10)
const updatePage = (page: number) => {
  pageIndex.value = page - 1
}

const { data, status } = await useRuns(orgId, workspaceId, pageIndex, pageSize)

const runs = computed(() => data.value?.items ?? [])
const totalCount = computed(() => data.value?.totalCount ?? 0)
const pending = computed(() => status.value === "pending")

const columns: TableColumn<Run>[] = [
  {
    accessorKey: "command",
    header: "Command",
    cell: ({ row }) =>
      h(
        NuxtLink,
        {
          to: `/runs/${row.original.linkId}`,
        },
        () => row.getValue("command"),
      ),
  },
  {
    accessorKey: "status",
    header: "Status",
    cell: ({ row }) =>
      h(StatusBadge, { status: row.getValue("status") as number }),
  },
  {
    header: "End Time",
    accessorKey: "endTime",
    cell: ({ row }) =>
      h(
        "div",
        {},
        useDateFormat(row.getValue("endTime"), "YYYY-MM-DD HH:mm:ss").value,
      ),
  },
  {
    header: "Duration",
    cell: ({ row }) => {
      const diffMs = Math.abs(row.original.endTime - row.original.startTime)

      const minutes = Math.floor(diffMs / (1000 * 60))
      const seconds = Math.floor((diffMs % (1000 * 60)) / 1000)

      return h(
        "div",
        {},
        minutes > 0 ? `${minutes}m ${seconds}s` : `${seconds}s`,
      )
    },
  },
  {
    accessorKey: "branch",
    header: "Branch",
    cell: ({ row }) => h("div", {}, row.getValue("branch") ?? "no branch"),
  },
]

useHead({
  title: `Runs - ${workspace.value.name}`,
})
</script>

<template>
  <div>
    <div>
      <h1 class="text-2xl">{{ workspace.name }} - Runs</h1>
    </div>
    <div class="border rounded mt-4">
      <UTable
        ref="table"
        :data="runs"
        :columns="columns"
        :loading="pending"
        :pagination-options="{
          manualPagination: true,
          rowCount: totalCount,
        }"
        class="flex-1"
      />
      <div class="flex justify-center border-t border-default pt-4 pb-4">
        <UPagination
          :default-page="pageIndex + 1"
          :items-per-page="pageSize"
          :total="totalCount"
          @update:page="updatePage"
        />
      </div>
    </div>
  </div>
</template>
