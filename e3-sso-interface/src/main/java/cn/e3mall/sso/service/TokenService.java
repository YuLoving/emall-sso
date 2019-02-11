package cn.e3mall.sso.service;

import cn.e3mall.common.utils.E3Result;

/**  

* <p>Title: TokenService</p>  

* <p>Description:根据token来查询用户信息 </p>  

* @author 赵天宇

* @date 2019年1月23日  

*/
public interface TokenService {
	E3Result getuserbytoken(String token);

}
