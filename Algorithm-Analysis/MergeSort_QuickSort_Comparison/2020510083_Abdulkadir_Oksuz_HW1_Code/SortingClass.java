import java.util.Random;

public class SortingClass {

    public SortingClass(int[] arr)  {
        System.out.println("\nsize: "+arr.length);

        int[] clone = arr.clone();
        long startTime = System.nanoTime();
        mergeSort(clone,"TwoParts");
        long timeElapsed = (System.nanoTime() - startTime);
        double millisecond = timeElapsed / 1e6; //converting to ms
        System.out.println("time elapsed for 2-way merge sort: "+millisecond+" ms");

        clone = arr.clone();
        startTime = System.nanoTime();
        mergeSort(clone,"ThreeParts");
        timeElapsed = (System.nanoTime() - startTime);
        millisecond = timeElapsed / 1e6; //converting to ms
        System.out.println("time elapsed for 3-way merge sort: "+millisecond+" ms");

        clone = arr.clone();
        startTime = System.nanoTime();
        quickSort(clone,"FirstElement");
        timeElapsed = (System.nanoTime() - startTime);
        millisecond = timeElapsed / 1e6; //converting to ms
        System.out.println("time elapsed for FirstElement indexed quick sort: "+millisecond+" ms");

        clone = arr.clone();
        startTime = System.nanoTime();
        quickSort(clone,"RandomElement");
        timeElapsed = (System.nanoTime() - startTime);
        millisecond = timeElapsed / 1e6; //converting to ms
        System.out.println("time elapsed for Random indexed quick sort: "+millisecond+" ms");

        clone = arr.clone();
        startTime = System.nanoTime();
        quickSort(clone,"MidOfFirstMidLastElement");
        timeElapsed = (System.nanoTime() - startTime);
        millisecond = timeElapsed / 1e6; //converting to ms
        System.out.println("time elapsed for MidOfFirstMidLastElement indexed quick sort: "+millisecond+" ms");

    }

    public void mergeSort(int[] arrayToSort, String numberOfPartitions) {
        switch (numberOfPartitions){
            case "TwoParts": {
                merge_sort(arrayToSort,0, arrayToSort.length-1);
                break;
            }
            case "ThreeParts": {
                merge_sort3(arrayToSort,0,arrayToSort.length-1);
                break;
            }
            default:
                System.out.println("enter 2 or 3 only");
                break;

        }

    }

    // partition by 2
    private void merge_sort(int arr[], int l, int r)
    {
        if (l < r)
        {
            // Find the middle point
            int m = (l+r)/2;

            // Sort first and second halves
            merge_sort(arr, l, m);
            merge_sort(arr , m+1, r);

            // Merge the sorted halves
            merge(arr, l, m, r);
        }
    }
    // partition by 2
    private void merge(int arr[], int l, int m, int r)
    {
        int n1 = m - l + 1;
        int n2 = r - m;

        int L[] = new int [n1];
        int R[] = new int [n2];

        for (int i=0; i<n1; ++i)
            L[i] = arr[l + i];
        for (int j=0; j<n2; ++j)
            R[j] = arr[m + 1+ j];



        int i = 0, j = 0;

        int k = l;
        while (i < n1 && j < n2)
        {
            if (L[i] <= R[j])
            {
                arr[k] = L[i];
                i++;
            }
            else
            {
                arr[k] = R[j];
                j++;
            }
            k++;
        }

        while (i < n1)
        {
            arr[k] = L[i];
            i++;
            k++;
        }

        while (j < n2)
        {
            arr[k] = R[j];
            j++;
            k++;
        }
    }

