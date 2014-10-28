    package ibm.game.server;

import java.net.InetAddress;
import java.net.UnknownHostException;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.Delimiters;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.*;

public class GameServerHandler extends SimpleChannelInboundHandler<String> {

	// static final ChannelGroup channels = new
	// DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	// static final HashMap<String, AGameSession> GameList = new HashMap<String,
	// AGameSession>();
	static final GameList gl = new GameList();

	@Override
	public void channelActive(final ChannelHandlerContext ctx)
			throws UnknownHostException {
		// Once session is secured, send a greeting and register the channel to
		// the global channel
		// list so the channel received the messages from others.
		ArrayList al = gl.getAllWaiting();
		String response = "GL:";

		if (al.size() > 0) {
			for (int i = 0; i < al.size() - 1; i++) {
				response += al.get(i) + ",";
			}

			response += al.get(al.size() - 1);
		}
		response += "\n";

		ctx.writeAndFlush(response);

		// channels.add(ctx.channel());

	}

	public void channelInactive(ChannelHandlerContext ctx)
			throws UnknownHostException {
		// Once session is secured, send a greeting and register the channel to
		// the global channel
		// list so the channel received the messages from others.

		// ctx.writeAndFlush(
		// "Welcome to " + InetAddress.getLocalHost().getHostName() +
		// " Gamet service!\n");

		// channels.add(ctx.channel());

		Channel ch = ctx.channel();

		if (gl.isMainPlayer(ch)) {

			gl.deleteGameByMain(ch);
		} else {
			gl.setNullSecond(ch);
		}

		// System.out.println(InetAddress.getLocalHost().getHostName() +
		// "Channel inactived\n" );

	}

	public void startNewGame(ChannelHandlerContext ctx, String[] request) {
		String gameid = gl.getGameIDByChannel(ctx.channel());

		String response = "FAIL:EXISTS\n";
		if (gameid == null) {

			gameid = gl.NewAGame(ctx.channel());

			int width = AGameSession.width;
			int height = AGameSession.height;

			Position p = gl.getPositionByID(gameid, ctx.channel());

			Constraint cons1 = AGameSession.cons1;
			Constraint cons2 = AGameSession.cons2;

			int an1 = gl.getAngleByID(gameid, ctx.channel());
			// int an2 = AGameSession.angle2;

			response = "NEW:" + gameid + ":" + width + "," + height + ":"
					+ cons1.getX0() + "," + cons1.getY0() + "," + cons1.getX1()
					+ "," + cons1.getY1() + "," + cons2.getX0() + ","
					+ cons2.getY0() + "," + cons2.getX1() + "," + cons2.getY1()
					+ ":" + p.getX() + "," + p.getY() + ":" + an1 + "\n";
		}

		ctx.writeAndFlush(response);

		// ArrayList al = gl.getAllWaiting();

		// response = al.toString();

		// ctx.writeAndFlush(response + "\n");

	}

	public void Move(ChannelHandlerContext ctx, String[] request) {
		String gameid = request[1];
		Position newP = null;
		int newAngle = -1;
		fireInfo fi = null;

		Channel ch = ctx.channel();
		int id = 1;

		id = gl.getPartNum(gameid, ch);

		int direction = Integer.parseInt(request[2]);
		String response = "";

		switch (direction) {

		case 37:
			newAngle = gl.rotate(gameid, ctx.channel(), -AGameSession.rotate);

			break;

		case 38:

			newP = gl.move(gameid, ctx.channel(), AGameSession.step);
			break;

		case 39:

			newAngle = gl.rotate(gameid, ctx.channel(), AGameSession.rotate);
			break;

		case 40:

			newP = gl.move(gameid, ctx.channel(), -AGameSession.step);
			break;
			
		case 32:

			fi = gl.fire(gameid, ctx.channel());
			break;		
			

		default:

		}

		if (newP != null) {

			response = "POSITION:" + newP.getX() + "," + newP.getY() + ":" + id
					+ "\n";
			System.out.println(response);

			//ctx.writeAndFlush(response);
			gl.sendMessageForTwo(gameid, response);

		}

		if (newAngle != -1) {

			response = "ANGLE:" + newAngle + ":" + id + "\n";
			System.out.println(response);

			gl.sendMessageForTwo(gameid, response);
			//ctx.writeAndFlush(response);

		}
		
		if (fi != null)
		{
			
			response = "FIRE:" + fi.x0 + "," + fi.y0 + "," + fi.x1 + "," + fi.y1 + ":" + fi.targeted + ":" + fi.part + "\n";
			gl.sendMessageForTwo(gameid, response);
			
			if (fi.targeted){
				
				 if (fi.part == 1)
				 {
					 response = "SCORE:" + fi.fule + ":2\n";
					 gl.sendMessageForTwo(gameid, response);
				 }
				
				 if (fi.part == 2)
				 {
					 response = "SCORE:" + fi.fule + ":1\n";
					 gl.sendMessageForTwo(gameid, response);
				 }
				
				
			}
			
			
			
		}

		// ArrayList al = gl.getAllWaiting();

		// response = al.toString();

		// ctx.writeAndFlush(response + "\n");

	}

