package cn.iocoder.yudao.module.fish.service.zkpondfishparent;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishparent.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishparent.ZkPondFishParentDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 鱼池操作父 Service 接口
 *
 * @author admin2
 */
public interface ZkPondFishParentService {

    /**
     * 创建鱼池操作父
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkPondFishParent(@Valid ZkPondFishParentSaveReqVO createReqVO);

    /**
     * 更新鱼池操作父
     *
     * @param updateReqVO 更新信息
     */
    void updateZkPondFishParent(@Valid ZkPondFishParentSaveReqVO updateReqVO);

    /**
     * 删除鱼池操作父
     *
     * @param id 编号
     */
    void deleteZkPondFishParent(Long id);

    /**
    * 批量删除鱼池操作父
    *
    * @param ids 编号
    */
    void deleteZkPondFishParentListByIds(List<Long> ids);

    /**
     * 获得鱼池操作父
     *
     * @param id 编号
     * @return 鱼池操作父
     */
    ZkPondFishParentDO getZkPondFishParent(Long id);

    /**
     * 获得鱼池操作父分页
     *
     * @param pageReqVO 分页查询
     * @return 鱼池操作父分页
     */
    PageResult<ZkPondFishParentDO> getZkPondFishParentPage(ZkPondFishParentPageReqVO pageReqVO);

}