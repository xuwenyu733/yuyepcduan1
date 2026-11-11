package cn.iocoder.yudao.module.system.controller.admin.dept.utils;

import cn.iocoder.yudao.framework.common.exception.CommonException;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeptListParser {

    /**
     * 将部门list修改成tree
     * @param list
     */
    public static JSONArray toTree(List<DeptDO> list) {
        if(CollectionUtils.isEmpty(list))
            throw new RuntimeException("将部门list修改成tree，但是部门list为空");
        JSONArray result = JSON2Tree.listToTree(JSONArray.parseArray(JSON.toJSONString(list)),"id","parentId","children");
        return result;
    }

    /**
     * 从部门list里面找parentId所属子部门及本身
     * @param list
     * @param parentId
     * @return
     */
    public static List<DeptDO> selectSonDepts(List<DeptDO> list, Long parentId) {
        if(CollectionUtils.isEmpty(list))
            throw new CommonException(403, "【从部门list里面找parentId所属子部门及本身】部门list不能为空");
        // 1. 先转成tree
        JSONArray result = JSON2Tree.listToTree(JSONArray.parseArray(JSON.toJSONString(list)),"id","parentId","children");
        // 2. 找到当前
        JSONObject parentTree = JSON2Tree.selectParentTreeByPidValue(result, "id", "children", parentId.toString());
        if(parentTree == null)
            throw new RuntimeException("从部门list里面找parentId所属子部门及本身，parentId错误");
        // 3. 将parentTree转换成list
        List<DeptDO> res = new ArrayList<>();
        JSON2Tree.treeToList(DeptDO.class, parentTree,"children", res);
        if(!CollectionUtils.isEmpty(res))
            res.forEach(obj -> obj.setChildren(new ArrayList<>()));
        return res;
    }
    public static List<Long> selectSonDeptIdss(List<DeptDO> list, Long parentId) {
        List<Long> deptIds = selectSonDepts(list, parentId).stream().map(obj -> obj.getId()).collect(Collectors.toList());
        return deptIds;
    }

}
