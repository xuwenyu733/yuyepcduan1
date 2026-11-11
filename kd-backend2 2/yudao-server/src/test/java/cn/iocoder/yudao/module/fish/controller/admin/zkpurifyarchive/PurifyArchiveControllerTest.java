package cn.iocoder.yudao.module.fish.controller.admin.zkpurifyarchive;

import cn.iocoder.yudao.module.fish.controller.admin.zkpurifyarchive.vo.PurifyArchiveSaveReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifycostdetail.vo.PurifyCostDetailSaveReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifydailymonitoring.vo.PurifyDailyMonitoringSaveReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifyoutpool.vo.PurifyOutPoolSaveReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifypool.vo.PurifyPoolSaveReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifypurchase.vo.PurifyPurchaseSaveReqVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyprocess.PurifyProcessDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpurifyprocess.PurifyProcessMapper;
import cn.iocoder.yudao.module.fish.service.zkpurifyarchive.PurifyArchiveService;
import cn.iocoder.yudao.module.fish.service.zkpurifycostdetail.PurifyCostDetailService;
import cn.iocoder.yudao.module.fish.service.zkpurifydailymonitoring.PurifyDailyMonitoringService;
import cn.iocoder.yudao.module.fish.service.zkpurifyoutpool.PurifyOutPoolService;
import cn.iocoder.yudao.module.fish.service.zkpurifypool.PurifyPoolService;
import cn.iocoder.yudao.module.fish.service.zkpurifypurchase.PurifyPurchaseService;
import cn.iocoder.yudao.server.YudaoServerApplication;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = YudaoServerApplication.class)
@RunWith(SpringRunner.class)
public class PurifyArchiveControllerTest {

    @Resource
    private PurifyPurchaseService purifyPurchaseService;
    @Resource
    private PurifyPoolService purifyPoolService;
    @Resource
    private PurifyArchiveService purifyArchiveService;
    @Resource
    private PurifyProcessMapper purifyProcessMapper;
    @Resource
    private PurifyDailyMonitoringService purifyDailyMonitoringService;
    @Resource
    private PurifyOutPoolService purifyOutPoolService;
    @Resource
    private PurifyCostDetailService purifyCostDetailService;

    private Long tenantId = 1L;
    private Long deptId = 103L;

    @Test
    public void initData() {
        // 1. 创建鱼池
        createPool("P001", "100", "A区");
        createPool("P002", "120", "A区");
        createPool("P003", "80", "B区");

        // 2. 批次1：鲈鱼（已完成）
        Long purchaseId1 = createPurchase("PUR2023100101", "鲈鱼", "杭州", new BigDecimal("1000"), new BigDecimal("20"));
        Long archiveId1 = createArchive(purchaseId1, "PUR2023100101", "鲈鱼", "杭州", "P001",
                new BigDecimal("500"), 1000, LocalDateTime.now().minusDays(10));
        Long processId1 = getProcessId(archiveId1);
        // 监测：死亡 10尾, 5kg
        createMonitoring(processId1, LocalDateTime.now().minusDays(8), 10, new BigDecimal("5"));
        createMonitoring(processId1, LocalDateTime.now().minusDays(5), 5, new BigDecimal("2.5"));
        // 出池：全部出完
        createOutPool(processId1, LocalDateTime.now().minusDays(1), new BigDecimal("480"), 985, new BigDecimal("25")); // 赚了
        // 成本
        createCost(processId1, "electricity", new BigDecimal("200"));
        createCost(processId1, "feed", new BigDecimal("500"));

        // 3. 批次2：鮰鱼（进行中）
        Long purchaseId2 = createPurchase("PUR2023100501", "鮰鱼", "湖州", new BigDecimal("2000"), new BigDecimal("15"));
        Long archiveId2 = createArchive(purchaseId2, "PUR2023100501", "鮰鱼", "湖州", "P002",
                new BigDecimal("800"), 1000, LocalDateTime.now().minusDays(5));
        Long processId2 = getProcessId(archiveId2);
        // 监测
        createMonitoring(processId2, LocalDateTime.now().minusDays(3), 2, new BigDecimal("1.6"));
        createCost(processId2, "labor", new BigDecimal("300"));

        // 4. 批次3：大黄鱼（刚入池）
        Long purchaseId3 = createPurchase("PUR2023101001", "大黄鱼", "宁波", new BigDecimal("500"), new BigDecimal("40"));
        Long archiveId3 = createArchive(purchaseId3, "PUR2023101001", "大黄鱼", "宁波", "P003",
                new BigDecimal("200"), 400, LocalDateTime.now().minusDays(1));
    }

