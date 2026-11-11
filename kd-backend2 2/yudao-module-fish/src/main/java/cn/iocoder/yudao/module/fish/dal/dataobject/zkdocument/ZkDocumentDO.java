package cn.iocoder.yudao.module.fish.dal.dataobject.zkdocument;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 文档 DO
 *
 * @author 系统管理员
 */
@TableName("zk_document")
@KeySequence("zk_document_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkDocumentDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 文档标题
     */
    private String title;

    /**
     * 文档类型：申报文档、设计文档、实验文档、工作计划、报告文档、其他
     */
    private String type;

    /**
     * 文档描述
     */
    private String description;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 作者姓名
     */
    private String author;

    /**
     * 部门
     */
    private String department;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 状态：shared-共享, private-私有
     */
    private String status;

    /**
     * 协作者（JSON数组或逗号分隔）
     */
    private String collaborators;

    /**
     * 租户ID
     */
    private Long tenantId;
}



