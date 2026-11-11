package cn.iocoder.yudao.module.system.dal.mysql.user;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.user.vo.user.UserPageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mapper
public interface AdminUserMapper extends BaseMapperX<AdminUserDO> {

    default AdminUserDO selectByUsername(String username) {
        return selectOne(AdminUserDO::getUsername, username);
    }

    default AdminUserDO selectByEmail(String email) {
        return selectOne(AdminUserDO::getEmail, email);
    }

    default AdminUserDO selectByMobile(String mobile) {
        return selectOne(AdminUserDO::getMobile, mobile);
    }

    default PageResult<AdminUserDO> selectPage(UserPageReqVO reqVO, Collection<Long> deptIds, Collection<Long> userIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdminUserDO>()
                .likeIfPresent(AdminUserDO::getUsername, reqVO.getUsername())
                .likeIfPresent(AdminUserDO::getMobile, reqVO.getMobile())
                .eqIfPresent(AdminUserDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(AdminUserDO::getCreateTime, reqVO.getCreateTime())
                .inIfPresent(AdminUserDO::getDeptId, deptIds)
                .inIfPresent(AdminUserDO::getId, userIds)
                .orderByDesc(AdminUserDO::getId));
    }

    default List<AdminUserDO> selectListByNickname(String nickname) {
        return selectList(new LambdaQueryWrapperX<AdminUserDO>().like(AdminUserDO::getNickname, nickname));
    }

    default List<AdminUserDO> selectListByStatus(Integer status) {
        return selectList(AdminUserDO::getStatus, status);
    }

    default List<AdminUserDO> selectListByDeptIds(Collection<Long> deptIds) {
        return selectList(AdminUserDO::getDeptId, deptIds);
    }

    @Select({
            "SELECT su.* FROM system_users su \n" +
                    "LEFT JOIN system_user_role sur ON sur.`user_id` = su.`id`\n" +
                    "LEFT JOIN system_role sr ON sr.`id` = sur.`role_id` " +
                    "${ew.customSqlSegment}"
    })
    List<AdminUserDO> getUserListByDeptIdAndRoleCode(@Param("ew") QueryWrapper<AdminUserDO> queryWrapper);

    /**
     SELECT sur.user_id AS uid,GROUP_CONCAT(sur.role_id) AS roleIds, GROUP_CONCAT(sr.name) AS roleNames,
     sd.name AS deptName, su.*  FROM system_user_role sur
     LEFT JOIN system_role sr ON sr.id = sur.role_id
     LEFT JOIN system_users su ON su.id = sur.user_id
     LEFT JOIN system_dept sd ON sd.id = su.dept_id
     WHERE sur.user_id >= 1
     GROUP BY sur.user_id
     * @return
     */
    @Select({
            "SELECT sur.user_id AS uid,GROUP_CONCAT(sur.role_id) AS roleIds, GROUP_CONCAT(sr.name) AS roleNames, " +
                    "sd.name AS deptName, su.*  FROM system_user_role sur " +
                    "LEFT JOIN system_role sr ON sr.id = sur.role_id " +
                    "LEFT JOIN system_users su ON su.id = sur.user_id " +
                    "LEFT JOIN system_dept sd ON sd.id = su.dept_id " +
                    "${ew.customSqlSegment} " +
                    "GROUP BY sur.user_id"
    })
    List<AdminUserDO> getFullUserInfo(@Param("ew") QueryWrapper<AdminUserDO> queryWrapper);

    @Select({
            "SELECT DISTINCT su.id AS tempId, su.*, sd.name AS deptName, sr.id AS roleId, sr.`name` AS roleName FROM system_users su \n" +
                    "INNER JOIN system_dept sd ON sd.id = su.`dept_id`\n" +
                    "INNER JOIN system_user_role sur ON sur.`user_id` = su.`id`\n" +
                    "INNER JOIN system_role sr ON sr.`id` = sur.`role_id` " +
                    "${ew.customSqlSegment}"
    })
    List<AdminUserDO> getUserListByRoleIds(@Param("ew") QueryWrapper<AdminUserDO> queryWrapper);

    default List<AdminUserDO> selectListByDeptIdsAndAndUserIds(Set<Long> deptIds, Set<Long> userIds) {
        return selectList(new LambdaQueryWrapperX<AdminUserDO>()
                .eqIfPresent(AdminUserDO::getStatus, CommonStatusEnum.ENABLE.getStatus())
                .inIfPresent(AdminUserDO::getDeptId, deptIds)
                .inIfPresent(AdminUserDO::getId, userIds));
    }

}
