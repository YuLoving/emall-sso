package cn.e3mall.sso.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Service;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.TokenService;

/**  

* <p>Title: TokenServiceImpl</p>  

* <p>Description: 根据token来查询用户信息</p>  

* @author 赵天宇

* @date 2019年1月23日  

*/
@Service
public class TokenServiceImpl implements TokenService {
	
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${SESSION_EXPIRE}")
	private Integer SESSION_EXPIRE;

	@Override
	public E3Result getuserbytoken(String token) {
		// 根据token去Redis中用户信息
		String json = jedisClient.get("SESSION:"+token);
		// 如果没有数据，则用户登录过期，返回登录过期
		if(StringUtils.isBlank(json)){
			E3Result.build(201, "用户登录已经过期");
		}
		// 如果有数据，则更新token的过期时间
		jedisClient.expire("SESSION:"+token,SESSION_EXPIRE );
		//返回结果，result中包含TbUser对象(json转成对象)
		return E3Result.ok(JsonUtils.jsonToPojo(json, TbUser.class));
	}

}
