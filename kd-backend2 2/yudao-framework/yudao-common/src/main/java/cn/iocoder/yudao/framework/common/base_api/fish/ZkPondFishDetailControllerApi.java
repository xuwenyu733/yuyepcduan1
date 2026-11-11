package cn.iocoder.yudao.framework.common.base_api.fish;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

public interface ZkPondFishDetailControllerApi {

    CommonResult<Long> createZkPondFishDetail(String param);

    CommonResult<Boolean> updateZkPondFishDetail(String param);

    CommonResult<Boolean> deleteZkPondFishDetail(Long id);

}
