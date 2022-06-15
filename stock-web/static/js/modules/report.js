function renderReportMenu(current) {
  var arr = [
    { id: 1, title: '股票列表', url: '/report/stockList.html' },
    { id: 2, title: '每日统计', url: '/report/dailyList.html' },
    { id: 3, title: '选票', url: '/report/selectStockList.html' }

  ];

  renderMenu(arr, '.menu-nav', current);
}
