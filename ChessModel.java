package puzzles.chess.model;

import puzzles.common.Coordinates;
import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.IOException;
import java.util.*;

/**
 * the model for a chess game
 *
 * @author Kristin Yap
 */
public class ChessModel {
    /** the collection of observers of this model */
    private final List<Observer<ChessModel, String>> observers = new LinkedList<>();

    /** the current configuration */
    private ChessConfig currentConfig;
    /** amount of rows on the board */
    private int rows;
    /** amount of cols on the board */
    private int cols;
    /** hashmap mapping coordinates to the type of piece */
    private HashMap<Coordinates, Character> pieces;
    /** the current loaded file */
    private String currentFile;
    /** the current selected position on the board */
    private static Coordinates selection;
    /** possible game states */
    public enum GameState{ONGOING, NO_SOLUTION, WON, HINT, INVALID_MOVE, SELECT_NEXT, CAPTURE, NEW, SOLVED}
    /** the game's current state */
    private static GameState gameState;
    private EnumMap<GameState, String> STATE_MSGS =
            new EnumMap<>(Map.of(
                    GameState.NEW, "Loaded: ",
                    GameState.WON, "You won!",
                    GameState.SOLVED, "Already solved!",
                    GameState.NO_SOLUTION, "No more valid moves",
                    GameState.ONGOING, "",
                    GameState.HINT, "Next move!",
                    GameState.INVALID_MOVE, "Invalid selection " ,
                    GameState.SELECT_NEXT, "Selected " ,
                    GameState.CAPTURE, "Captured "
            ));

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<ChessModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(String data) {
        for (var observer : observers) {
            observer.update(this, data);
        }
    }

    /**
     * reads in a file name and creates a new chess config from the file
     * initializes the current config in model
     *
     * @param filename chess file
     * @throws IOException
     */
    public ChessModel(String filename) throws IOException {
        currentFile = filename;
        currentConfig = new ChessConfig(filename);
        initializeCurrentConfig();
    }

    /** initializes private fields from current config */
    public void initializeCurrentConfig(){
        rows = currentConfig.getRows();
        cols = currentConfig.getCols();
        pieces = new HashMap<>();
        for(Coordinates piece: currentConfig.getPieces()){
            pieces.put(piece, currentConfig.getCell(piece.row(), piece.col()));
        }
    }

    /**
     * loads a new file
     *
     * @param filename name of file being read in
     */
    public void load(String filename){
        try {
            currentFile = filename;
            currentConfig = new ChessConfig(filename);
            initializeCurrentConfig();
            gameState = GameState.NEW;
            alertObservers(STATE_MSGS.get(gameState));
            gameState = GameState.ONGOING;
        } catch (IOException ioe){
            System.err.println("Cannot find file.");
        }
    }

    /** loads in the previously loaded file */
    public void reset() {
        load(currentFile);
    }

    /**
     * solves the rest of the puzzle from the current config
     * sets the current config to the next step in the path to the solution
     * if there is no solution from the current config, alert the user that
     * there are no more valid moves to get to the solution
     */
    public void hint(){
        LinkedList<Configuration> path = new LinkedList<>();
        for(Configuration config: Solver.getShortestPath(currentConfig)){
            path.add(config);
        }
        path.remove(0);
        Configuration goal = Solver.getSolution();
        if(goal == null){
            gameState = GameState.NO_SOLUTION;
            alertObservers(STATE_MSGS.get(gameState));
        }
        else{
            Configuration next = path.get(0);
            currentConfig = (ChessConfig) next;
            initializeCurrentConfig();
            if(next.equals(goal)){
                gameState = GameState.SOLVED;
                alertObservers(STATE_MSGS.get(gameState));
            }
            else{
                gameState = GameState.HINT;
                alertObservers(STATE_MSGS.get(gameState));
                gameState = GameState.ONGOING;
            }
        }
    }

    /**
     * selects the first piece
     *
     * @param piece1 coordinates of first selected piece
     * @return the coordinates of the selected piece if it is a valid selection
     */
    public Coordinates select1(Coordinates piece1){
        if(pieces.containsKey(piece1)){
            selection = piece1;
            gameState = GameState.SELECT_NEXT;
            alertObservers(STATE_MSGS.get(gameState));
            return selection;
        }
        else{
            gameState = GameState.INVALID_MOVE;
            alertObservers(STATE_MSGS.get(gameState));
            gameState = GameState.ONGOING;
            return null;
        }
    }

    /**
     * gets the second selected piece and passes it into the select method
     *
     * @param piece2 coordinates of the second selected piece
     */
    public void select2(Coordinates piece2){
        select(selection, piece2);
    }

    /**
     * if the first and second selected pieces are both valid, perform a capture
     * otherwise, alert the user that the move is invalid
     *
     * @param piece1 coordinates of the first piece
     * @param piece2 coordinates of the second piece
     */
    public void select(Coordinates piece1, Coordinates piece2){
        if(pieces.containsKey(piece2) && !(piece1 == null)){
            Configuration move = new ChessConfig(currentConfig, piece1, piece2);
            boolean yurr = true;
            for(Configuration neighbor: currentConfig.getNeighbors()){
                if(move.equals(neighbor)){
                    currentConfig = (ChessConfig) move;
                    pieces.put(piece2, pieces.get(piece1));
                    pieces.remove(piece1);
                    if(pieces.size() == 1){
                        gameState = GameState.WON;
                        alertObservers(STATE_MSGS.get(gameState));
                    }
                    else{
                        gameState = GameState.CAPTURE;
                        alertObservers(STATE_MSGS.get(gameState));
                        gameState = GameState.ONGOING;
                    }
                    yurr = false;
                    break;
                }
            }
            if (yurr) {
                gameState = GameState.INVALID_MOVE;
                alertObservers(STATE_MSGS.get(gameState));
                gameState = GameState.ONGOING;
            }
        }
        else{
            gameState = GameState.INVALID_MOVE;
            alertObservers(STATE_MSGS.get(gameState));
            gameState = GameState.ONGOING;
        }
    }

    /** returns the current gamestate */
    public GameState gameState(){
        return gameState;
    }

    /** gets the hashmap mapping pieces to the type */
    public HashMap<Coordinates, Character> getPieces(){
        return pieces;
    }

    /** returns the current config */
    public ChessConfig getCurrentConfig(){
        return currentConfig;
    }

    /** returns rows */
    public int getRows(){
        return rows;
    }

    /** return cols */
    public int getCols(){
        return cols;
    }

    /** returns the string representation of the config */
    @Override
    public String toString(){
        return currentConfig.toString();
    }
}
