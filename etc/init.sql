create table user_transaction_count_7d (
    id serial primary key,
    user_id text,
    transaction_count_7d integer,
    feature_timestamp timestamp
);
