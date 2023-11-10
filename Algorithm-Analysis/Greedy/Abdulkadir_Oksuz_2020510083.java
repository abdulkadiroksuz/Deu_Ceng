import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

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

        int n=15, p=5, c=5; //n years, p player trained, c coach demand per player

        Greedy(n,p,c);
    }

    public static void Greedy(int n, int p, int c) {
        int[] plan = new int[n+1];
        int cost = 0;

        for(int i = 1 ; i<=n;i++) {
            if(yearly_player_demand.get(i) > p) {
                plan[i] = p;
                cost += (yearly_player_demand.get(i)-p)*c;
            }else if(yearly_player_demand.get(i)==p){
                plan[i] = p;
            }else {
                plan[i] = yearly_player_demand.get(i);
            }
        }
        Output(cost, plan);
    }

    public static void Output(int cost, int[] plan) {
        System.out.println("\nMinimum Price: "+cost+"TL \nThe Plan for best price");
        for(int m = 1;m<plan.length;m++){
            System.out.println(String.format("Year %2d:    Total promote number for this year: %-10d Trained: %-10d", m, yearly_player_demand.get(m),plan[m]));
        }
    }



}
