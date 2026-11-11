package cn.iocoder.yudao.module.fish.dal.mysql.zkapplog;

import java.util.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.ToolModelItem;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkapplog.ZkAppLogDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkapplog.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import cn.iocoder.yudao.module.fish.service.dictdata.vo.DictDataVo;

/**
 * app记录 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkAppLogMapper extends BaseMapperX<ZkAppLogDO> {

    default PageResult<ZkAppLogDO> selectPage(ZkAppLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkAppLogDO>()
                .eqIfPresent(ZkAppLogDO::getAppVersion, reqVO.getAppVersion())
                .eqIfPresent(ZkAppLogDO::getAppName, reqVO.getAppName())
                .eqIfPresent(ZkAppLogDO::getFileUrl, reqVO.getFileUrl())
                .eqIfPresent(ZkAppLogDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(ZkAppLogDO::getCreateTime, reqVO.getCreateTime())
                .likeIfPresent(ZkAppLogDO::getDeviceMac, reqVO.getDeviceMac())
                .orderByDesc(ZkAppLogDO::getId));
    }

    default List<ZkAppLogDO> selectList(ZkAppLogExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkAppLogDO>()
                .eqIfPresent(ZkAppLogDO::getAppVersion, reqVO.getAppVersion())
                .eqIfPresent(ZkAppLogDO::getAppName, reqVO.getAppName())
                .eqIfPresent(ZkAppLogDO::getFileUrl, reqVO.getFileUrl())
                .eqIfPresent(ZkAppLogDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(ZkAppLogDO::getCreateTime, reqVO.getCreateTime())
                .likeIfPresent(ZkAppLogDO::getDeviceMac, reqVO.getDeviceMac())
                .orderByDesc(ZkAppLogDO::getId));
    }


    @Select({
            "SELECT id, `name` AS modelName, `actual_path` AS targetPath, " +
                    " `component_name` AS targetPageName ,`judge_param_path` as judgeParamPath,`app_path` as appPath " +
                    "FROM system_menu sm ${ew.customSqlSegment}"
    })
    List<ToolModelItem> selectModels(@Param("ew") QueryWrapper queryWrapper);

    @Select({
            "SELECT * FROM system_dict_data ${ew.customSqlSegment}"
    })
    List<DictDataVo> getByDictTypes(@Param("ew") QueryWrapper queryWrapper);

}
