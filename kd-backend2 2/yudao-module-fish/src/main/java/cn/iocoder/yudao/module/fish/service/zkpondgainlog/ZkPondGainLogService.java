package cn.iocoder.yudao.module.fish.service.zkpondgainlog;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondgainlog.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondgainlog.ZkPondGainLogDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 鱼生长 Service 接口
 *
 * @author admin2
 */
public interface ZkPondGainLogService {

    /**
     * 创建鱼生长
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkPondGainLog(@Valid ZkPondGainLogSaveReqVO createReqVO);

    /**
     * 更新鱼生长
     *
     * @param updateReqVO 更新信息
     */
    void updateZkPondGainLog(@Valid ZkPondGainLogSaveReqVO updateReqVO);

    /**
     * 删除鱼生长
     *
     * @param id 编号
     */
    void deleteZkPondGainLog(Long id);

    /**
    * 批量删除鱼生长
    *
    * @param ids 编号
    */
    void deleteZkPondGainLogListByIds(List<Long> ids);

    /**
     * 获得鱼生长
     *
     * @param id 编号
     * @return 鱼生长
     */
    ZkPondGainLogDO getZkPondGainLog(Long id);

    /**
     * 获得鱼生长分页
     *
     * @param pageReqVO 分页查询
     * @return 鱼生长分页
     */
    PageResult<ZkPondGainLogDO> getZkPondGainLogPage(ZkPondGainLogPageReqVO pageReqVO);

}