	public void QuitGame(ChannelHandlerContext ctx, String[] request) {

		Channel ch = ctx.channel();

		if (gl.isMainPlayer(ch)) {

			gl.deleteGameByMain(ch);
		} else {
			gl.setNullSecond(ch);
		}

		ctx.close();
	}

	public void JoinGame(ChannelHandlerContext ctx, String[] request) {

		Channel ch = ctx.channel();
		String gameid = request[1];
		String response = "FAIL:JOIN\n";
		if (gl.JoinAGame(gameid, ch) != null) {

			int width = AGameSession.width;
			int height = AGameSession.height;

			Position p = gl.getPositionByID(gameid, ctx.channel());

			Constraint cons1 = AGameSession.cons1;
			Constraint cons2 = AGameSession.cons2;

			response = "NEWJOIN:" + gameid + ":" + width + "," + height + ":"
					+ cons1.getX0() + "," + cons1.getY0() + "," + cons1.getX1()
					+ "," + cons1.getY1() + "," + cons2.getX0() + ","
					+ cons2.getY0() + "," + cons2.getX1() + "," + cons2.getY1()
					+ ":" + p.getX() + "," + p.getY() + ":2\n";
			
			
			
			
			gl.sendMessageForTwo(gameid, response);
			
			Position newP = gl.getMainPostion(gameid);
			
			if (newP != null) {

				response = "POSITION:" + newP.getX() + "," + newP.getY() + ":1" 
						+ "\n";
				System.out.println(response);

				ctx.writeAndFlush(response);
			//	gl.sendMessageForTwo(gameid, response);

			}
			
			int newAngle = gl.getMainAngle(gameid);

			if (newAngle != -1) {

				response = "ANGLE:" + newAngle + ":1" + "\n";
				System.out.println(response);

				//gl.sendMessageForTwo(gameid, response);
				ctx.writeAndFlush(response);

			}
			
			
			
			
			
			
			
			
		}


        

	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, String msg)
			throws Exception {

		System.out.println(msg);

		String[] request = msg.split(":");
		String cmd = request[0];

		if (cmd.equals("NEWGAME")) {

			startNewGame(ctx, request);

		}

		if (cmd.equals("MOVE")) {

			Move(ctx, request);

		}

		if (cmd.equals("QUIT")) {

			QuitGame(ctx, request);

		}

		if (cmd.equals("JOIN")) {

			JoinGame(ctx, request);

		}

		// Send the received message to all channels but the current one.

		/*
		 * 
		 * for (Channel c: channels) { if (c != ctx.channel()) {
		 * c.writeAndFlush("[" + ctx.channel().remoteAddress() + "] " + msg +
		 * '\n'); } else { c.writeAndFlush("[you] " + msg + '\n'); } }
		 * 
		 * // Close the connection if the client has sent 'bye'. if
		 * ("bye".equals(msg.toLowerCase())) { ctx.close(); }
		 */

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		Channel ch = ctx.channel();

		if (gl.isMainPlayer(ch)) {

			gl.deleteGameByMain(ch);
		} else {
			gl.setNullSecond(ch);
		}

		ctx.close();
	}

}
