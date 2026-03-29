export type Task = {
  taskId: string
  projectName: string
  target: string
  startTime: Date
  endTime: Date
  cacheStatus: string
  params: string
  status: number
}
