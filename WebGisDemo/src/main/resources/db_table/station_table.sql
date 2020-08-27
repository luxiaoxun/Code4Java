DROP TABLE IF EXISTS `station`;
CREATE TABLE `station` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `country_code` varchar(16) DEFAULT '',
  `provider` varchar(32) DEFAULT '',
  `lac` varchar(32) DEFAULT '',
  `cell` varchar(32) DEFAULT '',
  `latitude` DOUBLE(20,8) DEFAULT NULL,
  `longitude` DOUBLE(20,8) DEFAULT NULL,
  `address` varchar(512) DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='station table'

INSERT INTO `dataservice`.`station` (`id`, `country_code`, `provider`, `lac`, `cell`, `latitude`, `longitude`, `address`)
VALUES ('1', '406', '0', '111', '222', '32.12800000', '118.77420000', '南京测试点1');

INSERT INTO `dataservice`.`station` (`id`, `country_code`, `provider`, `lac`, `cell`, `latitude`, `longitude`, `address`)
VALUES ('2', '406', '1', '123', '234', '32.22700000', '118.86420000', '南京测试点2');

INSERT INTO `dataservice`.`station` (`id`, `country_code`, `provider`, `lac`, `cell`, `latitude`, `longitude`, `address`)
VALUES ('3', '406', '0', '222', '333', '30.32600000', '118.78420000', '南京测试点3');

INSERT INTO `dataservice`.`station` (`id`, `country_code`, `provider`, `lac`, `cell`, `latitude`, `longitude`, `address`)
VALUES ('4', '406', '1', '123', '234', '31.22700000', '118.56420000', '南京测试点4');

INSERT INTO `dataservice`.`station` (`id`, `country_code`, `provider`, `lac`, `cell`, `latitude`, `longitude`, `address`)
VALUES ('5', '406', '0', '222', '333', '32.32600000', '118.68420000', '南京测试点5');
