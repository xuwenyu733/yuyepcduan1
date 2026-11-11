package cn.iocoder.yudao.module.fish.utils;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @创建人 lr
 * @创建时间 2024-04-12
 * @描述
 */
public class QueryUtil {

    public static List<String> roles = Arrays.asList("admin", "fzr", "yy", "ke_admin", "kede_group_leader", "kede_caiwu");

    public static void filterQuery1(QueryWrapper queryWrapper, AdminUserRespDTO adminUserRespDTO, String userIdP, String deptIdP) {
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


    public static void filterQuery2(LambdaQueryWrapperX queryWrapper, AdminUserRespDTO adminUserRespDTO, SFunction userIdP, SFunction deptIdP) {
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

    public static <T, V, P> void inOrEq(LambdaQueryWrapperX<T> queryWrapper, SFunction<T, ?> column, P p,Class<V> vClass, List<String> inPropertyList, List<String> eqPropertyList) {
        boolean flag = false;
        if(EasyListUtil.isNotEmpty(inPropertyList)) {
            for(String inProperty : inPropertyList) {
                ArrayList valByProperty = CommonUtil.getValByProperty(p, inProperty, ArrayList.class);
                if(EasyListUtil.isNotEmpty(valByProperty)) {
                    queryWrapper.or(obj -> obj.in(column, valByProperty));
                    flag = true;
                }
            }
        }
        if(EasyListUtil.isNotEmpty(eqPropertyList) && !flag) {
            for(String eqProperty : eqPropertyList) {
                V valByProperty = CommonUtil.getValByProperty(p, eqProperty, vClass);
                if(valByProperty != null) queryWrapper.or(obj -> obj.eq(column, valByProperty));
            }
        }
    }

    /**
     * 部门范围：优先 deptIds IN（本部门+子部门），否则 deptId EQ。
     * 使用 AND 条件，避免与其它 where 拼成 OR 导致结果异常。
     * <p>
     * 注意：deptIds 可能是 {@code Collections.singletonList} / 任意 {@link List}，
     * 不可按 {@link ArrayList} 强转（空部门哨兵 -1 会 ClassCastException → 500）。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T, P> void applyDeptFilter(LambdaQueryWrapperX<T> queryWrapper, SFunction<T, ?> column, P p) {
        if (queryWrapper == null || p == null) {
            return;
        }
        Object rawDeptIds = CommonUtil.getValByProperty(p, "deptIds", Object.class);
        List deptIds = null;
        if (rawDeptIds instanceof List) {
            deptIds = new ArrayList<>((List) rawDeptIds);
        }
        if (EasyListUtil.isNotEmpty(deptIds)) {
            queryWrapper.in(column, deptIds);
            return;
        }
        Long deptId = CommonUtil.getValByProperty(p, "deptId", Long.class);
        if (deptId != null && deptId > 0) {
            queryWrapper.eq(column, deptId);
        }
    }

}
