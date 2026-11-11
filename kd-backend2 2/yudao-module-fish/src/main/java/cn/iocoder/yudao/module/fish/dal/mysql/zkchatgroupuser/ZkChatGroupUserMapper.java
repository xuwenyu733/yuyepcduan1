package cn.iocoder.yudao.module.fish.dal.mysql.zkchatgroupuser;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatgroupuser.ZkChatGroupUserDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkchatgroupuser.vo.*;

/**
 * 群聊成员 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkChatGroupUserMapper extends BaseMapperX<ZkChatGroupUserDO> {

    default PageResult<ZkChatGroupUserDO> selectPage(ZkChatGroupUserPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkChatGroupUserDO>()
                .eqIfPresent(ZkChatGroupUserDO::getGroupId, reqVO.getGroupId())
                .eqIfPresent(ZkChatGroupUserDO::getRemark, reqVO.getRemark())
                .likeIfPresent(ZkChatGroupUserDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkChatGroupUserDO::getUserType, reqVO.getUserType())
                .betweenIfPresent(ZkChatGroupUserDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkChatGroupUserDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkChatGroupUserDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(ZkChatGroupUserDO::getId));
    }

}