package cn.iocoder.yudao.module.fish.service.zkpaper;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpaper.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpaper.ZkPaperDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 试卷 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkPaperService {

    /**
     * 创建试卷
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    ZkPaperDO createZkPaper(@Valid ZkPaperCreateReqVO createReqVO);

    /**
     * 更新试卷
     *
     * @param updateReqVO 更新信息
     */
    ZkPaperDO updateZkPaper(@Valid ZkPaperUpdateReqVO updateReqVO);

    /**
     * 删除试卷
     *
     * @param id 编号
     */
    void deleteZkPaper(Long id);

    /**
     * 获得试卷
     *
     * @param id 编号
     * @return 试卷
     */
    ZkPaperDO getZkPaper(Long id);

    /**
     * 获得试卷列表
     *
     * @param ids 编号
     * @return 试卷列表
     */
    List<ZkPaperDO> getZkPaperList(Collection<Long> ids);

    /**
     * 获得试卷分页
     *
     * @param pageReqVO 分页查询
     * @return 试卷分页
     */
    PageResult<ZkPaperDO> getZkPaperPage(ZkPaperPageReqVO pageReqVO);

    /**
     * 获得试卷列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 试卷列表
     */
    List<ZkPaperDO> getZkPaperList(ZkPaperExportReqVO exportReqVO);

    List<ZkPaperDO> listForNeedExam(ZkPaperPageReqVO pageVO);
}
