package cn.iocoder.yudao.module.fish.dal.mysql.zkchatgroup;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatgroup.ZkChatGroupDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkchatgroup.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 群聊组 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkChatGroupMapper extends BaseMapperX<ZkChatGroupDO> {

    default PageResult<ZkChatGroupDO> selectPage(ZkChatGroupPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkChatGroupDO>()
                .likeIfPresent(ZkChatGroupDO::getGroupName, reqVO.getGroupName())
                .eqIfPresent(ZkChatGroupDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(ZkChatGroupDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkChatGroupDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkChatGroupDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(ZkChatGroupDO::getId));
    }

    @Select({"SELECT * FROM zk_chat_group \n" +
            "WHERE id IN (\n" +
            "\tSELECT zcg.id FROM zk_chat_group zcg \n" +
            "LEFT JOIN zk_chat_group_user zcgu ON zcgu.`group_id` = zcg.`id`\n" +
            "${ew.customSqlSegment} GROUP BY zcg.`id`\n" +
            ")"
    })
    IPage<ZkChatGroupDO> customPage(Page<ZkChatGroupDO> page, @Param(Constants.WRAPPER) QueryWrapper<ZkChatGroupDO> queryWrapper);

}