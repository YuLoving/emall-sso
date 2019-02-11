package cn.e3mall.sso.service.impl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import cn.e3mall.pojo.TbUserExample.Criteria;
import cn.e3mall.sso.service.LoginService;

/**  

* <p>Title: LoginServiceImpl</p>  

* <p>Description:用户登录处理 </p>  

* @author 赵天宇

* @date 2019年1月22日  

*/
public class LoginServiceImpl implements LoginService {
	private static final Logger logger=LoggerFactory.getLogger(LoginServiceImpl.class);
	
	@Autowired
	private TbUserMapper userMapper;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${SESSION_EXPIRE}")
	private Integer SESSION_EXPIRE;
	
	
	@Override
	public E3Result userlogin(String username, String password) {
		//1.判断用户名和密码是否正确
				/*
				 * 根据用户名查询数据库中是否有该记录，
				 * 如果有的话，再匹配密码是否正确，如果没有则登录失败
				 * */
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		//执行查询
		List<TbUser> list = userMapper.selectByExample(example);
		if(list ==null ||list.size()==0){
			logger.info("=========用户名错误=======");
			return E3Result.build(400, "用户名或密码错误，登录失败");
		}
		//取用户信息
		TbUser user = list.get(0);
		//判断密码是否正确，因为数据库中密码是md5加密的，
		//所以先将用户输入的密码先md5加密再和数据库中对比
		String newmd5Dpass = DigestUtils.md5DigestAsHex(password.getBytes());
		if(!user.getPassword().equals(newmd5Dpass))
		{	
			logger.info("=========密码错误=======");
			return E3Result.build(400, "用户名或密码错误，登录失败");
		}
		
		//3.如果正确，生成token  
		String token = UUID.randomUUID().toString();
				//哪怕是把用户信息放入Redis中，为了安全不应该携带密码过去
		user.setPassword(null);
		//4.把用户信息写入Redis，key:token  value:用户信息
		jedisClient.set("SESSION:"+token, JsonUtils.objectToJson(user));
		//5.设置session（key）的过期时间
		jedisClient.expire("SESSION:"+token, SESSION_EXPIRE);
		//6.把token返回
		return E3Result.ok(token);
	}

}
