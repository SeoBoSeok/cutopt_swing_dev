class Point {
	public double x;
	public double y;

	public Point(){
		this.x=0;
		this.y=0;
	}

	public Point(double x, double y){
		this.x = x;
		this.y = y;
	}

	public Point(Point pt){
		this.x = pt.x;
		this.y = pt.y;
	}
	public void set(Point pt){
		this.x = pt.x;
		this.y = pt.y;
	}
}