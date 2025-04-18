package org.graphoenix.server.application.workspace.usecase

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.graphoenix.server.domain.workspace.entity.AccessToken
import org.graphoenix.server.domain.workspace.gateway.AccessTokenRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
@MockKExtension.CheckUnnecessaryStub
class GetWorkspaceAccessTokenTest {
  @MockK
  private lateinit var mockAccessTokenRepository: AccessTokenRepository

  @InjectMockKs
  private lateinit var getWorkspaceAccessToken: GetWorkspaceAccessToken

  @Test
  fun `should return the access token`() =
    runTest {
      // Given
      val dummyTokenValue = "test-token"
      val dummyRequest = GetWorkspaceAccessTokenRequest(encodedAccessToken = dummyTokenValue)
      val dummyAccessToken = mockk<AccessToken>(relaxed = true)

      coEvery { mockAccessTokenRepository.findByEncodedValue(dummyTokenValue) } returns dummyAccessToken

      // When
      val result = getWorkspaceAccessToken(dummyRequest) { it }

      // Then
      expect(result.accessToken).toEqual(dummyAccessToken)
      coVerify(exactly = 1) { mockAccessTokenRepository.findByEncodedValue(dummyTokenValue) }
    }
}
