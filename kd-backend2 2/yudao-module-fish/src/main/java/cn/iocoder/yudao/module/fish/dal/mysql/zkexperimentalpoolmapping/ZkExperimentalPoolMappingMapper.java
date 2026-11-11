package cn.iocoder.yudao.module.fish.dal.mysql.zkexperimentalpoolmapping;

import java.util.*;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpoolmapping.ZkExperimentalPoolMappingDO;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 * 实验与鱼池关联 Mapper
 *
 * @author admin2
 */
@Mapper
public interface ZkExperimentalPoolMappingMapper extends BaseMapperX<ZkExperimentalPoolMappingDO> {

    default List<ZkExperimentalPoolMappingDO> selectListByExperimentalId(Long experimentalId) {
        return selectList(new LambdaQueryWrapper<ZkExperimentalPoolMappingDO>()
                .eq(ZkExperimentalPoolMappingDO::getExperimentalId, experimentalId));
    }

    default void deleteByExperimentalId(Long experimentalId) {
        delete(new LambdaQueryWrapper<ZkExperimentalPoolMappingDO>()
                .eq(ZkExperimentalPoolMappingDO::getExperimentalId, experimentalId));
    }
}