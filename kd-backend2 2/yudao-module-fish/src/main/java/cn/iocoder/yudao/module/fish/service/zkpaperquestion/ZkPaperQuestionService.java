package cn.iocoder.yudao.module.fish.service.zkpaperquestion;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpaperquestion.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpaperquestion.ZkPaperQuestionDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 试题 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkPaperQuestionService {

    /**
     * 创建试题
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkPaperQuestion(@Valid ZkPaperQuestionCreateReqVO createReqVO);

    /**
     * 更新试题
     *
     * @param updateReqVO 更新信息
     */
    void updateZkPaperQuestion(@Valid ZkPaperQuestionUpdateReqVO updateReqVO);

    /**
     * 删除试题
     *
     * @param id 编号
     */
    void deleteZkPaperQuestion(Long id);

    /**
     * 获得试题
     *
     * @param id 编号
     * @return 试题
     */
    ZkPaperQuestionDO getZkPaperQuestion(Long id);

    /**
     * 获得试题列表
     *
     * @param ids 编号
     * @return 试题列表
     */
    List<ZkPaperQuestionDO> getZkPaperQuestionList(Collection<Long> ids);

    /**
     * 获得试题分页
     *
     * @param pageReqVO 分页查询
     * @return 试题分页
     */
    PageResult<ZkPaperQuestionDO> getZkPaperQuestionPage(ZkPaperQuestionPageReqVO pageReqVO);

    /**
     * 获得试题列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 试题列表
     */
    List<ZkPaperQuestionDO> getZkPaperQuestionList(ZkPaperQuestionExportReqVO exportReqVO);

    boolean customSaveBatch(List<ZkPaperQuestionDO> serPaperQuestions);

    void removeByPaperId(Long paperId);

    List<ZkPaperQuestionDO> listByIds(List<Long> questionIds);
}
