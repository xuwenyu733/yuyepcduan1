package cn.iocoder.yudao.server.redis;

import cn.iocoder.yudao.framework.redis.core.RedisService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * redis操作Service
 * @author liurui
 *
 */
@Service
@Slf4j
public class RedisServiceImpl implements RedisService {
	
	@Resource(name = "stringRedisTemplate")
	private RedisTemplate<String, String> redisTemplate;
	
	/**
	 * 保存字符串类型数据
	 * @param str_name 保存字符串名字
	 * @param str_value	保存字符串值
	 * @param expire_minutes	保存字符串时间，是分钟
	 * @return
	 */
	public boolean insertStr(String str_name,String str_value,Long expire_minutes){
		try {
			log.info("redisService保存redis字符串str_name:{}---str_value:{}--expire_time:{}",
					str_name,str_value,expire_minutes);
			redisTemplate.opsForValue().set(str_name, str_value, expire_minutes, TimeUnit.MINUTES);
			return true;
		} catch (Exception e) {
			log.error("redisService保存系统异常:",e.getLocalizedMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 更新字符串类型数据
	 * @param str_name	字符串名字
	 * @param str_value	字符串值
	 * @param expire_time	字符串时间，是分钟
	 * @return
	 */
	public boolean updateStr(String str_name,String str_value,Long expire_time){
		try {
			log.info("redisService更新redis字符串str_name:{}---str_value:{}--expire_time:{}",
					str_name,str_value,expire_time);
			//如果存在直接删除
			Boolean hasKey = redisTemplate.hasKey(str_name);
			if(hasKey){
				redisTemplate.delete(str_name);
			}
			//重新保存
			redisTemplate.opsForValue().set(str_name, str_value, expire_time, TimeUnit.MINUTES);
			return true;
		} catch (Exception e) {
			log.error("redisService更新系统异常:",e.getLocalizedMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 删除字符串类型数据
	 * @param str_name	字符串名字
	 * @return
	 */
	public Boolean deleteStr(String str_name){
		try {
			log.info("redisService删除redis字符串str_name:{}",str_name);
			//如果存在就获取
			Boolean hasKey = redisTemplate.hasKey(str_name);
			if(hasKey){
				redisTemplate.delete(str_name);
			}
			return true;
		} catch (Exception e) {
			log.error("redisService删除系统异常:",e.getLocalizedMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 查找字符串类型数据
	 * @param str_name	字符串名字
	 * @return
	 */
	public String selectStr(String str_name){
		try {
			log.info("redisService保存redis字符串str_name:{}",str_name);
			//如果存在就获取
			Boolean hasKey = redisTemplate.hasKey(str_name);
			Object str = null;
			if(hasKey){
				str = redisTemplate.opsForValue().get(str_name);
				log.info("redisService查找redis字符串str_name:{}--str_value：{}",
						str_name,str);
			}
			return str!=null?str.toString():"";
		} catch (Exception e) {
			log.error("redisService查询系统异常:",e.getLocalizedMessage());
			e.printStackTrace();
			return null;
		}
	}
}
