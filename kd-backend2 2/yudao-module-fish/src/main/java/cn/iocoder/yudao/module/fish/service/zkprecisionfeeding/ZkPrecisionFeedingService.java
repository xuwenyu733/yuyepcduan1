package cn.iocoder.yudao.module.fish.service.zkprecisionfeeding;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkprecisionfeeding.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkprecisionfeeding.ZkPrecisionFeedingDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 精准投喂 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkPrecisionFeedingService {

    /**
     * 创建精准投喂
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkPrecisionFeeding(@Valid ZkPrecisionFeedingCreateReqVO createReqVO);

    /**
     * 更新精准投喂
     *
     * @param updateReqVO 更新信息
     */
    void updateZkPrecisionFeeding(@Valid ZkPrecisionFeedingUpdateReqVO updateReqVO);

    /**
     * 删除精准投喂
     *
     * @param id 编号
     */
    void deleteZkPrecisionFeeding(Long id);

    /**
     * 获得精准投喂
     *
     * @param id 编号
     * @return 精准投喂
     */
    ZkPrecisionFeedingDO getZkPrecisionFeeding(Long id);

    /**
     * 获得精准投喂列表
     *
     * @param ids 编号
     * @return 精准投喂列表
     */
    List<ZkPrecisionFeedingDO> getZkPrecisionFeedingList(Collection<Long> ids);

    /**
     * 获得精准投喂分页
     *
     * @param pageReqVO 分页查询
     * @return 精准投喂分页
     */
    PageResult<ZkPrecisionFeedingDO> getZkPrecisionFeedingPage(ZkPrecisionFeedingPageReqVO pageReqVO);

    /**
     * 获得精准投喂列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 精准投喂列表
     */
    List<ZkPrecisionFeedingDO> getZkPrecisionFeedingList(ZkPrecisionFeedingExportReqVO exportReqVO);

}
