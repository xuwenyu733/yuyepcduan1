package cn.iocoder.yudao.module.fish.service.zkschedulingtype;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkschedulingtype.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkschedulingtype.ZkSchedulingTypeDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 检测配置 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkSchedulingTypeService {

    /**
     * 创建检测配置
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkSchedulingType(@Valid ZkSchedulingTypeCreateReqVO createReqVO);

    /**
     * 更新检测配置
     *
     * @param updateReqVO 更新信息
     */
    void updateZkSchedulingType(@Valid ZkSchedulingTypeUpdateReqVO updateReqVO);

    /**
     * 删除检测配置
     *
     * @param id 编号
     */
    void deleteZkSchedulingType(Long id);

    /**
     * 获得检测配置
     *
     * @param id 编号
     * @return 检测配置
     */
    ZkSchedulingTypeDO getZkSchedulingType(Long id);

    /**
     * 获得检测配置列表
     *
     * @param ids 编号
     * @return 检测配置列表
     */
    List<ZkSchedulingTypeDO> getZkSchedulingTypeList(Collection<Long> ids);

    /**
     * 获得检测配置分页
     *
     * @param pageReqVO 分页查询
     * @return 检测配置分页
     */
    PageResult<ZkSchedulingTypeDO> getZkSchedulingTypePage(ZkSchedulingTypePageReqVO pageReqVO);

    /**
     * 获得检测配置列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 检测配置列表
     */
    List<ZkSchedulingTypeDO> getZkSchedulingTypeList(ZkSchedulingTypeExportReqVO exportReqVO);

}
