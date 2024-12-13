package ca.paint;

import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Parse a file in Version 1.0 PaintSaveFile format. An instance of this class
 * understands the paint save file format, storing information about
 * its effort to parse a file. After a successful parse, an instance
 * will have an ArrayList of PaintCommand suitable for rendering.
 * If there is an error in the parse, the instance stores information
 * about the error. For more on the format of Version 1.0 of the paint 
 * save file format, see the associated documentation.
 * 
 * @author 
 *
 */
public class PaintFileParser {
	private int lineNumber = 0; // the current line being parsed
	private String errorMessage =""; // error encountered during parse
	private PaintModel paintModel; 
	
	/**
	 * Below are Patterns used in parsing 
	 */
	// File
	private Pattern pFileStart=Pattern.compile("^PaintSaveFileVersion1.0$");
	private Pattern pFileEnd=Pattern.compile("^EndPaintSaveFile$");

	// Common
	private Pattern pShapeColor=Pattern.compile("^color:(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2}),(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2}),(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})$");
	private Pattern pShapeFilled=Pattern.compile("^filled:(true|false)$");
	private Pattern pShapePointsStart=Pattern.compile("^points$");
	private Pattern pShapePoints=Pattern.compile("point:[(](\\d{1,3}),(\\d{1,3})[)]$");
	private Pattern pShapePointsEnd=Pattern.compile("^endpoints$");

	// Circle
	private Pattern pCircleStart=Pattern.compile("^Circle$");
	private Pattern pCircleCenter=Pattern.compile("^center:[(](\\d{1,3}),(\\d{1,3})[)]$");
	private Pattern pCircleRadius=Pattern.compile("^radius:(\\d+)$");
	private Pattern pCircleEnd=Pattern.compile("^EndCircle$");

	// Rectangle
	private Pattern pRectangleStart=Pattern.compile("^Rectangle$");
	private Pattern pRectanglePoint1=Pattern.compile("^p1:[(](\\d{1,3}),(\\d{1,3})[)]$");
	private Pattern pRectanglePoint2=Pattern.compile("^p2:[(](\\d{1,3}),(\\d{1,3})[)]$");
	private Pattern pRectangleEnd=Pattern.compile("^EndRectangle$");

	// Squiggle
	private Pattern pSquiggleStart=Pattern.compile("^Squiggle$");
	private Pattern pSquiggleEnd=Pattern.compile("^EndSquiggle$");

	// Polyline
	private Pattern pPolylineStart=Pattern.compile("^Polyline$");
	private Pattern pPolylineEnd=Pattern.compile("^EndPolyline$");

	/**
	 * Store an appropriate error message in this, including 
	 * lineNumber where the error occurred.
	 * @param mesg
	 */
	private void error(String mesg){
		this.errorMessage = "Error in line "+lineNumber+" "+mesg;
		System.out.println(this.errorMessage);
	}
	
	/**
	 * 
	 * @return the error message resulting from an unsuccessful parse
	 */
	public String getErrorMessage(){
		return this.errorMessage;
	}