    private void createPool(String number, String size, String location) {
        PurifyPoolSaveReqVO req = new PurifyPoolSaveReqVO();
        req.setPoolNumber(number);
        req.setSize(new BigDecimal(size));
        req.setLocation(location);
        req.setManager("张三");
        req.setStatus("available");
        req.setDeptId(deptId);
        purifyPoolService.createPurifyPool(req);
    }

    private Long createPurchase(String no, String category, String source, BigDecimal qty, BigDecimal price) {
        PurifyPurchaseSaveReqVO req = new PurifyPurchaseSaveReqVO();
        req.setPurchaseOrderNo(no);
        req.setCategory(category);
        req.setSource(source);
        req.setQuantity(qty);
        req.setUnitPrice(price);
        req.setTotalAmount(qty.multiply(price));
        req.setPurchaseDate(LocalDateTime.now().minusDays(15));
        req.setDeptId(deptId);
        return purifyPurchaseService.createPurifyPurchase(req);
    }

    private Long createArchive(Long purchaseId, String purchaseNo, String category, String source,
                               String poolNo, BigDecimal weight, Integer count, LocalDateTime inTime) {
        PurifyArchiveSaveReqVO req = new PurifyArchiveSaveReqVO();
        req.setPurchaseRecordId(purchaseId);
        req.setPurchaseOrderNo(purchaseNo);
        req.setCategory(category);
        req.setSource(source);
        req.setPoolNumber(poolNo);
        req.setInPoolWeight(weight);
        req.setInPoolCount(count);
        req.setInPoolTime(inTime);
        req.setAvgWeight(weight.divide(new BigDecimal(count), 3, BigDecimal.ROUND_HALF_UP));
        req.setDeptId(deptId);
        req.setAssessor("李四");
        return purifyArchiveService.createPurifyArchive(req);
    }

    private Long getProcessId(Long archiveId) {
        List<PurifyProcessDO> list = purifyProcessMapper.selectList(
                new LambdaQueryWrapper<PurifyProcessDO>().eq(PurifyProcessDO::getArchiveId, archiveId));
        return list.isEmpty() ? null : list.get(0).getId();
    }

    private void createMonitoring(Long processId, LocalDateTime date, Integer deathCount, BigDecimal deathWeight) {
        PurifyDailyMonitoringSaveReqVO req = new PurifyDailyMonitoringSaveReqVO();
        req.setProcessId(processId);
        req.setMonitorDate(date);
        req.setDeathCount(deathCount);
        req.setDeathWeight(deathWeight);
        req.setFeedAmount(new BigDecimal("10"));
        req.setOxygen(new BigDecimal("6.5"));
        req.setPh(new BigDecimal("7.2"));
        req.setTemperature(new BigDecimal("20"));
        req.setHealthStatus("正常");
        req.setFeedTime(date);
        req.setDeptId(deptId);
        purifyDailyMonitoringService.createPurifyDailyMonitoring(req);
    }

    private void createOutPool(Long processId, LocalDateTime date, BigDecimal weight, Integer count, BigDecimal price) {
        PurifyOutPoolSaveReqVO req = new PurifyOutPoolSaveReqVO();
        req.setProcessId(processId);
        req.setOutPoolTime(date);
        req.setOutPoolWeight(weight);
        req.setOutPoolCount(count);
        req.setSalesPrice(price);
        req.setTotalAmount(weight.multiply(price));
        req.setCustomerName("盒马鲜生");
        req.setDeptId(deptId);
        req.setSpecification("精品");
        purifyOutPoolService.createPurifyOutPool(req);
    }

    private void createCost(Long processId, String type, BigDecimal amount) {
        PurifyCostDetailSaveReqVO req = new PurifyCostDetailSaveReqVO();
        req.setProcessId(processId);
        req.setCostType(type);
        req.setAmount(amount);
        req.setOccurrenceDate(LocalDateTime.now());
        req.setDeptId(deptId);
        purifyCostDetailService.createPurifyCostDetail(req);
    }
}
