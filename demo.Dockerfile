FROM docker.io/ubuntu:24.04

RUN useradd sam -m

RUN \
    apt-get update && \
    apt-get install -y \
        python3-pip  \
        openjdk-21-jdk-headless \
        curl \
        vim \
        nano \
        jq \
        ncat \
        && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists && \
    true

COPY etc/run.sh /home/sam/.local/bin/run

RUN \
    mkdir /home/sam/etc &&\
    chmod +x /home/sam/.local/bin/run &&\
    chown -R sam:sam /home/sam/.local

COPY etc/create_data_connection.sql /home/sam/etc

USER sam

WORKDIR /home/sam

RUN \
    curl -L -o install.sh https://hazelcast.com/clc/install.sh &&\
    bash install.sh && \
    rm install.sh

ENV HZ_PHONE_HOME_ENABLED=false
ENV CLC_SKIP_UPDATE_CHECK=1

COPY requirements.txt .

RUN \
    pip install --user --break-system-packages -r requirements.txt --no-warn-script-location &&\
    printf "\nexport PATH=$PATH:$HOME/.local/bin:$HOME/.hazelcast/bin\n\n" >> .bashrc

ENV PATH="$PATH:/home/sam/.local/bin:/home/sam/.hazelcast/bin"

COPY BatchFeatures.ipynb .

RUN \
    mkdir -p /home/sam/feast/data &&\
    clc config add default cluster.address=hazelcast

ENTRYPOINT ["/home/sam/.local/bin/jupyter"]
CMD ["lab", "--ip", "0.0.0.0", "--no-browser"]