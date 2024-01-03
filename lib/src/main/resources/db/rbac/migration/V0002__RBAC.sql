/*
    The "permission" table is part of the RBAC system and contains information 
    about permissions. Each row in the table represents a specific permissioned
    action, without which an actor is not allowed to complete.

    IMPORTANT: Static permission migrations *MUST BE* additive *NOT* subtractive!
*/
CREATE TABLE IF NOT EXISTS permission (
    id              UUID,
    product         TEXT        NOT NULL,
    subject         VARCHAR(16) NOT NULL,
    operation       VARCHAR(8)  NOT NULL,
    resource        TEXT        DEFAULT NULL,
    description     TEXT        NOT NULL,

    PRIMARY KEY(id),
    CONSTRAINT uq_permission UNIQUE(product, subject, operation, resource)
);

/*
    The "role" table is part of the RBAC system and contains information about
    actor roles. An actor role represents a logical grouping between a set of
    permissions and a set of actors, and can also represent a job function or
    job title in an organization.

    Each role is scoped to a subject. A role scoped to a higher subject will 
    cascade permissions down to ALL sub-subjects.

    Each role can also be either mutable or immutable. An immutable role is one
    which CANNOT be modified (an obvious case is the admin role).
*/
CREATE TABLE IF NOT EXISTS "role" (
    id              UUID,
    subject         VARCHAR(16) NOT NULL,
    subject_id      UUID        NOT NULL,
    name            TEXT        NOT NULL,
    description     TEXT        DEFAULT NULL,
    mutable         BOOLEAN     DEFAULT TRUE,
    admin           BOOLEAN     DEFAULT FALSE,

    PRIMARY KEY(id),
    CONSTRAINT uq_role UNIQUE(subject_id, name)
);

/*
    The "role_grant" table is part of the RBAC system and links a permission
    to a specific role.
*/
CREATE TABLE IF NOT EXISTS role_grant (
    role_id         UUID        NOT NULL,
    permission_id   UUID        NOT NULL,

    CONSTRAINT fk_role_id
        FOREIGN KEY(role_id)
        REFERENCES "role"(id),
    CONSTRAINT fk_permission_id
        FOREIGN KEY(permission_id)
        REFERENCES permission(id)
);
CREATE UNIQUE INDEX uq_role_grant ON role_grant(role_id, permission_id);

/*
    The "actor_role" table is part of the RBAC system and links an actor to a
    specific role.
*/
CREATE TABLE IF NOT EXISTS actor_role (
    actor_id        UUID        NOT NULL,
    role_id         UUID        NOT NULL,

    CONSTRAINT fk_role_id
        FOREIGN KEY(role_id)
        REFERENCES "role"(id)
);
CREATE UNIQUE INDEX uq_actor_role ON actor_role(actor_id, role_id);

/*
    The "actor_grant" table is part of the RBAC system and represents a
    specific permission grant to an actor for a specific subject.

    Actors may be granted permissions by either being assigned to one or
    more roles ("actor_role" table) or by explicit permission granting
    here.
*/
CREATE TABLE IF NOT EXISTS actor_grant (
    actor_id        UUID        NOT NULL,
    permission_id   UUID        NOT NULL,
    subject         VARCHAR(16) NOT NULL,
    subject_id      UUID        NOT NULL,

    CONSTRAINT fk_permission_id
        FOREIGN KEY(permission_id)
        REFERENCES permission(id),
    CONSTRAINT uq_actor_grant UNIQUE(actor_id, permission_id, subject, subject_id)
);
