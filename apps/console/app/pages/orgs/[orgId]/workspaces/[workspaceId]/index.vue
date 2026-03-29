<script setup lang="ts">
definePageMeta({
  middleware: (from) => {
    return navigateTo(`${from.fullPath}/runs`)
  },
})
</script>

<template>
  <div />
</template>
