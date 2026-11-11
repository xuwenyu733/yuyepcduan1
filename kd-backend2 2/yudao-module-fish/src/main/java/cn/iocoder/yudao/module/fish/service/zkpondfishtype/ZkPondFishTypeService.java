package cn.iocoder.yudao.module.fish.service.zkpondfishtype;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishtype.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishtype.ZkPondFishTypeDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 鱼苗种类 Service 接口
 *
 * @author admin2
 */
public interface ZkPondFishTypeService {

    /**
     * 创建鱼苗种类
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkPondFishType(@Valid ZkPondFishTypeSaveReqVO createReqVO);

    /**
     * 更新鱼苗种类
     *
     * @param updateReqVO 更新信息
     */
    void updateZkPondFishType(@Valid ZkPondFishTypeSaveReqVO updateReqVO);

    /**
     * 删除鱼苗种类
     *
     * @param id 编号
     */
    void deleteZkPondFishType(Long id);

    /**
    * 批量删除鱼苗种类
    *
    * @param ids 编号
    */
    void deleteZkPondFishTypeListByIds(List<Long> ids);

    /**
     * 获得鱼苗种类
     *
     * @param id 编号
     * @return 鱼苗种类
     */
    ZkPondFishTypeDO getZkPondFishType(Long id);

    /**
     * 获得鱼苗种类分页
     *
     * @param pageReqVO 分页查询
     * @return 鱼苗种类分页
     */
    PageResult<ZkPondFishTypeDO> getZkPondFishTypePage(ZkPondFishTypePageReqVO pageReqVO);

    void saveBatch(List<ZkPondFishTypeDO> list);

    void remove(com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ZkPondFishTypeDO> queryWrapper);
}