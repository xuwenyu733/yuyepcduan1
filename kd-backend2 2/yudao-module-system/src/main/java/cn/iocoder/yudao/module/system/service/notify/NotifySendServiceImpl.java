package cn.iocoder.yudao.module.system.service.notify;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import cn.iocoder.yudao.framework.common.base_api.BaseUserRelationService;
import cn.iocoder.yudao.framework.common.base_api.enums.UserRelationTypeEnum;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.enums.RedisKeyConstants;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyMap3;
import cn.iocoder.yudao.framework.common.util.easy.EasyMapUtil;
import cn.iocoder.yudao.framework.common.util.easy.EmailUtil;
import cn.iocoder.yudao.framework.redis.core.PlusRedisService;
import cn.iocoder.yudao.module.infra.websocket.WebSocketUsers;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.module.system.dal.dataobject.notify.NotifyMessageDO;
import cn.iocoder.yudao.module.system.dal.dataobject.notify.NotifyTemplateDO;
import cn.iocoder.yudao.module.system.enums.notify.NotifyTemplateCodeEnum;
import com.alibaba.fastjson.JSON;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.*;

/**
 * 站内信发送 Service 实现类
 *
 * @author xrcoder
 */
@Service
@Validated
@Slf4j
public class NotifySendServiceImpl implements NotifySendService {

    @Resource
    private NotifyTemplateService notifyTemplateService;

    @Resource
    private NotifyMessageService notifyMessageService;

    @Resource
    private PlusRedisService plusRedisService;

    @Resource
    private BaseUserRelationService baseUserRelationService;

    @Value("${KEDE_COMMON_API_URL}")
    private String KEDE_COMMON_API_URL;

    @Value("${KEDE_COMMON_API_PREUUID}")
    private String KEDE_COMMON_API_PREUUID;

    @Resource
    private AdminUserApi adminUserApi;


    @Override
    public Long sendSingleNotifyToAdmin(Long userId, String templateCode, Map<String, Object> templateParams) {
        return sendSingleNotify(userId, UserTypeEnum.ADMIN.getValue(), templateCode, templateParams);
    }

    @Override
    public Long sendSingleNotifyToMember(Long userId, String templateCode, Map<String, Object> templateParams) {
        return sendSingleNotify(userId, UserTypeEnum.MEMBER.getValue(), templateCode, templateParams);
    }

    @Override
    public Long sendSingleNotify(Long userId, Integer userType, String templateCode, Map<String, Object> templateParams) {
        return sendSingleNotify3(userId, userType, templateCode, templateParams).getId();
    }
    public NotifyMessageDO sendSingleNotify3(Long userId, Integer userType, String templateCode, Map<String, Object> templateParams) {
        return sendSingleNotify4(userId, userType, templateCode, templateParams, new HashMap<>());
    }
    public NotifyMessageDO sendSingleNotify4(Long userId, Integer userType, String templateCode, Map<String, Object> templateParams,
                                             Map<String, String> map) {
        // 校验模版
        NotifyTemplateDO template = validateNotifyTemplate(templateCode);
        if (Objects.equals(template.getStatus(), CommonStatusEnum.DISABLE.getStatus())) {
            log.info("[sendSingleNotify][模版({})已经关闭，无法给用户({}/{})发送]", templateCode, userId, userType);
            return null;
        }
        // 校验参数
        validateTemplateParams(template, templateParams);

        // 发送站内信
        String content = notifyTemplateService.formatNotifyTemplateContent(template.getContent(), templateParams);
        NotifyMessageDO message3 = notifyMessageService.createNotifyMessage4(userId, userType, template, content, templateParams, map);
        return message3;
    }

    @VisibleForTesting
    public NotifyTemplateDO validateNotifyTemplate(String templateCode) {
        // 获得站内信模板。考虑到效率，从缓存中获取
        NotifyTemplateDO template = notifyTemplateService.getNotifyTemplateByCodeFromCache(templateCode);
        // 站内信模板不存在
        if (template == null) {
            throw exception(NOTICE_NOT_FOUND);
        }
        return template;
    }

