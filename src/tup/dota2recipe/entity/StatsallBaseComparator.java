package tup.dota2recipe.entity;

import java.util.Comparator;

/**
 * HeroItem statsall.* 排序 Comparator
 * 
 * @author tupunco
 */
public abstract class StatsallBaseComparator implements Comparator<HeroItem> {
    @Override
    public int compare(HeroItem object1, HeroItem object2) {
        if (object1.statsall != null && object2.statsall != null)
            return compare(object2.statsall, object1.statsall);
        else
            return 0;
    }

    /**
     * Compares the two specified objects to determine their relative ordering.
     * @param stat1
     * @param stat2
     * @return
     */
    protected abstract int compare(HeroStatsItem stat1, HeroStatsItem stat2);
}
