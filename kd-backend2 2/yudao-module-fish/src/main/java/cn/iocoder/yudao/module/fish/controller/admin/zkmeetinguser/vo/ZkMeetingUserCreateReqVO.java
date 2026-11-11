package cn.iocoder.yudao.module.fish.controller.admin.zkmeetinguser.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 会议用户创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkMeetingUserCreateReqVO extends ZkMeetingUserBaseVO {

}
