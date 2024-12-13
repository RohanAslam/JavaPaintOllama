package ca.paint;

import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;

public class PolylineManipulatorStrategy extends ShapeManipulatorStrategy {
    PolylineManipulatorStrategy(PaintModel paintModel) {
        super(paintModel);
    }

    private PolylineCommand polylineCommand;
    private int curPoint = 0;

    @Override
    public void mousePressed(MouseEvent e) {
        MouseButton mouseButton = e.getButton();

        if (mouseButton == MouseButton.PRIMARY) {
            if (this.curPoint != 0) {
                // Update the existing polyline with a new point
                polylineCommand.add(new Point((int) e.getX(), (int) e.getY()));
                this.curPoint++;
            } else {
                // Start a new polyline
                Point origin = new Point((int) e.getX(), (int) e.getY());
                polylineCommand = new PolylineCommand();
                polylineCommand.add(origin); // Add the starting point
                this.addCommand(polylineCommand); // Add to PaintModel
                this.curPoint = 1;
            }
        } else if (mouseButton == MouseButton.SECONDARY) {
            // Mark polyline as finished
            this.curPoint = 0;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (this.curPoint != 0) {
            Point point = new Point((int) e.getX(), (int) e.getY());
            this.polylineCommand.set(point, this.curPoint - 1);
        }
    }
}
