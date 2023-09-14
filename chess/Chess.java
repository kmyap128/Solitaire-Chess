package puzzles.chess.solver;

import puzzles.chess.model.ChessConfig;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.IOException;
import java.util.Collection;

/**
 * main program for chess
 *
 * @author Kristin Yap
 */
public class Chess {
    /**
     * solves for a chess puzzle
     *
     * @param start starting board
     */
    public static void solveChess(ChessConfig start){
        int totalConfigs = 1; // counts initial config
        int uniqueConfigs = 0;
        Collection<Configuration> path = Solver.getShortestPath(start);
        totalConfigs += Solver.getTotalConfigs();
        uniqueConfigs += Solver.getUniqueConfigs();
        System.out.println("Total Configs: " + totalConfigs);
        System.out.println("Unique Configs: " + uniqueConfigs);
        if(path.isEmpty()){
            System.out.println("No solution");
        }
        else{
            int steps = 0;
            for(Configuration board: path) {
                System.out.println("Step " + steps + ": " + "\n" + board.toString());
                steps++;
            }
        }
    }

    /**
     * the main method, runs chess solver
     *
     * @param args contains the chess file
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Chess filename");
        }
        else{
            try{
                ChessConfig init = new ChessConfig(args[0]);
                System.out.println("Initial Board:");
                System.out.println(init);
                solveChess(init);
            }
            catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        }
    }
}
