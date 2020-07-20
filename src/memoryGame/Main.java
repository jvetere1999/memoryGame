/** Main display class
 * @author Jacob Vetere
 */
package memoryGame;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Main extends Application {

    private final String [][] valueBoard = new String[4][4];//Holder of the values on board

    private final int[] colRowMem = new int[4];/*Index 0 and 1 = formerRow and FormerCol
                                           Index 2 and 3 = currRow and currCol*/

    private final int[] trackerVars = {9, 0, 0};/*Index 0 bounds
                                            Index 1 counts pairs
                                            Index 2 Counts number of tries*/

    private String dirName = "Images";//default folder (Relative file location inside of package)

    private boolean check = false;//For switching between which functions run
    private boolean isWrong = false;//For reseting block colors on next set sellection

    private final GridPane mainGrid = new GridPane();//Public grid plane to allow for updating of nodes outside of main

    private Label countTrack;//public label for updating text outside of main
    private Label pairs;//public label for updating text outside of main
    private Label endGame;//public label for updating text outside of main

    private final Font timesLabel = new Font( "Times New Roman",100);// Multi use font
    private final Font timesLabelSmall = new Font( "Times New Roman",30);// Multi use font

    private final int UI_COL = 6;//Column for the UI to be centered along
    private static int type = 1;//1(Numbers), 2(Letters) or 3(memoryGame.Images)

    //Initialization function abstract form Application
    @Override
    public void start(Stage primaryStage) throws Exception{
        System.out.println(getClass().getResource("Images"));
        fillMain();

        IOfillOut();

        mainGrid.add(makeNewLableText("Memory Type",200, 20), UI_COL +1,0);
        mainGrid.add(util.createRadio(), UI_COL +1,1);

        Scene scene = new Scene(mainGrid, 1550,850);

        primaryStage.setTitle("Memory Game");

        primaryStage.setScene(scene);

        primaryStage.show();
    }

    //Helper to create new game button and corresponding event
    public Button newGameButton(){

        Button newGame = new Button("New Game");

        newGame.setMaxWidth(200);
        newGame.setMaxHeight(200);

        newGame.setFont(timesLabelSmall);

        newGame.setOnAction(actionEvent -> newGameButtonEvent());

        return newGame;
    }

    public void newGameButtonEvent(){/*Resets and refills GridPlane at end of game
                                       Also resets all tracker values and row vals*/
            mainGrid.getChildren().clear();
            try {

                fillMain();

            } catch (IOException e) {

                e.printStackTrace();

            }

            IOfillOut();

            mainGrid.add(makeNewLableText("Memory Type",200, 20), UI_COL +1,0);
            mainGrid.add(util.createRadio(), UI_COL +1,1);

            check = false;

            colRowMem[0] = 0;
            colRowMem[1] = 0;
            trackerVars[1] = 0;
            trackerVars[2] = 0;

            switch (type){
                case 1:
                    endGame.setText("New Number Memory Game");
                    break;

                case 2:
                    endGame.setText("New Character Memory Game");
                    break;

                case 3:
                    endGame.setText("New Image Memory Game");
                    break;
            }
            countTrack.setText("0");
            pairs.setText("0");
    }
    //Helper to set type from util
    public static void setType(int newType){
        type=newType;
    }
    //Function for creating reset button and corasponding event handler
    public Button resetBoard(){

        Button resetBoard = new Button("Reset Board");

        resetBoard.setMaxWidth(200);
        resetBoard.setMaxHeight(200);

        resetBoard.setFont(timesLabelSmall);
        
        resetBoard.setOnAction(actionEvent -> {
            mainGridFill();

            check = false;

            colRowMem[0] = 0;
            colRowMem[1] = 0;
            trackerVars[1] = 0;

            pairs.setText("0");
        });
        return resetBoard;
    }
    //Function and event handler for type of question selected

    //Fills out the new game reset game and labels for tries and pairs found
    public void IOfillOut(){

        ioLables();

        Button newGame = newGameButton();
        Button resetGame = resetBoard();

        mainGrid.add(newGame, UI_COL,3);
        mainGrid.add(resetGame, UI_COL -1,3);

        endGame = makeNewLableText("Blue   = non-solved number\n" +
                "Red    = a chosen square\n" +
                "Yellow = a found pair",400,20);

        ioDisplayType();

        mainGrid.add(endGame, UI_COL -1, 0);
        mainGrid.setPadding(new Insets(10, 10, 10, 10));

    }
    //Helper to create new lables
    public void ioLables(){
        countTrack = makeNewLableText(Integer.toString(trackerVars[2]),200,25);
        pairs = makeNewLableText(Integer.toString(trackerVars[1]),200,25);

        mainGrid.add(makeNewLableText("Number of tries ",200,20), UI_COL -1,1);
        mainGrid.add(countTrack, UI_COL,1);
        mainGrid.add(makeNewLableText("Number of pairs found ",200,20), UI_COL -1,2);
        mainGrid.add(pairs, UI_COL,2);
    }
    //Chooses what Type of IO to display based on type
    public void ioDisplayType(){
        if(type == 1) {

            mainGrid.add(makeNewLableText("Change bound of Numbers\n" +
                    "Press enter to change\n" +
                    "Values under 8 will be ignored\n" +
                    "Default to 8 (Curr = "+(trackerVars[0]-1)+")", 200, 16), UI_COL + 1, 2);
            mainGrid.add(interactiveTextField(true,"Set Upper Bound "), UI_COL + 1, 3);
        }

        else if(type == 3){

            mainGrid.add(makeNewLableText("Enter Folder Name\n" +
                    "Current: \n"+dirName, 300, 18), UI_COL + 1, 2);
            mainGrid.add(interactiveTextField(false, "Set Folder"), UI_COL + 1, 3);

        }
    }
    //Interactive text field with event for clearing initial text and changing value uppon enter
    public TextField interactiveTextField(boolean bounds, String iniText){

        TextField newField = new TextField(iniText);
        newField.setOnMousePressed(event -> newField.clear());
        newField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER))
            {
                if (bounds) {
                    int val = Integer.parseInt(newField.getText());

                    trackerVars[0] = val >= 8 ? val + 1 : 9;
                }
                else{
                    dirName = newField.getText();
                }
            }
        });

        return newField;
    }
    //Creates a text field with an event on key event of enter to change String for directory
    //Fills the main 2d array with the variables from previous established functions depending on the radio button
    public void fillMain() throws IOException {

        Random rnd = new Random();
        ArrayList<String> valList;

        if (type == 1) {

            valList = fillDataInt();

        }
        else if (type == 2){

            valList = fillDataString();

        }else{
            valList = fillDataImage();
        }

        for(int row = 0; row < valueBoard.length; row++){

            for(int col = 0; col < valueBoard[row].length; col++){

                int valIndex = rnd.nextInt(valList.size());

                valueBoard[row][col] = valList.get(valIndex);

                valList.remove(valIndex);
            }
        }
        mainGridFill();
    }
    //Fills grid plane with event cyan rectangles
    public void mainGridFill(){

        mainGrid.setHgap(10);
        mainGrid.setVgap(10);

        //Itterates through the 2d grid array using a nested for loop
        for(int row = 0; row < valueBoard.length; row++) {

            for (int col = 0; col < valueBoard[row].length; col++) {

                Rectangle rtr = makeNewEvent(Color.CYAN);
                mainGrid.add(rtr,col,row);

            }
        }
    }
    //Checks for a match between global values of formerRow and Former column verses passed in row and col
    public boolean checkForMatch(int row, int col){

        if(valueBoard[colRowMem[0]][colRowMem[1]].equals(valueBoard[row][col])){

            trackerVars[1]++;
            pairs.setText(Integer.toString(trackerVars[1]));

            return true;
        }

        return false;

    }
    //Function used to fill an array list with random orders of variables in the type of letters
    //(Uses rand to genertate a random number between 0 and bounds)
    public ArrayList<String> fillDataInt(){

        Random rnd = new Random();

        ArrayList<String> rtr_arrList_Vals = new ArrayList<>();

        for (int x=0; x<8;x++){
            //random number based on either default of 9 non inclusive or user input value
            String hidden=Integer.toString(rnd.nextInt(trackerVars[0]));

            while(rtr_arrList_Vals.contains(hidden)){

                hidden=Integer.toString(rnd.nextInt(trackerVars[0]));

            }

            rtr_arrList_Vals.add(hidden);
            rtr_arrList_Vals.add(hidden);

        }

        return rtr_arrList_Vals;
    }
    //Function used to fill an array list with random orders of variables in the type of letters
    //(Uses rand with a bounds of 26 offset by 65 translated to ASCII to get letters)
    public ArrayList<String> fillDataString(){


        Random rnd = new Random();

        ArrayList<String> rtr_arrList_Vals = new ArrayList<>();

        for (int x = 0; x < 8; x++){

            String hidden = Character.toString((char) (rnd.nextInt(26)+65));

            while( rtr_arrList_Vals.contains(hidden )){

                hidden = Character.toString((char) (rnd.nextInt(26)+65));

            }

            rtr_arrList_Vals.add(hidden);
            rtr_arrList_Vals.add(hidden);
        }

        return rtr_arrList_Vals;
    }
    //Calls helper class util to gather data from sent directory
    public ArrayList<String> fillDataImage() throws IOException {

        Random rnd = new Random();

        URL url = getClass().getResource(dirName);
        File dir = new File(url.getPath());

        ArrayList<String> rtr_arrList_Images= util.getImages(dir);

        Collections.shuffle(rtr_arrList_Images,rnd);

        return rtr_arrList_Images;
    }
    //Creates Rectangle with an event while taking in a color to set the block to
    public Rectangle makeNewEvent(Paint color){

        Rectangle rtr_Event_Rectangle = util.makeNewRec(color);


        //Event for handling mouse clicks for several situations

        rtr_Event_Rectangle.setOnMouseClicked(event -> {//This event handles the vast majority of logic through out the game
            eventLogic(rtr_Event_Rectangle);
        });
        return rtr_Event_Rectangle;
    }
    //Helper to create rectangles of an input color with no event
    public Rectangle makeNewNoEvent(Paint color) {

        return util.makeNewRec(color);
    }
    //Helper used to make new label of a specfic member of main board
    public Label makeNewLable(int row,int col){

        Label rtr_Label = new Label(valueBoard[row][col]);

        rtr_Label.setFont(timesLabel);

        rtr_Label.setMaxWidth(200);

        rtr_Label.setAlignment(Pos.CENTER);

        return rtr_Label;
    }

    //Helper to create UI labels with input of message width and font size for fine tuning
    public Label makeNewLableText (String display, int width, int FontSize){

        Font timesLabelText =new Font( "Times New Roman",FontSize);

        Label rtr_Label = new Label(display);

        rtr_Label.setFont(timesLabelText);

        rtr_Label.setMaxWidth(width);

        rtr_Label.setAlignment(Pos.CENTER);

        return rtr_Label;
    }

    public void eventLogic(Rectangle rtr_Event_Rectangle){
        int rows = GridPane.getRowIndex(rtr_Event_Rectangle);
        int column = GridPane.getColumnIndex(rtr_Event_Rectangle);

        //Upon next mouse even set the last two blocks to Cyan
        if(isWrong){ isWrongLogic();}

        mainGrid.add(makeNewNoEvent(Color.RED), column, rows);

        //Call to logic helper class
        if(!check) { noCheck(rows, column); }

        //If second selection check if the value held in the corasponding blocks of mainGrid
        else {
            trackerVars[2]++;//Iterates trys
            countTrack.setText(Integer.toString(trackerVars[2]));//Sets text for tries

            //Checking for matching values if so set corasponding nodes to yellow eventless
            if(checkForMatch(rows, column)) {
                if (type == 3){ imageCheck(rows,column);}

                else {elseCheck(rows,column);}
            }

            //if not set second selection to show number and to red
            else{

                if (type == 3){  notItImages(rows,  column);}

                else {
                    Label num = makeNewLable(rows, column);
                    mainGrid.add(num, column, rows);
                    num.requestFocus();
                }

                colRowMem[2] = rows ;
                colRowMem[3] = column;
                isWrong = true;
            }

            // forces first if to run
            check=false;

            // if max number of pairs is reached display end message
            if(trackerVars[1]==8){

                endGame.setText("You have found every Pair");

            }

        }

    }
    public void isWrongLogic(){
        mainGrid.add(makeNewEvent(Color.CYAN), colRowMem[3] , colRowMem[2] );
        mainGrid.add(makeNewEvent(Color.CYAN), colRowMem[1], colRowMem[0]);

        isWrong = false;
    }
    public void noCheck(int rows, int column){
        colRowMem[0] = rows;
        colRowMem[1] = column;

        check=true;
        //If images is selected show images
        if (type == 3){

            Image image = new Image(new File(valueBoard[colRowMem[0]][colRowMem[1]]).toURI().toString());
            ImageView imageView = new ImageView(image);

            imageView.setFitHeight(200);
            imageView.setFitWidth(200);
            mainGrid.add(imageView,column,rows);
        }
        else {
            Label num = makeNewLable(colRowMem[0], colRowMem[1]);

            mainGrid.add(num, colRowMem[1], colRowMem[0]);

            num.requestFocus();
        }
    }
    public void imageCheck(int rows, int column){
        Image image = new Image(new File(valueBoard[colRowMem[0]][colRowMem[1]]).toURI().toString());
        ImageView imageView = new ImageView(image);

        imageView.setFitHeight(200);
        imageView.setFitWidth(200);

        mainGrid.add(imageView,column,rows);

        Image image2 = new Image(new File(valueBoard[rows][column]).toURI().toString());
        ImageView imageView2 = new ImageView(image2);

        imageView2.setFitHeight(200);
        imageView2.setFitWidth(200);

        mainGrid.add(imageView2,column,rows);
    }
    public void elseCheck(int rows, int column){
        mainGrid.add(makeNewNoEvent(Color.YELLOW), column, rows);
        mainGrid.add(makeNewNoEvent(Color.YELLOW), colRowMem[1], colRowMem[0]);

        Label hidden1 = makeNewLable(colRowMem[0], colRowMem[1]);
        Label hidden2 = makeNewLable(rows, column);

        mainGrid.add(hidden1, colRowMem[1], colRowMem[0]);
        hidden1.requestFocus();

        mainGrid.add(hidden2, column, rows);
        hidden2.requestFocus();
    }
    public void notItImages(int rows, int column){
        Image image = new Image(new File(valueBoard[rows][column]).toURI().toString());

        ImageView imageView = new ImageView(image);

        imageView.setFitHeight(200);
        imageView.setFitWidth(200);

        mainGrid.add(imageView,column,rows);
    }
    public static void main(String[] args) {
        launch(args);
    }
}
