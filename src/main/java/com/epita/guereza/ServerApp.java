package com.epita.guereza;

import com.epita.eventbus.NettyServer;

public class ServerApp {
    private final NettyServer ns = new NettyServer();
    private final int port;

    protected ServerApp(int port) {
        this.port = port;
    }

    public void run() {
        ns.run(port);
    }
}