    // partition by 3
    private void merge_sort3(int arr[], int l, int r)
    {
        int lm; //left middle
        int rm; //right middle

        if (r-l >= 2) // general formula: r-l+1 >= numberOfPartition other cases are accepted as base case. If we operate base cases program will throw stackOverflow exception due to infinite recursion
        {
            lm = (r-l+1)/3 + l;
            rm = r-(r-l+1)/3;

            // Sort first and second halves
            merge_sort3(arr, l, lm-1); //left part
            merge_sort3(arr, lm,rm); //middle part
            merge_sort3(arr , rm+1, r); //right part

            // Merge the sorted halves
            merge3(arr, l, lm, rm, r);

        }
    }
    // partition by 3
    private void merge3(int arr[], int l, int lm, int rm, int r)
    {
        int n1 = lm-l; //size of left array
        int n2 = r - rm; //size of right array
        int n3 = rm - lm +1; //size of mid array

        int L[] = new int [n1];
        int R[] = new int [n2];
        int M[] = new int[n3];

        for (int i=0; i<n1; ++i)
            L[i] = arr[l + i];
        for (int j=0; j<n2; ++j)
            R[j] = arr[rm+1 + j];
        for (int k=0; k<n3; k++)
            M[k] = arr[lm+k];


        int i = 0, j = 0, k =0, c=l;

        while ((i<n1 && j<n2) || (i<n1 && k<n3) || (j<n2 && k<n3 )) {

            if(i<n1 && j<n2 && k<n3) { //if all arrays are full
                if(L[i] <= R[j] && L[i]<=M[k]){
                    arr[c] = L[i];
                    L[i]=-1;
                    i++;
                    c++;
                }else if(R[j]<=L[i] && R[j]<=M[k]) {
                    arr[c] = R[j];
                    R[j] = -1;
                    j++;
                    c++;
                }
                else if(M[k]<= R[j] && M[k]<= L[i]){
                    arr[c] = M[k];
                    M[k]=-1;;
                    k++;
                    c++;
                }
            }

            //rest checks is for other cases except 2 empty array
            else if(i<n1 && j<n2 && k>=n3){
                if(R[j]<=L[i]){
                    arr[c] = R[j];
                    R[j]=-1;
                    j++;
                    c++;
                }else {
                    arr[c] = L[i];
                    L[i]=-1;
                    i++;
                    c++;
                }
            }
            else if(i<n1 && k<n3 && j>=n2){
                if(L[i]<=M[k]){
                    arr[c]=L[i];
                    L[i]=-1;
                    i++;
                    c++;
                }else {
                    arr[c] = M[k];
                    M[k]=-1;
                    k++;
                    c++;
                }
            }
            else if(j<n2 && k<n3 && i>=n1){
                if(R[j]<=M[k]){
                    arr[c]=R[j];
                    R[j]=-1;
                    j++;
                    c++;
                }else {
                    arr[c] = M[k];
                    M[k]=-1;
                    k++;
                    c++;
                }
            }

        }

        //rest loops is necessary to add the elements left in the array which is left with elements
        while (i < n1)
        {
            arr[c] = L[i];
            i++;
            c++;
        }

        while (j < n2)
        {
            arr[c] = R[j];
            j++;
            c++;
        }

        while (k < n3)
        {
            arr[c] = M[k];
            k++;
            c++;
        }

    }

    public void quickSort(int[] arrayToSort, String pivotType) {
        switch (pivotType){
            case "FirstElement":{
                quickSort_firstP(arrayToSort,0,arrayToSort.length-1);
                break;
            }
            case "RandomElement":{
                quickSort_RandomP(arrayToSort,0,arrayToSort.length-1);
                break;
            }
            case "MidOfFirstMidLastElement":{
                quickSort_MidOfFirst(arrayToSort,0,arrayToSort.length-1);
                break;
            }
            default:
                System.out.println("there is no such pivotType");
                break;
        }
    }

    private void quickSort_firstP(int[] arrayToSort, int l, int r ) {
        if(l<r) {
            int p = partitionL(arrayToSort,l,r); //returns pivot
            quickSort_firstP(arrayToSort, l,p-1);
            quickSort_firstP(arrayToSort,p+1,r);
        }
    }

