package com.game.role.controller;

import com.game.backpack.bean.Goods;
import com.game.backpack.service.BackpackService;
import com.game.dispatcher.RequestAnnotation;
import com.game.property.bean.Property;
import com.game.property.manager.PropertyManager;
import com.game.role.bean.ConcreteRole;
import com.game.role.service.RoleService;
import com.game.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * @ClassName RoleController
 * @Description 角色控制器
 * @Author DELL
 * @Date 2019/5/2917:58
 * @Version 1.0
 */
@RequestAnnotation("/role")
@Controller
public class RoleController {
    @Autowired
    private RoleService roleService;

    @Autowired
    private BackpackService backpackService;
    /**
     * 进入世界
     */
    @RequestAnnotation("/enterMap")
    public void enterMap(){
        System.out.println("enterMap");
    }

    /**
     * 获取角色role所在地图
     * @param roleName
     * @return
     */
    @RequestAnnotation("/getMap")
    public String getMap(String roleName){
        //根据roleName查找map_id
        int map_id = roleService.getMapIdByRoleName(roleName);
        //根据map_id查找地图
        String map_name = roleService.getMapNameByMapId(map_id);
        //返回值
        return roleName+"目前在"+map_name;
    }

    /**
     * 角色受到伤害
     * @param roleName
     * @return
     */
    @RequestAnnotation("/roleByHurted")
    public String roleByHurted(String roleName){
        ConcreteRole concreteRole = MapUtils.getMapRolename_Role().get(roleName);
        if(concreteRole==null){
            return roleName+"还没登录，请先登录";
        }
        if(concreteRole.getCurHp()>0){
            if(concreteRole.getCurHp()-10>0){
                concreteRole.setHp();
                return roleName+"受到伤害，生命值减10";
            }else{
                concreteRole.setHp();
                return roleName+"受到伤害，生命值减为0";
            }
        }else{
            return "已死亡";
        }
    }

    /**
     * 角色加血
     * @param roleName
     * @return
     */
    @RequestAnnotation("/roleByAddBlood")
    public String roleByAddBlood(String roleName){
        ConcreteRole concreteRole = MapUtils.getMapRolename_Role().get(roleName);
        if(concreteRole==null){
            return roleName+"还没登录，请先登录";
        }
        if(concreteRole.getCurHp()>=100){
            return "已满血";
        }else{
            handleHp(concreteRole);
            return roleName+"的血量已加10Hp";
        }
    }

    private void handleHp(ConcreteRole concreteRole) {
        if(concreteRole.getCurHp()+10>=100){
            Property property = PropertyManager.getMap().get(concreteRole.getLevel());
            property.setHp(property.getHp());
            concreteRole.setHp();
        }else{
            concreteRole.setHp();
        }

    }

    /**
     * 通过rolename获取角色状态信息
     * @param roleName
     * @return
     */
    @RequestAnnotation("/getRoleState")
    public String getRoleState(String roleName){
        ConcreteRole role = MapUtils.getMapRolename_Role().get(roleName);
        if(role==null){
            return roleName+"还没登录，请先登录";
        }
        Property property = PropertyManager.getMap().get(role.getLevel());
        return roleName+"血量："+property.getHp()+"\t魔法值："+property.getMp()+"\t攻击力："+property.getAttack()+"\t防御力："+property.getDefend();
    }
    @RequestAnnotation("/roleUseGoods")
    public String roleUseGoods(String roleName,String goodsName){
        //获取角色
        ConcreteRole role = MapUtils.getMapRolename_Role().get(roleName);
        //获取角色的物品列表
        List<Goods> goodsList = backpackService.getGoodsByRoleId(role.getId());
        //获取角色的具体物品
        Goods goods = getGoods(goodsList,goodsName);
        //判断物品是否存在
       boolean isExisted = checkGoodsIsExist(goods);
       //判断物品的功能、使用物品
        String msg = checkAndUseGoodsFunction(role,goods,isExisted);
        //返回信息
        return msg;

    }

    /**
     * 判断物品的功能、使用物品
     * @param role
     * @param goods
     * @param isExisted
     * @return
     */
    private String checkAndUseGoodsFunction(ConcreteRole role,Goods goods,boolean isExisted) {
        if(isExisted){
            //检查物品的类型
            Integer type = goods.getType();
            if(type>1){
                return "无法使用该物品,该物品既不是血包，也不是蓝包";
            }
            Goods localGoods = MapUtils.getGoodsMap().get(goods.getName());
            //检查物品是血包，还是蓝包（0：血包；1：蓝包）
            if(localGoods.getType()==0){
                //检查角色的血是否已满
                boolean isFull = checkFullBoold(role);
                if(isFull){
                    return "血已满，无需使用"+localGoods.getName();
                }
                double pro = Double.parseDouble(localGoods.getHp());

                Property property = PropertyManager.getMap().get(role.getLevel());
                property.setHp(role.getCurHp()+(int)(role.getCurHp()*pro));
                role.setHp();

                System.out.println("加血后："+role.getCurHp());
                MapUtils.getMapRolename_Role().put(role.getName(),role);
            }else{
                boolean isFull = checkFullMagic(role);
                if(isFull){
                    return "蓝已满，无需使用"+localGoods.getName();
                }
                double pro = Double.parseDouble(localGoods.getHp());

                Property property = PropertyManager.getMap().get(role.getLevel());
                property.setMp(role.getCurMp()+(int)(role.getCurMp()*pro));
                role.setMp();

                System.out.println("加蓝后："+role.getCurMp());
                MapUtils.getMapRolename_Role().put(role.getName(),role);
            }
            //更新物品数据库信息
            backpackService.updateGoodsByRoleIdDel(role.getId(),goods.getId());
            return "成功使用"+goods.getName();
        }else{
            return "物品不存在";
        }
    }

    private boolean checkFullMagic(ConcreteRole role) {
        return role.getCurMp()>=100?true:false;
    }

    private boolean checkFullBoold(ConcreteRole role) {
        return role.getCurHp()>=100?true:false;
    }

    /**
     * 检查物品是否存在
     * @param goods
     * @return
     */
    private boolean checkGoodsIsExist(Goods goods) {
        if(goods==null||goods.getCount()<=0){
            return false;
        }
        return true;
    }

    /**
     * 获取物品
     * @param goodsList
     * @param goodsName
     * @return
     */
    private Goods getGoods(List<Goods> goodsList, String goodsName) {
        Goods goods = null;
        for (int i = 0; i < goodsList.size(); i++) {
            if(goodsList.get(i).getName().equals(goodsName)){
                goods = goodsList.get(i);
            }
        }
        return goods;
    }
}
