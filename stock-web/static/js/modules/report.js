function renderReportMenu(current) {
  var arr = [
    { id: 1, title: '股票列表', url: '/report/stockList.html' },
    { id: 2, title: '每日统计', url: '/report/dailyList.html' },
    { id: 3, title: '回撤选票', url: '/report/selectStockList.html' },
    { id: 4, title: '热力图', url: '/hotmap/hotmap.html' },
    { id: 5, title: '热力选票', url: '/hotmap/hotMapStockList.html' }

  ];

  renderMenu(arr, '.menu-nav', current);
}
