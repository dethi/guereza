package com.epita.guereza;

import com.epita.eventbus.EventBusClient;
import com.epita.eventbus.NettyServer;

public class ServerApp extends App {
    final NettyServer ns;
    final int port;

    protected ServerApp(EventBusClient eventBus, int port) {
        super(eventBus);

        ns = new NettyServer();
        this.port = port;
    }

    @Override
    public void run() {
        ns.run(port);
    }
}
