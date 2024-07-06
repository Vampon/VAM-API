export default [
  {
    path: '/welcome',
    name: '欢迎',
    icon: 'smile',
    component: './Welcome',
  },
  { path: '/interface_square', name: '接口市集', icon: 'ApiOutlined', component: './InterfaceSquare' },
  { path: '/interface_info/:id', name: '查看接口', icon: 'ApiOutlined', component: './InterfaceInfo', hideInMenu: true }, //icon: 'smile',
  { path: '/recharge/list', icon: 'PayCircleOutlined', name: '积分商城', component: './Recharge' },
  {
    path: '/user',
    layout: false,
    routes: [
      { name: '登录', path: '/user/login', component: './User/Login' },
      { name: '注册', path: '/user/register', component: './User/Register' }
    ],
  },
  {
    path: '/order/list',
    name: '我的订单',
    icon: 'ProfileOutlined',
    component: './Order/OrderList',
  },
  {
    path: '/admin',
    name: '管理页',
    icon: 'crown',
    access: 'canAdmin',
    routes: [
      { name: '接口管理', icon: 'table', path: '/admin/interface_info', component: './Admin/InterfaceInfoV2' },
      { name: '接口分析', icon: 'analysis', path: '/admin/interface_analysis', component: './Admin/InterfaceAnalysis' },
      {name: '用户管理页', icon: 'table', path: '/admin/user_manager', component: './Admin/UserList' },
      {name: '商品管理', icon: 'table', path: '/admin/productInfo/list', component: './Admin/ProductInfoList'},
    ],
  },
  {
    path: '/order/pay/:id',
    icon: 'PayCircleOutlined',
    name: '订单支付',
    component: './Order/PayOrder',
    hideInMenu: true,
  },
  {
    path: '/order/info/:id',
    icon: 'ProfileOutlined',
    name: '订单详情',
    component: './Order/OrderInfo',
    hideInMenu: true,
  },
  // { name: '个人中心', icon: 'UserOutlined', path: '/profile', component: './User/Profile' },
  { path: '/user/info', name: '个人中心', icon: 'user', component: './User/UserInfo' },
  { path: '/', redirect: '/welcome' },
  { path: '*', layout: false, component: './404' },
];
