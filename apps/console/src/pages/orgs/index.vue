<script setup lang="ts">
useHead({
  title: "Organizations",
})

const organizations = await useOrganizations()
</script>

<template>
  <div>
    <div>
      <h1 class="text-2xl">Organizations</h1>
    </div>

    <div class="mt-4">
      <div class="border rounded">
        <ul class="divide-y">
          <li v-for="organization in organizations" :key="organization.id">
            <NuxtLink :to="`/orgs/${organization.id}/workspaces`">
              <div class="flex p-4">
                <p>{{ organization.name }}</p>
              </div>
            </NuxtLink>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>
