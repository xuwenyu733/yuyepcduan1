
package cn.iocoder.yudao.module.workflow.api.dto;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.module.workflow.api.enums.WorkflowContants;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Data
public class TaskParam {
    private String processName;
    private String processKey;//流程标识
    private String approveNo;//审批编号
    private String taskId;//任务id
    private String fileId;//任务id
    private String businessKey;//业务主键
    private String processInstanceId;//流程实例id
    private String approveUserId;//审批用户id
    private String approveMessage;//审批意见
    private String approveResult;//审批结果
    private String submitToPrev = "n";//y按顺序提交,y提交至退回节点 //退回后的流程再次提交直接到上一次的退回人那里
    private String finishProcess = "n";//n流程主动结束
    private String sameToComplete = "n";//处理下一个节点，如果下一个节点任务的审批人和岗位对应人跟前面的审批人一样，则自动提交任务
//    private AdminUserRespDTO user;
    private Map<String, Object> variables = new HashMap();//流程参数

    public TaskParam() {

    }

//    public Map<String, Object> getBaseParam(AdminUserRespDTO user) {
//        if (StringUtils.isBlank(this.businessKey)) {
//            throw exception(new ErrorCode(1123123123, "流程主键不能为空!"));
//        }
//        Map<String, Object> variables = this.getVariables();//组装task参数
//        variables.put("businessKey", this.businessKey);//流程主键
//        variables.put("approveNo", this.approveNo);//审批编号
//        variables.put("startUserId", String.valueOf(user.getId()));//发起人Id
//        variables.put("startUserName", user.getNickname());//发起人姓名
//        //variables.put("approveResult", this.approveResult);//true流程主动结束
////        variables.put("finishProcess", this.finishProcess);//true流程主动结束
////        variables.put("submitToPrev", this.submitToPrev);//是否按顺序提交
//        variables.put("taskState", WorkflowContants.ProcessResult.AGREE.getValue());//流程正常流转状态
//        return variables;
//    }

}
