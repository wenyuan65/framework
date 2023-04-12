package com.panda.framework.rank;

public interface RankDataConvertor<T, R> {

    RankData convert(T t);

    R buildMessage(RankData rankData);

}
