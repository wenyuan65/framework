package com.panda.framework.rank.impl;

import com.panda.framework.log.Logger;
import com.panda.framework.log.LoggerFactory;
import com.panda.framework.rank.Rank;
import com.panda.framework.rank.RankBelow;
import com.panda.framework.rank.RankData;

import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

public abstract class AbstractRankBelow implements RankBelow {

    private static final Logger logger = LoggerFactory.getLogger(Rank.class);

    /** 排行分数的区间大小 */
    protected int range;
    /** 排序比较器 */
    protected Comparator<RankData> comparator;
    /** 分段存储排行数据 */
    protected TreeMap<Long, List<RankData>> rankMap = new TreeMap<>();

    public AbstractRankBelow(Comparator<RankData> comparator, int range) {
        this.comparator = comparator;
        this.range = range;
    }

    @Override
    public void clear() {
        rankMap.clear();
    }

    /**
     * 计算分数区间
     * @param score
     * @return
     */
    protected long calcRange(long score) {
        return (score / range) * range;
    }

}
