package cn.iocoder.yudao.module.fish.controller.base_component;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.easy.EmailUtil;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @创建人 lr
 * @创建时间 2025-08-08
 * @描述
 */
@Component
@Slf4j
public class EmailNoticeComponent {

    @Autowired
    private AdminUserApi adminUserApi;

    public CommonResult<String> noticeUser(Long userId, String subTitle, String content) {
        if(userId == null)  return CommonResult.error(403, "用户id为空,userId: "+userId);
        // 1. 获取用户信息
        AdminUserRespDTO currUser = adminUserApi.getUser(userId);
        if(currUser == null || StringUtils.isEmpty(currUser.getEmail())) return CommonResult.error(403, "用户为空或该用户没有对应邮箱,userId: "+userId);
        // 2. 执行通知
        EmailUtil.send_SC(subTitle, content, "", currUser.getEmail());

        return CommonResult.success("成功");
    }

}
