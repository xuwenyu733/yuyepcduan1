package cn.iocoder.yudao.module.fish.dal.mysql.zkchatmsgreceive;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatmsgreceive.ZkChatMsgReceiveDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkchatmsgreceive.vo.*;

/**
 * 聊天消息接受 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkChatMsgReceiveMapper extends BaseMapperX<ZkChatMsgReceiveDO> {

    default PageResult<ZkChatMsgReceiveDO> selectPage(ZkChatMsgReceivePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkChatMsgReceiveDO>()
                .eqIfPresent(ZkChatMsgReceiveDO::getChatMsgId, reqVO.getChatMsgId())
                .eqIfPresent(ZkChatMsgReceiveDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkChatMsgReceiveDO::getHandleType, reqVO.getHandleType())
                .betweenIfPresent(ZkChatMsgReceiveDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkChatMsgReceiveDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(ZkChatMsgReceiveDO::getId));
    }

}