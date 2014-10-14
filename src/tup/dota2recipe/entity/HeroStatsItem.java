/**
 * 
 */
package tup.dota2recipe.entity;

import java.util.Locale;

/**
 * 英雄统计参数实体
 * 
 * @author tupunco
 */
public final class HeroStatsItem {
    /**
     * 初始力量
     */
    public double init_str;
    /**
     * 初始敏捷
     */
    public double init_agi;
    /**
     * 初始智力
     */
    public double init_int;

    /**
     * 力量成长
     */
    public double lv_str;
    /**
     * 敏捷成长
     */
    public double lv_agi;
    /**
     * 智力成长
     */
    public double lv_int;

    /**
     * 初始血量
     */
    public double init_hp;
    /**
     * 初始魔法
     */
    public double init_mp;
    /**
     * 初始护甲
     */
    public double init_armor;

    /**
     * 血量成长
     */
    public double lv_hp;
    /**
     * 魔法成长
     */
    public double lv_mp;
    /**
     * 护甲成长
     */
    public double lv_armor;

    /**
     * 初始最小攻击力
     */
    public double init_min_dmg;
    /**
     * 初始最大攻击力
     */
    public double init_max_dmg;
    /**
     * 攻击成长
     */
    public double lv_dmg;
    /**
     * 初始移动速度
     */
    public double init_ms;

    /**
     * 
     */
    @Override
    public String toString() {
        return String
                .format(Locale.getDefault(),
                        "[HeroStatsItem: init_str=%f, init_agi=%f, init_int=%f, lv_str=%f, lv_agi=%f, lv_int=%f, init_hp=%f, init_mp=%f, init_armor=%f, lv_hp=%f, lv_mp=%f, lv_armor=%f, init_min_dmg=%f, init_max_dmg=%f, lv_dmg=%f, init_ms=%f]",
                        init_str, init_agi, init_int, lv_str, lv_agi, lv_int, init_hp, init_mp,
                        init_armor, lv_hp, lv_mp, lv_armor, init_min_dmg, init_max_dmg, lv_dmg,
                        init_ms);
    }
}
