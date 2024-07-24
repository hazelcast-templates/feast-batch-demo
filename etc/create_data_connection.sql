CREATE DATA CONNECTION IF NOT EXISTS demo
TYPE JDBC
SHARED
OPTIONS (
  'jdbcUrl'='jdbc:postgresql://postgresql:5432/demo',
  'user'='demo',
  'password'='demo',
  'maximumPoolSize'='50'
);
