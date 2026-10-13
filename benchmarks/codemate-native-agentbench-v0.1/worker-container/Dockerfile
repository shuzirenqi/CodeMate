FROM eclipse-temurin:21-jre-alpine@sha256:326837fba06a8ff5482a17bafbd65319e64a6e997febb7c85ebe7e3f73c12b11

LABEL org.opencontainers.image.title="CodeMate Native AgentBench Candidate Worker"
LABEL org.opencontainers.image.description="Networkless runtime for a read-only mounted CodeMate Candidate jar"
LABEL com.codemate.agentbench.role="candidate-worker"
LABEL com.codemate.agentbench.scope="relay-worker-only"

USER root
RUN addgroup -S -g 10002 codemateworker \
    && adduser -S -D -H -u 10002 -G codemateworker -h /home/codemate -s /sbin/nologin codemateworker \
    && mkdir -p /workspace /home/codemate /opt/codemate \
    && chown 10002:10002 /workspace /home/codemate \
    && chmod 0555 /opt/codemate

ENV HOME=/home/codemate \
    LANG=C.UTF-8 \
    LC_ALL=C.UTF-8

WORKDIR /workspace
USER codemateworker:codemateworker

ENTRYPOINT ["java"]
CMD ["-version"]
