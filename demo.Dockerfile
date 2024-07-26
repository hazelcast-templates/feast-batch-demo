FROM docker.io/ubuntu:24.04

RUN useradd sam -m

RUN \
    apt-get update && \
    apt-get install -y \
        python3-venv  \
        openjdk-21-jdk-headless \
        curl \
        vim \
        nano \
        jq \
        ncat \
        make \
        && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists && \
    true

COPY etc/run.sh /home/sam/.local/bin/run

RUN \
    mkdir /home/sam/etc &&\
    chmod +x /home/sam/.local/bin/run &&\
    chown -R sam:sam /home/sam/.local

USER sam

WORKDIR /home/sam

COPY etc/create_data_connection.sql /home/sam/etc

RUN \
    curl -L -o install.sh https://hazelcast.com/clc/install.sh &&\
    bash install.sh && \
    rm install.sh

ENV HZ_PHONE_HOME_ENABLED=false
ENV CLC_SKIP_UPDATE_CHECK=1

COPY --chown=sam:sam feature_repo /home/sam/feature_repo/
COPY --chown=sam:sam jet /home/sam/jet/
COPY requirements.txt .

ENV PATH="$PATH:/home/sam/.local/bin:/home/sam/.hazelcast/bin:/home/sam/.venv/bin"

RUN \
    python3 -m venv .venv &&\
    $HOME/.venv/bin/pip install -r requirements.txt

COPY BatchFeatures.ipynb .

RUN \
    mkdir -p /home/sam/feast/data &&\
    clc config add default cluster.address=hazelcast &&\
    run build_jet batch_features

ENTRYPOINT ["/home/sam/.venv/bin/jupyter"]
CMD ["lab", "--ip", "0.0.0.0", "--no-browser", "--ServerApp.token", ""]