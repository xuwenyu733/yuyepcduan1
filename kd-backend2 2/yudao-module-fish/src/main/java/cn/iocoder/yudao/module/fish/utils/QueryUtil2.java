package cn.iocoder.yudao.module.fish.utils;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.util.Arrays;
import java.util.List;

/**
 * @创建人 lr
 * @描述
 */
public class QueryUtil2<T> {

    public static List<String> roles = Arrays.asList("admin", "fzr", "yy", "ke_admin", "kede_group_leader", "kede_caiwu");

    public void filterQuery(LambdaQueryWrapperX<T> queryWrapper, AdminUserRespDTO adminUserRespDTO, SFunction<T, ?> userIdP, SFunction<T, ?> deptIdP) {
        boolean flag = false;
        for(String role : roles) {
            if(adminUserRespDTO.getRoleCodes().contains(role))
                flag = true;
        }
        if(flag) {
            queryWrapper.in(deptIdP, adminUserRespDTO.getDeptIds());
        }else {
            queryWrapper.eq(userIdP, adminUserRespDTO.getId());
        }
    }


}
