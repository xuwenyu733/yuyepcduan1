package cn.iocoder.yudao.module.fish.controller.admin.zkpaper.to;

import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpaperquestion.ZkPaperQuestionDO;
import cn.idev.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = false) // 设置 chain = false，避免用户导入有问题
public class PaperQuestion {

    /**
     * 试题类型
     */
    @ExcelProperty("题型（必选）")
    private String typeName;

    /**
     * 题干
     */
    @ExcelProperty("题干（必填）")
    private String question;

    /**
     * 答案
     */
    @ExcelProperty("答案（必填）")
    private String answer;

    /**
     * 解析
     */
    @ExcelProperty("答案解析（选填）")
    private String analysis;

    /**
     * 标签
     */
    @ExcelProperty("标签（仅导入题库时有效，每个标签最多8个字）")
    private String tag;

    /**
     * 分数
     */
    @ExcelProperty("分数（导入试卷时必填）")
    private Double score;

    /**
     * 选项
     */
    @ExcelProperty("选项A")
    private String optionsA;

    @ExcelProperty("选项B")
    private String optionsB;

    @ExcelProperty("选项C")
    private String optionsC;

    @ExcelProperty("选项D")
    private String optionsD;


    public ZkPaperQuestionDO changer() {
        ZkPaperQuestionDO serPaperQuestion = new ZkPaperQuestionDO();
        CommonUtil.copyPropertiesIgnoreNull(this, serPaperQuestion);

        // 试题类型 1单选题 2多选题 3判断题
        if(Objects.equals(this.getTypeName(), "单选题")) {
            serPaperQuestion.setType("1");
        } else if(Objects.equals(this.getTypeName(), "多选题")) {
            serPaperQuestion.setType("2");
        } else if(Objects.equals(this.getTypeName(), "判断题")) {
            serPaperQuestion.setType("3");
        }

        // 封装options
        HashMap<String, String> map = new HashMap<>();
        map.put("A", this.getOptionsA());
        map.put("B", this.getOptionsB());
        if(StringUtils.isNotBlank(this.getOptionsC())) map.put("C", this.getOptionsC());
        if(StringUtils.isNotBlank(this.getOptionsD())) map.put("D", this.getOptionsD());

        serPaperQuestion.setOptions(JSON.toJSONString(map));

        return serPaperQuestion;
    }

}
