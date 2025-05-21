<script setup lang="ts">
import type { TableColumn } from "@nuxt/ui"
import type { Task } from "~/types/task"
import { useDateFormat } from "@vueuse/shared"

const StatusBadge = resolveComponent("StatusBadge")

const route = useRoute()
const linkId = route.params.linkId

const run = await useRun(linkId)

const pageIndex = ref(0)
const pageSize = ref(10)
const updatePage = (page: number) => {
  pageIndex.value = page - 1
}

const { data, status } = await useTasks(
  linkId,
  run.value.workspaceId,
  pageIndex,
  pageSize,
)

const tasks = computed(() => data.value?.items ?? [])
const totalCount = computed(() => data.value?.totalCount ?? 0)
const pending = computed(() => status.value === "pending")

const columns: TableColumn<Task>[] = [
  {
    accessorKey: "taskId",
    header: "Task",
  },
  {
    header: "Status",
    accessorKey: "status",
    cell: ({ row }) =>
      h(StatusBadge, { status: row.getValue("status") as number }),
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
    header: "Cache",
    accessorKey: "cacheStatus",
    cell: ({ row }) => {
      switch (row.getValue("cacheStatus")) {
        case "remote-cache-hit":
          return h("div", {}, "Remote cache hit")
        case "local-cache-hit":
          return h("div", {}, "Local cache hit")
        default:
          return h("div", {}, "Cache miss")
      }
    },
  },
]

useHead({
  title: `Run - ${run.value.command}`,
})
</script>

<template>
  <div>
    <div>
      <h1 class="text-2xl">{{ run.command }}</h1>
    </div>
    <div class="mt-4">
      <div class="flex flex-row">
        <StatusBadge :status="run.status" />
        <span class="ml-2"
          >{{ useDateFormat(run.startTime, "YYYY-MM-DD HH:mm:ss") }} -
          {{ useDateFormat(run.endTime) }}</span
        >
      </div>
    </div>
    <div class="border rounded mt-4">
      <UTable
        ref="table"
        :data="tasks"
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
