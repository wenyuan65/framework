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
//    public void putRank(RankType rankType, Rank rank) {
//        if (rankMap.containsKey(rankType)) {
//            throw new RuntimeException("排行榜已经存在，不可重复添加，" + rankType.getType());
//        }
//
//        rankMap.put(rankType, rank);
//    }
//
//    public Rank getRank(RankType rankType) {
//        return rankMap.get(rankType);
//    }
//
//    public List<RankData> getRankData(RankType rankType) {
//        Rank rank = getRank(rankType);
//        if (rank == null) {
//            return null;
//        }
//
//        return rank.getRankData();
//    }
//
//    public void addRank(RankType rankType, RankData rankData) {
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
//    public void stopRank(RankType rankType) {
//        Rank rank = getRank(rankType);
//        if (rank == null) {
//            return;
//        }
//
//        rank.stopRank();
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
