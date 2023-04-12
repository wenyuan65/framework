package com.panda.framework.rank;

import com.panda.framework.log.Logger;
import com.panda.framework.log.LoggerFactory;

import java.util.*;

public class Rank {

    private static final Logger logger = LoggerFactory.getLogger(Rank.class);

    /** 排行显示的人数 */
    private int count = 100;
    /** 排行分数的区间大小 */
    private int range = 10;
    /** 是否停止排行 */
    private volatile boolean stopped = false;

    /** 排序比较器 */
    private Comparator<RankData> comparator = new RankDataComparator();
    /** 精确统计队列 */
    private List<RankData> rankList;
    /** 分段存储排行数据 */
    private TreeMap<Long, List<RankData>> rankMap = new TreeMap<>();

    public Rank() {
    }

    /**
     * 初始化排行榜数据
     * @param count 排行榜显示的数据数量
     * @param range 排行榜积分分段
     * @param dataList 排行数据
     */
    public void init(int count, int range, List<RankData> dataList) {
        if (count <= 0 || range <= 0) {
            throw new IllegalArgumentException("排行榜参数错误");
        }
        this.count = count;
        this.range = range;

        rankList = new ArrayList<>(this.count);
        if (dataList.size() <= count) {
            Collections.sort(dataList, this.comparator);

            rankList.addAll(dataList);
        } else {
            for (RankData rankData : dataList) {
                addRank(rankData);
            }
        }
    }

    /**
     * 读取排行榜的数据
     * @return
     */
    public List<RankData> getRankData() {
        List<RankData> list = new ArrayList<>(rankList.size());
        for (RankData rankData : rankList) {
            list.add(rankData.copy());
        }

        return list;
    }

    /**
     * 给一个当前数据，查询排行名次，不在排行榜中的数据返回-1
     * @param data
     * @return
     */
    public int queryRank(RankData data) {
        if (rankList.size() == 0) {
            return -1;
        }
        // 排行榜未满
        if (rankList.size() < count) {
            int index = Collections.binarySearch(rankList, data, this.comparator);
            return index < 0 ? -1 : index + 1;
        }
        // 排行榜已满
        RankData lastData = rankList.get(rankList.size() - 1);
        int cmp = this.comparator.compare(data, lastData);
        if (cmp < 0) {
            // 查询的数据在精确排行榜中
            int index = Collections.binarySearch(rankList, data, this.comparator);
            return index < 0 ? -1 : index + 1;
        } else if (cmp > 0) {
            return calcRankInTreeMap(data);
        } else {
            return rankList.size();
        }
    }

    /**
     * 新增一个排行榜的数据
     * @param data
     * @return 排行榜前N名是否发生变化
     */
    public boolean addRank(RankData data) {
        if (rankList.size() < count) {
            int index = Collections.binarySearch(rankList, data, this.comparator);
            if (index < 0) {
                int insertIndex = - index - 1;
                rankList.add(insertIndex, data);
                return true;
            }
            return false;
        }

        RankData lastData = rankList.get(rankList.size() - 1);
        int cmp = this.comparator.compare(data, lastData);
        if (cmp < 0) {
            addRankList(data);
            return true;
        } else if (cmp > 0) {
            addInexactRankMap(data);
            return false;
        }
        return false;
    }

    /**
     * 更新排行榜数据
     * @param newData
     * @param oldData
     * @return 排行榜前N名是否发生变化
     */
    public boolean updateRank(RankData newData, RankData oldData) {
        if (oldData == null) {
            return addRank(newData);
        }
        if (newData.getScore() == oldData.getScore()) {
            return false;
        }

        RankData lastData = rankList.get(rankList.size() - 1);
        int cmp1 = this.comparator.compare(oldData, lastData);
        int cmp2 = this.comparator.compare(newData, lastData);
        // 删除旧的数据
        if (cmp1 <= 0) {
            removeRankList(oldData);
        } else {
            removeInexactRankMap(oldData);
        }
        if (cmp2 <= 0) {
            addRankList(newData);
        } else {
            addInexactRankMap(newData);

            // 精确排行榜上少人
            if (rankList.size() < count) {
                RankData rankData = pollHighestDataFromInexactRankMap();
                addRankList(rankData);
            }
        }

        return cmp1 <= 0 || cmp2 <= 0;
    }

    private void addRankList(RankData data) {
        // 插入的数据在精确排行榜中
        int index = Collections.binarySearch(rankList, data, this.comparator);
        if (index < 0) {
            int insertIndex = - index - 1;
            rankList.add(insertIndex, data);

            if (rankList.size() > count) {
                RankData lastData = rankList.remove(rankList.size() - 1);
                addInexactRankMap(lastData);
            }
        } else {
            logger.error("向rankList中添加数据时，找到数据重复, {}, {}, {}", data.getPlayerId(), data.getScore(), index);
        }
    }

    private void removeRankList(RankData data) {
        int index = Collections.binarySearch(rankList, data, this.comparator);
        if (index >= 0) {
            rankList.remove(index);
        } else {
            logger.error("删除rankList中的数据时，未找到数据, {}, {}, {}", data.getPlayerId(), data.getScore(), index);
        }
    }

    private void addInexactRankMap(RankData data) {
        long score = data.getScore();
        long scoreRange = (score / range) * range;

        List<RankData> dataList = rankMap.get(scoreRange);
        if (dataList == null) {
            dataList = new ArrayList<>();
            rankMap.put(scoreRange, dataList);
        }

        dataList.add(data);
    }

    private void removeInexactRankMap(RankData data) {
        long score = data.getScore();
        long scoreRange = (score / range) * range;

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

    /**
     * 找出非精确排行榜中最靠前的数据
     * @return
     */
    private RankData pollHighestDataFromInexactRankMap() {
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
        removeInexactRankMap(highestRankData);

        return highestRankData;
    }

    /**
     * 计算非精确的排行名次, 不在列表中会返回-1
     * @param data
     * @return
     */
    private int calcRankInTreeMap(RankData data) {
        long score = data.getScore();
        long scoreRange = (score / range) * range;
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

        return this.count + rankInBeforeRange + rankInRange;
    }

    public void setComparator(Comparator<RankData> comparator) {
        this.comparator = comparator;
    }

    public void stopRank() {
        stopped = true;
    }

    public void clearRank() {
        rankList.clear();
        rankMap.clear();
    }

}
