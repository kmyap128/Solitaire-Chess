package puzzles.chess.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import puzzles.common.Coordinates;
import puzzles.common.Observer;
import puzzles.chess.model.ChessModel;
import puzzles.hoppers.model.HoppersModel;

import java.io.File;
import java.io.IOException;

/**
 * GUI implementation for chess
 *
 * @author Kristin Yap
 */
public class ChessGUI extends Application implements Observer<ChessModel, String> {
    private ChessModel model;

    /** The size of all icons, in square dimension */
    private final static int ICON_SIZE = 75;
    /** the font size for labels and buttons */
    private final static int FONT_SIZE = 12;

    private Stage stage;

    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";

    // for demonstration purposes
    private Image bishop = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"bishop.png"));
    private Image king = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"king.png"));
    private Image knight = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"knight.png"));
    private Image pawn = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"pawn.png"));
    private Image queen = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"queen.png"));
    private Image rook = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"rook.png"));
    /** 2d array of buttons representing the board */
    private Button[][] board;
    /** text at the top */
    private Label text;
    /** has the first piece been selected? */
    private boolean selection1;
    /** coordinates of piece 1 as a string */
    private String piece1;
    /** coordinates of piece 2 as a string */
    private String piece2;
    /** coordinates of the current selection */
    private Coordinates selection;
    /** current file name */
    private String filename;
    /** has the gui been initialized? */
    private boolean initialized;

    /** a definition of light and dark and for the button backgrounds */
    private static final Background LIGHT =
            new Background( new BackgroundFill(Color.WHITE, null, null));
    private static final Background DARK =
            new Background( new BackgroundFill(Color.MIDNIGHTBLUE, null, null));

    /**
     * initializes fields
     *
     * @throws IOException
     */
    @Override
    public void init() throws IOException {
        // get the file name from the command line
        filename = getParameters().getRaw().get(0);
        model = new ChessModel(filename);
        File f = new File(filename);
        filename = f.getName();
        model.addObserver(this);
    }

    /** sets top of border pane */
    public void setTop(BorderPane pane){
        text = new Label("Loaded: " + filename);
        text.setStyle( "-fx-font: 18px Menlo");
        text.setAlignment(Pos.CENTER);
        pane.setTop(text);
    }

    /** sets middle of border pane */
    public void setMiddle(BorderPane pane){
        GridPane game = new GridPane();
        boolean isBlue;
        board = new Button[model.getRows()][model.getCols()];
        for(int row = 0; row < model.getRows(); row++){
            if(row % 2 == 0){
                isBlue = false;
            }
            else{
                isBlue = true;
            }
            for(int col = 0; col < model.getCols(); col++){
                Coordinates selection = new Coordinates(row, col);
                board[row][col] = new Button();
                if(model.getPieces().containsKey(selection)){
                    char piece = model.getPieces().get(selection);
                    if(piece == 'B'){
                        board[row][col].setGraphic(new ImageView(bishop));
                    }
                    else if (piece == 'K') {
                        board[row][col].setGraphic(new ImageView(king));
                    }
                    else if(piece == 'N'){
                        board[row][col].setGraphic(new ImageView(knight));
                    }
                    else if(piece == 'P'){
                        board[row][col].setGraphic(new ImageView(pawn));
                    }
                    else if(piece == 'Q'){
                        board[row][col].setGraphic(new ImageView(queen));
                    }
                    else if(piece == 'R'){
                        board[row][col].setGraphic(new ImageView(rook));
                    }
                }
                board[row][col].setOnAction((event -> {
                    if(!selection1){
                        selection1 = true;
                        this.selection = selection;
                        piece1 = selection.toString();
                        model.select1(selection);
                    }
                    else {
                        this.selection = selection;
                        piece2 = selection.toString();
                        model.select2(selection);
                        selection1 = false;
                    }
                }));
                board[row][col].setMinSize(ICON_SIZE, ICON_SIZE);
                board[row][col].setMaxSize(ICON_SIZE, ICON_SIZE);
                if (isBlue) {
                    board[row][col].setBackground(DARK);
                    isBlue = false;
                }
                else{
                    board[row][col].setBackground(LIGHT);
                    isBlue = true;
                }
                game.add(board[row][col], col, row);
            }
        }
        pane.setCenter(game);
    }

    /** sets bottom of borderpane */
    public void setBottom(BorderPane pane){
        BorderPane bottom = new BorderPane();
        HBox buttons = new HBox();
        Button load = new Button("Load");
        load.setOnAction((event -> {
            fileChoose();
        }));
        buttons.getChildren().add(load);
        Button reset = new Button("Reset");
        reset.setOnAction((event -> {
            model.reset();
        }));
        buttons.getChildren().add(reset);
        Button hint = new Button("Hint");
        hint.setOnAction(event -> {
            model.hint();
        });
        buttons.getChildren().add(hint);
        bottom.setCenter(buttons);
        pane.setBottom(bottom);
    }

    /** opens file chooser */
    private void fileChoose(){
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load a game board.");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/data/chess"));
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*txt*"));
            File selectedFile = fileChooser.showOpenDialog(this.stage);
            filename = selectedFile.getName();
            this.model.load("data/chess/" + selectedFile.getName());
            start(stage);
        }
        catch(Exception e){
            update(model, "No file chosen.");
        }
    }

    /** sets the stage */
    @Override
    public void start(Stage stage) throws Exception {
        initialized = true;
        this.stage = stage;
        BorderPane pane = new BorderPane();
        setTop(pane);
        setMiddle(pane);
        setBottom(pane);
        Scene scene = new Scene(pane);
        stage.setTitle("Chess GUI");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * updates the view
     *
     * @param chessModel the object that wishes to inform this object
     *                about something that has happened.
     * @param msg optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(ChessModel chessModel, String msg) {
        if(!initialized){
            return;
        }
        for(int row = 0; row < model.getRows(); row++) {
            for (int col = 0; col < model.getCols(); col++) {
                char piece = model.getCurrentConfig().getCell(row, col);
                if(piece == 'B'){
                    board[row][col].setGraphic(new ImageView(bishop));
                }
                else if (piece == 'K') {
                    board[row][col].setGraphic(new ImageView(king));
                }
                else if(piece == 'N'){
                    board[row][col].setGraphic(new ImageView(knight));
                }
                else if(piece == 'P'){
                    board[row][col].setGraphic(new ImageView(pawn));
                }
                else if(piece == 'Q'){
                    board[row][col].setGraphic(new ImageView(queen));
                }
                else if(piece == 'R'){
                    board[row][col].setGraphic(new ImageView(rook));
                }
                else{
                    board[row][col].setGraphic(null);
                }
            }
        }
        this.stage.sizeToScene();  // when a different sized puzzle is loaded
        final ChessModel.GameState gameState = model.gameState();
        if(gameState == ChessModel.GameState.NEW){
            text.setText("Loaded: " + filename);
        }
        if(gameState == ChessModel.GameState.HINT){
            text.setText("Next move!");
        }
        if(gameState == ChessModel.GameState.SOLVED){
            text.setText("Already solved!");
        }
        if(gameState == ChessModel.GameState.NO_SOLUTION){
            text.setText("No more valid moves:(");
        }
        if(gameState == ChessModel.GameState.WON){
            text.setText("You won! :D");
        }
        if(gameState == ChessModel.GameState.INVALID_MOVE){
            text.setText("Invalid selection " + selection.toString());
        }
        if(gameState == ChessModel.GameState.SELECT_NEXT){
            text.setText("Selected " + piece1);
        }
        if(gameState == ChessModel.GameState.CAPTURE){
            text.setText("Captured " + piece2 + " from " + piece1 + "!");
        }
        if(gameState == ChessModel.GameState.ONGOING){
            text.setText(msg);
        }
    }

    /** launches the gui */
    public static void main(String[] args) {
        Application.launch(args);
    }
}
