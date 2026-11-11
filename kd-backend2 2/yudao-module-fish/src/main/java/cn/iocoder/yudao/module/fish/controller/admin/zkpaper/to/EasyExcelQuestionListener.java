package cn.iocoder.yudao.module.fish.controller.admin.zkpaper.to;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class EasyExcelQuestionListener extends AnalysisEventListener<PaperQuestion2> {

    private List<PaperQuestion2> dataList;

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {

        // 需要处理表头的可以在这里处理
        for (Integer key : headMap.keySet()) {
            System.out.print(key + ":" + headMap.get(key) + "\t");
        }
        System.out.println();
    }

    @Override
    public void invoke(PaperQuestion2 data, AnalysisContext context) {
        dataList.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }

}
