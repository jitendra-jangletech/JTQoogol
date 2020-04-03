package com.jangletech.qoogol.listener;

/**
 * Created by Pritali on 3/6/2020.
 */
public interface QueViewClick {
    void getQueViewClick(String strQuestTag, int position);
    void onTabClickClick(int queNo,String strQuestTag, int position);
    void onTabPositionChange(int position);
}
