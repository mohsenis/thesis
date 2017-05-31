package utils;

public class Permutate {  //From:  http://www.geeksforgeeks.org/print-all-possible-combinations-of-r-elements-in-a-given-array-of-size-n/
	
    static int sum;
	
	static void combinationUtil(int arr[], int data[], int start,
                                int end, int index, int r)
    {
        if (index == r)
        {
        	int n = 1;
            for (int j=0; j<r; j++){
//                System.out.print(data[j]+" ");
            	n *= data[j];
            }
            sum += n;
//            System.out.println("");
            return;
        }
 
        for (int i=start; i<=end && end-i+1 >= r-index; i++)
        {
            data[index] = arr[i];
            combinationUtil(arr, data, i+1, end, index+1, r);
        }
    }
    
    public static int sumProduct(int arr[])
    {
    	sum = 0;
    	int n = arr.length;
    	int r = n-1;
    	int data[]=new int[r];
        
        combinationUtil(arr, data, 0, n-1, 0, r);
//        System.out.println(sum);
        return sum;
    }
 
    public static void main (String[] args) {
        int arr[] = {2,3,4};
        sumProduct(arr);
    }
}
