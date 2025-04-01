package org.graphoenix.server.application

interface UseCase<R, P> {
  suspend operator fun <T> invoke(
    request: R,
    presenter: (P) -> T,
  ): T
}
