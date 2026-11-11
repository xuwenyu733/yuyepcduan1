package cn.iocoder.yudao.module.fish.datapermission.config;

import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import cn.iocoder.yudao.module.fish.dal.dataobject.fishsystemlog.FishSystemLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkaskforleave.ZkAskforLeaveDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkbiologicalasset.ZkBiologicalAssetDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkbiologycapital.ZkBiologyCapitalDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatlog.ChatLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdatarevise.ZkDataReviseDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdayrecord.ZkDayRecordDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdeathsurvivalrate.ZkDeathSurvivalRateDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevice.ZkDeviceDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicealarm.ZkDeviceAlarmDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdeviceevent.ZkDeviceEventDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicemetric.ZkDeviceMetricDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdeviceproperties.ZkDevicePropertiesDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicerecord.ZkDeviceRecordDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimental.ZkExperimentalDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentaldata.ZkExperimentalDataDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalmedia.ZkExperimentalMediaDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpool.ZkExperimentalPoolDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfcrmodel.ZkFcrModelDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfeedinglog.ZkFeedingLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishhealth.ZkFishHealthDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishoperationlog.ZkFishOperationLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishproofing.ZkFishProofingDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowbaitingredients.ZkGrowBaitIngredientsDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowdailyrecord.ZkGrowDailyRecordDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowfarmingbatch.ZkGrowFarmingBatchDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowfishspecies.ZkGrowFishSpeciesDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowmodelconfig.ZkGrowModelConfigDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrownutritionalrequirements.ZkGrowNutritionalRequirementsDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowthmodel.ZkGrowthModelDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkillcontrol.ZkIllControlDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkinspectionreport.ZkInspectionReportDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkintelligentdiagnosis.ZkIntelligentDiagnosisDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkissue.ZkIssueDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkkonwledgetree.ZkKonwledgeTreeDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmanualinspection.ZkManualInspectionDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmeeting.ZkMeetingDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmeetingrecord.ZkMeetingRecordDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmeetinguser.ZkMeetingUserDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zknetbox.ZkNetBoxDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpaper.ZkPaperDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpaperanswer.ZkPaperAnswerDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpaperquestion.ZkPaperQuestionDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishparent.ZkPondFishParentDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondhealth.ZkPondHealthDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondsystem.ZkPondSystemDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondwarn.ZkPondWarnDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkprecisionfeeding.ZkPrecisionFeedingDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkproductcost.ZkProductCostDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyarchive.PurifyArchiveDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifycostdetail.PurifyCostDetailDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifydailymonitoring.PurifyDailyMonitoringDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifydictdata.PurifyDictDataDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyoutpool.PurifyOutPoolDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifypool.PurifyPoolDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyprocess.PurifyProcessDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifypurchase.PurifyPurchaseDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifytreatment.PurifyTreatmentDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkscatteredinventory.ZkScatteredInventoryDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkscheduling.ZkSchedulingDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkschedulingtype.ZkSchedulingTypeDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkschedulinguser.ZkSchedulingUserDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkspecslog.ZkSpecsLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktask.ZkTaskDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail.TaskDetailDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkthresholdrule.ZkThresholdRuleDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktrainingknowledge.TrainingKnowledgeDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkusemedicine.ZkUseMedicineDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkusemedicinelog.ZkUseMedicineLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkworkplan.ZkWorkPlanDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkworkshop.ZkWorkshopDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * fish 模块的数据权限 Configuration
 *
 * @author 东东
 */
@Configuration(proxyBeanMethods = false)
public class FishDataPermissionConfiguration {

