package org.graphoenix.server.presentation.http.controller.dto

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
sealed class VerifyClientBundleDto(
  val valid: Boolean,
  val url: String?,
  val version: String?,
) {
  class ValidVerifyClientBundleDto : VerifyClientBundleDto(true, null, null)

  class InvalidVerifyClientBundleDto(
    url: String,
    version: String,
  ) : VerifyClientBundleDto(false, url, version)
}
