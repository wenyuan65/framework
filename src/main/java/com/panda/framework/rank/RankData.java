package com.panda.framework.rank;

public class RankData {
    /** 玩家角色id */
    private long playerId;
    /** 排行的分数 */
    private long score;
    /** 排行榜更新时间 */
    private long updateTime;
    /** 字符串参数，存储名称，头像等信息 */
    private String[] param;
    /** 整数参数 */
    private int[] param2;

    public RankData() {
    }

    public RankData(long playerId, long score, long updateTime) {
        this.playerId = playerId;
        this.score = score;
        this.updateTime = updateTime;
    }

    public RankData(long playerId, long score, long updateTime, String[] param, int[] param2) {
        this.playerId = playerId;
        this.score = score;
        this.updateTime = updateTime;
        this.param = param;
        this.param2 = param2;
    }

    public RankData copy() {
        RankData data = new RankData();
        data.setPlayerId(this.getPlayerId());
        data.setScore(this.getScore());
        data.setUpdateTime(this.getUpdateTime());

        if (this.param != null) {
            String[] newParam = new String[this.param.length];
            System.arraycopy(this.param, 0, newParam, 0, this.param.length);
            data.setParam(newParam);
        }
        if (this.param2 != null) {
            int[] newParam = new int[this.param2.length];
            System.arraycopy(this.param2, 0, newParam, 0, this.param2.length);
            data.setParam2(newParam);
        }

        return data;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String[] getParam() {
        return param;
    }

    public void setParam(String[] param) {
        this.param = param;
    }

    public int[] getParam2() {
        return param2;
    }

    public void setParam2(int[] param2) {
        this.param2 = param2;
    }

    @Override
    public String toString() {
        return playerId + ", " + score;
    }
}
