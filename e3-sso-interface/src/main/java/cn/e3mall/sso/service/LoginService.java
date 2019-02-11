package cn.e3mall.sso.service;

import cn.e3mall.common.utils.E3Result;

/**  

* <p>Title: LoginService</p>  

* <p>Description: </p>  

* @author 赵天宇

* @date 2019年1月22日  

*/
public interface LoginService {
	//参数：用户名和密码
	//业务逻辑：
		/*
		 * 1.判断用户名和密码是否正确
		 * 2.如果不正确，返回登录失败
		 * 3.如果正确，生成token
		 * 4.把用户信息写入Redis，key:token  value:用户信息
		 * 5.设置session（key）的过期时间
		 * 6.把token返回
		 * */
	//返回值：e3result 保护token信息
	E3Result userlogin(String username,String password);
	
	
}
