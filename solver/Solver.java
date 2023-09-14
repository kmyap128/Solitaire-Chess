package puzzles.common.solver;

import java.util.*;

/**
 * the common solver implementing BFS to find the shortest path
 * to a solution for a puzzle
 *
 * @author Kristin Yap
 */
public class Solver {
    /** the total number of configs */
    private static int totalConfigs;
    /** the total number of unique configs */
    private static int uniqueConfigs;
    private static Configuration solution;
    /**
     * finds the shortest path to the solution
     *
     * @param start the starting config
     * @return the sequence from start to end configs
     */
    public static Collection<Configuration> getShortestPath(Configuration start){
        Configuration startPoint = start;
        totalConfigs = 0;
        uniqueConfigs = 0;
        List<Configuration> queue = new LinkedList<>();
        queue.add(startPoint);
        Map<Configuration, Configuration> predecessors = new HashMap<>();
        predecessors.put(startPoint, startPoint);
        while (!queue.isEmpty()) {
            // the next node to process is at the front of the queue
            Configuration current = queue.remove(0);
            if (current.isSolution()) {
                solution = current;
                break;
            }
            // loop over all neighbors of current
            for (Configuration neighbor : current.getNeighbors()) {
                // process unvisited neighbors
                if(!predecessors.containsKey(neighbor)) {
                    predecessors.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
            totalConfigs += current.getNeighbors().size();
            uniqueConfigs = predecessors.size();
        }
        return constructPath(predecessors, startPoint, solution);
    }

    /**
     * method to return a path from the starting to ending location
     *
     * @param predecessors Map used to reconstruct the path
     * @param startPoint the starting config
     * @param endPoint the ending config
     * @return list containing the sequence of Configuration comprising the path,
     *          or an empty list if no path exists
     */
    private static List<Configuration> constructPath(Map<Configuration, Configuration> predecessors,
                                                     Configuration startPoint, Configuration endPoint){
        List<Configuration> path = new LinkedList<>();
        if(predecessors.containsKey(endPoint)) {
            Configuration currConfig = endPoint;
            while (currConfig != startPoint) {
                path.add(0, currConfig);
                currConfig = predecessors.get(currConfig);
            }
            path.add(0, startPoint);
        }
        return path;
    }

    public static Configuration getSolution(){
        return solution;
    }
    /**
     * returns the total number of configs
     *
     * @return total configs
     */
    public static int getTotalConfigs(){
        return totalConfigs;
    }

    /**
     * returns the number of unique configs
     *
     * @return unique configs
     */
    public static int getUniqueConfigs(){
        return uniqueConfigs;
    }
}
