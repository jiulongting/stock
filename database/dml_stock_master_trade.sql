INSERT INTO `trade_method` (`id`, `name`, `url`, `state`, `description`) VALUES ('1', 'get_asserts', 'https://jy.xzsec.com/Com/GetAssetsEx?validatekey=${validatekey}', '1', '我的资产');
INSERT INTO `trade_method` (`id`, `name`, `url`, `state`, `description`) VALUES ('2', 'submit', 'https://jy.xzsec.com/Trade/SubmitTrade?validatekey=${validatekey}', '1', '提交挂单');
INSERT INTO `trade_method` (`id`, `name`, `url`, `state`, `description`) VALUES ('3', 'revoke', 'https://jy.xzsec.com/Trade/RevokeOrders?validatekey=${validatekey}', '1', '撤单');
INSERT INTO `trade_method` (`id`, `name`, `url`, `state`, `description`) VALUES ('4', 'get_stock_list', 'https://jy.xzsec.com/Search/GetStockList?validatekey=${validatekey}', '1', '我的持仓');
INSERT INTO `trade_method` (`id`, `name`, `url`, `state`, `description`) VALUES ('5', 'get_orders_data', 'https://jy.xzsec.com/Search/GetOrdersData?validatekey=${validatekey}', '1', '当日委托');
INSERT INTO `trade_method` (`id`, `name`, `url`, `state`, `description`) VALUES ('6', 'get_deal_data', 'https://jy.xzsec.com/Search/GetDealData?validatekey=${validatekey}', '1', '当日成交');
INSERT INTO `trade_method` (`id`, `name`, `url`, `state`, `description`) VALUES ('7', 'authentication', 'https://jy.xzsec.com/Login/Authentication', '1', '登录');
INSERT INTO `trade_method` (`id`, `name`, `url`, `state`, `description`) VALUES ('8', 'get_his_deal_data', 'https://jy.xzsec.com/Search/GetHisDealData?validatekey=${validatekey}', '1', '历史成交');
INSERT INTO `trade_method` (`id`, `name`, `url`, `state`, `description`) VALUES ('9', 'get_his_orders_data', 'https://jy.xzsec.com/Search/GetHisOrdersData?validatekey=${validatekey}', '1', '历史委托');

INSERT INTO `trade_user` (`id`, `account_id`, `name`, `cookie`, `validate_key`, `state`) VALUES ('1', '资金账号', 'wild', '', '', '1');

INSERT INTO `task` (`id`, `name`, `state`, `description`) VALUES ('9', 'trade_ticker', '1', 'trade ticker');

INSERT INTO `execute_info` (`id`, `task_id`, `start_time`, `complete_time`, `params_str`, `is_manual`, `state`, `message`, `create_user_id`) VALUES ('9', '9', NULL, NULL, '', '0', '2', NULL, '0');

INSERT INTO `trade_rule` (`id`, `rate`, `state`, `description`) VALUES ('1', '0.013700', '1', '1.37%');
INSERT INTO `trade_rule` (`id`, `rate`, `state`, `description`) VALUES ('2', '0.020700', '1', '2.07%');
INSERT INTO `trade_rule` (`id`, `rate`, `state`, `description`) VALUES ('3', '0.030700', '1', '3.07%');

INSERT INTO  `trade_stock_info_rule` (`id`, `stock_code`, `rule_id`, `state`) VALUES ('1', '601236', '2', '1');
INSERT INTO  `trade_stock_info_rule` (`id`, `stock_code`, `rule_id`, `state`) VALUES ('2', '002945', '2', '1');
INSERT INTO `trade_stock_info_rule` (`id`, `stock_code`, `rule_id`, `state`) VALUES ('3', '601688', '1', '1');

INSERT INTO `user` (`id`, `username`, `password`, `name`) VALUES ('1', 'wild', '48FA4C38043122C03A61B1FB03D378EE', 'wild');

INSERT INTO `trade_strategy` (`id`, `name`, `bean_name`, `state`) VALUES ('1', 'volumeStrategy', 'volumeStrategyHandler', '1');
