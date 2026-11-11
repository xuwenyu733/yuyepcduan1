package cn.iocoder.yudao.framework.common.base_api.fish;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

public interface ZkScatteredInventoryControllerApi {

    CommonResult<Long> createZkScatteredInventory(Map<String, Object> param);

    CommonResult<Boolean> deleteZkScatteredInventoryByBusId(Long busId);
}
