package ca.paint;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;

/**
 * Shape class for storing information about a polyline.
 */
public class PolylineCommand extends PaintCommand {
    private ArrayList<Point> points =  new ArrayList<Point>();
    private Point origin;


    /**
     * Add a new point to this polyline.
     *
     * @param origin a point
     */
    public void add(Point origin) {
        points.add(origin);
    }

    /**
     * Sets point at index.
     *
     * @param point a point
     * @param index valid index within the ArrayList points
     */
    public void set(Point point, int index) {
        points.set(index, point);
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Return an array of the points in this polyline.
     *
     * @return array of points in polyline
     */
    public ArrayList<Point> getPoints() {
        return this.points;
    }

    @Override
    public void execute(GraphicsContext g) {
        ArrayList<Point> points = this.getPoints();
        g.setStroke(this.getColor());
        if(points.size() > 1) {
            for (int i = 0; i < points.size() - 1; i++){
                g.strokeLine(points.get(i).x, points.get(i).y, points.get(i+ 1).x,
                        points.get(i+1).y);
            }
        }
    }

    public String toString() {
        String s = "";
        s += "Polyline" + "\n";

        s += super.toString();
        s+="\tpoints" + "\n";

        for (Point p: this.getPoints()){
            s+="\t\tpoint:" + p + "\n";
        }
        s+="\tend points" + "\n";
        s += "End Polyline";

        return s;
    }
}

