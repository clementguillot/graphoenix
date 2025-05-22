export type Run = {
  id: string
  linkId: string
  status: number
  command: string
  branch?: string
  startTime: Date
  endTime: Date
  workspaceId: string
}
