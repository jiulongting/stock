-- DROP DATABASE IF EXISTS `stock_master`;
-- CREATE DATABASE `stock_master` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;

DROP TABLE IF EXISTS `robot`;
DROP TABLE IF EXISTS `stock_selected`;
DROP TABLE IF EXISTS `holiday_calendar`;
DROP TABLE IF EXISTS `stock_info`;
DROP TABLE IF EXISTS `stock_log`;
DROP TABLE IF EXISTS `daily_index`;
DROP TABLE IF EXISTS `execute_info`;
DROP TABLE IF EXISTS `task`;
DROP TABLE IF EXISTS `execute_info`;
DROP TABLE IF EXISTS `trade_user`;
DROP TABLE IF EXISTS `trade_method`;
DROP TABLE IF EXISTS `trade_rule`;
DROP TABLE IF EXISTS `trade_stock_info_rule`;
DROP TABLE IF EXISTS `trade_order`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `trade_strategy`;

CREATE TABLE `holiday_calendar` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_date` (`date`)
) ENGINE=InnoDB;

CREATE TABLE `stock_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(6) NOT NULL,
  `name` varchar(50) NOT NULL,
  `exchange` varchar(2) NOT NULL,
  `abbreviation` varchar(50) NOT NULL,
  `state` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `type` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `description` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exchange_code` (`exchange`,`code`)
) ENGINE=InnoDB;

CREATE TABLE `stock_log` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `stock_info_id` int(11) unsigned NOT NULL,
  `date` date NOT NULL,
  `type` tinyint(4) unsigned NOT NULL,
  `old_value` varchar(50) NOT NULL DEFAULT '',
  `new_value` varchar(50) NOT NULL DEFAULT '',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `description` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `daily_index` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `stock_info_id` int(11) unsigned NOT NULL,
  `date` date NOT NULL,
  `pre_closing_price` decimal(20, 2) NOT NULL,
  `opening_price` decimal(20, 2) NOT NULL,
  `highest_price` decimal(20, 2) NOT NULL,
  `lowest_price` decimal(20, 2) NOT NULL,
  `closing_price` decimal(20, 2) NOT NULL,
  `trading_volume` bigint NOT NULL,
  `trading_value` decimal(20, 2) NOT NULL,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `ik_stock_info_id` (`stock_info_id`)
) ENGINE=InnoDB;

CREATE TABLE `task` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `state` tinyint(4) unsigned NOT NULL,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `description` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB;

CREATE TABLE `execute_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `task_id` int(11) unsigned NOT NULL,
  `start_time` DATETIME DEFAULT NULL,
  `complete_time` DATETIME DEFAULT NULL,
  `params_str` varchar(200) NOT NULL DEFAULT '',
  `is_manual` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `state` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `message` varchar(500) DEFAULT NULL,
  `create_user_id` int(11) unsigned NOT NULL DEFAULT '0',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `stock_selected` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL,
  `rate` decimal(20, 2) NOT NULL DEFAULT '0.02',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `description` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `robot` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `type` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `webhook` varchar(200) NOT NULL DEFAULT '',
  `state` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `description` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `trade_user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `account_id` varchar(50) NOT NULL,
  `cookie` varchar(500) NOT NULL,
  `validate_key` varchar(50) NOT NULL,
  `state` tinyint(4) unsigned NOT NULL DEFAULT '1',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `trade_method` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `url` varchar(100) NOT NULL,
  `state` tinyint(4) unsigned NOT NULL DEFAULT '1',
  `description` varchar(100) NOT NULL DEFAULT '',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `trade_rule` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `rate` decimal(20,6) NOT NULL,
  `state` tinyint(4) unsigned NOT NULL DEFAULT '1',
  `description` varchar(100) NOT NULL DEFAULT '',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `trade_stock_info_rule` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `stock_code` varchar(50) NOT NULL,
  `rule_id` int(11) unsigned NOT NULL,
  `state` tinyint(4) unsigned NOT NULL DEFAULT '1',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `trade_order` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `trade_code` varchar(50) NOT NULL,
  `stock_code` varchar(50) NOT NULL,
  `price` decimal(20, 2) NOT NULL,
  `volume` int(11) unsigned NOT NULL,
  `trade_type` varchar(50) NOT NULL,
  `entrust_code` varchar(50) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL NOT NULL DEFAULT '',
  `mobile` varchar(50) NOT NULL DEFAULT '',
  `email` varchar(50) NOT NULL DEFAULT '',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB;

CREATE TABLE `trade_strategy` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `bean_name` varchar(50) NOT NULL,
  `state` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB;
