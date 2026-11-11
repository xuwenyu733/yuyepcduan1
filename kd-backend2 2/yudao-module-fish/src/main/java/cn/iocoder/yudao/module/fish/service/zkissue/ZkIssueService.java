package cn.iocoder.yudao.module.fish.service.zkissue;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkissue.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkissue.ZkIssueDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 问题总结 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkIssueService {

    /**
     * 创建问题总结
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkIssue(@Valid ZkIssueCreateReqVO createReqVO);

    /**
     * 更新问题总结
     *
     * @param updateReqVO 更新信息
     */
    void updateZkIssue(@Valid ZkIssueUpdateReqVO updateReqVO);

    /**
     * 删除问题总结
     *
     * @param id 编号
     */
    ZkIssueDO deleteZkIssue(Long id);

    /**
     * 获得问题总结
     *
     * @param id 编号
     * @return 问题总结
     */
    ZkIssueDO getZkIssue(Long id);

    /**
     * 获得问题总结列表
     *
     * @param ids 编号
     * @return 问题总结列表
     */
    List<ZkIssueDO> getZkIssueList(Collection<Long> ids);

    /**
     * 获得问题总结分页
     *
     * @param pageReqVO 分页查询
     * @return 问题总结分页
     */
    PageResult<ZkIssueDO> getZkIssuePage(ZkIssuePageReqVO pageReqVO);

    /**
     * 获得问题总结列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 问题总结列表
     */
    List<ZkIssueDO> getZkIssueList(ZkIssueExportReqVO exportReqVO);

}
