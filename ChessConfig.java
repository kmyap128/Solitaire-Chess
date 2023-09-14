package puzzles.chess.model;

import puzzles.common.Coordinates;
import puzzles.common.solver.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 *  * the full representation of a configuration in the Chess puzzle
 *
 * @author Kristin Yap
 */
public class ChessConfig implements Configuration {
    /** chess puzzle rows */
    private static int rows;
    /** chess puzzle cols */

    private static int cols;
    /** grid of chars representing the chess board */

    private char[][] grid;
    private final static char bishop = 'B';
    private final static char king = 'K';
    private final static char knight = 'N';
    private final static char pawn = 'P';
    private final static char queen = 'Q';
    private final static char rook = 'R';
    private final static char empty = '.';
    /** collection of coordinates of each piece */
    private static Collection<Coordinates> pieces;

    /**
     * initial chess configuration
     *
     * @param filename chess file
     * @throws IOException if file not found
     */
    public ChessConfig(String filename) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            String line = in.readLine();
            String[] dim = line.split("\\s+");
            rows = Integer.parseInt(dim[0]);
            cols = Integer.parseInt(dim[1]);
            grid = new char[rows][cols];
            pieces = new ArrayList<>();
            for(int row = 0; row < rows; row++){
                String line2 = in.readLine();
                String[] chars = line2.split("\\s+");
                int col = 0;
                for(String ch: chars){
                    char spot = ch.charAt(0);
                    grid[row][col] = spot;
                    if(spot != '.'){
                        Coordinates piece = new Coordinates(row,col);
                        pieces.add(piece);
                    }
                    col += 1;
                }
            }
        }
    }

    /**
     * copy constructor for chess config
     *
     * @param other previous chess config
     * @param original coordinates of the original piece
     * @param capture coordinates of the captured piece
     */
    public ChessConfig(ChessConfig other, Coordinates original, Coordinates capture){
        rows = other.getRows();
        cols = other.getCols();
        this.grid = new char[rows][cols];
        for (int row = 0; row < rows; row++) {
            System.arraycopy(other.grid[row], 0, this.grid[row], 0, cols);
        }
        char temp = grid[original.row()][original.col()];
        grid[original.row()][original.col()] = empty;
        grid[capture.row()][capture.col()] = temp;
    }

    /**
     * gets the collection of piece coordinates
     *
     * @return collection of coordinates
     */
    public Collection<Coordinates> getPieces(){
        return pieces;
    }

    /** is the current config the solution? */
    @Override
    public boolean isSolution() {
        boolean win = false;
        int pieces = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (grid[row][col] != empty) {
                    pieces++;
                }
            }
        }
        if (pieces == 1) {
            win = true;
        }
        return win;
    }

    /**
     * is this config valid?
     *
     * @param row current row position
     * @param col current col position
     * @return true if valid, false otherwise
     */
    public boolean isValid(int row, int col){
        return grid[row][col] != empty;
    }

    /**
     * returns collection of neighbors for a config
     *
     * @return collection of configs
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        ArrayList<Configuration> neighbors = new ArrayList<>();
        for(Coordinates piece: pieces){
            int row = piece.row();
            int col = piece.col();
            // configs for a bishop piece
            if(grid[row][col] == bishop){
                makeBishop(neighbors, row, col);
            }
            // configs for a king piece
            else if(grid[row][col]  == king){
                makeKing(neighbors, row, col);
            }
            // configs for a knight piece
            else if(grid[row][col] == knight){
                makeKnight(neighbors, row, col);
            }
            // configs for a pawn piece
            else if(grid[row][col] == pawn){
                makePawn(neighbors, row, col);
            }
            // configs for a queen piece
            else if(grid[row][col] == queen){
                makeQueen(neighbors, row, col);
            }
            // configs for a rook piece
            else if(grid[row][col] == rook) {
                makeRook(neighbors, row, col);
            }
        }
        return neighbors;
    }

    /**
     * creates valid bishop configs
     *
     * @param neighbors collection of neighbors
     * @param row current row position
     * @param col current col position
     */
    private void makeBishop(Collection<Configuration> neighbors, int row, int col){
        Coordinates original = new Coordinates(row, col);
        // diagonal up left
        int currRow = row;
        int currCol = col;
        while(currRow > 0 && currCol > 0){
            currRow--;
            currCol--;
            if(isValid(currRow,currCol)){
                Coordinates capture = new Coordinates(currRow, currCol);
                ChessConfig bishop = new ChessConfig(this, original, capture);
                neighbors.add(bishop);
            }
        }
        // diagonal up right
        currRow = row;
        currCol = col;
        while(currRow > 0 && currCol < cols - 1){
            currRow--;
            currCol++;
            if(isValid(currRow,currCol)){
                Coordinates capture = new Coordinates(currRow, currCol);
                ChessConfig bishop = new ChessConfig(this, original, capture);
                neighbors.add(bishop);
            }
        }
        // diagonal down left
        currRow = row;
        currCol = col;
        while(currRow < rows - 1 && currCol > 0){
            currRow++;
            currCol--;
            if(isValid(currRow,currCol)){
                Coordinates capture = new Coordinates(currRow, currCol);
                ChessConfig bishop = new ChessConfig(this, original, capture);
                neighbors.add(bishop);
            }
        }
        // diagonal down right
        currRow = row;
        currCol = col;
        while(currRow < rows - 1 && currCol < cols - 1){
            currRow++;
            currCol++;
            if(isValid(currRow,currCol)){
                Coordinates capture = new Coordinates(currRow, currCol);
                ChessConfig bishop = new ChessConfig(this, original, capture);
                neighbors.add(bishop);
            }
        }
    }


    /**
     * creates valid king configs
     *
     * @param neighbors collection of neighbors
     * @param row current row position
     * @param col current col position
     */
    private void makeKing(Collection<Configuration> neighbors, int row, int col){
        Coordinates original = new Coordinates(row, col);
        if(row - 1 > -1){
            if(isValid(row-1, col)){
                Coordinates capture = new Coordinates(row - 1, col);
                ChessConfig king = new ChessConfig(this, original, capture);
                neighbors.add(king);
            }
            if(col - 1 > -1){
                if(isValid(row-1, col-1)){
                    Coordinates capture = new Coordinates(row - 1, col - 1);
                    ChessConfig king1 = new ChessConfig(this, original, capture);
                    neighbors.add(king1);
                }
            }
            if(col + 1 < cols){
                if(isValid(row-1,col+1)){
                    Coordinates capture = new Coordinates(row - 1, col + 1);
                    ChessConfig king1 = new ChessConfig(this, original, capture);
                    neighbors.add(king1);
                }
            }
        }
        if(row + 1 < rows){
            if(isValid(row+1,col)){
                Coordinates capture = new Coordinates(row + 1, col);
                ChessConfig king = new ChessConfig(this, original, capture);
                neighbors.add(king);
            }
            if(col - 1 > -1){
                if(isValid(row+1,col-1)){
                    Coordinates capture = new Coordinates(row + 1, col - 1);
                    ChessConfig king1 = new ChessConfig(this, original, capture);
                    neighbors.add(king1);
                }
            }
            if(col + 1 < cols) {
                if(isValid(row+1,col+1)){
                    Coordinates capture = new Coordinates(row + 1, col + 1);
                    ChessConfig king1 = new ChessConfig(this, original, capture);
                    neighbors.add(king1);
                }
            }
        }
        if(col - 1 > -1){
            if(isValid(row, col-1)){
                Coordinates capture = new Coordinates(row, col - 1);
                ChessConfig king = new ChessConfig(this, original, capture);
                neighbors.add(king);
            }
        }
        if(col + 1 < cols){
            if(isValid(row, col+1)){
                Coordinates capture = new Coordinates(row, col + 1);
                ChessConfig king = new ChessConfig(this, original, capture);
                neighbors.add(king);
            }
        }
    }

    /**
     * creates valid knight configs
     *
     * @param neighbors collection of neighbors
     * @param row current row position
     * @param col current col position
     */
    private void makeKnight(Collection<Configuration> neighbors, int row, int col){
        Coordinates original = new Coordinates(row, col);
        if(row - 2 > -1){
            if(col - 1 > -1){
                if(isValid(row-2,col-1)){
                    Coordinates capture = new Coordinates(row-2, col-1);
                    ChessConfig knight = new ChessConfig(this, original, capture);
                    neighbors.add(knight);
                }
            }
            if(col + 1 < cols){
                if(isValid(row-2,col+1)){
                    Coordinates capture = new Coordinates(row-2, col+1);
                    ChessConfig knight = new ChessConfig(this, original, capture);
                    neighbors.add(knight);
                }
            }
        }
        if(row + 2 < rows){
            if(col - 1 > -1){
                if(isValid(row+2,col-1)){
                    Coordinates capture = new Coordinates(row+2,col-1);
                    ChessConfig knight = new ChessConfig(this, original, capture);
                    neighbors.add(knight);
                }
            }
            if(col + 1 < cols){
                if(isValid(row+2,col+1)){
                    Coordinates capture = new Coordinates(row+2,col+1);
                    ChessConfig knight = new ChessConfig(this, original, capture);
                    neighbors.add(knight);
                }
            }
        }
        if(col - 2 > -1){
            if(row - 1 > -1){
                if(isValid(row-1,col-2)){
                    Coordinates capture = new Coordinates(row-1,col-2);
                    ChessConfig knight = new ChessConfig(this, original, capture);
                    neighbors.add(knight);
                }
            }
            if(row + 1 < rows){
                if(isValid(row+1,col-2)){
                    Coordinates capture = new Coordinates(row+1,col-2);
                    ChessConfig knight = new ChessConfig(this, original, capture);
                    neighbors.add(knight);
                }
            }
        }
        if(col + 2 < cols){
            if(row - 1 > -1){
                if(isValid(row-1,col+2)){
                    Coordinates capture = new Coordinates(row-1,col+2);
                    ChessConfig knight = new ChessConfig(this, original, capture);
                    neighbors.add(knight);
                }
            }
            if(row + 1 < rows){
                if(isValid(row+1,col+2)){
                    Coordinates capture = new Coordinates(row+1,col+2);
                    ChessConfig knight = new ChessConfig(this, original, capture);
                    neighbors.add(knight);
                }
            }
        }
    }

    /**
     * create valid pawn configs
     *
     * @param neighbors collection of neighbors
     * @param row current row position
     * @param col current col position
     */
    private void makePawn(Collection<Configuration> neighbors, int row, int col){
        Coordinates original = new Coordinates(row, col);
        if(row - 1 > -1){
            if(col - 1 > -1){
                if(isValid(row-1, col-1)){
                    Coordinates capture = new Coordinates(row-1, col-1);
                    ChessConfig pawn = new ChessConfig(this, original, capture);
                    neighbors.add(pawn);
                }
            }
            if(col + 1 < cols){
                if(isValid(row-1, col+1)){
                    Coordinates capture = new Coordinates(row-1, col+1);
                    ChessConfig pawn = new ChessConfig(this, original, capture);
                    neighbors.add(pawn);
                }
            }
        }
    }

    /**
     * creates valid queen configs
     *
     * @param neighbors collection of neighbors
     * @param row current row position
     * @param col current col position
     */
    private void makeQueen(Collection<Configuration> neighbors, int row, int col){
        int currRow = row;
        int currCol = col;
        Coordinates original = new Coordinates(row, col);
        // diagonal up left
        while(currRow > 0 && currCol > 0){
            currRow--;
            currCol--;
            if(isValid(currRow, currCol)){
                Coordinates capture = new Coordinates(currRow,currCol);
                ChessConfig queen = new ChessConfig(this, original, capture);
                neighbors.add(queen);
            }
        }
        // diagonal up right
        currRow = row;
        currCol = col;
        while(currRow > 0 && currCol < cols - 1){
            currRow--;
            currCol++;
            if(isValid(currRow, currCol)){
                Coordinates capture = new Coordinates(currRow,currCol);
                ChessConfig queen = new ChessConfig(this, original, capture);
                neighbors.add(queen);
            }
        }
        // diagonal down left
        currRow = row;
        currCol = col;
        while(currRow < rows - 1 && currCol > 0){
            currRow++;
            currCol--;
            if(isValid(currRow, currCol)){
                Coordinates capture = new Coordinates(currRow,currCol);
                ChessConfig queen = new ChessConfig(this, original, capture);
                neighbors.add(queen);
            }
        }
        // diagonal down right
        currRow = row;
        currCol = col;
        while(currRow < rows - 1 && currCol < cols - 1){
            currRow++;
            currCol++;
            if(isValid(currRow, currCol)){
                Coordinates capture = new Coordinates(currRow,currCol);
                ChessConfig queen = new ChessConfig(this, original, capture);
                neighbors.add(queen);
            }
        }
        currRow = row;
        currCol = col;
        while(currRow < rows - 1){
            currRow++;
            if(isValid(currRow, currCol)){
                Coordinates capture = new Coordinates(currRow,currCol);
                ChessConfig queen = new ChessConfig(this, original, capture);
                neighbors.add(queen);
            }
        }
        currRow = row;
        currCol = col;
        while(currRow > 0){
            currRow--;
            if(isValid(currRow, currCol)){
                Coordinates capture = new Coordinates(currRow,currCol);
                ChessConfig queen = new ChessConfig(this, original, capture);
                neighbors.add(queen);
            }
        }
        currRow = row;
        currCol = col;
        while(currCol < cols - 1){
            currCol++;
            if(isValid(currRow, currCol)){
                Coordinates capture = new Coordinates(currRow,currCol);
                ChessConfig queen = new ChessConfig(this, original, capture);
                neighbors.add(queen);
            }
        }
        currRow = row;
        currCol = col;
        while(currCol > 0){
            currCol--;
            if(isValid(currRow, currCol)){
                Coordinates capture = new Coordinates(currRow,currCol);
                ChessConfig queen = new ChessConfig(this, original, capture);
                neighbors.add(queen);
            }
        }
    }

    /**
     * creates valid rook configs
     *
     * @param neighbors collection of neighbors
     * @param row current row position
     * @param col current col position
     */
    private void makeRook(Collection<Configuration> neighbors, int row, int col){
        int currRow = row;
        int currCol = col;
        Coordinates original = new Coordinates(row, col);
        while(currRow < rows - 1){
            currRow++;
            if(isValid(currRow, currCol)){
                Coordinates capture = new Coordinates(currRow, currCol);
                ChessConfig rook = new ChessConfig(this,original, capture);
                neighbors.add(rook);
            }
        }
        currRow = row;
        currCol = col;
        while(currRow > 0){
            currRow--;
            if(isValid(currRow, currCol)){
                Coordinates capture = new Coordinates(currRow, currCol);
                ChessConfig rook = new ChessConfig(this,original, capture);
                neighbors.add(rook);
            }
        }
        currRow = row;
        currCol = col;
        while(currCol < cols - 1){
            currCol++;
            if(isValid(currRow, currCol)){
                Coordinates capture = new Coordinates(currRow, currCol);
                ChessConfig rook = new ChessConfig(this,original, capture);
                neighbors.add(rook);
            }
        }
        currRow = row;
        currCol = col;
        while(currCol > 0){
            currCol--;
            if(isValid(currRow, currCol)){
                Coordinates capture = new Coordinates(currRow, currCol);
                ChessConfig rook = new ChessConfig(this,original, capture);
                neighbors.add(rook);
            }
        }
    }

    /**
     * gets char at position (row,col)
     *
     * @param row desired row position
     * @param col desired col position
     * @return char at position
     */
    public char getCell(int row, int col){
        return grid[row][col];
    }

    /**
     * sets char at position(row, col)
     *
     * @param row desired row position
     * @param col desired col position
     * @param pos char to set position to
     */
    public void setCell(int row, int col, char pos){
        grid[row][col] = pos;
    }

    /** return rows */
    public int getRows(){
        return rows;
    }

    /** return cols */
    public int getCols(){
        return cols;
    }

    /**
     * does this config board equal the other?
     *
     * @param other config board
     * @return true if the boards are equal, false otherwise
     */
    @Override
    public boolean equals(Object other){
        boolean result = true;
        if(other instanceof ChessConfig board){
            // checks if cells are identical among both configs
            for(int row = 0; row < rows; row++){
                for(int col = 0; col < cols; col++){
                    if(!(this.getCell(row, col) == board.getCell(row, col))){
                        result = false;
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * returns hashCode for the current grid
     *
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.grid);
    }

    /**
     * a string representation of the board
     *
     * @return string representation of chess board
     */
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        for(int row = 0; row < rows; row++){
            for(int col =0; col < cols; col++){
                if(col != cols -1){
                    result.append(getCell(row, col)).append(" ");
                }
                else{
                    result.append(getCell(row, col)).append(System.lineSeparator());
                }
            }
        }
        return result.toString();
    }
}
