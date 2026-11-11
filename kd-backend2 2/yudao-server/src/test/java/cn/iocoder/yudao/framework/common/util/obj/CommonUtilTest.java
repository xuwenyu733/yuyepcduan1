package cn.iocoder.yudao.framework.common.util.obj;

import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import com.alibaba.fastjson.JSON;

public class CommonUtilTest {

    public static void main(String[] args) {
        DeptDO deptDO = new DeptDO();
        CommonUtil.setValByProperty(deptDO, "name", "dkdkdk");
        System.out.println(JSON.toJSONString(deptDO));
        System.out.println(CommonUtil.getValByProperty(deptDO, "name", String.class));
    }

}
