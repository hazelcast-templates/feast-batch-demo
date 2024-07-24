package com.example;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.aggregate.AggregateOperations;
import com.hazelcast.jet.pipeline.DataConnectionRef;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.file.FileFormat;
import com.hazelcast.jet.pipeline.file.FileSources;
import com.hazelcast.map.impl.MapEntrySimple;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

public class Main {

    public static Pipeline createPipeline(String dataSetPath, LocalDateTime endDate) {
        var endDateEpoch = endDate.toEpochSecond(ZoneOffset.UTC);
        var beginDate = endDate.minusDays(7);
        var beginDateEpoch = beginDate.toEpochSecond(ZoneOffset.UTC);
        var pipeline = Pipeline.create();
        var source =
        pipeline
            .readFrom(FileSources.files(dataSetPath)
                .glob("demo_data.jsonl")
                .format(FileFormat.json(Transaction.class))
                .build());

        var last7Days = source
                .filter(transaction -> {
                    var transactionTime = transaction.getUnixTime();
                    return transactionTime > beginDateEpoch && transactionTime <= endDateEpoch;
                });

        last7Days
            .groupingKey((Transaction::getAccountNumber))
            .aggregate((AggregateOperations.counting()))
            .map(item -> {
                var userId = item.getKey();
                var utc = new UserTransactionCount(userId, item.getValue(), endDateEpoch);
                return (Map.Entry<String, UserTransactionCount>) new MapEntrySimple(userId, utc);
            })
            .writeTo(Sinks.jdbc("INSERT INTO user_transaction_count_7d(user_id, transaction_count_7d, feature_timestamp) values(?, ?, ?) ON CONFLICT DO NOTHING",
                    DataConnectionRef.dataConnectionRef("demo"),
                    (stmt, item) -> {
                        var utc = item.getValue();
                        stmt.setString(1, utc.getUserId());
                        stmt.setLong(2, utc.getTransactionCount7d());
                        stmt.setTimestamp(3, Timestamp.from(Instant.ofEpochSecond(utc.getFeatureTimestamp())));
                    }));

        return pipeline;
    }

    public static void backfillFeatures(HazelcastInstance hz, String dataSetPath, LocalDateTime earliestEndDate) {
        var endDate = earliestEndDate;
        for (int i = 0; i < 8; i++) {
            hz.getJet().newJob(createPipeline(dataSetPath, endDate));
            endDate = endDate.minusDays(1);
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new RuntimeException("dataSetPath is required");
        }
        var hz = Hazelcast.bootstrappedInstance();
        var endDate = LocalDateTime.now();
        var dataSetPath = args[0];
        backfillFeatures(hz, dataSetPath, endDate);
    }
}
