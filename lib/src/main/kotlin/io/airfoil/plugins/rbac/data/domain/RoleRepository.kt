package io.airfoil.plugins.rbac.data.domain

import io.airfoil.plugins.rbac.data.domain.dto.Subject
import io.airfoil.plugins.rbac.data.domain.dto.SubjectId
import io.airfoil.plugins.rbac.data.domain.dto.Role
import io.airfoil.plugins.rbac.data.domain.dto.RoleId
import io.airfoil.plugins.rbac.exception.RoleNotFoundException

interface RoleRepository {
    // create a new role
    fun create(role: Role): Role
    
    // update an existing role in the repository
    fun update(id: RoleId, block: (Role) -> Role): Role
    
    // upsert (create or update) a role in the repository
    fun upsert(role: Role): Role

    // fetch a role by id
    fun fetchOrNull(id: RoleId): Role?
    fun fetch(id: RoleId): Role =
        fetchOrNull(id) ?: throw RoleNotFoundException(id)

    // fetch roles by subject and subject id
    fun fetchBySubject(subject: Subject, subjectId: SubjectId): List<Role>

    // fetch role by subject, subject id and name
    fun fetchBySubjectAndNameOrNull(subject: Subject, subjectId: SubjectId, roleName: String): Role?
    fun fetchBySubjectAndName(subject: Subject, subjectId: SubjectId, roleName: String): Role =
        fetchBySubjectAndNameOrNull(subject, subjectId, roleName) ?: throw RoleNotFoundException(name = roleName)
}
