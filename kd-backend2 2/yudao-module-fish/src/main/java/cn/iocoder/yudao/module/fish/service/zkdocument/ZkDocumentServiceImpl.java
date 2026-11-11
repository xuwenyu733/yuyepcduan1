package cn.iocoder.yudao.module.fish.service.zkdocument;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.fish.controller.admin.zkdocument.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdocument.ZkDocumentDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkdocument.ZkDocumentMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.DOCUMENT_NOT_EXISTS;

/**
 * 文档 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
@Slf4j
public class ZkDocumentServiceImpl implements ZkDocumentService {

    @Resource
    private ZkDocumentMapper documentMapper;

    @Resource
    private WPSConfig wpsConfig;

    @Resource
    private WPSTokenUtil wpsTokenUtil;

    @Override
    public Long createDocument(ZkDocumentCreateReqVO createReqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        String userName = SecurityFrameworkUtils.getLoginUserNickname();

        // 如果前端没有传递部门，则从当前登录用户获取部门编码（部门ID）
        String department = createReqVO.getDepartment();
        if (department == null || department.isEmpty()) {
            // 获取当前登录用户的部门ID作为部门编码
            Long deptId = SecurityFrameworkUtils.getLoginUserDeptId();
            if (deptId != null) {
                // 将部门ID转换为字符串作为部门编码
                department = String.valueOf(deptId);
            }
        }

        // 插入
        ZkDocumentDO document = BeanUtils.toBean(createReqVO, ZkDocumentDO.class);
        document.setAuthorId(userId);
        document.setAuthor(userName != null ? userName : "未知用户");
        document.setDepartment(department);
        documentMapper.insert(document);

        // 返回
        return document.getId();
    }

    @Override
    public PageResult<ZkDocumentRespVO> getDocumentPage(ZkDocumentPageReqVO pageReqVO) {
        PageResult<ZkDocumentDO> pageResult = documentMapper.selectPage(pageReqVO);
        PageResult<ZkDocumentRespVO> result = BeanUtils.toBean(pageResult, ZkDocumentRespVO.class);
        // 设置uploadDate字段（映射到createTime）
        result.getList().forEach(vo -> vo.setUploadDate(vo.getCreateTime()));
        return result;
    }

    @Override
    public ZkDocumentRespVO getDocument(Long id) {
        ZkDocumentDO document = documentMapper.selectById(id);
        if (document == null) {
            throw exception(DOCUMENT_NOT_EXISTS);
        }
        ZkDocumentRespVO respVO = BeanUtils.toBean(document, ZkDocumentRespVO.class);
        // 设置uploadDate字段（映射到createTime）
        respVO.setUploadDate(respVO.getCreateTime());
        return respVO;
    }

    @Override
    public void deleteDocument(Long id) {
        // 校验存在
        validateDocumentExists(id);
        // 校验权限：只有创建者可以删除
        ZkDocumentDO document = documentMapper.selectById(id);
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (!document.getAuthorId().equals(userId)) {
            throw exception(DOCUMENT_NOT_EXISTS); // 使用不存在错误，避免暴露权限信息
        }
        // 删除
        documentMapper.deleteById(id);
    }

    @Override
    public void updateDocumentStatus(Long id, String status) {
        // 校验存在
        validateDocumentExists(id);
        // 校验权限：只有创建者可以修改状态
        ZkDocumentDO document = documentMapper.selectById(id);
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (!document.getAuthorId().equals(userId)) {
            throw exception(DOCUMENT_NOT_EXISTS);
        }
        // 更新
        ZkDocumentDO updateObj = new ZkDocumentDO();
        updateObj.setId(id);
        updateObj.setStatus(status);
        documentMapper.updateById(updateObj);
    }

    @Override
    public ZkDocumentStatisticsVO getDocumentStatistics() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        // 查询所有文档（未删除）
        List<ZkDocumentDO> allDocuments = documentMapper.selectList(
                new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<ZkDocumentDO>());
        long total = allDocuments.size();

        // 查询共享文档
        long shared = allDocuments.stream()
                .filter(doc -> "shared".equals(doc.getStatus()))
                .count();

        // 查询我的文档
        long myDocuments = allDocuments.stream()
                .filter(doc -> doc.getAuthorId() != null && doc.getAuthorId().equals(userId))
                .count();

        // 查询本月新增
        long thisMonth = allDocuments.stream()
                .filter(doc -> {
                    if (doc.getCreateTime() == null) {
                        return false;
                    }
                    LocalDate createDate = doc.getCreateTime().toLocalDate();
                    return !createDate.isBefore(startDate) && !createDate.isAfter(endDate);
                })
                .count();

        return ZkDocumentStatisticsVO.builder()
                .total(total)
                .shared(shared)
                .myDocuments(myDocuments)
                .thisMonth(thisMonth)
                .build();
    }

    @Override
    public ZkDocumentWPSLinkVO getWPSEditLink(Long documentId) {
        return getWPSLink(documentId, "edit");
    }

    /**
     * 获取WPS WebOffice企业版URL（企业版正确用法）
     * 
     * WebOffice企业版必须通过URL打开：https://weboffice.wps.cn/office/?app_id=xxx&token=xxx
     * WebOffice会回调后端的download接口获取文件内容（BASE64），而不是直接使用文档URL
     */
    public ZkDocumentWPSLinkVO getWPSLink(Long documentId, String mode) {
        // 校验文档存在
        ZkDocumentDO document = documentMapper.selectById(documentId);
        if (document == null) {
            throw exception(DOCUMENT_NOT_EXISTS);
        }

        // 校验权限
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if ("edit".equals(mode)) {
            // 编辑模式：只有创建者或共享文档才能编辑
            if (!"shared".equals(document.getStatus()) && !document.getAuthorId().equals(userId)) {
                throw exception(DOCUMENT_NOT_EXISTS);
            }
        } else {
            // 查看模式：共享文档所有人都可以查看，私有文档只有创建者可以查看
            if (!"shared".equals(document.getStatus()) && !document.getAuthorId().equals(userId)) {
                throw exception(DOCUMENT_NOT_EXISTS);
            }
        }

        // 检查WPS配置是否有效（WebOffice SDK需要AppID，但不需要Token）
        if (!wpsConfig.isConfigValid()) {
            log.error("WPS配置无效，无法使用WebOffice SDK。请检查配置：enabled={}, appId={}, appSecret={}",
                    wpsConfig.getEnabled(),
                    wpsConfig.getAppId() != null && !wpsConfig.getAppId().isEmpty() ? "已配置" : "未配置",
                    wpsConfig.getAppSecret() != null && !wpsConfig.getAppSecret().isEmpty() ? "已配置" : "未配置");
            throw new RuntimeException(
                    "WPS配置无效，无法使用WebOffice SDK。请在配置文件中设置 wps.enabled=true, wps.app.id, wps.app.secret");
        }

        log.info("获取WebOffice SDK配置，文档ID: {}, 模式: {}", documentId, mode);

        // 生成WPS Token
        String token = wpsTokenUtil.generateToken(
                wpsConfig.getAppId(),
                wpsConfig.getAppSecret(),
                documentId.toString(),
                userId,
                wpsConfig.getTokenExpire());

        // 构建WPS WebOffice企业版URL
        // 格式：https://weboffice.wps.cn/office/?app_id=xxx&file_id=xxx&token=xxx
        String wpsUrl = String.format(
                "https://weboffice.wps.cn/office/?app_id=%s&file_id=%s&token=%s",
                wpsConfig.getAppId(),
                documentId.toString(),
                token);

        // 返回完整的WPS URL（用于uni-app web-view打开）
        return ZkDocumentWPSLinkVO.builder()
                .fileId(documentId.toString())
                .url(wpsUrl) // 添加完整的WPS URL
                .build();
    }

    private void validateDocumentExists(Long id) {
        if (documentMapper.selectById(id) == null) {
            throw exception(DOCUMENT_NOT_EXISTS);
        }
    }
}
