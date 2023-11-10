import java.util.Arrays;
import java.util.Random;

public class sort_Test {
    public static void main(String[] args) {
        /** To prevent stack overflow error in quicksort add the VM option "-Xss26m"
         * */

        int[] arr100000 = new int[100000];
        int[] arr10000 = new int[10000];
        int[] arr1000 = new int[1000];
        Random rand = new Random();

        System.out.println("equal integers");
        for (int i = 0; i < arr1000.length; i++) {
            arr1000[i] = 1;
        }
        for (int i = 0; i < arr10000.length; i++) {
            arr10000[i] = 1;
        }
        for (int i = 0; i < arr100000.length; i++) {
            arr100000[i] = 1;
        }

        new SortingClass(arr1000);
        new SortingClass(arr10000);
        new SortingClass(arr100000);


        System.out.println("\nRandom integers");
        for (int i = 0; i < arr1000.length; i++) {
            arr1000[i] = rand.nextInt(10001);
        }
        for (int i = 0; i < arr10000.length; i++) {
            arr10000[i] = rand.nextInt(10001);
        }
        for (int i = 0; i < arr100000.length; i++) {
            arr100000[i] = rand.nextInt(10001);
        }

        new SortingClass(arr1000);
        new SortingClass(arr10000);
        new SortingClass(arr100000);

        System.out.println("\nIncreasing integers");
        for (int i = 0; i < arr1000.length; i++) {
            arr1000[i] = i;
        }
        for (int i = 0; i < arr10000.length; i++) {
            arr10000[i] = i;
        }
        for (int i = 0; i < arr100000.length; i++) {
            arr100000[i] = i;
        }

        new SortingClass(arr1000);
        new SortingClass(arr10000);
        new SortingClass(arr100000);

        System.out.println("\ndecreasing integers");
        for (int i = 0; i < arr1000.length; i++) {
            arr1000[i] = (arr1000.length-i);
        }
        for (int i = 0; i < arr10000.length; i++) {
            arr10000[i] = (arr10000.length-i);
        }
        for (int i = 0; i < arr100000.length; i++) {
            arr100000[i] = (arr100000.length-i);
        }

        new SortingClass(arr1000);
        new SortingClass(arr10000);
        new SortingClass(arr100000);


    }
}
