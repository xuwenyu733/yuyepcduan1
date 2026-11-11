package cn.iocoder.yudao.module.system.controller.admin.dept.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @创建人 lr
 * @创建时间 2023-07-20
 * @描述
 */
public class JSON2Tree {

    /**
     - listToTree
     - <p>方法说明<p>
     - 将JSONArray数组转为树状结构
     - @param arr 需要转化的数据
     - @param id 数据唯一的标识键值
     - @param pid 父id唯一标识键值
     - @param child 子节点键值
     - @return JSONArray
     */
    public static JSONArray listToTree(JSONArray arr, String id, String pid, String child){
        JSONArray r = new JSONArray();
        JSONObject hash = new JSONObject();
        //将数组转为Object的形式，key为数组中的id
        for(int i=0;i<arr.size();i++){
            JSONObject json = (JSONObject) arr.get(i);
            hash.put(json.getString(id), json);
        }
        //遍历结果集
        for(int j=0;j<arr.size();j++){
            //单条记录
            JSONObject aVal = (JSONObject) arr.get(j);
            //在hash中取出key为单条记录中pid的值
            Object pidObj = aVal.get(pid);
            if(pidObj == null) {
                r.add(aVal);
            } else {
                JSONObject hashVP = (JSONObject) hash.get(pidObj.toString());
                //如果记录的pid存在，则说明它有父节点，将她添加到孩子节点的集合中
                if(hashVP != null){
                    //检查是否有child属性
                    if(hashVP.get(child) != null){
                        JSONArray ch = (JSONArray) hashVP.get(child);
                        ch.add(aVal);
                        hashVP.put(child, ch);
                    }else {
                        JSONArray ch = new JSONArray();
                        ch.add(aVal);
                        hashVP.put(child, ch);
                    }
                }else{
                    r.add(aVal);
                }
            }
        }
        return r;
    }

    public static JSONObject selectParentTreeByPidValue(JSONArray arr, String id, String child, String pidValue) {
        if(arr == null || arr.size() == 0) return null;
        for(int i=0;i<arr.size();i++) {
            // 单条记录
            JSONObject aVal = (JSONObject) arr.get(i);
            // 获取当前
            String idLocalValue = aVal.get(id).toString();
            if(idLocalValue.equals(pidValue)) {
                return aVal;
            }
            // 获取子
            JSONArray aValArr = (JSONArray) aVal.get(child);
            JSONObject res = selectParentTreeByPidValue(aValArr, id, child, pidValue);
            if(res != null) return res;
        }
        return null;
    }


    public static <T> void treeToList(Class<T> tClass, JSONObject jsonObject, String child, List<T> res) {
        if(jsonObject == null) return;
        // 1. 先加当前对象
        res.add(JSON.parseObject(jsonObject.toJSONString(), tClass));
        JSONArray aValArr = (JSONArray) jsonObject.get(child);
        if(aValArr != null && aValArr.size() > 0) {
            for(int i = 0; i < aValArr.size(); i++) {
                JSONObject aVal = (JSONObject) aValArr.get(i);
                treeToList(tClass, aVal, child, res);
            }
        }
    }

}
