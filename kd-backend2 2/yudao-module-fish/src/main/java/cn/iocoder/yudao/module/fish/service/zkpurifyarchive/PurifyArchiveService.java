package cn.iocoder.yudao.module.fish.service.zkpurifyarchive;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifyarchive.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyarchive.PurifyArchiveDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 入池档案 Service 接口
 *
 * @author admin2
 */
public interface PurifyArchiveService {

    /**
     * 创建入池档案
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPurifyArchive(@Valid PurifyArchiveSaveReqVO createReqVO);

    /**
     * 更新入池档案
     *
     * @param updateReqVO 更新信息
     */
    void updatePurifyArchive(@Valid PurifyArchiveSaveReqVO updateReqVO);

    /**
     * 删除入池档案
     *
     * @param id 编号
     */
    void deletePurifyArchive(Long id);

    /**
    * 批量删除入池档案
    *
    * @param ids 编号
    */
    void deletePurifyArchiveListByIds(List<Long> ids);

    /**
     * 获得入池档案
     *
     * @param id 编号
     * @return 入池档案
     */
    PurifyArchiveDO getPurifyArchive(Long id);

    /**
     * 获得入池档案分页
     *
     * @param pageReqVO 分页查询
     * @return 入池档案分页
     */
    PageResult<PurifyArchiveDO> getPurifyArchivePage(PurifyArchivePageReqVO pageReqVO);

    List<PurifyArchiveDO> getByIds(Set<Long> achrivedIdSet);

    /**
     * 获取风味优化统计数据
     * @param pageReqVO 过滤条件
     * @return 统计数据
     */
    PurifyStatsRespVO getPurifyStats(PurifyArchivePageReqVO pageReqVO);

    /**
     * 获得记录查询分页
     *
     * @param pageReqVO 分页查询
     * @return 记录查询分页
     */
    PageResult<PurifyArchiveRespVO> getRecordQueryPage(PurifyArchivePageReqVO pageReqVO);

    /**
     * 同步所有流程统计数据
     */
    void syncAllProcessStats();
}