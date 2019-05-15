import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final int NUMBER_COUNT = 50000;
    private static final int THREADS = 2;
    private static List<Integer> numbers = new ArrayList<>();
    private static int[][] splitArray;
    private static List<Thread> threads = new ArrayList<>();
    private static int[] sortedNumbers = new int[NUMBER_COUNT];

    public static void main(String[] args) throws InterruptedException {
        numbers = Numbers.GenerateNumber(NUMBER_COUNT);
        splitArray = fillSplitArray(THREADS , numbers);

        long startingTime = System.currentTimeMillis();

        class Sort {
            synchronized int[] SelectionSort(int[] arr) {
                int n = arr.length;

                // One by one move boundary of unsorted subarray
                for (int i = 0; i < n-1; i++)
                {
                    // Find the minimum element in unsorted array
                    int min_idx = i;
                    for (int j = i+1; j < n; j++)
                        if (arr[j] < arr[min_idx])
                            min_idx = j;

                    // Swap the found minimum element with the first
                    // element
                    int temp = arr[min_idx];
                    arr[min_idx] = arr[i];
                    arr[i] = temp;
                }

                return arr;
            }
        }

        Sort sort = new Sort();

        class SelectionSortThread extends Thread {
            private int splitArrayIndex;
            private SelectionSortThread(int splitArrayIndex) {
                this.splitArrayIndex = splitArrayIndex;
            }
            public void run() {
                System.out.println(Arrays.toString(splitArray[splitArrayIndex]));
                splitArray[splitArrayIndex] = sort.SelectionSort(splitArray[splitArrayIndex]);
            }
        }

        for (int i = 0; i < THREADS; i++)
            threads.add(new SelectionSortThread(i));

        for(Thread thread: threads)
            thread.start();

        for(Thread thread: threads)
            thread.join();


        int x = 0;
        for (int i = 0; i < splitArray.length; i++) {
            for (int j = 0; j < splitArray[i].length; j++) {
                sortedNumbers[x] = splitArray[i][j];
                x++;
            }
        }

        sortedNumbers = sort.SelectionSort(sortedNumbers);

        System.out.println(Arrays.toString(sortedNumbers));
        System.out.println("Time taken " + (System.currentTimeMillis() - startingTime) + "ms");
    }

    private static int[][] fillSplitArray(int arrayAmount, List<Integer> listToUse) {
        if (listToUse.size() == 0) {
            return new int[0][0];
        }

        int splitLength = (int) Math.ceil((double) listToUse.size() / (double) arrayAmount);
        int[][] splits = new int[arrayAmount][];

        int j = 0;
        int k = 0;
        for (int i = 0; i < listToUse.size(); i++) {
            if (k == splitLength) {
                k = 0;
                j++;
            }
            if (splits[j] == null) {
                int remainingNumbers = listToUse.size() - i;
                splits[j] = new int[Math.min(remainingNumbers, splitLength)];
            }
            splits[j][k++] = listToUse.get(i);
        }
        return splits;
    };
}
