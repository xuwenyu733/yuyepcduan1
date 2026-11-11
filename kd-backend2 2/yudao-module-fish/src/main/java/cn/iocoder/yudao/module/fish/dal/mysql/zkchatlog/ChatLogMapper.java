package cn.iocoder.yudao.module.fish.dal.mysql.zkchatlog;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatlog.ChatLogDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkchatlog.vo.*;

/**
 * 聊天日志 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ChatLogMapper extends BaseMapperX<ChatLogDO> {

    default PageResult<ChatLogDO> selectPage(ChatLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ChatLogDO>()
                .likeIfPresent(ChatLogDO::getRoleName, reqVO.getRoleName())
                .eqIfPresent(ChatLogDO::getChatContent, reqVO.getChatContent())
                .eqIfPresent(ChatLogDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ChatLogDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ChatLogDO::getDeptId, reqVO.getDeptId())
                .betweenIfPresent(ChatLogDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ChatLogDO::getFid, reqVO.getFid())
                .orderByDesc(ChatLogDO::getId));
    }

    default List<ChatLogDO> selectList(ChatLogExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ChatLogDO>()
                .likeIfPresent(ChatLogDO::getRoleName, reqVO.getRoleName())
                .eqIfPresent(ChatLogDO::getChatContent, reqVO.getChatContent())
                .eqIfPresent(ChatLogDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ChatLogDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ChatLogDO::getDeptId, reqVO.getDeptId())
                .betweenIfPresent(ChatLogDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ChatLogDO::getFid, reqVO.getFid())
                .orderByDesc(ChatLogDO::getId));
    }

}
