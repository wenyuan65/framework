//package com.panda.framework.rank.convert;
//
//import com.dongyou.hsqwz.entity.arena.PlayerArena;
//import com.dongyou.hsqwz.rank.RankData;
//import com.dongyou.hsqwz.rank.RankDataConvertor;
//import com.google.protobuf.Message;
//
//public class ArenaRankDataConvertor implements RankDataConvertor<PlayerArena> {
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
//    public Message buildMessage(RankData rankData) {
//        return null;
//    }
//
//}
