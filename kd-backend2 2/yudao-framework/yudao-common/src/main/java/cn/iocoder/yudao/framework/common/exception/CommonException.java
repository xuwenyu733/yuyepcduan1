package cn.iocoder.yudao.framework.common.exception;

import lombok.Data;

/**
 *  通用异常
 * @创建人 lr
 * @创建时间 2023-07-26
 * @描述
 */
@Data
public class CommonException extends RuntimeException{

    private Integer code;

    public CommonException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
