/**
创建数据库
 */
CREATE DATABASE test;
USE test;
CREATE TABLE `tb_order` (
                          `id` varchar(225) NOT NULL,
                          `orderName` varchar(255) DEFAULT NULL,
                          `orderDate` datetime DEFAULT NULL,
                          `dwState` varchar(255) DEFAULT NULL COMMENT '下载状态',
                          `remark` varchar(255) DEFAULT NULL,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;