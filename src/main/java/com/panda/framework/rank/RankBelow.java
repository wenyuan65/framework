package com.panda.framework.rank;

/**
 * 精确排行榜之后，玩家的排行数据存储逻辑
 */
public interface RankBelow {

    /**
     * 增加排行数据
     * @param data
     */
    void addRankData(RankData data);

    /**
     * 删除排行数据
     * @param data
     */
    void removeRankData(RankData data);

    /**
     * 找出分数最高的数据
     * @return
     */
    RankData removeFirstRankData();

    /**
     * 查询数据的排名，不在列表中会返回-1
     * @param data
     * @return
     */
    int queryRank(RankData data);

    /**
     * 清理所有数据
     */
    void clear();

}
