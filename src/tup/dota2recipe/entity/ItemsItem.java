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
     * 合成目标keyName(合成卷轴使用)
     */
    public String parent_keyName;
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
    /**
     * 是否是公开(物品列表UI内显示)物品
     */
    public boolean ispublic;
    /**
     * 是否当前物品是合成卷轴
     */
    public boolean isrecipe;

    // -------------------ItemsDetail
    public int cost;
    public String desc;
    public String attrib;
    public String mc;
    public int cd;
    public String lore;
    /**
     * 合成所需物品
     */
    public String[] components;
    public List<ItemsItem> components_i;
    /**
     * 可合成物品
     */
    public String[] tocomponents;
    public List<ItemsItem> tocomponents_i;

    /**
     * 是否已经收藏
     * -1 未加载, 0 否, 1 是
     */
    public int hasCollection = -1;

    @Override
    public String toString() {
        return String.format("[ItemsItem key_name:%s,dname:%s,dname_l:%s,itemcat:%s]",
                keyName, dname, dname_l, itemcat_l);
    }
}
