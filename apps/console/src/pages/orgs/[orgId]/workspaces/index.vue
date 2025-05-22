<script setup lang="ts">
const route = useRoute()

const org = await useOrganization(route.params.orgId)
const workspaces = await useWorkspaces(route.params.orgId)

useHead({
  title: `Organization - ${org.value.name}`,
})
</script>

<template>
  <div>
    <div>
      <h1 class="text-2xl">
        {{ org.name }}
      </h1>
    </div>

    <div class="mt-4">
      <div class="border rounded">
        <ul class="divide-y">
          <li v-for="workspace in workspaces" :key="workspace.id">
            <NuxtLink
              :to="`/orgs/${workspace.orgId}/workspaces/${workspace.id}/runs`"
            >
              <div class="flex p-4">
                <p>{{ workspace.name }}</p>
              </div>
            </NuxtLink>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>
