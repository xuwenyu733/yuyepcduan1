/**
 * 
 */
package cn.iocoder.yudao.framework.common.util.easy;

import cn.hutool.core.lang.func.Func1;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import cn.iocoder.yudao.framework.common.util.obj.EasyKeyUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapperImpl;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author liurui
 * @date 2019年11月13日  
 */
@SuppressWarnings("rawtypes")
@Slf4j
public class EasyListUtil {
	public static Boolean isNotEmpty(List list) {
		if(list == null) return false;
		if(list.isEmpty()) return false;
		Boolean flag = false;
		for(Object item : list) {
			if(item != null) {
				flag = true;
				break;
			}
		}
		if(!flag) return false;
		return true;
	}
	public static Boolean isEmpty(List list) {
		return !isNotEmpty(list);
	}

	/**
	 * 排序，如果为空返回一个空数组，没有则返回该list对象
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> sort(List list) {
		if(isEmpty(list)) list = new ArrayList<>();
		Collections.sort(list);
		return list;
	}
	
	public static List A_AddTo_B(List currList,List targetList,Integer size) {
		for (Object object : currList) {
			targetList.add(object);
//			if(targetList.size() >= size) break;
		}
		return targetList;
	}
	
	public static List<Long> quchong(List<Long> currList) {
		Set<Long> tempSet = new HashSet<>();
		List<Long> res = new ArrayList<>();
    	for(Long ti : currList) {
    		tempSet.add(ti);
    	}
    	for(Long ll : tempSet) {
    		res.add(ll);
    	}
		return res;
	}

	/**
	 * 将list中对象属性统计为字符串，英文逗号隔开
	 * @param objects
	 * @param property
	 * @return
	 */
	public static String listPropertyToSplitStr(List<?> objects, String property) {
		if(isEmpty(objects)) return "";
		Set<String> set = new HashSet<>();
		for(Object obj : objects) {
			BeanWrapperImpl beanWrapper = new BeanWrapperImpl(obj);
			Object propertyValue = beanWrapper.getPropertyValue(property);
			if(propertyValue != null && StringUtil.isNotEmpty(propertyValue.toString())) {
				set.add(propertyValue.toString());
			}
		}
		if(set == null|| set.size() == 0) return "";
		return set.stream().collect(Collectors.joining(","));
	}

	/**
	 * list移除元素，通过元素属性和元素值
	 * @param objects list列表
	 * @param property 属性名
	 * @param value 属性值
	 * @return
	 */
	public static List<?> listRemoveByPropertyAndPropertyValue(List<?> objects, String property, Object value) {
		if(isEmpty(objects)) return null;
		Iterator<?> iterator = objects.iterator();
		while (iterator.hasNext()) {
			Object next = iterator.next();
			BeanWrapperImpl beanWrapper = new BeanWrapperImpl(next);
			Object propertyValue = beanWrapper.getPropertyValue(property);
			if(propertyValue != null && StringUtil.isNotEmpty(propertyValue.toString()) && Objects.equals(value, propertyValue)) {
				iterator.remove();
			}
		}
		return objects;
	}

	/**
	 * 判断list中是否有该值
	 * @param objects list列表
	 * @param property 属性名
	 * @param value 属性值
	 * @return
	 */
	public static synchronized Boolean judgeListIsHasValue(List<?> objects, String property, Object value) {
		if(isEmpty(objects)) return false;
		Iterator<?> iterator = objects.iterator();
		while (iterator.hasNext()) {
			Object next = iterator.next();
			BeanWrapperImpl beanWrapper = new BeanWrapperImpl(next);
			Object propertyValue = beanWrapper.getPropertyValue(property);
			if(propertyValue != null && StringUtil.isNotEmpty(propertyValue.toString()) && Objects.equals(value, propertyValue)) {
				return true;
			}
		}
		return false;
	}

	public static Object getValueByPropertyAndPropertyValue(List<?> objects, String property, Object value) {
		if(isEmpty(objects)) return false;
		Iterator<?> iterator = objects.iterator();
		while (iterator.hasNext()) {
			Object next = iterator.next();
			BeanWrapperImpl beanWrapper = new BeanWrapperImpl(next);
			Object propertyValue = beanWrapper.getPropertyValue(property);
			if(propertyValue != null && StringUtil.isNotEmpty(propertyValue.toString()) && Objects.equals(value, propertyValue)) {
				return next;
			}
		}
		return null;
	}

	public static List<?> listFillParentName(List<?> list, String parentName) {
		return null;
	}

	/**
	 * 深拷贝一个list
	 * @param tempList
	 * @return
	 */
	public static synchronized <T> List<T> copyList(List<T> tempList) {
		if(isEmpty(tempList)) return null;
		List<T> res = new ArrayList<>();
		res.addAll(tempList);
		return res;
	}

