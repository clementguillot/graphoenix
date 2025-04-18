<script setup lang="ts">
const { user } = useUserSession()
</script>

<template>
  <div>
    <h2>Hello {{ user.email }}</h2>
  </div>
</template>
