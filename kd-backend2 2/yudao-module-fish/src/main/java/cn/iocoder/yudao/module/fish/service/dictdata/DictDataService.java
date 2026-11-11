package cn.iocoder.yudao.module.fish.service.dictdata;

import cn.iocoder.yudao.module.fish.service.dictdata.vo.DictDataVo;

import java.util.List;

/**
 * @创建人 lr
 * @创建时间 2025-04-17
 * @描述
 */
public interface DictDataService {

    List<DictDataVo> getByDictTypes(List<String> dictTypes);

    String getLabel(List<DictDataVo> list, String value);
    String getValue(List<DictDataVo> list, String label);
}