    /**
     * 校验站内信模版参数是否确实
     *
     * @param template 邮箱模板
     * @param templateParams 参数列表
     */
    @VisibleForTesting
    public void validateTemplateParams(NotifyTemplateDO template, Map<String, Object> templateParams) {
        template.getParams().forEach(key -> {
            Object value = templateParams.get(key);
            if (value == null) {
                throw exception(NOTIFY_SEND_TEMPLATE_PARAM_MISS, key);
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> insertNotify(Long userId, String msg) {
        return insertNotify(userId, msg, null);
    }
    public Map<String, Object> insertNotify(Long userId, String msg, Map<String, String> map) {
        // 1. 发送 通用 通知
        Map<String, Object> content = new EasyMap3<>().put("content", msg).value();
        NotifyMessageDO notifyMessageDO = sendSingleNotify4(userId, UserTypeEnum.ADMIN.getValue(), NotifyTemplateCodeEnum.GENERAL_TEMPLATE.getCode(), content, map);
        log.info("【添加notify通知-{}】middle,通知内容:{}", userId, JSON.toJSONString(notifyMessageDO));
        // 2. 从redis获取历史未读
        List<NotifyMessageDO> notifyMessageDOS = selectNeedNotify3(userId);
        if(EasyListUtil.isEmpty(notifyMessageDOS)) notifyMessageDOS = new ArrayList<>();
        notifyMessageDOS.add(notifyMessageDO);
        // 3. 将未读消息 放入redis 缓存
        String key = String.format(RedisKeyConstants.NEED_NOTIFY_KEY, userId.toString());
        plusRedisService.insertStr(key, JSON.toJSONString(notifyMessageDOS), RedisKeyConstants.NOTIFY_EXPIRE);
        // 4. 如果当前用户不在线，需要发送 App的离线通知
        if(!judgeUserIsOnLine(userId)) sendAppOfflineNotify(userId, notifyMessageDO.getId().toString(), "通知", msg);
        return BeanUtil.beanToMap(notifyMessageDO);
    }

    @Override
    public Map<String, Object> insertNotifyByMap(Map<String, String> map) {
        String userId = map.get("userId");
        String msg = map.get("msg");

        return insertNotify(Long.parseLong(userId), msg, map);
    }

    @Override
    public List<Map<String, Object>> selectNeedNotify(Long userId) {
        // 1. redis获取未读消息
        List<NotifyMessageDO> notifyMessageDOS = selectNeedNotify3(userId);
        if(EasyListUtil.isEmpty(notifyMessageDOS)) return new ArrayList<>();
        // 2. bean 2 map
        List<Map<String, Object>> list = notifyMessageDOS.stream().map(BeanUtil::beanToMap).toList();
        return list;
    }

    @Override
    public boolean judgeUserIsOnLine(Long userId) {
        if(userId == null) return false;
        return WebSocketUsers.APP_SESSION_MAP.containsKey(userId.toString());
    }

    @Override
    public String sendAppOfflineNotify(Long userId, String msgId, String title, String msg) {
        if(userId == null) return "用户id不能为空";
        if(judgeUserIsOnLine(userId)) return "当前用户在线，无需离线通知";
        // 1. 获取 cid
        Map<String, Object> userRelation = baseUserRelationService.getUserRelation(userId, UserRelationTypeEnum.GE_TUI.getRelationType());
        Object relationValue = EasyMapUtil.getMapValue(userRelation, "relationValue");
        if(relationValue == null ) return "用户未绑定个推";
        String cid = relationValue.toString();
        // 1.1 获取用户信息
        AdminUserRespDTO user = adminUserApi.getUser(userId);
        // 2. 发送个推
        Map<String, String> value = new EasyMap3<String>().put("preUUID", KEDE_COMMON_API_PREUUID)
                .put("cid", cid)
                .put("msgId", msgId)
                .put("title", String.format("【%s】%s", title, user.getNickname()))
                .put("msg", msg)
                .value();
        String body = HttpUtil.createPost(KEDE_COMMON_API_URL + "/getui/sendMsg")
                .body(JSON.toJSONString(value))
                .execute().body();
        log.warn("【发送个推-{}】middle,结果:{}", userId, body);
        if(!body.contains("200")) {
            // 3. 没有发送成功，则邮件提示
            String sub = String.format("【发送个推】异常，userId:%s(%s)", userId, user.getNickname());
            String error_msg = String.format("title:%s, msg:%s, error: %s", title, msg, body);
            EmailUtil.send(sub, error_msg);
            log.error(sub, error_msg);
        }
        return body;
    }

    @Override
    public List<NotifyMessageDO> selectNeedNotify3(Long userId) {
        // 1. redis获取需要提醒数据
        String key = String.format(RedisKeyConstants.NEED_NOTIFY_KEY, userId.toString());
        List<NotifyMessageDO> notifyMessageDOS = plusRedisService.selectArr(key, NotifyMessageDO.class);
        if(EasyListUtil.isNotEmpty(notifyMessageDOS)) return clearHadHandle(userId, notifyMessageDOS);
        // 2. 从数据库 获取
        List<NotifyMessageDO> list = notifyMessageService.selectByUserId(userId, UserTypeEnum.ADMIN.getValue(), false);
        if(EasyListUtil.isNotEmpty(list)) return clearHadHandle(userId, list);

        return notifyMessageDOS;
    }

    /**
     * redis中的消息列表可能 部分已经处理过了，需要清除掉这些 ， HAD_NOTIFY_KEY 记录了已经处理的id
     * @param mayNeedRead 可能未处理的列表
     * @return 最终返回实际需要处理的列表
     */
    public List<NotifyMessageDO> clearHadHandle(Long userId, List<NotifyMessageDO> mayNeedRead) {
        if(EasyListUtil.isEmpty(mayNeedRead)) return mayNeedRead;
        // 1. 获取 已经处理的 消息id
        List<Long> hadHandleIds = plusRedisService.selectArr(RedisKeyConstants.HAD_NOTIFY_KEY, Long.class);
        if(EasyListUtil.isEmpty(hadHandleIds)) return mayNeedRead;
        // 2. 判断 是否已读
        List<NotifyMessageDO> finalNeedNotifyList = new ArrayList<>();
        for(NotifyMessageDO item : mayNeedRead) {
            if(hadHandleIds.contains(item.getId())) continue;
            if(item.getReadStatus()) continue;
            finalNeedNotifyList.add(item);
        }
        // 3. 最终未处理的消息列表需要 存储
        String key = String.format(RedisKeyConstants.NEED_NOTIFY_KEY, userId.toString());
        plusRedisService.insertStr(key, JSON.toJSONString(finalNeedNotifyList), RedisKeyConstants.NOTIFY_EXPIRE);
        return finalNeedNotifyList;
    }
}
