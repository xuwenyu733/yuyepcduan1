package cn.iocoder.yudao.module.fish.service.zkpaper;

import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpaper.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpaper.ZkPaperDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkpaper.ZkPaperConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpaper.ZkPaperMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 试卷 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkPaperServiceImpl implements ZkPaperService {

    @Resource
    private ZkPaperMapper zkPaperMapper;
    @Autowired
    private AdminUserApi adminUserApi;

    @Override
    public ZkPaperDO createZkPaper(ZkPaperCreateReqVO createReqVO) {
        // 插入
        ZkPaperDO zkPaper = BeanUtils.toBean(createReqVO, ZkPaperDO.class);
        zkPaperMapper.insert(zkPaper);
        // 返回
        return zkPaper;
    }

    @Override
    public ZkPaperDO updateZkPaper(ZkPaperUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkPaperExists(updateReqVO.getId());
        // 更新
        ZkPaperDO updateObj = BeanUtils.toBean(updateReqVO, ZkPaperDO.class);
        zkPaperMapper.updateById(updateObj);
        return updateObj;
    }

    @Override
    public void deleteZkPaper(Long id) {
        // 校验存在
        validateZkPaperExists(id);
        // 删除
        zkPaperMapper.deleteById(id);
    }

    private void validateZkPaperExists(Long id) {
        if (zkPaperMapper.selectById(id) == null) {
            throw exception(ZK_PAPER_NOT_EXISTS);
        }
    }

    @Override
    public ZkPaperDO getZkPaper(Long id) {
        return zkPaperMapper.selectById(id);
    }

    @Override
    public List<ZkPaperDO> getZkPaperList(Collection<Long> ids) {
        return zkPaperMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkPaperDO> getZkPaperPage(ZkPaperPageReqVO pageReqVO) {
        return zkPaperMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkPaperDO> getZkPaperList(ZkPaperExportReqVO exportReqVO) {
        return zkPaperMapper.selectList(exportReqVO);
    }

    @Override
    public List<ZkPaperDO> listForNeedExam(ZkPaperPageReqVO pageVO) {
        AdminUserRespDTO adminUserRespDTO = adminUserApi.getUser(SecurityFrameworkUtils.getLoginUserId());

        QueryWrapper<ZkPaperDO> queryWrapper = new QueryWrapper<>();
        Date startTime = EasyDateUtil.plusMinutes(null, 30);
        Date endTime = new Date();
        queryWrapper
//                .eq("dept_id", adminUserRespDTO.getDeptId())
                .le("start_time", startTime)
                .ge("end_time", endTime);

        List<ZkPaperDO> zkPaperDOS = zkPaperMapper.selectList(queryWrapper);

        return zkPaperDOS;
    }

}
