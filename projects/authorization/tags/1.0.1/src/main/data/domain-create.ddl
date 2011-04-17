
    create table permissions (
        assignableId bigint not null,
        assignableTypeId bigint not null,
        permissableId bigint not null,
        permissableTypeId bigint not null,
        createdById bigint,
        createdOn datetime,
        lastModifiedById bigint,
        lastModifiedOn datetime,
        comment varchar(255),
        permissionMask bigint,
        primary key (assignableId, assignableTypeId, permissableId, permissableTypeId)
    );
