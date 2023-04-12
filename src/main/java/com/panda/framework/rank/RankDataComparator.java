package com.panda.framework.rank;

import java.util.Comparator;

public class RankDataComparator implements Comparator<RankData> {

    @Override
    public int compare(RankData data1, RankData data2) {
        if (data1.getScore() != data2.getScore()) {
            return (int) (data2.getScore() - data1.getScore());
        }

        if (data1.getUpdateTime() != data2.getUpdateTime()) {
            return (int) (data1.getUpdateTime() - data2.getUpdateTime());
        }

        return (int) (data1.getPlayerId() - data2.getPlayerId());
    }

}
