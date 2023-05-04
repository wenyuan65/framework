package com.panda.framework.rank.impl;

import com.panda.framework.log.Logger;
import com.panda.framework.log.LoggerFactory;
import com.panda.framework.rank.Rank;
import com.panda.framework.rank.RankBelow;
import com.panda.framework.rank.RankData;

import java.util.*;

/**
 * 精确的排名规则
 */
public class ExactRank extends AbstractRankBelow {

    private static final Logger logger = LoggerFactory.getLogger(Rank.class);

    public ExactRank(Comparator<RankData> comparator, int range) {
        super(comparator, range);
    }

    @Override
    public void addRankData(RankData data) {
        long scoreRange = calcRange(data.getScore());
        List<RankData> dataList = rankMap.get(scoreRange);
        if (dataList == null) {
            dataList = new ArrayList<>();
            dataList.add(data);

            rankMap.put(scoreRange, dataList);
        } else {
            int index = Collections.binarySearch(dataList, data, this.comparator);
            if (index < 0) {
                int insertIndex = - index - 1;
                dataList.add(insertIndex, data);
            } else {
                logger.error("向ExactRank中添加数据时，找到数据重复, {}, {}, {}", data.getPlayerId(), data.getScore(), index);
            }
        }
    }

    @Override
    public void removeRankData(RankData data) {
        long scoreRange = calcRange(data.getScore());
        List<RankData> dataList = rankMap.get(scoreRange);
        if (dataList == null) {
            logger.error("删除ExactRank中的数据时，未找到列表, {}, {}, {}", data.getPlayerId(), data.getScore(), scoreRange);
            return;
        }

        int index = Collections.binarySearch(dataList, data, this.comparator);
        if (index >= 0) {
            dataList.remove(index);
        }

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

        RankData firstRankData = list.remove(0);

        if (list.size() == 0) {
            rankMap.remove(entry.getKey());
        }

        return firstRankData;
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

        int rankInRange = 1;
        int index = Collections.binarySearch(rankData, data, this.comparator);
        if (index >= 0) {
            rankInRange = index + 1;
        }

        return rankInBeforeRange + rankInRange;
    }

}