	/**
	 * Parse the specified file
	 * @param file
	 * @return
	 */
	public boolean parse(File file){
		boolean retVal = false;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			PaintModel pm = new PaintModel();
			retVal = this.parse(br, pm);
		} catch (FileNotFoundException e) {
			error("File Not Found: "+file.getName());
		} finally {
			try { br.close(); } catch (Exception e){};
		}
		return retVal;
	}

	/**
	 * Parse the specified inputStream as a Paint Save File Format file.
	 * @param inputStream
	 * @return
	 */
	public boolean parse(BufferedReader inputStream){
		PaintModel pm = new PaintModel();
		return this.parse(inputStream, pm);
	}

	/**
	 * Returns edited paintModel
	 * @return PaintModel
	 */
	public PaintModel getPaintModel(){
		return this.paintModel;
	}



	/**
	 * Parse the inputStream as a Paint Save File Format file.
	 * The result of the parse is stored as an ArrayList of Paint command.
	 * If the parse was not successful, this.errorMessage is appropriately
	 * set, with a useful error message.
	 * 
	 * @param inputStream the open file to parse
	 * @param paintModel the paint model to add the commands to
	 * @return whether the complete file was successfully parsed
	 */
	public boolean parse(BufferedReader inputStream, PaintModel paintModel) {
		this.paintModel = paintModel;
		this.errorMessage="";
		
		// During the parse, we will be building one of the 
		// following commands. As we parse the file, we modify 
		// the appropriate command.
		
		CircleCommand circleCommand = null; 
		RectangleCommand rectangleCommand = null;
		SquiggleCommand squiggleCommand = null;
		PolylineCommand polylineCommand = null;
	
		try {	
			int state=0; Matcher m; String l;
			
			this.lineNumber=0;
			while ((l = inputStream.readLine()) != null) {
				l = l.replaceAll("\\s+",""); // remove all spaces
				this.lineNumber++;
				if (l.isEmpty()) {
					continue;
				}
				System.out.println(lineNumber+" "+l+" "+state);
				switch(state){
					case 0:
						m=pFileStart.matcher(l);
						if(m.matches()){
							state=1;
							break;
						}
						error("Expected Start of Paint Save File");
						return false;
					case 1: // Looking for the start of a new object or end of the save file
						m=pCircleStart.matcher(l);
						if(m.matches()){
							circleCommand = new CircleCommand(new Point(0,0),0);
							state= 2;
							break;
						}
						m=pRectangleStart.matcher(l);
						if(m.matches()){
							rectangleCommand = new RectangleCommand(new Point(0,0),new Point(0,0));
							state = 7;
							break;
						}
						m=pSquiggleStart.matcher(l);
						if(m.matches()){
							squiggleCommand = new SquiggleCommand();
							state = 12;
							break;
						}
						m=pPolylineStart.matcher(l);
						if(m.matches()){
							polylineCommand = new PolylineCommand();
							state = 17;
							break;
						}
						m=pFileEnd.matcher(l);
						if (m.matches()){
							System.out.println("End of file reached.");
							state = 22;
							break;
						}
						error("Expected Start of Shape or End Paint Save File");
						return false;
					case 2:
						m = pShapeColor.matcher(l);
						if (m.matches()) {
							circleCommand.setColor(extractColor(m));
							state = 3;
							break;
						}
						error("Expected Circle color");
						return false;
					case 3:
						m = pShapeFilled.matcher(l);
						if (m.matches()) {
							circleCommand.setFill(Boolean.parseBoolean(m.group(1)));
							state = 4;
							break;
						}
						error("Expected Circle filled");
						return false;
					case 4:
						m = pCircleCenter.matcher(l);
						if (m.matches()) {
							circleCommand.setCentre(extractPoint(m));
							state = 5;
							break;
						}
						error("Expected Circle center");
						return false;
					case 5:
						m = pCircleRadius.matcher(l);
						if (m.matches()) {
							circleCommand.setRadius(Integer.parseInt(m.group(1)));
							state = 6;
							break;
						}
						error("Expected Circle Radius");
						return false;
					case 6:
						m = pCircleEnd.matcher(l);
						if (m.matches()) {
							paintModel.addCommand(circleCommand);
							circleCommand = null;
							state = 1;
							break;
						}
						error("Expected End Circle");
						return false;
					case 7:
						m = pShapeColor.matcher(l);
						if (m.matches()) {
							rectangleCommand.setColor(extractColor(m));
							state = 8;
							break;
						}
						error("Expected Rectangle color");
						return false;
					case 8:
						m = pShapeFilled.matcher(l);
						if (m.matches()) {
							rectangleCommand.setFill(Boolean.parseBoolean(m.group(1)));
							state = 9;
							break;
						}
						error("Expected Rectangle filled");
						return false;
					case 9:
						m = pRectanglePoint1.matcher(l);
						if (m.matches()) {
							rectangleCommand.setP1(extractPoint(m));
							state = 10;
							break;
						}
						error("Expected Rectangle P1");
						return false;
					case 10:
						m = pRectanglePoint2.matcher(l);
						if (m.matches()) {
							rectangleCommand.setP2(extractPoint(m));
							state = 11;
							break;
						}
						error("Expected Rectangle P2");
						return false;
					case 11:
						m = pRectangleEnd.matcher(l);
						if (m.matches()) {
							paintModel.addCommand(rectangleCommand);
							rectangleCommand = null;
							state = 1;
							break;
						}
						error("Expected End Rectangle");
						return false;
					case 12:
						m = pShapeColor.matcher(l);
						if (m.matches()) {
							squiggleCommand.setColor(extractColor(m));
							state = 13;
							break;
						}
						error("Expected Squiggle color");
						return false;
					case 13:
						m = pShapeFilled.matcher(l);
						if (m.matches()) {
							squiggleCommand.setFill(Boolean.parseBoolean(m.group(1)));
							state = 14;
							break;
						}
						error("Expected Squiggle filled");
						return false;
					case 14:
						m = pShapePointsStart.matcher(l);
						if (m.matches()) {
							state = 15;
							break;
						}
						error("Expected Squiggle points");
						return false;
					case 15:
						m = pShapePointsEnd.matcher(l);
						while(!m.matches()) {
							Matcher pointM = pShapePoints.matcher(l);

							if (pointM.matches()){
								squiggleCommand.add(extractPoint(pointM));
							}else {
								error("Expected Squiggle point or end points");
								return false;
							}

							l = inputStream.readLine();

							if (l == null) {
								error("Unexpected end of file");
								return false;
							}

							l = l.replaceAll("\\s+","");
							System.out.println(lineNumber+" "+l+" "+state);

							if (l.isEmpty()) {
								continue;
							}

							m = pShapePointsEnd.matcher(l);
							this.lineNumber++;
						}
						state = 16;
						break;
					case 16:
						m = pSquiggleEnd.matcher(l);
						if (m.matches()) {
							paintModel.addCommand(squiggleCommand);
							squiggleCommand = null;
							state = 1;
							break;
						}
						error("Expected End Squiggle");
						return false;
					case 17:
						m = pShapeColor.matcher(l);
						if (m.matches()) {
							polylineCommand.setColor(extractColor(m));
							state = 18;
							break;
						}
						error("Expected Polyline color");
						return false;
					case 18:
						m = pShapeFilled.matcher(l);
						if (m.matches()) {
							polylineCommand.setFill(Boolean.parseBoolean(m.group(1)));
							state = 19;
							break;
						}
						error("Expected Polyline filled");
						return false;
					case 19:
						m = pShapePointsStart.matcher(l);
						if (m.matches()) {
							state = 20;
							break;
						}
						error("Expected Polyline points");
						return false;
					case 20:
						m = pShapePointsEnd.matcher(l);
						while(!m.matches()) {
							Matcher pointM = pShapePoints.matcher(l);

							if (pointM.matches()){
								polylineCommand.add(extractPoint(pointM));
							}else {
								error("Expected Polyline point or end points");
								return false;
							}

							l = inputStream.readLine();

							if (l == null) {
								error("Unexpected end of file");
								return false;
							}

							l = l.replaceAll("\\s+","");
							System.out.println(lineNumber+" "+l+" "+state);

							if (l.isEmpty()) {
								continue;
							}

							m = pShapePointsEnd.matcher(l); // Re-check for the end of shape
							this.lineNumber++; // Increment the line number
						}
						state = 21;
						break;
					case 21:
						m = pPolylineEnd.matcher(l);
						if (m.matches()) {
							paintModel.addCommand(polylineCommand);
							polylineCommand = null;
							state = 1;
							break;
						}
						error("Expected End Polyline");
						return false;
					case 22:
						if (!l.isEmpty()) {
							error("Extra content after End of File");
							return false;
						}
						return true;
						/**
                             * I have around 20+/-5 cases in my FSM. If you have too many
                             * more or less, you are doing something wrong. Too few, and I bet I can find
                             * a bad file that you will say is good. Too many and you are not capturing the right concepts.
                             *
                             * Here are the errors I catch. All of these should be in your code.
                             *
							 	error("Expected Start of Paint Save File");
                                error("Expected Start of Shape or End Paint Save File");
                                error("Expected Circle color");
                                error("Expected Circle filled");
                                error("Expected Circle center");
                                error("Expected Circle Radius");
                                error("Expected End Circle");
                                error("Expected Rectangle color");
                                error("Expected Rectangle filled");
                                error("Expected Rectangle p1");
                                error("Expected Rectangle p2");
                                error("Expected End Rectangle");
                                error("Expected Squiggle color");
                                error("Expected Squiggle filled");
                                error("Expected Squiggle points");
                                error("Expected Squiggle point or end points");
                                error("Expected End Squiggle");
                                error("Expected Polyline color");
                                error("Expected Polyline filled");
                                error("Expected Polyline points");
                                error("Expected Polyline point or end points");
                                error("Expected End Polyline");
                                error("Extra content after End of File");

                                error("Unexpected end of file");
                             */
				}
			}
			if (state != 22) {
				error("Unexpected end of file");
			}
		}  catch (Exception e){
		}
		return true;
	}

	private Color extractColor(Matcher colorMatcher) {
		int r = Integer.parseInt(colorMatcher.group(1));
		int g = Integer.parseInt(colorMatcher.group(2));
		int b = Integer.parseInt(colorMatcher.group(3));
        return Color.rgb(r, g, b);
    }

	private Point extractPoint(Matcher pointMatcher){
		int x = Integer.parseInt(pointMatcher.group(1));
		int y = Integer.parseInt(pointMatcher.group(2));
		return new Point(x,y);
	}
}
