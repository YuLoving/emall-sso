package cn.e3mall.sso.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import cn.e3mall.pojo.TbUserExample.Criteria;
import cn.e3mall.sso.service.RegisterService;

/**  

* <p>Title: RegisterServiceImpl</p>  

* <p>Description: 用户注册</p>  

* @author 赵天宇

* @date 2019年1月22日  

*/
public class RegisterServiceImpl implements RegisterService {
	
	private static final Logger logger=LoggerFactory.getLogger(RegisterServiceImpl.class);
	
	
	@Autowired
	private TbUserMapper userMapper;
	
	/**
	 * 注册时检查数据
	 * */
	@Override
	public E3Result checkdata(String param, int type) {
		//根据不同的type生成不同的查询条件
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		//1  用户名 、2 电话、3邮箱
		if(type==1){
			criteria.andUsernameEqualTo(param);
		}else if (type==2) {
			criteria.andPhoneEqualTo(param);
		}else if (type==3) {
			criteria.andEmailEqualTo(param);
		}else{
			E3Result.build(400, "条件数据类型有问题！");
		}
		//执行查询
		List<TbUser> list = userMapper.selectByExample(example);
		//判断结果中是否包含数据
		if(list !=null && list.size()>0){
			//如果有数据就返回false
			E3Result.ok(false);
		}
		//如果没有数据就返回true
		return E3Result.ok(true);
	}

	/*
	 * 注册成功时插入数据库
	 */
	@Override
	public E3Result register(TbUser User) {
		//对数据进行校验，防止一开始没有注册时检查数据
		if(StringUtils.isBlank(User.getPassword()) || StringUtils.isBlank(User.getPhone()) 
				||StringUtils.isBlank(User.getEmail())){
			return E3Result.build(400, "用户数据不完整，注册失败");
		}
		//1  用户名 、2 电话、3邮箱
		E3Result e3Result = checkdata(User.getUsername(), 1);
		if(!(boolean) e3Result.getData()){
			return E3Result.build(400, "用户名被占用，注册失败");
		}
		E3Result e3Result2 = checkdata(User.getPhone(), 2);
		if(!(boolean) e3Result2.getData()){
			return E3Result.build(400, "电话被占用，注册失败");
		}
		/*邮箱可以为null，非必填
		 * E3Result e3Result3 = checkdata(User.getEmail(), 3);
		if(!(boolean) e3Result3.getData()){
			return E3Result.build(400, "邮箱被占用，注册失败");
		}*/
		
		//补全pojo属性
		User.setCreated(new Date());
		User.setUpdated(new Date());
		//密码进行md5加密(spring中包含的DigestUtils)
		String md5pass = DigestUtils.md5DigestAsHex(User.getPassword().getBytes());
		logger.info("明密码："+User.getPassword()+"MD5加密后："+md5pass);
		User.setPassword(md5pass);
		//把用户插入到数据库中
		userMapper.insert(User);
		//返回添加成功
		return E3Result.ok();
	}

}
