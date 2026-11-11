package cn.iocoder.yudao.module.fish.util;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;

import java.util.List;

/**
 * 前端对接参数兼容（experimentalId / pondId 存塘编号等）
 */
public final class FishFrontParamHelper {

    private FishFrontParamHelper() {
    }

    public static Long resolveExperimentId(Long experimentId, Long experimentalId) {
        return experimentId != null ? experimentId : experimentalId;
    }

    /**
     * 解析鱼池 ID：支持数字 ID，或存塘编号（如 001）
     */
    public static Long resolvePondId(String pondId, ZkPondService zkPondService) {
        if (StrUtil.isBlank(pondId)) {
            throw new IllegalArgumentException("pondId 不能为空");
        }
        String trimmed = pondId.trim();
        try {
            return Long.parseLong(trimmed);
        } catch (NumberFormatException ignored) {
            List<ZkPondDO> ponds = zkPondService.getByPondDetailNum(trimmed);
            if (ponds != null && !ponds.isEmpty()) {
                return ponds.get(0).getId();
            }
            throw new IllegalArgumentException("鱼池不存在: " + trimmed);
        }
    }

    public static Long resolvePondId(Long pondId, String pondIdStr, ZkPondService zkPondService) {
        if (pondId != null) {
            return pondId;
        }
        return resolvePondId(pondIdStr, zkPondService);
    }

}
