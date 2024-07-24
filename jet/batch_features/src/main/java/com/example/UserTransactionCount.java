package com.example;

import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.jet.json.JsonUtil;

import java.io.IOException;

public class UserTransactionCount {
    private String userId;
    private long transactionCount7d;
    private long featureTimestamp;


    public UserTransactionCount(String userId, long transactionCount7d, long featureTimestamp) {
        this.userId = userId;
        this.transactionCount7d = transactionCount7d;
        this.featureTimestamp = featureTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTransactionCount7d() {
        return transactionCount7d;
    }

    public void setTransactionCount7d(long transactionCount7d) {
        this.transactionCount7d = transactionCount7d;
    }

    public long getFeatureTimestamp() {
        return featureTimestamp;
    }

    public void setFeatureTimestamp(long featureTimestamp) {
        this.featureTimestamp = featureTimestamp;
    }

    public HazelcastJsonValue toJson() throws IOException {
        return new HazelcastJsonValue(JsonUtil.toJson(this));
    }
}
