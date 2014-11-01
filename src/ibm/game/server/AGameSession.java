package ibm.game.server;

import java.util.*;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class AGameSession {

	static Random rd = new Random();
	final static int width = 1100;
	final static int height = 630;
	static Constraint cons1 = new Constraint(0,0,0,0);
	static Constraint cons2 = new Constraint(0,0,0,0);
	public static void setCons1(Constraint cons1) {
		AGameSession.cons1 = cons1;
	}

	public static void setCons2(Constraint cons2) {
		AGameSession.cons2 = cons2;
	}


	final static int step = 3;
	final static int rotate = 10;

	final String gameid;

	Channel c1;
	Channel c2;

	Position P1 = new Position();
	Position P2 = new Position();

	int angle1 = 0;
	int angle2 = 180;
	
	
	int fuel1 = 30;
	int fuel2 = 30;
	
	
	final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	public Channel getC1() {
		return c1;
	}

	public void setC1(Channel c1) {
		this.c1 = c1;
		channels.add(c1);
	}

	public Channel getC2() {
		return c2;
	}

	public void setC2(Channel c2) {
		this.c2 = c2;
		channels.add(c2);
	}

	public String getGameid() {
		return gameid;
	}

	public AGameSession() {
		gameid = "" + rd.nextInt(10000);

		P1.setX(rd.nextInt(160) + 10);
		P1.setY(rd.nextInt(500) + 50);

		P2.setX(rd.nextInt(160) + 610);
		P2.setY(rd.nextInt(500) + 50);

	}

	public Position getCurPos(Channel ch) {
		if (ch == c1) {
			return P1;
		}

		if (ch == c2) {
			return P2;
		}

		return null;

	}

	public int getCurAngle(Channel ch) {
		if (ch == c1) {
			return angle1;
		}

		if (ch == c2) {
			return angle2;
		}

		return 0;

	}
	
	public boolean isCloseEnough(int x0, int y0, int x1, int y1)
	{
		
		int d = 42;
		
		int distance = (int)Math.round((Math.sqrt((x1-x0)*(x1-x0) + (y1-y0)*(y1-y0))));
		
		return distance <= d;
		
		
		
	}
	
	

	public Position Move(Channel ch, int distance) {
		if (ch == c1) {
			int stepx = (int) Math.round(distance
					* Math.cos(angle1 * Math.PI / 180.0));
			int stepy = (int) Math.round(distance
					* Math.sin(angle1 * Math.PI / 180.0));
			if ((c2 != null &&!isCloseEnough(P1.getX() + stepx, P1.getY() + stepy, P2.getX(), P2.getY())) && (cons1.inside(P1.getX() + stepx, P1.getY() + stepy))) {

				P1.XStep(stepx);
				P1.YStep(stepy);

				return P1;

			} else
				return null;

		}

		if (ch == c2) {
			int stepx = (int) Math.round(distance
					* Math.cos(angle2 * Math.PI / 180.0));
			int stepy = (int) Math.round(distance
					* Math.sin(angle2 * Math.PI / 180.0));
			if ((!isCloseEnough(P2.getX() + stepx, P2.getY() + stepy, P1.getX(), P1.getY()))  && (cons2.inside(P2.getX() + stepx, P2.getY() + stepy))) {
				P2.XStep(stepx);
				P2.YStep(stepy);

				return P2;

			} else
				return null;

		}

		return null;

	}

	public int rotate(Channel ch, int r) {
		if (ch == c1) {
			angle1 += r;
            angle1 %= 360;
			return angle1;

		}

		if (ch == c2) {
			angle2 += r;
			angle2 %= 360;
			return angle2;

		}

		return 0;

	}

	public int getGamePart(Channel ch) {
		if (ch == c1) {
			return 1;

		}

		if (ch == c2) {

			return 2;

		}

		return 0;
	}
	
	public int sendBothMessage(String msg)
	{
		for (Channel c: channels) {
            
			    System.out.println("Sending message " + msg + " to " + c.toString());
			
                c.writeAndFlush(msg);
            
        }	
		
		return 0;
	}
	
	public fireInfo fire(Channel ch){
		
		fireInfo fi = null;
		
		if (ch == c1 ) {
			
			fi = new fireInfo();
			fi.part = 1;
			fi.x0 = P1.x + (int) Math.round(21
					* Math.cos(angle1 * Math.PI / 180.0));
		    fi.y0 = P1.y + (int) Math.round(21
					* Math.sin(angle1 * Math.PI / 180.0)) + 5;
		    
		    
		 
		    double ra = angle1/180.0 * Math.PI;
		    fi.x1 = (int)Math.round(fi.x0 + (2000) * Math.cos(ra));
		    fi.y1 = (int)Math.round(fi.y0 + 5 + (2000) * Math.sin(ra));
			
		    
			if (c2 != null)
			{
			if (((P2.x - P1.x)*Math.cos(ra)>=0) && ((P2.y-P1.y)*Math.sin(ra)>=0))
		     	{
				
			   shoot(1, fi);
				
			   if (fi.targeted)
			   {
				   fuel2 --;
				   fi.fule = fuel2;
			   }
				
				  
			}
			
			}
			
			
			
			
			

		}

		if (ch == c2) {
			
			fi = new fireInfo();
			fi.part = 2;
			fi.x0 = P2.x + (int) Math.round(21
					* Math.cos(angle2 * Math.PI / 180.0));
		    fi.y0 = P2.y + (int) Math.round(21
					* Math.sin(angle2 * Math.PI / 180.0)) + 5;
		    
		    
		    
		    
		    double ra = angle2/180.0 * Math.PI;
		    fi.x1 = (int)Math.round(fi.x0 + (2000) * Math.cos(ra));
		    fi.y1 = (int)Math.round(fi.y0 + 5 + (2000) * Math.sin(ra));
			
		    if (((P1.x - P2.x)*Math.cos(ra)>=0) && ((P1.y-P2.y)*Math.sin(ra)>=0))
			{
				
				
				shoot(2, fi);
				
				if (fi.targeted)
				{
					   fuel1 --;
					   fi.fule = fuel1;
				}
				
			}
			
			
			
			

		}
		
		return fi;
		
		
	}
	
	
	public int getFuel1() {
		return fuel1;
	}

	public void setFuel1(int fuel1) {
		this.fuel1 = fuel1;
	}

	public int getFuel2() {
		return fuel2;
	}

	public void setFuel2(int fuel2) {
		this.fuel2 = fuel2;
	}

	public void shoot(int part, fireInfo fi)
	{
		
		if (part == 1)
		{
			
			distance(fi, angle1, P2);
			
			
			
			
		}
		
		
		if (part == 2)
		{
			
			distance(fi, angle2, P1);
			
			
			
			
		}
		
		
	}
	
	
	public void distance(fireInfo fi, int angle, Position target)
	{
		//double d = Math.sqrt((target.x - fi.x0)*(target.x - fi.x0) +
		//		          (target.y - fi.y0)*(target.y - fi.y0));
		
		double ra = angle/180.0 * Math.PI;
		
		double a = Math.sin(ra);
		double b = -Math.cos(ra);
		double c= fi.y0 * Math.cos(ra) - fi.x0 * Math.sin(ra);
		
		double d = Math.abs(a*target.x + b*target.y + c) / Math.sqrt(a*a + b*b);
		
		
		if (d<=30)
		{
			fi.targeted = true;
			fi.x1 = target.x; //(int) Math.round((-b*b*c -a*b*b*fi.x0-a*a*b*fi.y0)/(a*a- b*b)/a - c/a);
			fi.y1 = target.y; //(int) Math.round((b*c + a*b*fi.x0 + a * a * fi.y0)/(a*a - b*b)); 
		
		
		}
		
		
	}
	
	

}
