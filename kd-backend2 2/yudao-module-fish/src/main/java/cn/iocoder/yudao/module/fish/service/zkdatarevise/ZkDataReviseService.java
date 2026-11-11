package cn.iocoder.yudao.module.fish.service.zkdatarevise;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkdatarevise.vo.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkfeedinglog.vo.ZkFeedingLogUpdateReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkpond.vo.ZkPondUpdateReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.vo.ZkPondFishDetailUpdateReqVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdatarevise.ZkDataReviseDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 数据修正记录 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkDataReviseService {

    /**
     * 创建数据修正记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkDataRevise(@Valid ZkDataReviseCreateReqVO createReqVO);

    /**
     * 更新数据修正记录
     *
     * @param updateReqVO 更新信息
     */
    void updateZkDataRevise(@Valid ZkDataReviseUpdateReqVO updateReqVO);

    /**
     * 删除数据修正记录
     *
     * @param id 编号
     */
    void deleteZkDataRevise(Long id);

    /**
     * 获得数据修正记录
     *
     * @param id 编号
     * @return 数据修正记录
     */
    ZkDataReviseDO getZkDataRevise(Long id);

    /**
     * 获得数据修正记录列表
     *
     * @param ids 编号
     * @return 数据修正记录列表
     */
    List<ZkDataReviseDO> getZkDataReviseList(Collection<Long> ids);

    /**
     * 获得数据修正记录分页
     *
     * @param pageReqVO 分页查询
     * @return 数据修正记录分页
     */
    PageResult<ZkDataReviseDO> getZkDataRevisePage(ZkDataRevisePageReqVO pageReqVO);

    /**
     * 获得数据修正记录列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 数据修正记录列表
     */
    List<ZkDataReviseDO> getZkDataReviseList(ZkDataReviseExportReqVO exportReqVO);

    void customInsert(ZkPondFishDetailUpdateReqVO updateReqVO);

    void customInsertPond(ZkPondUpdateReqVO updateReqVO);

    void customInsertFeedingLog(ZkFeedingLogUpdateReqVO updateReqVO);
}
