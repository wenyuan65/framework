package com.panda.framework.rank.impl;

import com.panda.framework.log.Logger;
import com.panda.framework.log.LoggerFactory;
import com.panda.framework.rank.Rank;
import com.panda.framework.rank.RankData;

import java.util.*;

/**
 * 非精确的排名规则
 */
public class InexactRank extends AbstractRankBelow {

    private static final Logger logger = LoggerFactory.getLogger(Rank.class);

    public InexactRank(Comparator<RankData> comparator, int range) {
        super(comparator, range);
    }

    @Override
    public void addRankData(RankData data) {
        long scoreRange = calcRange(data.getScore());
        List<RankData> dataList = rankMap.get(scoreRange);
        if (dataList == null) {
            dataList = new ArrayList<>();
            rankMap.put(scoreRange, dataList);
        }

        dataList.add(data);
    }

    @Override
    public void removeRankData(RankData data) {
        long scoreRange = calcRange(data.getScore());
        List<RankData> dataList = rankMap.get(scoreRange);
        if (dataList == null) {
            logger.error("删除rankMap中的数据时，未找到列表, {}, {}, {}", data.getPlayerId(), data.getScore(), scoreRange);
            return;
        }
        dataList.removeIf(t -> t.getPlayerId() == data.getPlayerId());

        if (dataList.size() == 0) {
            rankMap.remove(scoreRange);
        }
    }

    @Override
    public RankData removeFirstRankData() {
        Map.Entry<Long, List<RankData>> entry = rankMap.lastEntry();
        List<RankData> list = entry.getValue();
        if (list.size() <= 0) {
            logger.error("弹出rankMap中排名最靠前的数据时，未找到, {}", entry.getKey());
            return null;
        }

        RankData highestRankData = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            int cmp = this.comparator.compare(list.get(i), highestRankData);
            if (cmp < 0) {
                highestRankData = list.get(i);
            }
        }
        if (highestRankData == null) {
            return null;
        }
        removeRankData(highestRankData);

        return highestRankData;
    }

    @Override
    public int queryRank(RankData data) {
        long score = data.getScore();
        long scoreRange = calcRange(score);
        List<RankData> rankData = rankMap.get(scoreRange);

        if (rankData == null) {
            return -1;
        }

        // 类似于1000~1100, 900~1000, 800~900,....
        int rankInBeforeRange = 0;
        Map.Entry<Long, List<RankData>> entry = rankMap.lastEntry();
        while (entry != null && entry.getKey() > scoreRange) {
            rankInBeforeRange += entry.getValue().size();
            entry = rankMap.lowerEntry(entry.getKey());
        }

        int rankInRange = (int)Math.round(1.0 * (scoreRange + range - score) / range * rankData.size());
        rankInRange = Math.min(rankInRange, rankData.size());
        rankInRange = Math.max(rankInRange, 1);

        return rankInBeforeRange + rankInRange;
    }

}
