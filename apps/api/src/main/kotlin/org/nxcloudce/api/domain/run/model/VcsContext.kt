package org.nxcloudce.api.domain.run.model

data class VcsContext(
  val branch: String,
  val ref: String?,
  val title: String?,
  val headSha: String?,
  val baseSha: String?,
  val commitLink: String?,
  val author: String?,
  val authorUrl: String?,
  val authorAvatarUrl: String?,
  val repositoryUrl: String?,
  val platformName: String?,
)
