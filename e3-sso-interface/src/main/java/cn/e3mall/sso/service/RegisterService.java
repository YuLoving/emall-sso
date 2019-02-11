package cn.e3mall.sso.service;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbUser;

/**  

* <p>Title: RegisterService</p>  

* <p>Description: 用户注册</p>  

* @author 赵天宇

* @date 2019年1月22日  

*/
public interface RegisterService {
	//注册时检查数据
	E3Result checkdata(String param,int type);
	//注册成功时插入数据库
	E3Result register(TbUser User );
	
}
