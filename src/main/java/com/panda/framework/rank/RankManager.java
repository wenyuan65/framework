//package com.panda.framework.rank;
//
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class RankManager {
//
//    private static final RankManager instance = new RankManager();
//
//    private RankManager() {
//    }
//
//    public static RankManager getInstance() {
//        return instance;
//    }
//
//    private Map<RankType, Rank> rankMap = new ConcurrentHashMap<>();
//
//    /**
//     * 初始化排行榜
//     * @param rankType
//     * @param rankDataList
//     */
//    public void initRank(RankType rankType, List<RankData> rankDataList) {
//        Rank rank = new Rank();
//        rank.init(rankType.getCount(), rankType.getRange(), rankDataList);
//
//        addRank(rankType, rank);
//    }
//
//    /**
//     * 添加排行榜
//     * @param rankType
//     * @param rank
//     */
//    public void addRank(RankType rankType, Rank rank) {
//        if (rankMap.containsKey(rankType)) {
//            throw new RuntimeException("排行榜已经存在，不可重复添加，" + rankType.getType());
//        }
//
//        rankMap.put(rankType, rank);
//    }
//
//    /**
//     * 获取排行榜
//     * @param rankType
//     * @return
//     */
//    public Rank getRank(RankType rankType) {
//        return rankMap.get(rankType);
//    }
//
//    /**
//     * 获取排行榜精确数据
//     * @param rankType
//     * @return
//     */
//    public List<RankData> getRankData(RankType rankType) {
//        Rank rank = getRank(rankType);
//        if (rank == null) {
//            return null;
//        }
//
//        return rank.getRankData();
//    }
//
//    public int getRankNo(RankType rankType, RankData rankData) {
//        Rank rank = getRank(rankType);
//        if (rank == null) {
//            return -1;
//        }
//
//        return rank.queryRank(rankData);
//    }
//
//    public void addRankData(RankType rankType, RankData rankData) {
//        Rank rank = getRank(rankType);
//        if (rank == null) {
//            return;
//        }
//
//        rank.addRank(rankData);
//    }
//
//    public void updateRank(RankType rankType, RankData newData, RankData oldData) {
//        Rank rank = getRank(rankType);
//        if (rank == null) {
//            return;
//        }
//
//        rank.updateRank(newData, oldData);
//    }
//
//    public void startRank(RankType rankType) {
//        Rank rank = getRank(rankType);
//        if (rank == null) {
//            return;
//        }
//
//        rank.startRank();
//    }
//
//    public void stopRank(RankType rankType) {
//        Rank rank = getRank(rankType);
//        if (rank == null) {
//            return;
//        }
//
//        rank.stopRank();
//    }
//
//    public boolean isStopped(RankType rankType) {
//        Rank rank = getRank(rankType);
//        if (rank == null) {
//            return false;
//        }
//
//        return rank.isStopped();
//    }
//
//    public void clearRank(RankType rankType) {
//        Rank rank = getRank(rankType);
//        if (rank == null) {
//            return;
//        }
//
//        rank.clearRank();
//    }
//
//}
