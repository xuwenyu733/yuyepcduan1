package cn.iocoder.yudao.module.fish.service.zkchatlog;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkchatlog.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatlog.ChatLogDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkchatlog.ChatLogConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkchatlog.ChatLogMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 聊天日志 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ChatLogServiceImpl implements ChatLogService {

    @Resource
    private ChatLogMapper chatLogMapper;
    @Autowired
    private AdminUserApi adminUserApi;

    @Override
    public Long createChatLog(ChatLogCreateReqVO createReqVO) {
        // 插入
        ChatLogDO chatLog = BeanUtils.toBean(createReqVO, ChatLogDO.class);
        chatLogMapper.insert(chatLog);
        // 返回
        return chatLog.getId();
    }

    @Override
    public void updateChatLog(ChatLogUpdateReqVO updateReqVO) {
        // 校验存在
        validateChatLogExists(updateReqVO.getId());
        // 更新
        ChatLogDO updateObj = BeanUtils.toBean(updateReqVO, ChatLogDO.class);
        chatLogMapper.updateById(updateObj);
    }

    @Override
    public void deleteChatLog(Long id) {
        // 校验存在
        validateChatLogExists(id);
        // 删除
        chatLogMapper.deleteById(id);
    }

    private void validateChatLogExists(Long id) {
        if (chatLogMapper.selectById(id) == null) {
            throw exception(CHAT_LOG_NOT_EXISTS);
        }
    }

    @Override
    public ChatLogDO getChatLog(Long id) {
        return chatLogMapper.selectById(id);
    }

    @Override
    public List<ChatLogDO> getChatLogList(Collection<Long> ids) {
        return chatLogMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ChatLogDO> getChatLogPage(ChatLogPageReqVO pageReqVO) {
        return chatLogMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ChatLogDO> getChatLogList(ChatLogExportReqVO exportReqVO) {
        return chatLogMapper.selectList(exportReqVO);
    }

    @Override
    public Integer customSaveList(List<ChatLogDO> logDOS) {
        if(EasyListUtil.isEmpty(logDOS)) return 0;
        AdminUserRespDTO user = adminUserApi.getUser(SecurityFrameworkUtils.getLoginUserId());
        logDOS.forEach(obj -> {
            String username = user.getNickname();
            Long deptId = user.getDeptId();
            obj.build(username, deptId);
        });

        chatLogMapper.insertBatch(logDOS);
        return logDOS.size();
    }

    @Override
    public void customBatchUpdate(List<ChatLogDO> asList) {
        if(EasyListUtil.isEmpty(asList)) return;
        chatLogMapper.updateBatch(asList, asList.size());
    }

    @Override
    public ChatLogDO getByUserIdAndFid(Long userId, String roleName, String fid) {
        QueryWrapper<ChatLogDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("role_name", roleName)
                .eq("fid", fid).orderByDesc("id").last("limit 1");
        return chatLogMapper.selectOne(queryWrapper);
    }

    @Override
    public List<ChatLogDO> getByUserId(Long userId) {
        if(userId == null) return new ArrayList<>();
        QueryWrapper<ChatLogDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("id");
        List<ChatLogDO> chatLogDOS = chatLogMapper.selectList(queryWrapper);
        return chatLogDOS;
    }

}
