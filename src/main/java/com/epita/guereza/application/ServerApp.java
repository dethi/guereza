package com.epita.guereza.application;

import com.epita.eventbus.server.NettyServer;

public class ServerApp {
    private final NettyServer ns = new NettyServer();
    private final int port;

    public ServerApp(int port) {
        this.port = port;
    }

    public void run() {
        ns.run(port);
    }
}
