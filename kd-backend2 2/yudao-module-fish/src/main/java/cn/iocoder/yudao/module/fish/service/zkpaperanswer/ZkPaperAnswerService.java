package cn.iocoder.yudao.module.fish.service.zkpaperanswer;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpaperanswer.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpaperanswer.ZkPaperAnswerDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 答题记录 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkPaperAnswerService {

    /**
     * 创建答题记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkPaperAnswer(@Valid ZkPaperAnswerCreateReqVO createReqVO);

    /**
     * 更新答题记录
     *
     * @param updateReqVO 更新信息
     */
    void updateZkPaperAnswer(@Valid ZkPaperAnswerUpdateReqVO updateReqVO);

    /**
     * 删除答题记录
     *
     * @param id 编号
     */
    void deleteZkPaperAnswer(Long id);

    /**
     * 获得答题记录
     *
     * @param id 编号
     * @return 答题记录
     */
    ZkPaperAnswerDO getZkPaperAnswer(Long id);

    /**
     * 获得答题记录列表
     *
     * @param ids 编号
     * @return 答题记录列表
     */
    List<ZkPaperAnswerDO> getZkPaperAnswerList(Collection<Long> ids);

    /**
     * 获得答题记录分页
     *
     * @param pageReqVO 分页查询
     * @return 答题记录分页
     */
    PageResult<ZkPaperAnswerDO> getZkPaperAnswerPage(ZkPaperAnswerPageReqVO pageReqVO);

    /**
     * 获得答题记录列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 答题记录列表
     */
    List<ZkPaperAnswerDO> getZkPaperAnswerList(ZkPaperAnswerExportReqVO exportReqVO);

}
