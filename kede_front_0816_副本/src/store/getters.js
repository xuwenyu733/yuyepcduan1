const getters = {
  sidebar: state => state.app.sidebar,
  size: state => state.app.size,
  device: state => state.app.device,
  visitedViews: state => state.tagsView.visitedViews,
  cachedViews: state => state.tagsView.cachedViews,
  userId: state => state.user.id,
  token: state => state.user.token,
  avatar: state => state.user.avatar,
  name: state => state.user.name,
  nickname: state => state.user.nickname,
  deptId: state => state.user.deptId,
  introduction: state => state.user.introduction,
  roles: state => state.user.roles,
  permissions: state => state.user.permissions,
  componentList: state => state.user.componentList,
  permission_routes: state => state.permission.routes,
  // 工具栏
  topbarRouters:state => state.permission.topbarRouters,
  defaultRoutes:state => state.permission.defaultRoutes,
  sidebarRouters:state => state.permission.sidebarRouters,
  // MyChat 是否展示
  showFlag_MyChat: state => state.MyChat.showFlag_MyChat,
  // 视频聊天的 socket
  videoSocket: state => state.MyChat.videoSocket,
  // 数据字典
  dict_datas: state => state.dict.dictDatas
}
export default getters
