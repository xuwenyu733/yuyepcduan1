package cn.iocoder.yudao.framework.redis.core;

/**
 * redis操作Service
 * @author liurui
 *
 */
public interface RedisService {

	/**
	 * 保存字符串类型数据
	 * @param str_name 保存字符串名字
	 * @param str_value	保存字符串值
	 * @param expire_minutes	保存字符串时间，是分钟
	 * @return
	 */
	boolean insertStr(String str_name,String str_value,Long expire_minutes);
	
	/**
	 * 更新字符串类型数据
	 * @param str_name	字符串名字
	 * @param str_value	字符串值
	 * @param expire_time	字符串时间，是分钟
	 * @return
	 */
	boolean updateStr(String str_name,String str_value,Long expire_time);
	
	/**
	 * 删除字符串类型数据
	 * @param str_name	字符串名字
	 * @return
	 */
	Boolean deleteStr(String str_name);
	
	/**
	 * 查找字符串类型数据
	 * @param str_name	字符串名字
	 * @return
	 */
	String selectStr(String str_name);
}
