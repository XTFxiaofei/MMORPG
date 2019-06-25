package com.game.property.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.game.backpack.bean.Goods;
import com.game.backpack.service.BackpackService;
import com.game.equipment.bean.Equipment;
import com.game.equipment.bean.EquipmentBox;
import com.game.equipment.service.EquipmentService;
import com.game.property.bean.PropertyType;
import com.game.role.bean.ConcreteRole;
import com.game.role.service.RoleService;
import com.game.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @ClassName InjectProperty
 * @Description TODO
 * @Author DELL
 * @Date 2019/6/25 10:56
 * @Version 1.0
 */
@Component
public class InjectProperty {
    @Autowired
    private RoleService roleService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private BackpackService backpackService;
    /**
     * 初始化属性总值
     * @param roleName
     */
    public void initProperty(String roleName) {
        //总值=基础值+装备值+环境值（暂无）
        Map<Integer, JSONObject> basicPropertyMap = ConcreteRole.getBasicPropertyMap();
        ConcreteRole role = roleService.getRoleByRoleName(roleName);
        //获取角色等级
        int level = role.getLevel();
        //获取json
        JSONObject json = basicPropertyMap.get(level);
        //获取Key集合
        Set<String> keys = json.keySet();
        //迭代器
        Iterator<String> iterator = keys.iterator();
        //遍历,存储基础值
        while(iterator.hasNext()){
            //获取Key
            String key = iterator.next();
            //获取枚举相应类型
            PropertyType propertyType = PropertyType.map.get(key);
            //获取value
            String value = json.getString(key);
            //存储基础数据到总值
            ConcreteRole.getTotalPropertyMap().put(propertyType,Integer.parseInt(value));
        }

        //todo:注入装备栏属性
        //获取装备栏
        EquipmentBox equipmentBox = equipmentService.getEquipmet(role.getId());
        //获取装备
        Equipment equipment = JSON.parseObject(equipmentBox.getEquipmentBox(),Equipment.class);
        //数据格式："head":"1"--->装备类型：装备id
        List<Goods> goodsList = backpackService.getGoodsByRoleId(role.getId());
        String clothes = equipment.getClothes();
        String head = equipment.getHead();
        String pants = equipment.getPants();
        String shoes = equipment.getShoes();
        String weapon = equipment.getWeapon();
        List<Goods> ownEquipmentList = new ArrayList<>();


        for (int i = 0; i < goodsList.size(); i++) {
            if(head.equals(String.valueOf(goodsList.get(i).getId()))){
                Goods goods = MapUtils.getGoodsMap().get(goodsList.get(i).getName());
                ownEquipmentList.add(goods);
                System.out.println(ownEquipmentList.size());
            }else if(clothes.equals(String.valueOf(goodsList.get(i).getId()))){
                Goods goods = MapUtils.getGoodsMap().get(goodsList.get(i).getName());
                ownEquipmentList.add(goods);
            }else if(pants.equals(String.valueOf(goodsList.get(i).getId()))){
                Goods goods = MapUtils.getGoodsMap().get(goodsList.get(i).getName());
                ownEquipmentList.add(goods);
            }else if(shoes.equals(String.valueOf(goodsList.get(i).getId()))){
                Goods goods = MapUtils.getGoodsMap().get(goodsList.get(i).getName());
                ownEquipmentList.add(goods);
            }else if(weapon.equals(String.valueOf(goodsList.get(i).getId()))){
                Goods goods = MapUtils.getGoodsMap().get(goodsList.get(i).getName());
                ownEquipmentList.add(goods);
            }
        }

        //每一件装备
        for (Goods goods : ownEquipmentList) {
            //每一件装备的每一个属性
            for (Map.Entry<PropertyType,Integer>  entry:goods.getGoodsPropertyMap().entrySet()) {
                // 拿出玩家属性，加上装备属性，放回去
                ConcreteRole.getTotalPropertyMap().put(
                        entry.getKey(),
                        ConcreteRole.getTotalPropertyMap().get(entry.getKey())+entry.getValue());
            }

        }
//        for (int i = 0; i < ownEquipmentList.size(); i++) {
//            //装备栏的装给
//            Goods goods = ownEquipmentList.get(i);
//            String mp = goods.getMp();
//            String hp = goods.getHp();
//            String defend = goods.getDefend();
//            String attack = goods.getAttack();
//            //基础属性
//            Integer totalHp = PropertyManager.getTotalPropertyMap().get(PropertyType.HP);
//            Integer totalMp = PropertyManager.getTotalPropertyMap().get(PropertyType.MP);
//            Integer totalAttack = PropertyManager.getTotalPropertyMap().get(PropertyType.ATTACK);
//            Integer totalDefend = PropertyManager.getTotalPropertyMap().get(PropertyType.DEFEND);
//            //总值=基础值+装备值
//            totalHp = totalHp + Integer.parseInt(hp);
//            totalMp = totalMp + Integer.parseInt(mp);
//            totalAttack = totalAttack + Integer.parseInt(attack);
//            totalDefend = totalDefend + Integer.parseInt(defend);
//            //存储
//            PropertyManager.getTotalPropertyMap().put(PropertyType.HP,totalHp);
//            PropertyManager.getTotalPropertyMap().put(PropertyType.MP,totalMp);
//            PropertyManager.getTotalPropertyMap().put(PropertyType.ATTACK,totalAttack);
//            PropertyManager.getTotalPropertyMap().put(PropertyType.DEFEND,totalDefend);
//        }
        //把总值复制到当前值
        Set<Map.Entry<PropertyType, Integer>> entrySet = role.getTotalPropertyMap().entrySet();
        Iterator<Map.Entry<PropertyType, Integer>> iterator1 = entrySet.iterator();
        while (iterator1.hasNext()) {
            Map.Entry<PropertyType, Integer> map = iterator1.next();
            role.getCurPropertyMap().put(map.getKey(),map.getValue());
        }

    }
}
