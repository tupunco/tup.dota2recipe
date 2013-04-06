package tup.dota2recipe.entity;

import java.util.List;
import java.util.Map;

/**
 * 英雄详细实体
 * 
 * @author tupunco
 */
public final class HeroDetailItem extends HeroItem {
    public String bio;
    public String bio_l;

    /**
     * 统计信息
     */
    public String stats;
    /**
     * 统计信息-格式化后的
     */
    public List<String[]> stats1;
    /**
     * 详细统计信息
     */
    public String detailstats;
    /**
     * 详细统计信息-格式化后的-1
     * Hit Points/Mana/Damage/Armor
     * [4 array]
     */
    public List<String[]> detailstats1;
    /**
     * 详细统计信息-格式化后的-2
     * Sight Range/Attack Range/Missile Speed
     * [2 array]
     */
    public List<String[]> detailstats2;
    /**
     * 技能
     */
    public List<AbilityItem> abilities;
    /**
     * 推荐装备物品
     */
    public Map<String, String[]> itembuilds;
    public Map<String, List<ItemsItem>> itembuilds_i;

    @Override
    public String toString() {
        return String.format("[HeroDetailItem %s]", super.toString());
    }
}
