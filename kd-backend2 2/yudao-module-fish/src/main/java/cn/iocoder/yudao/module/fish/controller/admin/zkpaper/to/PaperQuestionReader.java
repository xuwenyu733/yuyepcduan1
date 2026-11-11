package cn.iocoder.yudao.module.fish.controller.admin.zkpaper.to;

import cn.idev.excel.EasyExcel;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpaperquestion.ZkPaperQuestionDO;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * @创建人 lr
 * @创建时间 2023-08-23
 * @描述
 */
public class PaperQuestionReader {

    public static List<ZkPaperQuestionDO> reade(String fileName) {
        // String fileName = "E:\\Use\\caogao\\test\\上传测试文件.xlsx";
        // 这里默认读取第一个sheet
        List<PaperQuestion> list = EasyExcel.read(fileName).head(PaperQuestion.class).sheet()
                .headRowNumber(2).doReadSync();

        List<ZkPaperQuestionDO> targetList = new ArrayList<>();
        for(PaperQuestion paperQuestion : list) {
            // System.out.println("=================="+ JSON.toJSONString(paperQuestion));
            targetList.add(paperQuestion.changer());
        }
        // targetList.forEach(obj -> System.out.println(JSON.toJSONString(obj)));
        return targetList;
    }


    public static String getPathByFid(String fid) {
        return "E:\\Work\\ke_de_work\\4.素材\\上传测试文件.xlsx";
    }

    public static void main(String[] args) {
        String str = "E:\\Use\\caogao\\test\\上传测试文件.xlsx";
        List<ZkPaperQuestionDO> reade = reade(str);

        for(ZkPaperQuestionDO item : reade) {
            System.out.println(JSON.toJSONString(item));
        }
    }

}
