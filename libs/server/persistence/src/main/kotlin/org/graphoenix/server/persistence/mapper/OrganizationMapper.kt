package org.graphoenix.server.persistence.mapper

import org.graphoenix.server.domain.workspace.entity.Organization
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId
import org.graphoenix.server.persistence.entity.OrganizationEntity

fun OrganizationEntity.toDomain() = Organization(id = OrganizationId(id.toString()), name = name)
