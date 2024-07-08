create table if not exists t_converse_message_range
(
    `converse_message_range_id` BIGINT auto_increment primary key,
    `converse_id`               varchar(20) not null,
    `first_message_id`          varchar(20) not null,
    `create_time`               BIGINT      not null,
    index `converse_message_desc` (`converse_id` desc, `first_message_id` desc)
) COMMENT ='消息范围表' ENGINE = InnoDB
                        DEFAULT CHARSET = utf8mb4
                        COLLATE = utf8mb4_unicode_ci;



