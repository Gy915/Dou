package com.bytedance.androidcamp.network.dou.bean;

import java.util.List;

public class FeedResponse {
    private List<Feed> feeds;
    private boolean success;
    public List<Feed> getFeeds(){
        return feeds;
    }
    public boolean getSuccess(){
        return success;
    }
}
