package cn.iocoder.yudao.module.fish.service.zkpondsystem;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondsystem.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondsystem.ZkPondSystemDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 存塘系统 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkPondSystemService {

    /**
     * 创建存塘系统
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkPondSystem(@Valid ZkPondSystemCreateReqVO createReqVO);

    /**
     * 更新存塘系统
     *
     * @param updateReqVO 更新信息
     */
    void updateZkPondSystem(@Valid ZkPondSystemUpdateReqVO updateReqVO);

    /**
     * 删除存塘系统
     *
     * @param id 编号
     */
    void deleteZkPondSystem(Long id);

    /**
     * 获得存塘系统
     *
     * @param id 编号
     * @return 存塘系统
     */
    ZkPondSystemDO getZkPondSystem(Long id);

    /**
     * 获得存塘系统列表
     *
     * @param ids 编号
     * @return 存塘系统列表
     */
    List<ZkPondSystemDO> getZkPondSystemList(Collection<Long> ids);

    /**
     * 获得存塘系统分页
     *
     * @param pageReqVO 分页查询
     * @return 存塘系统分页
     */
    PageResult<ZkPondSystemDO> getZkPondSystemPage(ZkPondSystemPageReqVO pageReqVO);

    /**
     * 获得存塘系统列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 存塘系统列表
     */
    List<ZkPondSystemDO> getZkPondSystemList(ZkPondSystemExportReqVO exportReqVO);

}
