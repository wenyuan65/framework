//package com.panda.framework.rank.convert;
//
//
//public class ArenaRankDataConvertor implements RankDataConvertor<PlayerArena, ArenaPb.PlayerArenaRankInfo.Builder> {
//
//    @Override
//    public RankData convert(PlayerArena playerArena) {
//        if (playerArena == null) {
//            return null;
//        }
//
//        RankData rankData = new RankData();
//        rankData.setPlayerId(playerArena.getPlayerId());
//        rankData.setScore(playerArena.getScore());
//        rankData.setUpdateTime(playerArena.getUpdateTime().getTime() / 1000L);
//        rankData.setParam(new String[] { playerArena.getNickName(), playerArena.getFormation() });
//        rankData.setParam2(new int[] { playerArena.getHead(), playerArena.getLevel(), playerArena.getFight(), playerArena.getWinCount(), playerArena.getTotalCount() });
//
//        return rankData;
//    }
//
//    @Override
//    public ArenaPb.PlayerArenaRankInfo.Builder buildMessage(RankData rankData, int rank) {
//        String[] param = rankData.getParam();
//        int[] param2 = rankData.getParam2();
//
//        int rate = 10000;
//        if (param2[4] != 0) {
//            rate = (int) (param2[3] * 10000L / param2[4]);
//        }
//
//        ArenaPb.PlayerArenaRankInfo.Builder builder = ArenaPb.PlayerArenaRankInfo.newBuilder();
//        builder.setPlayerId(rankData.getPlayerId());
//        builder.setNickName(param[0]);
//        builder.setHead(param2[0]);
//        builder.setRank(rank);
//        builder.setWinningRate(rate);
//        builder.setScore((int) rankData.getScore());
//
//        return builder;
//    }
//
//}
