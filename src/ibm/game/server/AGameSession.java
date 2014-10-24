package ibm.game.server;
import java.util.*;

import io.netty.channel.Channel;

public class AGameSession {
    	
	static Random rd = new Random();
	final static int width = 1100;
	final static int height = 630;
	final static Constraint cons1= new Constraint(10,10,500,590);
	final static Constraint cons2= new Constraint(610,10,1080,590);
	final static int step = 3;
	final static int rotate = 10;
	

		
	final String gameid;
	
	Channel c1;
	Channel c2;
	
	Position P1 = new Position();
	Position P2 = new Position();
	
	int angle1 = 0;
	int angle2 = 180;
	
	
	public Channel getC1() {
		return c1;
	}

	public void setC1(Channel c1) {
		this.c1 = c1;
	}

	public Channel getC2() {
		return c2;
	}

	public void setC2(Channel c2) {
		this.c2 = c2;
	}

	public String getGameid() {
		return gameid;
	}

	public AGameSession()
	{
		gameid = "" + rd.nextInt(10000);
		
		P1.setX(rd.nextInt(160) + 10);
		P1.setY(rd.nextInt(500) + 50);
		
		P2.setX(rd.nextInt(160) + 610);
		P2.setY(rd.nextInt(500) + 50);
			
	}
	
	public Position getCurPos(Channel ch)
	{
		if (ch == c1)
		{
			return P1;
		}
		
		if (ch == c2){
			return P2;
		}
		
		return null;
		
	}
	
	public int getCurAngle(Channel ch)
	{
		if (ch == c1)
		{
			return angle1;
		}
		
		if (ch == c2){
			return angle2;
		}
		
		return 0;
		
	}
	
	public Position Move(Channel ch, int distance)
	{
		if (ch == c1)
		{
			int stepx = (int) Math.round(distance * Math.cos(angle1 * Math.PI / 180.0));
		    int stepy = (int) Math.round(distance * Math.sin(angle1 * Math.PI / 180.0));		
			if (cons1.inside(P1.getX()+stepx, P1.getY()+stepy))
			{
				
				P1.XStep(stepx);
				P1.YStep(stepy);
				
				return P1;
				
				
			}else
				return null;
			
			
			
			
		}
		
		
		if (ch == c2)
		{
			int stepx = (int) Math.round(distance * Math.cos(angle2 * Math.PI / 180.0));
		    int stepy = (int) Math.round(distance * Math.sin(angle2 * Math.PI / 180.0));		
			if (cons2.inside(P2.getX()+stepx, P2.getY()+stepy))
			{
				P2.XStep(stepx);
				P2.YStep(stepy);
				
				return P2;
				
			
			}else
				return null;
			
		}
		
		
		return null;
		
	}
	
	public int rotate(Channel ch, int r)
	{
		if (ch == c1)
		{
			 angle1 += r;
			
			
			 return angle1;
			
		}
		
		
		if (ch == c2)
		{
			angle2 += r;
			return angle2;
			
		}
		
		
		return 0;
		
	}
	
	public int getGamePart(Channel ch)
	{
		if (ch == c1)
		{
			 return 1;
			
		}
		
		
		if (ch == c2)
		{
			
			return 2;
			
		}
		
		return 0;
	}
	
	

}
