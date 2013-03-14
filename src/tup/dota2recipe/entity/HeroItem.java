/**
 * 
 */
package tup.dota2recipe.entity;

/**
 * 英雄实体
 * 
 * @author tupunco
 */
public class HeroItem {
    public String keyName;
    /**
     * 属性类型
     */
    public String hp;
    /**
     * 阵容
     */
    public String faction;
    /**
     * 英雄名称
     */
    public String name;
    public String name_l;
    /**
     * 攻击类型
     */
    public String atk;
    public String atk_l;
    /**
     * 角色定位
     */
    public String[] roles;
    public String[] roles_l;

    @Override
    public String toString() {
        return String.format(
                "[HeroItem keyName:%s,name:%s,name_l:%s,hp:%s,faction:%s,atk:%s,roles:%s]",
                keyName, name, name_l, hp, faction, atk_l, roles_l);
    }
}