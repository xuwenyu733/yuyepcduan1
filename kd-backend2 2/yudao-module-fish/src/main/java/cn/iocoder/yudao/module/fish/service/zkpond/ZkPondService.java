package cn.iocoder.yudao.module.fish.service.zkpond;

import java.math.BigDecimal;
import java.util.*;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpond.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 存塘 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkPondService {

    /**
     * 创建存塘
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    ZkPondDO createZkPond(@Valid ZkPondCreateReqVO createReqVO);

    /**
     * 更新存塘
     *
     * @param updateReqVO 更新信息
     */
    List<ZkPondDO> updateZkPond(@Valid ZkPondUpdateReqVO updateReqVO);

    /**
     * 删除存塘
     *
     * @param id 编号
     */
    void deleteZkPond(Long id);

    /**
     * 获得存塘
     *
     * @param id 编号
     * @return 存塘
     */
    ZkPondDO getZkPond(Long id);

    /**
     * 获得存塘列表
     *
     * @param ids 编号
     * @return 存塘列表
     */
    List<ZkPondDO> getZkPondList(Collection<Long> ids);

    /**
     * 获得存塘分页
     *
     * @param pageReqVO 分页查询
     * @return 存塘分页
     */
    PageResult<ZkPondDO> getZkPondPage(ZkPondPageReqVO pageReqVO);

    /**
     * 获得存塘列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 存塘列表
     */
    List<ZkPondDO> getZkPondList(ZkPondExportReqVO exportReqVO);

    List<ZkPondDO> getByPondNum(String pondNum);
    List<ZkPondDO> getByPondDetailNum(String pondDetailNum);

    void updateById(ZkPondDO zkPondDO);

    String getPondInfo();

    PondSummaryVo getSummaryInfo();

    void batchUpdate(List<ZkPondDO> list);

    void updateFishNum(Long pondId, Long newFishNum);

    ZkPondDO getOne();

    ZkPondDO customUpdateById(ZkPondDO zkPondDO);

    ZkPondDO selectFirst();

    List<ZkPondDO> selectByPondIds(Set<Long> pondSet);
    default Map<Long, ZkPondDO> selectMapByPondIds(Set<Long> pondSet) {
        return CollectionUtils.convertMap(selectByPondIds(pondSet), ZkPondDO::getId);
    }

    List<ZkPondDO> selectList();

    Integer customUpdateBatchById(List<ZkPondDO> pondDOList);

    /**
     * 分池后，需要更新存塘对应avg规格=(原塘重量g+分过来重量g)/(原塘鱼数+分过来鱼数)
     * @param targetPondId
     * @param splitAvgFishSpec
     */
    void updateFishSpecByPondId(Long targetPondId, BigDecimal splitAvgFishSpec);
}