	/**
	 * 将一个数组切分多个数组
	 * @param list
	 * @param size
	 * @param <T>
	 * @return
	 */
	public static synchronized <T> List<List<T>> splitBySize(List<T> list, Integer size) {
		List<List<T>> res = new ArrayList<>();
		if(isEmpty(list))
			return res;
		// 因为不能整除，说明会有多余的数据，所以循环次数加1
		if(list.size()%size != 0) {
			for(int j=0;j<list.size()/size+1;j++) {
				if((j+1)*size<list.size()) {
					res.add(list.subList(j*size,(j+1)*size));
				} else {
					res.add(list.subList(j*size, list.size()));
				}
			}
		} else {
			for(int i=0;i<list.size()/size;i++) {
				res.add(list.subList(i*size,(i+1)*size));
			}
		}
		return res;
	}

	/**
	 * 将list转为set
	 * @param list
	 * @param <T>
	 * @return
	 */
	public static synchronized <T> Set<T> toSet(List<T> list) {
		if(isEmpty(list)) return null;
		Set<T> set = new HashSet<>();
		for(T t : list) {
			set.add(t);
		}
		return set;
	}

	/**
	 * 多个list融合
	 * @param objs
	 * @param <T>
	 * @return
	 */
	@SafeVarargs
    public static synchronized <T> List<T> merge(List<T>...objs) {
		List<T> res = new ArrayList<>();
		for(List<T> list : objs) {
			if(isEmpty(list)) continue;
            res.addAll(list);
		}
		return res;
	}


	public static synchronized List<Integer> idsToList(String targetStr, String split) {
		if(targetStr == null || targetStr.trim().length() == 0) return null;
		String[] strArr = targetStr.split(split);
		List<Integer> list = new ArrayList<>();
		for(String str : strArr) {
			list.add(Integer.parseInt(str));
		}
		return list;
	}

	/**
	 * 获取 list里面重复的
	 * @param oList 列表
	 * @param k1 比较值
	 * @param k2 返回值
	 * @return 重复的 关键值
	 * @param <T> 列表泛型
	 * @param <K_1> 比较类型
	 * @param <K_2> 返回类型
	 */
	public static <T, K_1, K_2> List<K_2> getRepeat(List<T> oList, EasyGetter<T, K_1> k1, EasyGetter<T, K_2> k2) {
		ArrayList<K_2> res = new ArrayList<>();
		try {
			if(EasyListUtil.isEmpty(oList)) return res;
			Set<K_1> set = new HashSet<>();
			for(T t : oList) {
				if(!set.add(k1.get(t))) res.add(k2.get(t));
			}
			return res;
		} catch (Exception e) {
			String sub = String.format("【EasyListUtil 获取 list里面重复的 异常】k1：%s,k2:%s", k1, k2);
			String msg = String.format("对应对象：%s, error: %s", JSON.toJSONString(oList), ExceptionUtil.getStackTrace(e));
			EmailUtil.send(sub, msg);
			log.error(sub, msg);
		}
		return res;
	}

	/**
	 * 通过 列表对象获取 对象的ids
	 * @param list 列表对象
	 * @param easyGetter 属性Lambda表达式
	 * @return 单独获取属性 set
	 * @param <T> 对象泛型
	 * @param <R> 返回类型
	 */
	public static <T, R> Set<R> listToIds(List<T> list, EasyGetter<T, R> easyGetter) {
		Set<R> set = new HashSet<>();
		if(isEmpty(list)) return set;
		for(T t : list) {
			if(t == null) continue;
			set.add(easyGetter.get(t));
		}
		return set;
	}
	public static <T, R> List<R> listToIds3(List<T> list, EasyGetter<T, R> easyGetter) {
		List<R> set = new ArrayList<>();
		if(isEmpty(list)) return set;
		for(T t : list) {
			if(t == null) continue;
			set.add(easyGetter.get(t));
		}
		return set;
	}

	public static <T, TV> List<BiConsumer<T, TV>> setList(BiConsumer<T, TV>...sets) {
        return new ArrayList<>(Arrays.asList(sets));
	}
	public static <T, TV> List<Func1<T, TV>> getList(Func1<T, TV>...sets) {
		return new ArrayList<>(Arrays.asList(sets));
	}

