package com.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		NetClientOptions options = new NetClientOptions().setConnectTimeout(10000).setReconnectAttempts(10).
				setReconnectInterval(500);
		vertx.deployVerticle(new DemoApplication().new VertxTcpClientVerticle(vertx.createNetClient(options)));
	}

	public class VertxTcpClientVerticle extends AbstractVerticle {
		NetClient tcpClient;
		public VertxTcpClientVerticle(NetClient createNetClient) {
			this.tcpClient = createNetClient;
		}

		@Override
		public void start(Future<Void> startFuture) {
			System.out.println("VertxTcpClientVerticle started!");
			tcpClient.connect(8085, "localhost",
					res -> {
						if (res.succeeded()) {
							System.out.println("Connected!");
							NetSocket socket = res.result();
							socket.handler(buf -> {
								System.out.println("Received data length " + buf.length());
								byte[] b = buf.getBytes(0,buf.length());
								System.out.println(new String(b, StandardCharsets.UTF_8));
							});
						} else {
							System.out.println("Failed to connect: " + res.cause().getMessage());
						}
					});
		}

		@Override
		public void stop(Future stopFuture) throws Exception {
			System.out.println("VertxTcpClientVerticle stopped!");
			tcpClient.close();
		}
	}
}
