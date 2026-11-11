package cn.iocoder.yudao.module.fish.service.zkpurifydictdata;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifydictdata.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifydictdata.PurifyDictDataDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkpurifydictdata.PurifyDictDataMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 业务字典明细 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class PurifyDictDataServiceImpl implements PurifyDictDataService {

    @Resource
    private PurifyDictDataMapper purifyDictDataMapper;

    @Override
    public Long createPurifyDictData(PurifyDictDataSaveReqVO createReqVO) {
        // 插入
        PurifyDictDataDO purifyDictData = BeanUtils.toBean(createReqVO, PurifyDictDataDO.class);
        purifyDictDataMapper.insert(purifyDictData);

        // 返回
        return purifyDictData.getId();
    }

    @Override
    public void updatePurifyDictData(PurifyDictDataSaveReqVO updateReqVO) {
        // 校验存在
        validatePurifyDictDataExists(updateReqVO.getId());
        // 更新
        PurifyDictDataDO updateObj = BeanUtils.toBean(updateReqVO, PurifyDictDataDO.class);
        purifyDictDataMapper.updateById(updateObj);
    }

    @Override
    public void deletePurifyDictData(Long id) {
        // 校验存在
        validatePurifyDictDataExists(id);
        // 删除
        purifyDictDataMapper.deleteById(id);
    }

    @Override
        public void deletePurifyDictDataListByIds(List<Long> ids) {
        // 删除
        purifyDictDataMapper.deleteByIds(ids);
        }


    private void validatePurifyDictDataExists(Long id) {
        if (purifyDictDataMapper.selectById(id) == null) {
            throw exception(PURIFY_DICT_DATA_NOT_EXISTS);
        }
    }

    @Override
    public PurifyDictDataDO getPurifyDictData(Long id) {
        return purifyDictDataMapper.selectById(id);
    }

    @Override
    public PageResult<PurifyDictDataDO> getPurifyDictDataPage(PurifyDictDataPageReqVO pageReqVO) {
        return purifyDictDataMapper.selectPage(pageReqVO);
    }

}