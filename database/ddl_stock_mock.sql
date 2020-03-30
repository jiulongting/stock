```sql
-- DROP DATABASE IF EXISTS `stock_mock`;
-- CREATE DATABASE `stock_mock` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
CREATE TABLE `account` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `total_amount` decimal(20,2) NOT NULL DEFAULT '0',
  `available_amount` decimal(20,2) NOT NULL DEFAULT '0',
  `withdrawable_amount` decimal(20,2) NOT NULL DEFAULT '0',
  `frozen_amount` decimal(20,2) NOT NULL DEFAULT '0',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `entrustment` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `account_id` int(11) unsigned NOT NULL,
  `trade_type` varchar(50) NOT NULL,
  `entrust_code` varchar(50) NOT NULL,
  `stock_code` varchar(50) NOT NULL,
  `entrust_volume` bigint(20) unsigned NOT NULL,
  `entrust_price` decimal(20,2) NOT NULL,
  `state` tinyint(4) unsigned NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`)
) ENGINE=InnoDB;

CREATE TABLE `deal` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `account_id` int(11) unsigned NOT NULL,
  `deal_code` varchar(50) NOT NULL,
  `entrust_code` varchar(50) NOT NULL,
  `stock_code` varchar(50) NOT NULL,
  `trade_type` varchar(50) NOT NULL,
  `deal_volume` bigint(20) unsigned NOT NULL,
  `deal_price` decimal(20,2) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`)
) ENGINE=InnoDB;

CREATE TABLE `stock_position` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `account_id` int(11) unsigned NOT NULL,
  `stock_code` varchar(50) NOT NULL,
  `volume` bigint(20) unsigned NOT NULL,
  `available_volume` bigint(20) NOT NULL DEFAULT '0',
  `price` decimal(20,2) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`)
) ENGINE=InnoDB;
```
