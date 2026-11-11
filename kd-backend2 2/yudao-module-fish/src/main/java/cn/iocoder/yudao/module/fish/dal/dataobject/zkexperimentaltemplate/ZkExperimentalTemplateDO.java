package cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentaltemplate;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 实验模板 DO
 */
@TableName("zk_experimental_template")
@KeySequence("zk_experimental_template_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkExperimentalTemplateDO extends BaseDO {

    @TableId
    private Long id;

    /** 模板名称 */
    private String templateName;

    /** 模板类型 */
    private String templateType;

    /** 模板编码 */
    private String templateCode;

    /** 品种 */
    private String variety;

    /** 实验目标 */
    private String experimentalObjective;

    /** 实验内容 */
    private String experimentalContent;

    /** 实验方法 */
    private String experimentalMethod;

    /** 描述 */
    private String description;

    /** 状态 0启用 1禁用 */
    private Integer status;

    /** 所属部门 */
    private Long deptId;

}
