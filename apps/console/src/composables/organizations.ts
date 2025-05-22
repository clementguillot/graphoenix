import type { Organization } from "~/types/organization"

export const useOrganizations = async () => {
  const { data } = await useFetch<Organization[]>(`/api/orgs`, {
    key: "orgs",
  })

  if (data.value === null) {
    throw createError("Failed to fetch organizations")
  }

  return data as Ref<Organization[]>
}

export const useOrganization = async (orgId: string) => {
  const { data: orgs } = useNuxtData<Organization[]>("orgs")

  const { data: org } = await useFetch<Organization>(`/api/orgs/${orgId}`, {
    key: `org-${orgId}`,
    default() {
      return orgs.value?.find((org) => org.id === orgId)
    },
  })

  if (!org.value) {
    throw createError(`Failed to fetch organization ${orgId}`)
  }

  return org as Ref<Organization>
}
