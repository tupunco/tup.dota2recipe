package tup.dota2recipe.entity;

/**
 * 收藏实体
 * 
 * @author tupunco
 */
public class CollectionItem {
    public final static int KEY_TYPE_HERO = 0;
    public final static int KEY_TYPE_ITEMS = 1;
    
    public int id;
    /**
     * 收藏keyname
     */
    public String keyName;
    /**
     * 收藏类型
     * 0 Hero, 1 Items
     */
    public int type;
}
