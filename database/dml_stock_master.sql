INSERT INTO `user` (`id`, `username`, `password`, `name`) VALUES ('1', 'wild', 'e10adc3949ba59abbe56e057f20f883e', 'wild');

INSERT INTO `system_config` (`id`, `name`, `value1`, `value2`, `value3`, `state`) VALUES ('1', 'trade_mock', '0', '', '', '1');

INSERT INTO `task` (`id`, `name`, `state`, `description`) VALUES ('1', 'begin_of_year', '1', 'begin of year');
INSERT INTO `task` (`id`, `name`, `state`, `description`) VALUES ('2', 'end_of_year', '1', 'end of year');
INSERT INTO `task` (`id`, `name`, `state`, `description`) VALUES ('3', 'begin_of_day', '1', 'begin of day');
INSERT INTO `task` (`id`, `name`, `state`, `description`) VALUES ('4', 'end_of_day', '1', 'end of day');
INSERT INTO `task` (`id`, `name`, `state`, `description`) VALUES ('5', 'update_of_stock', '1', 'update of stock');
INSERT INTO `task` (`id`, `name`, `state`, `description`) VALUES ('6', 'update_of_stock_state', '1', 'update of stock state');
INSERT INTO `task` (`id`, `name`, `state`, `description`) VALUES ('7', 'update_of_daily_index', '1', 'update of daily index');
INSERT INTO `task` (`id`, `name`, `state`, `description`) VALUES ('8', 'ticker', '1', 'ticker');
INSERT INTO `task` (`id`, `name`, `state`, `description`) VALUES ('9', 'trade_ticker', '1', 'trade ticker');

INSERT INTO `execute_info` (`id`, `task_id`, `state`) VALUES ('1', '1', '2');
INSERT INTO `execute_info` (`id`, `task_id`, `state`) VALUES ('2', '2', '0');
INSERT INTO `execute_info` (`id`, `task_id`, `state`) VALUES ('3', '3', '2');
INSERT INTO `execute_info` (`id`, `task_id`, `state`) VALUES ('4', '4', '0');
INSERT INTO `execute_info` (`id`, `task_id`, `state`) VALUES ('5', '5', '2');
INSERT INTO `execute_info` (`id`, `task_id`, `state`) VALUES ('6', '6', '0');
INSERT INTO `execute_info` (`id`, `task_id`, `state`) VALUES ('7', '7', '2');
INSERT INTO `execute_info` (`id`, `task_id`, `state`) VALUES ('8', '8', '2');
INSERT INTO `execute_info` (`id`, `task_id`, `state`) VALUES ('9', '9', '0');

INSERT INTO `robot` (`id`, `type`, `webhook`, `state`) VALUES ('1', '0', 'http://webhook', '1');

INSERT INTO `stock_selected` (`id`, `code`, `rate`) VALUES ('1', '601456', '0.02');
INSERT INTO `stock_selected` (`id`, `code`, `rate`) VALUES ('2', '601688', '0.02');
INSERT INTO `stock_selected` (`id`, `code`, `rate`) VALUES ('3', '300059', '0.02');

INSERT INTO `trade_method` (`id`, `name`, `url`, `state`, `description`) VALUES ('1', 'get_asserts', 'https://jywg.18.cn/Com/GetAssetsEx?validatekey=${validatekey}', '1', '我的资产');
INSERT INTO `trade_method` (`id`, `name`, `url`, `state`, `description`) VALUES ('2', 'submit', 'https://jywg.18.cn/Trade/SubmitTrade?validatekey=${validatekey}', '1', '提交挂单');
INSERT INTO `trade_method` (`id`, `name`, `url`, `state`, `description`) VALUES ('3', 'revoke', 'https://jywg.18.cn/Trade/RevokeOrders?validatekey=${validatekey}', '1', '撤单');
INSERT INTO `trade_method` (`id`, `name`, `url`, `state`, `description`) VALUES ('4', 'get_stock_list', 'https://jywg.18.cn/Search/GetStockList?validatekey=${validatekey}', '1', '我的持仓');
INSERT INTO `trade_method` (`id`, `name`, `url`, `state`, `description`) VALUES ('5', 'get_orders_data', 'https://jywg.18.cn/Search/GetOrdersData?validatekey=${validatekey}', '1', '当日委托');
INSERT INTO `trade_method` (`id`, `name`, `url`, `state`, `description`) VALUES ('6', 'get_deal_data', 'https://jywg.18.cn/Search/GetDealData?validatekey=${validatekey}', '1', '当日成交');
INSERT INTO `trade_method` (`id`, `name`, `url`, `state`, `description`) VALUES ('7', 'authentication', 'https://jywg.18.cn/Login/Authentication', '1', '登录');
INSERT INTO `trade_method` (`id`, `name`, `url`, `state`, `description`) VALUES ('8', 'authentication_check', 'https://jywg.18.cn/Trade/Buy', '1', '登录验证');
INSERT INTO `trade_method` (`id`, `name`, `url`, `state`, `description`) VALUES ('9', 'get_his_deal_data', 'https://jywg.18.cn/Search/GetHisDealData?validatekey=${validatekey}', '1', '历史成交');
INSERT INTO `trade_method` (`id`, `name`, `url`, `state`, `description`) VALUES ('10', 'get_his_orders_data', 'https://jywg.18.cn/Search/GetHisOrdersData?validatekey=${validatekey}', '1', '历史委托');

INSERT INTO `trade_user` (`id`, `account_id`, `name`, `cookie`, `validate_key`, `state`) VALUES ('1', '资金账号', 'wild', '', '', '1');

INSERT INTO `trade_rule` (`id`, `stock_code`, `strategy_id`, `user_id`, `type`, `value`, `volume`, `open_price`, `highest_price`, `lowest_price`, `state`, `description`) VALUES ('1', '300059', '1', '1', '1', '1.000000', '300', '31', '40.000000', '30.000000', '0', '');

INSERT INTO `trade_strategy` (`id`, `name`, `bean_name`, `state`) VALUES ('1', 'gridStrategy', 'gridStrategyHandler', '1');
