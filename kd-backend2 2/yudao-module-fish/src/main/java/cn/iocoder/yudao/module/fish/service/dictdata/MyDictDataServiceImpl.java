package cn.iocoder.yudao.module.fish.service.dictdata;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.dal.mysql.zkapplog.ZkAppLogMapper;
import cn.iocoder.yudao.module.fish.service.dictdata.vo.DictDataVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.List;

/**
 * @创建人 lr
 * @创建时间 2025-04-17
 * @描述
 */
@Service
@Validated
public class MyDictDataServiceImpl implements DictDataService{
    @Autowired
    private ZkAppLogMapper zkAppLogMapper;
//    private DictDataMapper dictDataMapper;

    @Override
    public List<DictDataVo> getByDictTypes(List<String> dictTypes) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.in("dict_type", dictTypes);
        queryWrapper.eq("deleted", 0);
        return zkAppLogMapper.getByDictTypes(queryWrapper);
    }

    @Override
    public String getLabel(List<DictDataVo> list, String value) {
        if(EasyListUtil.isEmpty(list)) return "";
        for(DictDataVo item : list) {
            if(StringUtils.equals(value, item.getValue())) return item.getLabel();
        }
        return "";
    }

    @Override
    public String getValue(List<DictDataVo> list, String label) {
        if(EasyListUtil.isEmpty(list)) return "";
        for(DictDataVo item : list) {
            if(StringUtils.equals(label, item.getLabel())) return item.getValue();
        }
        return "";
    }
}
