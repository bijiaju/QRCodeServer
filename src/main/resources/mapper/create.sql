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
  `remark` varchar(255) DEFAULT '无',
  `updateUser` varchar(255) DEFAULT '无',
  `orderType` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_user` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `tb_product` (
                            `id` int(255) NOT NULL AUTO_INCREMENT,
                            `name` varchar(255) DEFAULT NULL,
                            `imagePath` varchar(255) DEFAULT NULL,
                            `uploadPath` varchar(255) DEFAULT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;



INSERT INTO `test`.`tb_user` (`id`, `name`, `nickname`, `password`) VALUES ('1', '毕洋强', 'bee', '123456');
INSERT INTO `test`.`tb_user` (`id`, `name`, `nickname`, `password`) VALUES ('97a7ec57-9596-4be1-8cca-73a9937c441a', '赵路遥', 'lion', '123456');


INSERT INTO `test`.`tb_product` (`id`, `name`, `imagePath`, `uploadPath`) VALUES ('1', '招财猫1', '/images/招财猫1.png', '/index3');

