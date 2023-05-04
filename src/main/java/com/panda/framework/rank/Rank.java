package com.panda.framework.rank;

import com.panda.framework.log.Logger;
import com.panda.framework.log.LoggerFactory;
import com.panda.framework.rank.impl.InexactRank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    /** 未显示在排行榜上的数据管理器 */
    private RankBelow rankBelow;

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

        // 初始化排行榜之后的数据管理器
//        rankBelow = new ExactRank(this.comparator, this.range);
        rankBelow = new InexactRank(this.comparator, this.range);
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
            int rank = rankBelow.queryRank(data);
            return rank > 0 ? this.count + rank : rank;
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
            rankBelow.addRankData(data);
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

        RankData lastData = rankList.get(rankList.size() - 1);
        int cmp1 = this.comparator.compare(oldData, lastData);
        int cmp2 = this.comparator.compare(newData, lastData);
        // 删除旧的数据
        if (cmp1 <= 0) {
            removeRankList(oldData);
        } else {
            rankBelow.removeRankData(oldData);
        }
        if (cmp2 <= 0) {
            addRankList(newData);
        } else {
            rankBelow.addRankData(newData);

            // 精确排行榜上少人,从排行榜之外的数据中取出最高分的人
            if (rankList.size() < count) {
                RankData rankData = rankBelow.removeFirstRankData();
                if (rankData != null) {
                    addRankList(rankData);
                }
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
                rankBelow.addRankData(lastData);
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

    /**
     * 如果需要设置新的比较器，必须在init()方法执行前完成
     * @param comparator
     */
    public void setComparator(Comparator<RankData> comparator) {
        this.comparator = comparator;
    }

    public void startRank() {
        stopped = false;
    }

    public void stopRank() {
        stopped = true;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void clearRank() {
        rankList.clear();

        rankBelow.clear();
    }

}
