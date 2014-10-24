package ibm.game.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

//import io.netty.example.telnet.TelnetServer;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class GameServerByJava {
	static final int PORT = Integer
			.parseInt(System.getProperty("port", "6666"));

	public static void main(String[] args) throws Exception {

		System.out.println("Java Game Server");
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new GameServerInitializer());

			System.out.println("Start Listening on " + PORT);
			b.bind(PORT).sync().channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

}
