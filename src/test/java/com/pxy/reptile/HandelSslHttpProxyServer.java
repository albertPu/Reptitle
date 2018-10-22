package com.pxy.reptile;

import com.pxy.reptile.server.HttpProxyServer;
import com.pxy.reptile.server.HttpProxyServerConfig;

public class HandelSslHttpProxyServer {

  public static void main(String[] args) throws Exception {
    HttpProxyServerConfig config =  new HttpProxyServerConfig();
    config.setHandleSsl(true);
    new HttpProxyServer()
        .serverConfig(config)
        .start(9999);
  }
}
