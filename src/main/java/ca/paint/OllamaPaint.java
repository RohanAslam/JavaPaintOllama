package ca.paint;

public class OllamaPaint extends Ollama{
    String format = FileIO.readResourceFile("paintSaveFileFormat.txt");
    String modifyFormat = FileIO.readResourceFile("modifyFileExample.txt");

    public OllamaPaint(String host){
        super(host);
    }

    /**
     * Ask llama3 to generate a new Paint File based on the given prompt
     * @param prompt
     * @param outFileName name of new file to be created in users home directory
     */
    public void newFile(String prompt, String outFileName){
        String system="The answer to this question should be a paint file that follows the following crucial rules and styles: " +format;
        String fullPrompt ="Provide a valid paint save file. Do not preface your response with anything. Produce the new Paint File, based on the following prompt:" + prompt;
        String response = this.call(system, fullPrompt);
        FileIO.writeHomeFile(response, outFileName);
    }

    /**
     * Ask llama3 to generate a new Paint File based on a modification of inFileName and the prompt
     * @param prompt the user supplied prompt
     * @param inFileName the Paint File Format file to be read and modified to outFileName
     * @param outFileName name of new file to be created in users home directory
     */
    public void modifyFile(String prompt, String inFileName, String outFileName){
        String givenFile = FileIO.readHomeFile(inFileName);
        String system="The answer to this question should be a modified paint file that follows the following crucial rules and styles: " +format + "and the modify file rules here:" + modifyFormat;
        String fullPrompt ="Please output the Paint save file content only, no additional text and no negative integers at all. Given this paint file:" + givenFile +", modify it based on this prompt:" + prompt;
        String response = this.call(system, fullPrompt);
        FileIO.writeHomeFile(response, outFileName);
    }

    /**
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void newFile1(String outFileName) {
        String prompt = "Create a house using squares, circles and rectangles. Square for the base and main of the house. Triangle above the square for the roof of the house. Lastly a vertical rectangle on the left for the chimney.";
        String system="The answer to this question should be a paint file that follows the following crucial rules and styles: " +format;
        String fullPrompt ="Provide a valid paint save file. Do not preface your response with anything. Produce the new Paint File, based on the following prompt:" + prompt;
        String response = this.call(system, fullPrompt);
        FileIO.writeHomeFile(response, outFileName);
    }

    /**
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void newFile2(String outFileName) {
        String prompt = "Make a target with concentric circles, alternating between red and black, starting with a red circle of radius 50 in the middle of the canvas and increasing the radius by 20 for each circle.";
        String system="The answer to this question should be a paint file that follows the following crucial rules and styles: " +format;
        String fullPrompt ="Provide a valid paint save file. Do not preface your response with anything. Produce the new Paint File, based on the following prompt:" + prompt;
        String response = this.call(system, fullPrompt);
        FileIO.writeHomeFile(response, outFileName);
    }

    /**
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void newFile3(String outFileName) {
        String prompt = "Create the French Flag using three vertical rectangles that are all the same width and height and spanning the width of the canvas, in the order of and filled with Blue, White and Red.";
        String system="The answer to this question should be a paint file that follows the following crucial rules and styles: " +format;
        String fullPrompt ="Provide a valid paint save file. Do not preface your response with anything. Produce the new Paint File, based on the following prompt:" + prompt;
        String response = this.call(system, fullPrompt);
        FileIO.writeHomeFile(response, outFileName);
    }

    /**
     * @param inFileName the name of the source file in the users home directory
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void modifyFile1(String inFileName, String outFileName) {
        String givenFile = FileIO.readHomeFile(inFileName);
        String prompt = "Make all shapes smaller by 50%";
        String system="The answer to this question should be a modified paint file that follows the following crucial rules and styles: " +format + "and the modify file rules here:" + modifyFormat;
        String fullPrompt ="Please output the Paint save file content only, no additional text and no negative integers at all. Given a this paint file: "+ givenFile + " modify it based on this prompt:" + prompt;
        String response = this.call(system, fullPrompt);
        FileIO.writeHomeFile(response, outFileName);
    }

    /**
     * @param inFileName the name of the source file in the users home directory
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void modifyFile2(String inFileName, String outFileName) {
        String givenFile = FileIO.readHomeFile(inFileName);
        String prompt = "change all shapes color to pink";
        String system="The answer to this question should be a modified paint file that follows the following crucial rules and styles: " +format + "and the modify file rules here:" + modifyFormat;
        String fullPrompt ="Please output the Paint save file content only, no additional text and no negative integers at all. Given a this paint file: "+ givenFile + " modify it based on this prompt:" + prompt;
        String response = this.call(system, fullPrompt);
        FileIO.writeHomeFile(response, outFileName);    }
    /**
     * @param inFileName the name of the source file in the users home directory
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void modifyFile3(String inFileName, String outFileName) {
        String givenFile = FileIO.readHomeFile(inFileName);
        String prompt = "Replace all shapes with houses";
        String system="The answer to this question should be a modified paint file that follows the following crucial rules and styles: " +format + "and the modify file rules here:" + modifyFormat;
        String fullPrompt ="Please output the Paint save file content only, no additional text and no negative integers at all. Given a this paint file: "+ givenFile + " modify it based on this prompt:" + prompt;
        String response = this.call(system, fullPrompt);
        FileIO.writeHomeFile(response, outFileName);    }

    public static void main(String [] args){
        String prompt = null;

        prompt="Draw a 100 by 120 rectangle with 4 radius 5 circles at each rectangle corner.";
        OllamaPaint op = new OllamaPaint("dh2010pc12.utm.utoronto.ca"); // Replace this with your assigned Ollama server.

        prompt="Draw a 100 by 120 rectangle with 4 radius 5 circles at each rectangle corner.";
        op.newFile(prompt, "OllamaPaintFile1.txt");
        op.modifyFile("Remove all shapes except for the circles.","OllamaPaintFile1.txt", "OllamaPaintFile2.txt" );

        prompt="Draw 5 concentric circles with different colors.";
        op.newFile(prompt, "OllamaPaintFile3.txt");
        op.modifyFile("Change all circles into rectangles.", "OllamaPaintFile3.txt", "OllamaPaintFile4.txt" );

        prompt="Draw a polyline then two circles then a rectangle then 3 polylines all with different colors.";
        op.newFile(prompt, "OllamaPaintFile4.txt");

        prompt="Modify the following Paint Save File so that each circle is surrounded by a non-filled rectangle. ";
        op.modifyFile("Change all circles into rectangles.", "OllamaPaintFile4.txt", "OllamaPaintFile5.txt" );

        for(int i=1;i<=3;i++){
            op.newFile1("PaintFile1_"+i+".txt");
            op.newFile2("PaintFile2_"+i+".txt");
            op.newFile3("PaintFile3_"+i+".txt");
        }
        for(int i=1;i<=3;i++){
            for(int j=1;j<=3;j++) {
                op.modifyFile1("PaintFile"+ i +"_"+j+ ".txt", "PaintFile"+ i +"_"+j+"_1.txt");
                op.modifyFile2("PaintFile"+ i +"_"+j+ ".txt", "PaintFile"+ i +"_"+j+"_2.txt");
                op.modifyFile3("PaintFile"+ i +"_"+j+ ".txt", "PaintFile"+ i +"_"+j+"_3.txt");
            }
        }
    }
}
