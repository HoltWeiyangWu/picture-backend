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


