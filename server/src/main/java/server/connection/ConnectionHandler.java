package server.connection;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionHandler {

    public static final ConnectionHandler instance = new ConnectionHandler();
    public BlockingQueue<Runnable> subscribersQueue = new LinkedBlockingQueue<>();

    private ConnectionHandler() {

    }
}
