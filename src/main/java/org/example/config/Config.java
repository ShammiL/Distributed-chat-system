package org.example.config;


public class Config {
    private Config() {
    }

    public static final int ELECTION_START_TRIES = 3;
    public static final int ELECTION_START_RETRY_SLEEP = 400;
    public static final int ELECTION_START_TIMEOUT = 2000;
    public static final int ELECTION_ANSWER_MESSAGE_TIMEOUT = 4000;
    public static final int COORDINATOR_MESSAGE_TIMEOUT = 6000;
    public static final int HEARTBEAT_THRESHOLD = ELECTION_START_TIMEOUT;

    public static final int HEARTBEAT_INTERVAL = 1000;
    public static final int MAX_POSSIBLY_DOWN_ROUNDS = 5;
    public static final String ALIVE = "ALIVE";
    public static final String POSSIBLY_DOWN = "POSSIBLY_DOWN";
    public static final String DOWN = "DOWN";
    public static final int HEARTBEAT_SLEEP_TIME = 1000;

    public static final int RETRY_JOB_INTERVAL = 5000;
    public static final int RETRY_JOB_START_DELAY = 5000;
    public static final int MAX_REQUEST_RETRIES = 5;
    public static final int MAX_RETRY_QUEUE_SIZE = 500;
}