    @Bean
    public DeptDataPermissionRuleCustomizer fishDeptDataPermissionRuleCustomizer() {
        return rule -> {
            // dept
            // rule.addDeptColumn(ZkAppLogDO.class);
            rule.addDeptColumn(ZkAskforLeaveDO.class);
            rule.addDeptColumn(ZkBiologicalAssetDO.class);
            rule.addDeptColumn(ZkBiologyCapitalDO.class);
            rule.addDeptColumn(ChatLogDO.class);
            rule.addDeptColumn(ZkDataReviseDO.class);
            // rule.addDeptColumn(ZkDayRecordDO.class);
            rule.addDeptColumn(ZkDeathSurvivalRateDO.class);
            rule.addDeptColumn(ZkDeviceDO.class);
            // rule.addDeptColumn(ZkDeviceAlarmDO.class);
            rule.addDeptColumn(ZkDeviceEventDO.class);
            rule.addDeptColumn(ZkPondHealthDO.class);
            // rule.addDeptColumn(DeviceManagementDO.class);
            rule.addDeptColumn(ZkDeviceMetricDO.class);
            rule.addDeptColumn(ZkDevicePropertiesDO.class);
            // rule.addDeptColumn(ZkDeviceRecordDO.class);
            rule.addDeptColumn(ZkFcrModelDO.class);
            rule.addDeptColumn(ZkFeedingLogDO.class);
            rule.addDeptColumn(ZkFishHealthDO.class);
            rule.addDeptColumn(ZkFishOperationLogDO.class);
            rule.addDeptColumn(ZkFishProofingDO.class);
            rule.addDeptColumn(FishSystemLogDO.class);
            rule.addDeptColumn(ZkGrowthModelDO.class);
            rule.addDeptColumn(ZkIllControlDO.class);
            rule.addDeptColumn(ZkInspectionReportDO.class);
            rule.addDeptColumn(ZkIntelligentDiagnosisDO.class);
            rule.addDeptColumn(ZkIssueDO.class);
            // rule.addDeptColumn(ZkKonwledgeTreeDO.class);
            rule.addDeptColumn(ZkManualInspectionDO.class);
            // rule.addDeptColumn(ZkMeetingDO.class);
            // rule.addDeptColumn(ZkMeetingRecordDO.class);
            // rule.addDeptColumn(ZkMeetingUserDO.class);
            rule.addDeptColumn(ZkNetBoxDO.class);
            // rule.addDeptColumn(OperatingCostDO.class);
            rule.addDeptColumn(ZkPaperDO.class);
            rule.addDeptColumn(ZkPaperAnswerDO.class);
            rule.addDeptColumn(ZkPaperQuestionDO.class);
            rule.addDeptColumn(ZkPondDO.class);
            rule.addDeptColumn(ZkPondFishDO.class);
            rule.addDeptColumn(ZkPondFishDetailDO.class);
            rule.addDeptColumn(ZkPondFishParentDO.class);
            rule.addDeptColumn(ZkPondSystemDO.class);
            rule.addDeptColumn(ZkPondWarnDO.class);
            rule.addDeptColumn(ZkPrecisionFeedingDO.class);
            rule.addDeptColumn(ZkProductCostDO.class);
            rule.addDeptColumn(ZkSchedulingDO.class);
            // rule.addDeptColumn(ZkSchedulingTypeDO.class);
            rule.addDeptColumn(ZkSchedulingUserDO.class);
            rule.addDeptColumn(ZkSpecsLogDO.class);
            rule.addDeptColumn(ZkTaskDO.class);
            rule.addDeptColumn(TaskDetailDO.class);
            rule.addDeptColumn(ZkThresholdRuleDO.class);
            rule.addDeptColumn(TrainingKnowledgeDO.class);
            rule.addDeptColumn(ZkUseMedicineDO.class);
            rule.addDeptColumn(ZkUseMedicineLogDO.class);
            rule.addDeptColumn(ZkWorkPlanDO.class);
            rule.addDeptColumn(ZkWorkshopDO.class);
            // 实验模块
            rule.addDeptColumn(ZkExperimentalDO.class);
            rule.addDeptColumn(ZkExperimentalDataDO.class);
            rule.addDeptColumn(ZkExperimentalMediaDO.class);
            rule.addDeptColumn(ZkExperimentalPoolDO.class);
            // 生长模型模块
            // rule.addDeptColumn(ZkGrowBaitIngredientsDO.class);
            // rule.addDeptColumn(ZkGrowDailyRecordDO.class);
            // rule.addDeptColumn(ZkGrowFarmingBatchDO.class);
            // rule.addDeptColumn(ZkGrowFishSpeciesDO.class);
            // rule.addDeptColumn(ZkGrowModelConfigDO.class);
            // rule.addDeptColumn(ZkGrowNutritionalRequirementsDO.class);

            // 风味优化模块
            rule.addDeptColumn(PurifyPurchaseDO.class);
            rule.addDeptColumn(PurifyPoolDO.class);
            rule.addDeptColumn(PurifyArchiveDO.class);
            rule.addDeptColumn(PurifyProcessDO.class);
            rule.addDeptColumn(PurifyDailyMonitoringDO.class);
            rule.addDeptColumn(PurifyTreatmentDO.class);
            rule.addDeptColumn(PurifyOutPoolDO.class);
            rule.addDeptColumn(PurifyDictDataDO.class);
            rule.addDeptColumn(PurifyCostDetailDO.class);
            // 零星库存
            rule.addDeptColumn(ZkScatteredInventoryDO.class);

        };
    }

}
