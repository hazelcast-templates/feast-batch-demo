FROM docker.io/hazelcast/hazelcast:5.4.0

RUN mkdir /home/hazelcast/data

ADD demo_data.tar.gz /home/hazelcast/data/
