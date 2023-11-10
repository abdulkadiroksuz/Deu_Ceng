import ADTPackage.LinkedListWithIterator;
import ADTPackage.LinkedStack;
import ADTPackage.QueueInterface;
import ADTPackage.StackInterface;
import GraphPackage.UndirectedGraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;

public class Test {
    public static char[][] maze;
    private static final String MAZE_NAME = "maze1.txt";

    public static void main(String[] args) throws Exception {
        UndirectedGraph<String> mazeSolver = new UndirectedGraph<>();

        readMaze(MAZE_NAME);
        insertToGraph(mazeSolver); // adds vertices to graph structure
        mazeSolver.connectEdges(); // connects the edges between vertices

        mazeSolver.displayEdges();
        mazeSolver.printAdjacencyMatrix();
        System.out.println("The number of edges found: "+mazeSolver.getNumberOfEdges());

        QueueInterface<String> bfsQueue = mazeSolver.getBreadthFirstSearch("0-1", String.format("%d-%d",maze.length-2 ,maze[0].length-1));
        System.out.println("BFS");
        int visitCount = printQueues(bfsQueue);
        System.out.println("The number of visited vertices for BFS: "+visitCount+"\n");


        QueueInterface<String> dfsQueue = mazeSolver.getDepthFirstSearch("0-1", String.format("%d-%d",maze.length-2 ,maze[0].length-1));
        System.out.println("DFS");
        visitCount = printQueues(dfsQueue);
        System.out.println("The number of visited vertices for DFS: "+visitCount+"\n");

        StackInterface<String> pathOfShortest = new LinkedStack<>();
        visitCount  = mazeSolver.getShortestPath("0-1", String.format("%d-%d",maze.length-2 ,maze[0].length-1), pathOfShortest);
        System.out.println("The Shortest Path");
        printStacks(pathOfShortest);
        System.out.println("The number of visited vertices for Shortest Path Algorithm: "+(visitCount+1)+"\n");

        StackInterface<String> pathOfCheapest = new LinkedStack<>();
        double pathCost = mazeSolver.getCheapestPath("0-1", String.format("%d-%d",maze.length-2 ,maze[0].length-1),pathOfCheapest);
        System.out.println("The Cheapest Path");
        visitCount = printStacks(pathOfCheapest);
        System.out.println("The number of visited vertices for Cheapest Path Algorithm: "+visitCount);
        System.out.println("The cost of the cheapest path: "+ pathCost);

    }

    public static int printQueues(QueueInterface<String> queue) throws Exception {
        //necessary method for print operations for queue structure

        int visitedVerticesNumber = 0;
        while (!queue.isEmpty()) {
            String dim = queue.dequeue();
            String[] cor = dim.split("-");
            maze[Integer.parseInt(cor[0])] [Integer.parseInt(cor[1])] = '.';
            visitedVerticesNumber++;
        }
        printMaze(maze);
        readMaze(MAZE_NAME);

        return visitedVerticesNumber;
    }

    public static int printStacks(StackInterface<String> path) throws Exception {
        //necessary method for print operations for stack structure
        int visitCounter = 0 ;
        while (!path.isEmpty()) {
            String val = path.pop();

            String[] coordinates = val.split("-");
            int rowNum = Integer.parseInt(coordinates[0]);
            int colNum = Integer.parseInt(coordinates[1]);

            maze[rowNum][colNum] = '.';

            visitCounter++;
        }
        printMaze(maze);
        readMaze(MAZE_NAME);
        return visitCounter;
    }

    public static void insertToGraph(UndirectedGraph<String> mazeSolver) {
        //used for vertex implementation

        for(int i = 0 ; i <maze.length; i++) {
            for(int j = 0 ; j<maze[i].length ; j++){

                if(maze[i][j]==' ') {
                    mazeSolver.addVertex(String.format("%d-%d",i,j));
                }

            }
        }
    }

    public static void readMaze(String mazeName) throws Exception {
        File folder = new File(mazeName); //enter the maze name here
        BufferedReader bufferedReader = new BufferedReader(new FileReader(folder));

        String line;

        LinkedListWithIterator<String> mazeLines = new LinkedListWithIterator<>();
        int lengthOfLine = -1;
        while (null != (line = bufferedReader.readLine())) {

            lengthOfLine = line.length();
            mazeLines.add(line);
        }
        if(lengthOfLine == -1) {
            throw new Exception("an error occured while reading file");
        }

        maze = new char[mazeLines.getLength()][lengthOfLine];

        //setup of maze
        Iterator<String> mazeLinesIterator = mazeLines.getIterator();

        int lineNumber = 0;
        while (mazeLinesIterator.hasNext()){
            String lineToAdd = mazeLinesIterator.next();

            for(int i = 0 ; i<lineToAdd.length();i++) {
                maze[lineNumber][i] = lineToAdd.charAt(i);
            }
            lineNumber++;
        }

    }

    public static void printMaze(char[][] mazeToPrint) {
        if(mazeToPrint!=null) {
            for (int i = 0 ; i < mazeToPrint.length ; i++) {
                for (int j = 0 ; j <mazeToPrint[i].length;j++){

                    System.out.print(mazeToPrint[i][j]);

                }
                System.out.println();
            }
        }else {
            for (int i = 0 ; i < maze.length ; i++) {
                for (int j = 0 ; j <maze[i].length;j++){

                    System.out.print(maze[i][j]);

                }
                System.out.println();
            }
        }

        System.out.println();
    }
}
