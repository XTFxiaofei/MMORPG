package com.game.user.controller;

import com.game.buff.controller.BuffController;
import com.game.dispatcher.RequestAnnotation;
import com.game.property.manager.InjectProperty;
import com.game.role.bean.ConcreteRole;
import com.game.role.service.RoleService;
import com.game.task.manager.InjectTaskData;
import com.game.user.manager.SessionMap;
import com.game.user.service.Login;
import com.game.user.service.RegisterService;
import com.game.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 用户controller
 */
@RequestAnnotation("/user")
@Component
public class UserController {
	@Autowired
	private Login login;

	@Autowired
	private RegisterService registerService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private BuffController buffController;

	@Autowired
	private InjectProperty injectProperty;
	@Autowired
	private InjectTaskData injectTaskData;
	/**
	 * 用户登录
	 * @param username
	 * @param password
	 * @return
	 */
	@RequestAnnotation("/login")
	public String login(String username, String password) {
		boolean isSuccess = login.login(username,password);
		ConcreteRole role = this.getRoleAfterLoginSuccess(username);
		if(isSuccess){
			//加用户名-角色对象
			MapUtils.getMapUsername_Role().put(username,role);
			buffController.executeBuff(role.getName());
			injectProperty.initProperty(role.getName());
			//校验账号

			checkAccount(username,role);
			return role.getName()+"上线了";
		}else{
			return role.getName()+"登录失败";
		}
	}

	/**
	 * 校验账号
	 * @param username
	 * @param role1
	 */
	private void checkAccount(String username, ConcreteRole role1) {
		ConcreteRole role = MapUtils.getMapRolename_Role().get(role1.getName());
		Map<String, ConcreteRole> sessionMap = SessionMap.getSessionMap();
		ConcreteRole localRole = sessionMap.get(username);
		if(localRole==null){
			sessionMap.put(username,role);
		}else{
			localRole.getCtx().channel().writeAndFlush("账号重复登录");
			logout(username);
		}

	}

	/**
	 * 用户登出
	 * @param username
	 * @return
	 */
	@RequestAnnotation("/logout")
	public void logout(String username) {
		//通过username找到map中的role
		ConcreteRole role = MapUtils.getMapUsername_Role().get(username);
		//移除角色信息，下线
		MapUtils.getMapUsername_Role().remove(username);
		 role.getCtx().channel().writeAndFlush(role.getName()+"下线了");
	}

	/**
	 * 打印当前场景的所有角色信息
	 * @param mapname
	 * @return
	 */
	@RequestAnnotation("/getRoleInfo")
	public String getRoleInfo(String mapname) {
		//获取当前场景的所有角色信息
		Map<String,ConcreteRole> map = MapUtils.getMapUsername_Role();
		return returnRoleInfo(map,mapname);
	}

	/**
	 * 返回用户角色信息
	 * @param map
	 * @param mapname
	 * @return
	 */
	private String returnRoleInfo(Map<String, ConcreteRole> map,String mapname) {
		Set<String> sets = map.keySet();
		StringBuffer sb = new StringBuffer();
		Iterator<String> iterator = sets.iterator();
		while(iterator.hasNext()){
			String name = iterator.next();
			String map_name = map.get(name).getConcreteMap().getName();
			if (map_name.equals(mapname)){
				sb.append(map.get(name).getName()+","+map.get(name).getConcreteMap().getName()+","+map.get(name).getCurHp());
			}
			if(iterator.hasNext()){
				sb.append(";");
			}
		}
		return sb.toString();
	}

	/**
	 * 注册
	 * @param username
	 * @param password
	 * @param ackpassword
	 * @return
	 */
	@RequestAnnotation("/registerService")
	public String registerService(String username,String password,String ackpassword){
		boolean isSuccess = registerService.register(username,password,ackpassword);
		if(isSuccess){
			return returnMsg(username);

		}else{
			return "注册失败";
		}
	}

	private String returnMsg(String username) {
		String msg = "成功注册用户"+username+",请继续注册游戏角色"+";\n"
				+"职业描述:\n"
				+"1、战士:高物理攻击和高防御，可以使用嘲讽技能，成为BOSS优先攻击的目标\n"
				+"2、牧师:带有治疗的职业，强力的治疗技能需要有吟唱时间\n"
				+"3、法师:群体法术伤害，可以攻击复数单位\n"
				+"4、召唤师:能召唤召唤兽协助战斗。宝宝也能释放技能，而且玩家切换场景的时候自动跟随，玩家收到攻击或主动攻击，宝宝也会释放技能。\n";
		return msg;
	}

	/**
	 * 成功登录后，获取用户对应人物角色
	 * @param username
	 * @return
	 */
	public ConcreteRole getRoleAfterLoginSuccess(String username){
		int id = login.getUserRoleIdByUsername(username);
		//查找role
		ConcreteRole role = roleService.getRole(id);
		//返回role
		return role;
	}
}