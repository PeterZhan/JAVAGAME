package ibm.game.server;

public class Position {
	
	
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
		int x;
		int y;
			
		
		public int XStep(int step)
		{
			
			x+=step;
			return x;
			
		}
		
		public int YStep(int step)
		{
			
			y+=step;
			return y;
			
		}

}
