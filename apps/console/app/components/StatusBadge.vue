<script setup lang="ts">
const props = defineProps<{
  status: number
}>()

function buildConfiguration(): {
  text: string
  icon: string
  color: "success" | "warning" | "error"
} {
  switch (props.status) {
    case 0:
      return {
        text: "Success",
        color: "success",
        icon: "i-lucide-circle-check",
      }
    case 2:
      return {
        text: "Cancelled",
        color: "warning",
        icon: "i-lucide-circle-alert",
      }
    case 1:
    default:
      return {
        text: "Failure",
        color: "error",
        icon: "i-lucide-circle-x",
      }
  }
}
</script>

<template>
  <UBadge
    :color="buildConfiguration().color"
    :icon="buildConfiguration().icon"
    variant="soft"
  >
    {{ buildConfiguration().text }}
  </UBadge>
</template>
