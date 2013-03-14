package tup.dota2recipe.entity;

import java.util.List;

/**
 * 物品实体
 * 
 * @author tupunco
 */
public final class ItemsItem {
    public String keyName;
    /**
     * 物品名称
     */
    public String dname;
    public String dname_l;
    /**
     * 按品质
     */
    public String qual;
    public String qual_l;
    /**
     * 按分类
     */
    public String itemcat;
    public String itemcat_l;
    /**
     * 基础分类
     */
    public String itembasecat;

    public boolean created;
    public boolean ispublic;
    
    // -------------------ItemsDetail
    public int cost;
    public String desc;
    public String attrib;
    public String mc;
    public int cd;
    public String lore;
    public String[] components;
    public List<ItemsItem> components_i;
    public String[] tocomponents;
    public List<ItemsItem> tocomponents_i;

    @Override
    public String toString() {
        return String.format("[ItemsItem key_name:%s,dname:%s,dname_l:%s,itemcat:%s]",
                keyName, dname, dname_l, itemcat_l);
    }
}
