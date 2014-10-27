package ibm.game.server;

import java.util.HashMap;
import io.netty.channel.Channel;
import java.util.*;

public class GameList {

	HashMap<String, AGameSession> GameListByID = new HashMap<String, AGameSession>();
	HashMap<Channel, AGameSession> GameListByCH1 = new HashMap<Channel, AGameSession>();
	HashMap<Channel, AGameSession> GameListByCH2 = new HashMap<Channel, AGameSession>();

	public synchronized String NewAGame(Channel ch) {

		AGameSession game = new AGameSession();
		GameListByID.put(game.getGameid(), game);
		GameListByCH1.put(ch, game);
		game.setC1(ch);
		

		return game.getGameid();

	}

	public synchronized String JoinAGame(String gameid, Channel ch) {

		AGameSession game = GameListByID.get(gameid);

		if (game != null) {

			GameListByCH2.put(ch, game);
			game.setC2(ch);

			return gameid;

		}

		return null;

	}

	public synchronized Position getPositionByID(String gameid, Channel ch) {

		AGameSession game = GameListByID.get(gameid);

		return game.getCurPos(ch);

	}

	public synchronized int getAngleByID(String gameid, Channel ch) {

		AGameSession game = GameListByID.get(gameid);

		return game.getCurAngle(ch);

	}

	public synchronized Position getPositionByChannel(Channel ch) {

		AGameSession game = GameListByCH1.get(ch);
		if (game == null)
			game = GameListByCH2.get(ch);

		if (game != null)
			return game.getCurPos(ch);
		else
			return null;

	}

	public synchronized String getGameIDByChannel(Channel ch) {

		AGameSession game = GameListByCH1.get(ch);

		if (game == null) {
			game = GameListByCH2.get(ch);

		}

		if (game != null)
			return game.getGameid();
		else
			return null;

	}

	public synchronized boolean isMainPlayer(Channel ch) {

		AGameSession game = GameListByCH1.get(ch);

		if (game != null) {
			return true;

		} else
			return false;

	}

	public synchronized void deleteGameByMain(Channel ch) {

		AGameSession game = GameListByCH1.get(ch);
		Channel ch2 = game.getC2();
		String gid = game.getGameid();

		GameListByCH1.remove(ch);
		GameListByCH2.remove(ch2);
		GameListByID.remove(gid);

		game = null;

	}

	public synchronized void setNullSecond(Channel ch) {
		AGameSession game = GameListByCH2.get(ch);
		if (game != null) {
			GameListByCH2.remove(ch);
			game.setC2(null);
			game.channels.remove(ch);

		}

	}

	public synchronized ArrayList getAllWaiting() {
		ArrayList al = new ArrayList();

		Iterator iter = GameListByID.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			AGameSession game = (AGameSession) entry.getValue();

			if (game.getC2() == null) {
				al.add(key);

			}

		}

		return al;

	}

	public synchronized Position move(String gameid, Channel ch, int distance) {
		AGameSession game = GameListByID.get(gameid);
		Position p = null;
		if (game != null) {
			p = game.Move(ch, distance);

		}

		return p;
	}

	public synchronized int rotate(String gameid, Channel ch, int r) {
		AGameSession game = GameListByID.get(gameid);

		if (game != null) {
			return game.rotate(ch, r);

		}

		return 0;
	}

	public synchronized int getPartNum(String gameid, Channel ch) {
		AGameSession game = GameListByID.get(gameid);

		if (game != null) {
			return game.getGamePart(ch);

		}

		return 0;
	}
	
	public synchronized int sendMessageForTwo(String gameid, String msg) {
		AGameSession game = GameListByID.get(gameid);

		if (game != null) {
			return game.sendBothMessage(msg);

		}

		return 0;
	}
	
	public synchronized Position getMainPostion(String gameid)
	{
		AGameSession game = GameListByID.get(gameid);
		
		if (game != null)
		{
			return game.P1;
		}
		
		return null;
		
	}
	
	public synchronized int getMainAngle(String gameid)
	{
		AGameSession game = GameListByID.get(gameid);
		
		if (game != null)
		{
			return game.angle1;
			
		}
			
		return 0;
		
		
	}
	
	public synchronized fireInfo fire(String gameid, Channel ch) {
		AGameSession game = GameListByID.get(gameid);

		if (game != null) {
			return game.fire(ch);

		}

		return null;
	}
	

}
