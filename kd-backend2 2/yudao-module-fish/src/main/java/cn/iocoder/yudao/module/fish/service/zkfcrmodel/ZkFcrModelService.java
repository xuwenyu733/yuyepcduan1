package cn.iocoder.yudao.module.fish.service.zkfcrmodel;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkfcrmodel.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfcrmodel.ZkFcrModelDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 饵料系数FCR模型 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkFcrModelService {

    /**
     * 创建饵料系数FCR模型
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkFcrModel(@Valid ZkFcrModelCreateReqVO createReqVO);

    /**
     * 更新饵料系数FCR模型
     *
     * @param updateReqVO 更新信息
     */
    void updateZkFcrModel(@Valid ZkFcrModelUpdateReqVO updateReqVO);

    /**
     * 删除饵料系数FCR模型
     *
     * @param id 编号
     */
    void deleteZkFcrModel(Long id);

    /**
     * 获得饵料系数FCR模型
     *
     * @param id 编号
     * @return 饵料系数FCR模型
     */
    ZkFcrModelDO getZkFcrModel(Long id);

    /**
     * 获得饵料系数FCR模型列表
     *
     * @param ids 编号
     * @return 饵料系数FCR模型列表
     */
    List<ZkFcrModelDO> getZkFcrModelList(Collection<Long> ids);

    /**
     * 获得饵料系数FCR模型分页
     *
     * @param pageReqVO 分页查询
     * @return 饵料系数FCR模型分页
     */
    PageResult<ZkFcrModelDO> getZkFcrModelPage(ZkFcrModelPageReqVO pageReqVO);

    /**
     * 获得饵料系数FCR模型列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 饵料系数FCR模型列表
     */
    List<ZkFcrModelDO> getZkFcrModelList(ZkFcrModelExportReqVO exportReqVO);

}
