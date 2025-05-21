<script setup lang="ts">
definePageMeta({
  middleware: (from) => {
    return navigateTo(`${from.fullPath}/workspaces`)
  },
})
</script>

<template>
  <div />
</template>
