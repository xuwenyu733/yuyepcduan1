package cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @创建人 lr
 * @创建时间 2025-05-15
 * @描述
 */
@Data
@NoArgsConstructor
public class ToolModelItem {

    private Long id;
    private String targetPath; // 路径
    private String targetPageName; // 组件名
    private String modelName; // 模块名，eg: 投放记录
    private String judgeParamPath; // 参数校验地址，eg：
    private String appPath; // app端跳转路径

}
