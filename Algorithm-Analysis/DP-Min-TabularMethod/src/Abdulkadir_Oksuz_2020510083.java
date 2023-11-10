import java.io.*;
import java.util.*;

public class Abdulkadir_Oksuz_2020510083 {

    static ArrayList<Integer> yearly_player_demand = new ArrayList<>();
    static ArrayList<Integer> players_salary = new ArrayList<>();

    static { //file read operations
        try {

            File file = new File("players_salary.txt");
            Scanner scanner = new Scanner(file);
            scanner.nextLine();
            players_salary.add(0);
            while (scanner.hasNext()) {
                scanner.next();
                players_salary.add(Integer.parseInt(scanner.next()));
            }
            scanner.close();


            file = new File("yearly_player_demand.txt");
            scanner = new Scanner(file);
            scanner.nextLine();
            yearly_player_demand.add(0);
            int i = 0;
            while (scanner.hasNext()) {
                scanner.next();
                int demandNum = Integer.parseInt(scanner.next());
                yearly_player_demand.add(demandNum);
            }
            scanner.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchElementException e){
            e.printStackTrace();
            throw new RuntimeException("Unknown txt format");
        }
    }//file read operations

    public static void main(String[] args) {

        int n=3, p=5, c=5; //n years, p player trained, c coach demand per player

        DP(n,p,c);
    }
    public static int makePlan(int[][] track, int[] plan, int i, int j){

        if(i == 0){
            plan[i] = j; //j is the number we trained in year 0
            return j;
        }
        plan[i]=yearly_player_demand.get(i)-track[i][j];
        return makePlan(track,plan,i-1, track[i][j]);
    }

    public static void output(int[][] track, int cost, int i, int p) {
        int[] plan = new int[i+1];
        makePlan(track, plan,i,0); //we are making plan with recursive calls (Back tracking)

        System.out.println("\nMinimum Price: "+cost+"TL \nThe Plan for best price");
        for(int m = 0;m<plan.length;m++){
            System.out.println(String.format("Year %d:    Total promote number for this year: %-10d Trained by us: %-10d Trained by coach: %-10d", m, plan[m],
                    Math.min(plan[m], p), plan[m]>p ? plan[m]-p : 0));
        }
    }

    public static void DP(int n, int p, int c) {

        int[][] dp =    new int[n + 1][players_salary.size()]; // Tabular method
        int[][] track = new int[n + 1][players_salary.size()]; //for back tracking

        for (int i = 0; i <= n; i++) {//year
            if(i==0) {
                for(int j = 0 ; j< players_salary.size();j++) {
                    dp[i][j] = players_salary.get(j);   //base case initialization
                }
                continue;
            }

            for (int j = 0; j < players_salary.size(); j++) { //excess number from demanded amount for year i
                int need = Math.max(0,j+ yearly_player_demand.get(i)-p); //holding unmet need

                int min = Integer.MAX_VALUE; //we hold minimum value
                for(int k = 0; k<=need;k++) { //We control the previous year until we meet unmet needs.
                    try {
                        if (min > dp[i - 1][k] + (need - k) * c) { //calculating current k state
                            min = players_salary.get(j) + dp[i - 1][k] + (need - k) * c; //updating min cost
                            track[i][j]=k; //How many did we exceed in previous year to achieve this cost
                        }
                    }catch (ArrayIndexOutOfBoundsException e){
                        break;
                    }
                }
                dp[i][j] = min; //Holding the best cost for current (year,excess number)
                if(i==n) {
                    output(track,min,i,p); //DP table ready to backtrack
                    return;
                }
            }
        }
    }
}
