from datetime import timedelta
from feast import FeatureView, Entity, ValueType
from feast.infra.offline_stores.contrib.postgres_offline_store.postgres_source import PostgreSQLSource

# from hazelcast_offline_store import HazelcastSource

# Add an entity for users
user_entity = Entity(
    name="user_id",
    description="A user that has executed a transaction or received a transaction",
    value_type=ValueType.STRING
)

user_transaction_count_7d_fv = FeatureView(
    name="user_transaction_count_7d",
    entities=[user_entity],
    ttl=timedelta(weeks=1),
    source=PostgreSQLSource(
        table="user_transaction_count_7d",
        timestamp_field="feature_timestamp"))