package cn.iocoder.yudao.module.system.dal.dataobject.oauth2;

import cn.iocoder.yudao.framework.common.enums.DeviceTypeEnum;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import cn.iocoder.yudao.module.system.dal.dataobject.tenant.TenantDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * OAuth2 访问令牌 DO
 *
 * 如下字段，暂时未使用，暂时不支持：
 * user_name、authentication（用户信息）
 *
 * @author 芋道源码
 */
@TableName(value = "system_oauth2_access_token", autoResultMap = true)
@KeySequence("system_oauth2_access_token_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class OAuth2AccessTokenDO extends TenantBaseDO {

    /**
     * 编号，数据库递增
     */
    @TableId
    private Long id;
    /**
     * 访问令牌
     */
    private String accessToken;
    /**
     * 刷新令牌
     */
    private String refreshToken;
    /**
     * 用户编号
     */
    private Long userId;
    @TableField(exist = false)
    private Long deptId;
    /**
     * 用户类型
     *
     * 枚举 {@link UserTypeEnum}
     */
    private Integer userType;
    /**
     * 用户信息
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, String> userInfo;
    /**
     * 客户端编号
     *
     * 关联 {@link OAuth2ClientDO#getId()}
     */
    private String clientId;
    /**
     * 设备类型： pc、app
     * 枚举位置：   cn.kbsys.kbs.framework.common.enums.DeviceTypeEnum
     */
    private String deviceType = DeviceTypeEnum.PC.getDeviceType();
    /**
     * 授权范围
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> scopes;
    /**
     * 过期时间
     */
    private LocalDateTime expiresTime;
    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private String nickname;
    @TableField(exist = false)
    private String roleCode;
    @TableField(exist = false)
    private DeptDO deptDO;
    @TableField(exist = false)
    private TenantDO tenantDO; // 租户信息

    public OAuth2AccessTokenDO build_auth(AdminUserDO user) {
        this.setTenantId(user.getTenantId());
        this.setDeptId(user.getDeptId());
        this.setUsername(user.getUsername());
        this.setNickname(user.getNickname());
        this.setRoleCode(user.getRoleCode());
        this.setTenantDO(user.getTenantDO());
        this.setDeptDO(user.getDeptDO());

        if(StringUtil.isEmpty(user.getUsername())) {
            this.setUsername("遗失");
        }
        if(StringUtil.isEmpty(user.getNickname())) {
            this.setNickname("遗失");
        }
        return this;
    }

    public OAuth2AccessTokenDO build_deviceType(String deviceType) {
        this.deviceType = deviceType;
        return this;
    }

}
