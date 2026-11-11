package cn.iocoder.yudao.module.fish.service.zkexperimentalmedia;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalmedia.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalmedia.ZkExperimentalMediaDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 实验多媒体 Service 接口
 *
 * @author admin2
 */
public interface ZkExperimentalMediaService {

    /**
     * 创建实验多媒体
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkExperimentalMedia(@Valid ZkExperimentalMediaSaveReqVO createReqVO);

    /**
     * 更新实验多媒体
     *
     * @param updateReqVO 更新信息
     */
    void updateZkExperimentalMedia(@Valid ZkExperimentalMediaSaveReqVO updateReqVO);

    /**
     * 删除实验多媒体
     *
     * @param id 编号
     */
    void deleteZkExperimentalMedia(Long id);

    /**
    * 批量删除实验多媒体
    *
    * @param ids 编号
    */
    void deleteZkExperimentalMediaListByIds(List<Long> ids);

    /**
     * 获得实验多媒体
     *
     * @param id 编号
     * @return 实验多媒体
     */
    ZkExperimentalMediaDO getZkExperimentalMedia(Long id);

    /**
     * 获得实验多媒体分页
     *
     * @param pageReqVO 分页查询
     * @return 实验多媒体分页
     */
    PageResult<ZkExperimentalMediaDO> getZkExperimentalMediaPage(ZkExperimentalMediaPageReqVO pageReqVO);

    List<ZkExperimentalMediaDO> getByIds(Set<Long> experimentalIdSet);

    Map<Long, List<ZkExperimentalMediaDO>> selectMapByIds(Set<Long> experimentalIdSet);
}