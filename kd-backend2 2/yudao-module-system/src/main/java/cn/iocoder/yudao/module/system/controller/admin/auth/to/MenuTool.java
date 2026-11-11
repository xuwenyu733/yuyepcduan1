package cn.iocoder.yudao.module.system.controller.admin.auth.to;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.MenuDO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MenuTool {

    /**
     * 菜单列表 获取 组件列表
     * @param menuList
     * @return
     */
    public static List<String> selectComponentList(List<MenuDO> menuList) {
        if(EasyListUtil.isEmpty(menuList)) return new ArrayList<>();
        // List<String> list = menuList.stream().filter(obj -> StringUtil.isNotEmptyWith0(obj.getComponent()))
        //         .map(obj -> String.format("@/views/%s.vue", obj.getComponent())).toList();
        List<String> list = menuList.stream().filter(obj -> StringUtil.isNotEmptyWith0(obj.getComponent()))
                .map(MenuDO::getComponent).toList();
        return list;
    }
}
