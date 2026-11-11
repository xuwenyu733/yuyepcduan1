package cn.iocoder.yudao.module.fish.service.zkdocument;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.fish.controller.admin.zkdocument.vo.*;
import jakarta.validation.Valid;

/**
 * 文档 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkDocumentService {

    /**
     * 创建文档
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createDocument(@Valid ZkDocumentCreateReqVO createReqVO);

    /**
     * 获得文档分页
     *
     * @param pageReqVO 分页查询
     * @return 文档分页
     */
    PageResult<ZkDocumentRespVO> getDocumentPage(ZkDocumentPageReqVO pageReqVO);

    /**
     * 获得文档详情
     *
     * @param id 编号
     * @return 文档详情
     */
    ZkDocumentRespVO getDocument(Long id);

    /**
     * 删除文档
     *
     * @param id 编号
     */
    void deleteDocument(Long id);

    /**
     * 更新文档状态（共享/私有）
     *
     * @param id 编号
     * @param status 状态
     */
    void updateDocumentStatus(Long id, String status);

    /**
     * 获得文档统计
     *
     * @return 文档统计
     */
    ZkDocumentStatisticsVO getDocumentStatistics();

    /**
     * 获得WPS编辑链接
     *
     * @param documentId 文档ID
     * @return WPS编辑链接和token
     */
    ZkDocumentWPSLinkVO getWPSEditLink(Long documentId);
}

