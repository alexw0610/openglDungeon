package server.connection;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class SubscriptionHandler {

    public static final SubscriptionHandler instance = new SubscriptionHandler();

    public HashMap<Integer, SubscribedClient> subscribedClients = new LinkedHashMap<>();

    private SubscriptionHandler() {

    }
}
