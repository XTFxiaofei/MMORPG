package com.game.backpack.bean;

/**
 * @ClassName Goods
 * @Description 商品实体类
 * @Author DELL
 * @Date 2019/6/17 15:10
 * @Version 1.0
 */
public class Goods {
    /**
     * 商品唯一id
     */
    private Integer id;
    /**
     * 商品名字
     */
    private String name;

    /**
     * 角色id
     */
    private Integer roleId;
    /**
     * 物品类型id，0：普通物品；非0：装备
     */
    private Integer type;
    /**
     * 物品描述
     */
    private String description;
    /**
     * 物品数量
     */
    private Integer count;
    /**
     * 耐久度
     */
    private Integer durability;
    /**
     *物品hp
     */
    private String hp;
    /**
     *物品mp
     */
    private String mp;
    /**
     *物品attack
     */
    private String attack;
    /**
     *物品defend
     */
    private String defend;
    /**
     * 物品最大数量
     */
    public static final int GOODS_MAXCOUNT = 99;
    /**
     * 角色拥有物品最大数量
     */
    public static final int ROLE_MAXGOODS = 10;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDurability() {
        return durability;
    }

    public void setDurability(Integer durability) {
        this.durability = durability;
    }

    public String getHp() {
        return hp;
    }

    public void setHp(String hp) {
        this.hp = hp;
    }

    public String getMp() {
        return mp;
    }

    public void setMp(String mp) {
        this.mp = mp;
    }

    public String getAttack() {
        return attack;
    }

    public void setAttack(String attack) {
        this.attack = attack;
    }

    public String getDefend() {
        return defend;
    }

    public void setDefend(String defend) {
        this.defend = defend;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
