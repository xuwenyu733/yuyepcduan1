package cn.iocoder.yudao.module.fish.enums;

/**
 * ERP 操作日志枚举
 * 目的：统一管理，也减少 Service 里各种“复杂”字符串
 *
 * @author 芋道源码
 */
public interface LogRecordConstants {

    // ======================= FISH MODULE =======================

    String GEN_FISH_OPERATION_LOG = "生成zk_fish_operation_log";
    String DELETE_POND_FISH_DETAIL = "删除鱼苗操作明细zk_pond_fish_detail";

}
