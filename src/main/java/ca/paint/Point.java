package ca.paint;

public class Point {
	int x, y; // Available to our package
	Point(int x, int y){
		this.x=x; this.y=y;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
