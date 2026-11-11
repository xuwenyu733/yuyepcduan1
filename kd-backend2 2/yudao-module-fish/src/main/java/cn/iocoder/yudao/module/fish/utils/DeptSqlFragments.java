package cn.iocoder.yudao.module.fish.utils;

/**
 * MyBatis {@code <script>} 部门 IN 片段（编译期常量，可拼进 @Select）。
 * 仅当入参 deptIds 非空时生效。
 * <ul>
 *   <li>null：平台超管全站，不加过滤</li>
 *   <li>非空列表：按 IN 过滤（无部门普通用户为 {@code [-1]}，结果为空）</li>
 * </ul>
 */
public final class DeptSqlFragments {

    private DeptSqlFragments() {
    }

    public static final String AND_DEPT_ID_IN =
            "<if test='deptIds != null and deptIds.size() &gt; 0'> AND dept_id IN "
                    + "<foreach collection='deptIds' item='did' open='(' separator=',' close=')'>#{did}</foreach>"
                    + "</if>";

    /** 仅历史未标注部门的水质记录（配合控制器：本部门无标注数据时回退） */
    public static final String AND_DEPT_ID_IS_NULL = " AND dept_id IS NULL ";

    public static final String AND_ZP_DEPT_ID_IN =
            "<if test='deptIds != null and deptIds.size() &gt; 0'> AND zp.dept_id IN "
                    + "<foreach collection='deptIds' item='did' open='(' separator=',' close=')'>#{did}</foreach>"
                    + "</if>";

    public static final String AND_A_DEPT_ID_IN =
            "<if test='deptIds != null and deptIds.size() &gt; 0'> AND a.dept_id IN "
                    + "<foreach collection='deptIds' item='did' open='(' separator=',' close=')'>#{did}</foreach>"
                    + "</if>";

    public static final String AND_ZDR_DEPT_ID_IN =
            "<if test='deptIds != null and deptIds.size() &gt; 0'> AND zdr.dept_id IN "
                    + "<foreach collection='deptIds' item='did' open='(' separator=',' close=')'>#{did}</foreach>"
                    + "</if>";

    /**
     * 水质记录按「鱼池部门」隔离：record → device → pond，过滤 zp.dept_id；
     * 同时兼容已标注在 zdr.dept_id 上的历史数据（OR）。
     * 不再只用 zk_device_record.dept_id（常为空或与鱼池部门不一致，导致有池无数据）。
     */
    public static final String AND_WATER_RECORD_DEPT_SCOPE =
            "<if test='deptIds != null and deptIds.size() &gt; 0'> AND ("
                    + "zp.dept_id IN "
                    + "<foreach collection='deptIds' item='did' open='(' separator=',' close=')'>#{did}</foreach>"
                    + " OR zdr.dept_id IN "
                    + "<foreach collection='deptIds' item='did' open='(' separator=',' close=')'>#{did}</foreach>"
                    + ")</if>";

    /** record→device→pond 联表（部门过滤时使用） */
    public static final String JOIN_WATER_RECORD_POND =
            " LEFT JOIN zk_device zd ON zd.device_sn = zdr.device_id "
                    + "AND (zd.device_metric_id = zdr.metric_id OR zd.device_metric_id IS NULL OR zd.device_metric_id = '') "
                    + "AND zd.deleted = 0 "
                    + "LEFT JOIN zk_pond zp ON zp.id = zd.pond_id AND zp.deleted = 0 ";
}