	public static <T, TK, TV> Map<TK, Map<String, Object>> one2manyToMap(List<T> list, Func1<T, TK> getK, List<Func1<T, TV>> getVs, List<String> vKeys) {
		Map<TK, List<T>> temp = new HashMap<>();
		Map<TK, Map<String, Object>> res = new HashMap<>();
		try {
			if(isEmpty(list)) return res;
			// 1. 分组
			for(T t : list) {
				if(t == null) continue;
				TK key = getK.call(t);
				List<T> ts = temp.get(key);
				if(isEmpty(ts)) ts = new ArrayList<>();
				ts.add(t);
				temp.put(key, ts);
			}
			// 2. 每组汇总
			for(TK tk : temp.keySet()) {
				List<T> ts = temp.get(tk);
				Map<String, Object> map = new HashMap<>();
				// 3. 统计 每个分组的value join值
				for(int i=0;i<vKeys.size();i++) {
					String vKey = vKeys.get(i);
					Func1<T, TV> vGet = getVs.get(i);

					List<TV> tempList = new ArrayList<>();
					for(T t : ts) {
						TV call = vGet.call(t);
						if(call == null) continue;
						tempList.add(call);
					}
					map.put(vKey, tempList.stream().map(Object::toString).collect(Collectors.joining(",")));
				}
				map.put(EasyKeyUtil.getPropertyName(getK), tk);

				res.put(tk, map);
			}
		} catch (Exception e) {
			String sub = String.format("【EasyListUtil通过property获取value的join异常】getK：%s,vKeys:%s", getK, JSON.toJSONString(vKeys));
			String msg = String.format("对应对象：%s, error: %s", JSON.toJSONString(list), ExceptionUtil.getStackTrace(e));
			EmailUtil.send(sub, msg);
			log.error(sub, msg);
		}
		return res;
	}


	/**
	 * list分组，组内的属性汇总值到map
	 * @param list 列表
	 * @param getK 分组值
	 * @param getVs 获取哪些值需要汇总
	 * @param vKeys 汇总的值的名字
	 * @return 返回 分组id，汇总值 的map
	 * @param <T> 列表泛型
	 * @param <TK> 分组值泛型
	 * @param <TV> 属性值的泛型
	 */
	public static <T, TK, TV> List<Map<String, Object>> one2manyToList(List<T> list, Func1<T, TK> getK, List<Func1<T, TV>> getVs, List<String> vKeys) {
		Map<TK, List<T>> temp = new HashMap<>();
		List<Map<String, Object>> res = new ArrayList<>();
		try {
			if(isEmpty(list)) return res;
			// 1. 分组
			for(T t : list) {
				if(t == null) continue;
				TK key = getK.call(t);
				List<T> ts = temp.get(key);
				if(isEmpty(ts)) ts = new ArrayList<>();
				ts.add(t);
				temp.put(key, ts);
			}
			// 2. 每组汇总
			for(TK tk : temp.keySet()) {
				List<T> ts = temp.get(tk);
				Map<String, Object> map = new HashMap<>();
				// 3. 统计 每个分组的value join值
				for(int i=0;i<vKeys.size();i++) {
					String vKey = vKeys.get(i);
					Func1<T, TV> vGet = getVs.get(i);

					List<TV> tempList = new ArrayList<>();
					for(T t : ts) {
						TV call = vGet.call(t);
						if(call == null) continue;
						tempList.add(call);
					}
					map.put(vKey, tempList.stream().map(Object::toString).collect(Collectors.joining(",")));
				}
				map.put(EasyKeyUtil.getPropertyName(getK), tk);

				res.add(map);
			}
		} catch (Exception e) {
			String sub = String.format("【EasyListUtil通过property获取value的join异常】getK：%s,vKeys:%s", getK, JSON.toJSONString(vKeys));
			String msg = String.format("对应对象：%s, error: %s", JSON.toJSONString(list), ExceptionUtil.getStackTrace(e));
			EmailUtil.send(sub, msg);
			log.error(sub, msg);
		}
		return res;
	}

	/**
	 * 列表分组 保留 分组后的 列表
	 * @param list 列表
	 * @param getK 分组key
	 * @param listKey 分组列表key
	 * @return map 分组id属性名，分组id属性值；分组列表属性名，分组列表属性值
	 * @param <T> 列表泛型
	 * @param <TK> 分组key
	 * @param <TV> 分组列表key
	 */
	public static <T, TK, TV> List<Map<String, Object>> one2manyToList3(List<T> list, Func1<T, TK> getK, String listKey) {
		Map<TK, List<T>> temp = new HashMap<>();
		List<Map<String, Object>> res = new ArrayList<>();
		try {
			if(isEmpty(list)) return res;
			// 1. 分组
			for(T t : list) {
				if(t == null) continue;
				TK key = getK.call(t);
				List<T> ts = temp.get(key);
				if(isEmpty(ts)) ts = new ArrayList<>();
				ts.add(t);
				temp.put(key, ts);
			}

			// 2. 每组汇总
			for(TK tk : temp.keySet()) {
				List<T> ts = temp.get(tk);
				Map<String, Object> map = new HashMap<>();
				map.put(EasyKeyUtil.getPropertyName(getK), tk);
				map.put(listKey, ts);

				res.add(map);
			}
		} catch (Exception e) {
			String sub = String.format("【EasyListUtil通过property获取value的join异常】getK：%s", getK);
			String msg = String.format("对应对象：%s, error: %s", JSON.toJSONString(list), ExceptionUtil.getStackTrace(e));
			EmailUtil.send(sub, msg);
			log.error(sub, msg);
		}
		return res;
	}

	/**
	 * 获取list的size
	 * @param list
	 * @param <T>
	 * @return
	 */
	public static <T> Integer size(List<T> list) {
		if(isEmpty(list)) return 0;
		return list.size();
	}
}
