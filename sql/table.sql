-- User Table
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment 'account',
    userPassword varchar(512)                           not null comment 'password',
    userName     varchar(256)                           null comment 'user name',
    userAvatar   varchar(1024)                          null comment 'avatar',
    userProfile  varchar(512)                           null comment 'profile',
    userRole     varchar(256) default 'user'            not null comment 'Roles：user/admin',
    editTime     datetime     default CURRENT_TIMESTAMP not null comment 'Edit time',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment 'Create time',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'Update time by admin',
    isDeleted     tinyint      default 0                 not null comment 'is deleted',
    UNIQUE KEY uk_userAccount (userAccount),
    INDEX idx_userName (userName)
    ) comment 'user' collate = utf8mb4_unicode_ci;

-- Picture table
create table if not exists picture
(
    id           bigint auto_increment comment 'id' primary key,
    url          varchar(512)                       not null comment 'picture url',
    name         varchar(128)                       not null comment 'picture name',
    introduction varchar(512)                       null comment 'introduction',
    category     varchar(64)                        null comment 'category',
    tags         varchar(512)                      null comment 'tags（JSON array）',
    picSize      bigint                             null comment 'picture file size',
    picWidth     int                                null comment 'picture width',
    picHeight    int                                null comment 'picture height',
    picScale     double                             null comment 'picture width-to-height ratio',
    picFormat    varchar(32)                        null comment 'picture format',
    creatorId    bigint                             not null comment 'creator id',
    createTime   datetime default CURRENT_TIMESTAMP not null comment 'create time',
    editTime     datetime default CURRENT_TIMESTAMP not null comment 'edit time',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update time',
    isDeleted     tinyint  default 0                 not null comment 'isDeleted',
    INDEX idx_name (name),                 -- Improve query performance on picture name
    INDEX idx_introduction (introduction), -- Approximate query on introduction
    INDEX idx_category (category),         -- Improve query performance on category
    INDEX idx_tags (tags),                 -- Improve query performance on tags
    INDEX idx_userId (creatorId)           -- Improve query performance on creator ID
) comment 'picture' collate = utf8mb4_unicode_ci;


ALTER TABLE picture
    -- Add new field for review
    ADD COLUMN reviewStatus INT DEFAULT 0 NOT NULL COMMENT 'Review status：0-Reviewing; 1-Pass; 2-Rejected',
    ADD COLUMN reviewMessage VARCHAR(512) NULL COMMENT 'Review details',
    ADD COLUMN reviewerId BIGINT NULL COMMENT 'Reviewer ID',
    ADD COLUMN reviewTime DATETIME NULL COMMENT 'Review time';

-- Create an index on review status
CREATE INDEX idx_reviewStatus ON picture (reviewStatus);


-- Storage space table
create table if not exists space
(
    id         bigint auto_increment comment 'id' primary key,
    spaceName  varchar(128)                       null comment 'Space name',
    spaceLevel int      default 0                 null comment 'Space level 0-Personal 1-Professional 2-Flagship',
    maxSize    bigint   default 0                 null comment 'Maximal storage size',
    maxCount   bigint   default 0                 null comment 'Maximal picture number',
    totalSize  bigint   default 0                 null comment 'Total storage used in the current space',
    totalCount bigint   default 0                 null comment 'Total number of pictures in the current space',
    creatorId  bigint                             not null comment 'Creator ID',
    createTime datetime default CURRENT_TIMESTAMP not null comment 'Create time',
    editTime   datetime default CURRENT_TIMESTAMP not null comment 'Edit time',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'Update time by Admins',
    isDeleted  tinyint  default 0                 not null comment 'isDeleted',
    -- Index design to improve query performance based on different indexes
    index idx_userId (creatorId),
    index idx_spaceName (spaceName),
    index idx_spaceLevel (spaceLevel)
) comment 'Picture storage space' collate = utf8mb4_unicode_ci;

ALTER TABLE picture
    ADD COLUMN spaceId bigint null comment 'Space ID (null means that the space is public )';

CREATE INDEX  idx_spaceId ON picture (spaceId);

ALTER TABLE space
    ADD COLUMN spaceType int default 0 not null comment "Space type: 0-private 1-team";

CREATE INDEX idx_spaceType ON space (spaceType);

-- Space-User relation table
create table if not exists space_user
(
    id         bigint auto_increment comment 'id' primary key,
    spaceId    bigint                                 not null comment 'Space id',
    userId     bigint                                 not null comment 'User id',
    role       varchar(128) default 'viewer'          null comment 'User role in this space：viewer/editor/admin',
    createTime datetime     default CURRENT_TIMESTAMP not null comment 'Create time',
    updateTime datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'Update time',
    -- Indexing design
    UNIQUE KEY uk_spaceId_userId (spaceId, userId), -- Unique index，each user can have only one role in a space
    INDEX idx_spaceId (spaceId),                    -- Improve query performance on spaceId
    INDEX idx_userId (userId)                       -- Improve query performance on userId
) comment 'space_user_relation_table' collate = utf8mb4_unicode_ci;
