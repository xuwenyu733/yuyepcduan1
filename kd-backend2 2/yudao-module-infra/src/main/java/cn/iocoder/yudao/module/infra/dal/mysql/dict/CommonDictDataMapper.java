package cn.iocoder.yudao.module.infra.dal.mysql.dict;

import cn.iocoder.yudao.module.infra.dal.dataobject.dict.CommonDictDataDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommonDictDataMapper {

    @Select({
            "SELECT * FROM system_dict_data WHERE dict_type = #{dict_type}"
    })
    List<CommonDictDataDO> getByDictType(@Param("dict_type") String dict_type);

}
