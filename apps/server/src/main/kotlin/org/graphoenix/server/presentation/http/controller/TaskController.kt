package org.graphoenix.server.presentation.http.controller

import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.openapi.annotations.Operation
import org.graphoenix.server.application.run.usecase.GetTaskPageByRun
import org.graphoenix.server.domain.run.valueobject.LinkId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.graphoenix.server.presentation.http.controller.dto.PageCollectionDto
import org.graphoenix.server.presentation.http.controller.dto.TaskDto
import org.jboss.resteasy.reactive.RestQuery

@Path("")
@Produces(MediaType.APPLICATION_JSON)
class TaskController(
  private val getTaskPageByRun: GetTaskPageByRun,
) {
  @Operation(
    summary = "Gets a page of tasks for a run",
  )
  @GET
  @Path("/private/runs/{linkId}/{workspaceId}/tasks")
  suspend fun getTaskPage(
    linkId: String,
    workspaceId: String,
    @RestQuery pageIndex: Int,
    @RestQuery pageSize: Int,
  ): PageCollectionDto<TaskDto> =
    getTaskPageByRun(
      GetTaskPageByRun.Request(
        runLinkId = LinkId(linkId),
        workspaceId = WorkspaceId(workspaceId),
        pageIndex = pageIndex,
        pageSize = pageSize,
      ),
    ) { response ->
      PageCollectionDto(
        items = response.tasks.items.map { TaskDto.from(it) },
        totalCount = response.tasks.totalCount,
      )
    }
}
