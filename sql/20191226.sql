CREATE DATABASE `warclock` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;


CREATE TABLE `warclock` (
                            `id` int(11) NOT NULL AUTO_INCREMENT,
                            `team` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `case_name` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `level` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `create_ts` bigint(13) DEFAULT NULL,
                            `start_deal_ts` bigint(13) DEFAULT NULL,
                            `finish_ts` bigint(13) DEFAULT NULL,
                            `state` tinyint(10) DEFAULT NULL COMMENT '1 创建 2 处理中 3 已完成',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
