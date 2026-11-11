package cn.iocoder.yudao.module.fish.dal.mysql.zkchatmsg;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatmsg.ZkChatMsgDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkchatmsg.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 聊天消息 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkChatMsgMapper extends BaseMapperX<ZkChatMsgDO> {

    default PageResult<ZkChatMsgDO> selectPage(ZkChatMsgPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkChatMsgDO>()
                .eqIfPresent(ZkChatMsgDO::getGroupId, reqVO.getGroupId())
                .eqIfPresent(ZkChatMsgDO::getTargetUserId, reqVO.getTargetUserId())
                .eqIfPresent(ZkChatMsgDO::getMsg, reqVO.getMsg())
                .eqIfPresent(ZkChatMsgDO::getFid, reqVO.getFid())
                .eqIfPresent(ZkChatMsgDO::getMsgType, reqVO.getMsgType())
                .betweenIfPresent(ZkChatMsgDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkChatMsgDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkChatMsgDO::getDeptId, reqVO.getDeptId())
                .orderByDesc(ZkChatMsgDO::getId));
    }

    @Select({
            "SELECT \n" +
                    "zcm.*,zcg.group_name,su_o.nickname AS userName,su_t.nickname AS targetUserName \n" +
                    "FROM zk_chat_msg zcm \n" +
                    "LEFT JOIN zk_chat_group zcg ON zcg.id = zcm.group_id\n" +
                    "LEFT JOIN system_users su_o ON su_o.id = zcm.user_id\n" +
                    "LEFT JOIN zk_chat_msg_receive zcmr ON zcmr.chat_msg_id = zcm.id \n" +
                    "LEFT JOIN system_users su_t ON su_t.id = zcmr.user_id " +
                    "${ew.customSqlSegment}"
    })
    List<ZkChatMsgDO> customList(@Param("ew")QueryWrapper<ZkChatMsgDO> queryWrapper);

}