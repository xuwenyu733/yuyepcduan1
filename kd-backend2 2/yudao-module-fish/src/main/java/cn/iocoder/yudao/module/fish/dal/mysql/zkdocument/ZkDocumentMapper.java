package cn.iocoder.yudao.module.fish.dal.mysql.zkdocument;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdocument.ZkDocumentDO;
import cn.iocoder.yudao.module.fish.controller.admin.zkdocument.vo.ZkDocumentPageReqVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文档 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkDocumentMapper extends BaseMapperX<ZkDocumentDO> {

    default PageResult<ZkDocumentDO> selectPage(ZkDocumentPageReqVO reqVO) {
        LambdaQueryWrapperX<ZkDocumentDO> queryWrapper = new LambdaQueryWrapperX<ZkDocumentDO>();

        // 关键词搜索：标题或作者（OR关系）
        if (reqVO.getKeyword() != null && !reqVO.getKeyword().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like(ZkDocumentDO::getTitle, reqVO.getKeyword())
                    .or()
                    .like(ZkDocumentDO::getAuthor, reqVO.getKeyword()));
        }

        // 其他筛选条件
        queryWrapper.eqIfPresent(ZkDocumentDO::getType, reqVO.getType())
                .eqIfPresent(ZkDocumentDO::getAuthorId, reqVO.getAuthorId())
                .orderByDesc(ZkDocumentDO::getCreateTime)
                .orderByDesc(ZkDocumentDO::getId);

        return selectPage(reqVO, queryWrapper);
    }
}
