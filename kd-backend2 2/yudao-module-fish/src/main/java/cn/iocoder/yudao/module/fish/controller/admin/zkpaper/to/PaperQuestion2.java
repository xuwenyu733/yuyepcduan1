package cn.iocoder.yudao.module.fish.controller.admin.zkpaper.to;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = false) // 设置 chain = false，避免用户导入有问题
public class PaperQuestion2 {

    /**
     * 试题类型
     */
    @ExcelProperty(value = "题型（必选）")
    private String typeName;

    /**
     * 题干
     */
    @ExcelProperty(value = "题干（必填）")
    private String question;

}