    private int partitionL(int[] arrayToSort, int l, int r ) {
        int pivot = arrayToSort[l];
        int line = l; //imaginary line to determine where to split array, also corresponds to pivot of array;

        for(int i = l+1; i<=r;i++) {
            if(arrayToSort[i]<pivot){
                int temp = arrayToSort[i];
                arrayToSort[i] = arrayToSort[line+1];
                arrayToSort[line+1] = temp;
                line++;
            }
        }
        int temp = arrayToSort[line];
        arrayToSort[line] = arrayToSort[l];
        arrayToSort[l] = temp;
        return line;
    }
    private int partitionR(int[] arrayToSort, int l, int r ) {

        int pivot = arrayToSort[r];
        int line = l; //imaginary line to determine where to split array, also corresponds to pivot of array;

        for(int i = l; i<r;i++) {
            if(arrayToSort[i]<pivot){
                int temp = arrayToSort[i];
                arrayToSort[i] = arrayToSort[line];
                arrayToSort[line] = temp;
                line++;
            }
        }
        int temp = arrayToSort[line];
        arrayToSort[line] = arrayToSort[r];
        arrayToSort[r] = temp;
        return line;
    }
    private int partitionNotLR(int[] arrayToSort, int l, int r , int pivotIdx) { //only used in random ordered arrays
        // (this function is not used in MidOfFirstLastMid operation)
        // partition of not left or not right
        int temp = arrayToSort[pivotIdx];
        arrayToSort[pivotIdx] = arrayToSort[r];
        arrayToSort[r] = temp;

        return partitionR(arrayToSort,l,r);
    }
    private int partitionMid(int[] arrayToSort, int l, int r, int pivotIndex){
        int pivotValue = arrayToSort[pivotIndex];
        int i = l;
        int j = r;
        while (i <= j) {
            while (arrayToSort[i] < pivotValue) {
                i++;
            }
            while (arrayToSort[j] > pivotValue) {
                j--;
            }
            if (i <= j) {
                int temp = arrayToSort[i];
                arrayToSort[i] = arrayToSort[j];
                arrayToSort[j] = temp;
                i++;
                j--;
            }
        }
        return i;

    }

    private void quickSort_RandomP(int[] arrayToSort, int l, int r ){
        if(l<r) {
            int p = partitionRandom(arrayToSort,l,r); //returns pivot
            quickSort_RandomP(arrayToSort, l,p-1);
            quickSort_RandomP(arrayToSort,p+1,r);
        }
    }

    private int partitionRandom(int[] arrayToSort, int l, int r ) {

        Random rnd = new Random();
        int pivotIdx = rnd.nextInt(l,r+1); //pivot index

        if(pivotIdx==l){
            return partitionL(arrayToSort,l,r);
        }else if(pivotIdx==r){
            return partitionR(arrayToSort,l,r);
        }else{ //pivot in the middle of the array
            return partitionNotLR(arrayToSort,l,r,pivotIdx);
        }
    }


    private void quickSort_MidOfFirst(int[] arrayToSort, int l, int r ) {
        if(l<r) {
            int p;
            int idxOfPivot = getMidOfFirstLastMid(arrayToSort,l,r); //returns index of median compares l,r and (l+r)/2 only (not median of array)
            if(idxOfPivot==l){
                p = partitionL(arrayToSort,l,r); //returns pivot
            }else if(idxOfPivot==r){
                p = partitionR(arrayToSort,l,r);
            }else {
                p = partitionMid(arrayToSort,l,r,idxOfPivot);
            }

            quickSort_MidOfFirst(arrayToSort, l,p-1);
            quickSort_MidOfFirst(arrayToSort,p+1,r);
        }
    }

    private int getMidOfFirstLastMid(int[] arr, int l, int r) {
        // (necessarry for partitioning midOfFirstLastMid operation)

        int m = (r-l)/2+l;
        if( (arr[m]<=arr[l] && arr[m]>=arr[r]) || (arr[m]<=arr[r] && arr[m]>=arr[l])) {
            return m;
        }
        if((arr[l]<=arr[m] && arr[l]>=arr[r]) || (arr[l]<=arr[r] && arr[l]>=arr[m]) ) {
            return l;
        }
        if((arr[r]<=arr[m] && arr[r]>=arr[l]) || (arr[r]<=arr[l] && arr[r]>=arr[m]) ) {
            return r;
        }


        //the algorithm below finds the median of the array. It results in more runtime
//        int[] arr2 = new int[r-l+1]; //temp array to find value of median of sub array
//
//        for(int i = 0 ; i<arr2.length; i++) {
//            arr2[i] = arr[l+i];
//        }
//
//        Arrays.sort(arr2); //sorted ascending order
//
//        int valueMedian = arr2[arr2.length/2]; //value of median of sub-array
//
//        for (int i = 0; i<arr.length;i++) {
//            if(arr[l+i]==valueMedian)
//                return i+l; //index of the pivot in the arr
//        }

        return -1; //unreachable
    }

}
