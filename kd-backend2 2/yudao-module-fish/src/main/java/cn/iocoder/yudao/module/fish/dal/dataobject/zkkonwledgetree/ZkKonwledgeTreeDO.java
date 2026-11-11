package cn.iocoder.yudao.module.fish.dal.dataobject.zkkonwledgetree;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 知识树 DO
 *
 * @author 系统管理员
 */
@TableName("zk_konwledge_tree")
@KeySequence("zk_konwledge_tree_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkKonwledgeTreeDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 父编号
     */
    private Long parentId;
    /**
     * 节点内容
     */
    private String nodeTitle;
    /**
     * 节点tag(多个英文逗号隔开)
     */
    private String nodeTag;
    /**
     * 节点类型 cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree.to.KnowledgeNodeTypeEnum
     */
    private String nodeType;
    /**
     * 记录人
     */
    private Long userId;
    /**
     * 所属部门
     */
    private Long deptId;
    private Long tenantId;
    /**
     * 文件id
     */
    private String fid;
    /**
     * 文件url
     */
    private String fileUrl;
    private Float sort;
    private String handleContent; // 解决思路

    @TableField(exist = false)
    private List<ZkKonwledgeTreeDO> sonList; // 子节点
    @TableField(exist = false)
    private ZkKonwledgeTreeDO parentNode; // 父节点
    @TableField(exist = false)
    private Boolean wasVisited = false; // 是否访问过
    @TableField(exist = false)
    private Integer level = 0; // 层级
    private String illName; // 疾病名称
    private String nodeDescribe; // 节点描述

    @TableField(exist = false)
    private String bodyInfo; // 构造树时 填充部位
    @TableField(exist = false)
    private String symptomInfo; // 构造树时 填充症状
    @TableField(exist = false)
    private String reasonInfo; // 构造树 填充 发病原因

    public ZkKonwledgeTreeDO build_level(Integer level) {
        this.level = level;
        return this;
    }
    public ZkKonwledgeTreeDO build_info(String bodyInfo, String symptomInfo, String reasonInfo) {
        this.bodyInfo = bodyInfo;
        this.symptomInfo = symptomInfo;
        this.reasonInfo = reasonInfo;
        return this;
    }
    public ZkKonwledgeTreeDO build_parentNode(ZkKonwledgeTreeDO parentNode) {
        this.parentNode = parentNode;
        return this;
    }

    public String select_str() {
        StringBuilder split = new StringBuilder();
        StringBuilder content = new StringBuilder();
        if(StringUtils.isNotBlank(nodeTitle)) content.append(String.format("标题:%s", nodeTitle));
        if(StringUtils.isNotBlank(nodeTag)) content.append(String.format("(%s)", nodeTag));
        if(StringUtils.isNotBlank(handleContent)) content.append(String.format(" 解决思路： %s", handleContent));

        return content.toString();
    }

    public String select_str_for_tree() {
        StringBuilder split = new StringBuilder();
        for(int i=0;i<level*8;i++) split.append(" ");
        String prefix = split.append("||==").toString();
        StringBuilder content = new StringBuilder(prefix);
        if(StringUtils.isNotBlank(nodeTitle)) content.append(String.format("%s", nodeTitle));
        if(StringUtils.isNotBlank(nodeTag)) content.append(String.format("(%s)", nodeTag));
        if(StringUtils.isNotBlank(handleContent)) content.append(String.format("： %s", handleContent));

        return content.toString();
    }

}
/**

 if(StringUtils.isNotBlank(nodeTitle)) content.append(String.format("节点标题：%s ；", nodeTitle));
 if(StringUtils.isNotBlank(nodeTag)) content.append(String.format("节点描述：%s ；", nodeTag));
 if(StringUtils.isNotBlank(handleContent)) content.append(String.format("解决思路及方法：%s ；", handleContent));


 *
 */