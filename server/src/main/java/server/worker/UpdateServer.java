package server.worker;

import server.connection.ConnectionHandler;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UpdateServer implements Runnable {
    private final ThreadPoolExecutor executor;

    public UpdateServer() {
        this.executor = new ThreadPoolExecutor(10, 32, 10l, TimeUnit.SECONDS, ConnectionHandler.instance.subscribersQueue);
        this.executor.prestartAllCoreThreads();
    }

    @Override
    public void run() {
        while (true) {
            System.out.println(String.format("Update Server load: %d users being served - Dynamic thread pool size: %d - Connection queue backlog: %d", executor.getActiveCount(), executor.getPoolSize(), executor.getQueue().size()));
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
