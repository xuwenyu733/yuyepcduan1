package cn.iocoder.yudao.module.fish.service.zkkonwledgetree;

import java.util.*;
import jakarta.validation.*;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.ReceiveOperateVo;
import cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkkonwledgetree.ZkKonwledgeTreeDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 知识树 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkKonwledgeTreeService {

    /**
     * 创建知识树
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkKonwledgeTree(@Valid ZkKonwledgeTreeCreateReqVO createReqVO);

    /**
     * 更新知识树
     *
     * @param updateReqVO 更新信息
     */
    void updateZkKonwledgeTree(@Valid ZkKonwledgeTreeUpdateReqVO updateReqVO);

    /**
     * 删除知识树
     *
     * @param id 编号
     */
    void deleteZkKonwledgeTree(Long id);

    /**
     * 获得知识树
     *
     * @param id 编号
     * @return 知识树
     */
    ZkKonwledgeTreeDO getZkKonwledgeTree(Long id);

    /**
     * 获得知识树列表
     *
     * @param ids 编号
     * @return 知识树列表
     */
    List<ZkKonwledgeTreeDO> getZkKonwledgeTreeList(Collection<Long> ids);

    /**
     * 获得知识树分页
     *
     * @param pageReqVO 分页查询
     * @return 知识树分页
     */
    PageResult<ZkKonwledgeTreeDO> getZkKonwledgeTreePage(ZkKonwledgeTreePageReqVO pageReqVO);

    /**
     * 获得知识树列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 知识树列表
     */
    List<ZkKonwledgeTreeDO> getZkKonwledgeTreeList(ZkKonwledgeTreeExportReqVO exportReqVO);

    List<ZkKonwledgeTreeDO> selectList();

    CommonResult<String> getInfoByVo(ReceiveOperateVo vo);
}
