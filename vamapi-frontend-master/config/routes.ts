export default [
  {
    path: '/welcome',
    name: '欢迎',
    icon: 'smile',
    component: './Welcome',
  },
  { path: '/interface_info', name: '接口市集', icon: 'ApiOutlined', component: './Index' },
  { path: '/interface_info/:id', name: '查看接口', icon: 'ApiOutlined', component: './InterfaceInfo', hideInMenu: true }, //icon: 'smile',
  {
    path: '/user',
    layout: false,
    routes: [
      { name: '登录', path: '/user/login', component: './User/Login' },
      { name: '注册', path: '/user/register', component: './User/Register' }
    ],
  },
  {
    path: '/admin',
    name: '管理页',
    icon: 'crown',
    access: 'canAdmin',
    routes: [
      { name: '接口管理', icon: 'table', path: '/admin/interface_info', component: './Admin/InterfaceInfo' },
      { name: '接口分析', icon: 'analysis', path: '/admin/interface_analysis', component: './Admin/InterfaceAnalysis' },
      {
        name: '用户管理页', icon: 'table', path: '/admin/user_manager', component: './Admin/UserList' },
    ],
  },
  // { name: '个人中心', icon: 'UserOutlined', path: '/profile', component: './User/Profile' },
  { path: '/user/info', name: '个人中心', icon: 'user', component: './User/UserInfo' },
  { path: '/', redirect: '/welcome' },
  { path: '*', layout: false, component: './404' },
];
