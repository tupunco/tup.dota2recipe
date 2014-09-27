package tup.dota2recipe.entity;

/**
 * 英雄技能实体
 * 
 * @author tupunco
 */
public final class AbilityItem {
    public String keyName;
    public String dname;
    public String affects;
    public String attrib;
    public String desc;
    public String dmg;
    public String cmb;
    public String notes;
    public String lore;
    public String hurl;

    @Override
    public String toString() {
        return String.format("[AbilityItem keyName:%s,dname:%s]",
                keyName, dname);
    }
}
