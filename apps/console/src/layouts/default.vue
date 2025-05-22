<script setup lang="ts">
import type { DropdownMenuItem } from "@nuxt/ui"

const { user } = useUserSession()
const userMenuItems = ref<DropdownMenuItem[]>([
  [
    {
      label: "Logout",
      icon: "i-lucide-log-out",
      onSelect() {
        $fetch("/auth/logout").then(() => {
          window.location.href = "/"
        })
      },
    },
  ],
])

const colorMode = useColorMode()

const isDark = computed({
  get() {
    return colorMode.value === "dark"
  },
  set() {
    colorMode.preference = colorMode.value === "dark" ? "light" : "dark"
  },
})

const firstChars = (email: string) => {
  return email.split("@")[0].slice(0, 2)
}
</script>

<template>
  <div class="min-h-screen">
    <!-- Top Bar (custom implementation instead of UHeader) -->
    <header
      class="border-b border-gray-200 dark:border-gray-800 px-4 sm:px-6 lg:px-8 py-3 flex items-center justify-between"
    >
      <div class="flex items-center">
        <NuxtLink to="/" class="flex items-end gap-2 shrink-0">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 431 431"
            class="w-auto h-6"
            fill="none"
          >
            <polygon
              fill="var(--ui-primary)"
              points="246.98 181.05 194.02 331.99 24.01 95 246.98 181.05"
            />
            <polygon
              fill="var(--ui-primary)"
              points="187.01 386 255.5 188.02 329.96 279.51 187.01 386"
            />
            <polygon
              fill="var(--ui-primary)"
              points="334 267 259.97 176.5 334 111 334 267 334 267"
            />
            <polygon
              fill="var(--ui-primary)"
              points="247 170.99 109.03 115.47 80 31.01 247 170.99 247 170.99"
            />
            <polygon
              fill="var(--ui-primary)"
              points="180.91 332.06 189.78 346.32 170.04 399.57 107 385.49 180.91 332.06"
            />
            <polygon
              fill="var(--ui-primary)"
              points="407 161 344 161 344 112 407 161"
            />
          </svg>
          <h2 class="text-lg font-semibold text-(--ui-primary)">Graphoenix</h2>
        </NuxtLink>
      </div>
      <div class="flex items-center gap-4">
        <ClientOnly v-if="!colorMode?.forced">
          <UButton
            :icon="isDark ? 'i-lucide-moon' : 'i-lucide-sun'"
            color="neutral"
            variant="ghost"
            @click="isDark = !isDark"
          />

          <template #fallback>
            <div class="size-8" />
          </template>
        </ClientOnly>
        <UDropdownMenu :items="userMenuItems">
          <UButton
            color="primary"
            variant="ghost"
            trailing-icon="i-lucide-chevron-down"
          >
            <UAvatar :text="firstChars(user.email)" size="sm" class="mr-2" />
            {{ user.email }}
          </UButton>
        </UDropdownMenu>
      </div>
    </header>

    <!-- Main Content -->
    <main class="flex">
      <div
        class="flex-1 p-4 sm:p-6 lg:p-8 transition-all duration-300 ease-in-out"
      >
        <slot />
      </div>
    </main>
  </div>
</template>
