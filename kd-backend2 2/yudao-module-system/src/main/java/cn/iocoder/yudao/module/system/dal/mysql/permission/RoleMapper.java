package cn.iocoder.yudao.module.system.dal.mysql.permission;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.permission.vo.role.RolePageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.RoleDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapperX<RoleDO> {

    default PageResult<RoleDO> selectPage(RolePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<RoleDO>()
                .likeIfPresent(RoleDO::getName, reqVO.getName())
                .likeIfPresent(RoleDO::getCode, reqVO.getCode())
                .eqIfPresent(RoleDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(BaseDO::getCreateTime, reqVO.getCreateTime())
                .orderByAsc(RoleDO::getSort));
    }

    default RoleDO selectByName(String name) {
        return selectOne(RoleDO::getName, name);
    }

    default RoleDO selectByCode(String code) {
        return selectOne(RoleDO::getCode, code);
    }

    default List<RoleDO> selectListByStatus(@Nullable Collection<Integer> statuses) {
        return selectList(RoleDO::getStatus, statuses);
    }

    @Select({
            "SELECT sr.* FROM system_users su\n" +
                    "INNER JOIN system_user_role sur ON sur.`user_id` = su.`id`\n" +
                    "INNER JOIN system_role sr ON sr.`id` = sur.`role_id`\n" +
                    "WHERE su.`id` = #{userId}"
    })
    List<RoleDO> getRoleByUserId(@Param("userId") Long userId);

    @Select({
            "SELECT su.*,temp.`roleName` as roleName, temp.`roleCode` as roleCode, sd.name as deptName FROM system_users su \n" +
                    "LEFT JOIN (\n" +
                    "\tSELECT user_id,GROUP_CONCAT(temp3.`name`) AS roleName,GROUP_CONCAT(temp3.`code`) AS roleCode FROM (\n" +
                    "\t\tSELECT su.`id` AS user_id,sr.id, MAX(sr.`name`) AS `name`,MAX(sr.`code`) AS `code` FROM system_users su\n" +
                    "\t\tLEFT JOIN system_user_role sur ON sur.`user_id` = su.`id`\n" +
                    "\t\tLEFT JOIN system_role sr ON sr.`id` = sur.`role_id` where sr.deleted=0 and sur.deleted=0 \n" +
                    "\t\tGROUP BY su.`id`,sr.id\n" +
                    "\t) AS temp3\n" +
                    "\tGROUP BY temp3.user_id\n" +
                    ") temp ON su.`id` = temp.user_id \n" +
                    "LEFT JOIN system_dept sd ON sd.`id` = su.`dept_id` " +
                    "${ew.customSqlSegment}"
    })
    List<AdminUserDO> getUserListByUserIds(@Param("ew") QueryWrapper<AdminUserDO> queryWrapper);
}
