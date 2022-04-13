import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class solution2 {
    final static int NUM_THREADS = 8;
    final static int MAX_TEMP = 70;
    final static int MIN_TEMP = -100;
    final static int DIFFERENCE = MAX_TEMP - MIN_TEMP;
    final static int NUM_INTEREST_POINTS = 5;
    
    static AtomicInteger counter = new AtomicInteger(); // default value is 0
    static int numHours = 1;
    static int numCycles;

    static AtomicBoolean wait = new AtomicBoolean(false);

    public static void main(String[] args) {
        try {
            numHours = Integer.parseInt(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {}
        numCycles = numHours * 60;

        AtomicInteger[] temps = new AtomicInteger[NUM_THREADS];
        AtomicBoolean[] mark = new AtomicBoolean[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            temps[i] = new AtomicInteger();
            mark[i] = new AtomicBoolean();
        }

        Thread[] threads = new Thread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            final int threadId = i;
            final Random rand = new Random();
            threads[i] = new Thread(() -> {
                while (counter.get() < numCycles) {
                    // busy wait till you get the signal to work.
                    // this way instead of continually generating
                    // random temperature readings you only generate
                    // one reading every cycle (minute).
                    while (wait.get()) {}
                    // once you get the signal to work generate a new
                    // temperature reading and set it.
                    temps[threadId].set(rand.nextInt(DIFFERENCE + 1) + MIN_TEMP); 
                    // this helps with the busy waiting. If the counter
                    // is even but the mark is false it means all threads
                    // haven't finished creating a tempreature yet
                    // which means that the other threads should busy wait
                    // till all the threads have finished creating a temperature.
                    mark[threadId].set(counter.get()%2 == 0);
                }
            });
            threads[i].start();
        }
        
        for (int i = 0; i < numHours; i++) {
            System.out.printf("Hour #%d\n", (i+1));

            // will be used to find the biggest 10 minute swings.
            int[] maxVals = new int[60];
            int[] minVals = new int[60];

            int[] top5Min = new int[NUM_INTEREST_POINTS];
            int[] top5Max = new int[NUM_INTEREST_POINTS];

            for (int j = 0; j < 60; j++) {
                wait.set(false);
                while(!checkParity(mark)) {}
                wait.set(true);
                counter.compareAndSet(i*60 + j, i*60 + j + 1);
                int max = MIN_TEMP;
                int min = MAX_TEMP;

                for (AtomicInteger k: temps) {
                    if (k.get() >= max) max = k.get();
                    if (k.get() <= min) min = k.get();

                    // also include maxes and mins for the
                    // top 5 of each.
                    // Note: I could have used a min-heap and max-heap
                    // for the top 5's but it's so small it
                    // it didn't seem worth it.
                    // Note 2: The reason this is done instead of just
                    // using the max and mins from this set we found above
                    // is because you can have multiple global extrema
                    // in the same set and one hides the other.
                    for (int l = 0; l < NUM_INTEREST_POINTS; l++) {
                        if (top5Max[l] < k.get()) {
                            top5Max[l] = k.get();
                            break;
                        } if (top5Min[l] > k.get()) {
                            top5Min[l] = k.get();
                            break;
                        }
                    }
                }

                maxVals[j] = max;
                minVals[j] = min;
            }

            System.out.printf("Maxes:\n");
            for (int k = 0; k < NUM_INTEREST_POINTS; k++) {
                System.out.printf("%d ", top5Max[k]);
            } System.out.printf("\n");
            System.out.printf("Mins:\n");
            for (int k = 0; k < NUM_INTEREST_POINTS; k++) {
                System.out.printf("%d ", top5Min[k]);
            } System.out.printf("\n");

            // sentinel value of -1 to account for
            // the extremely unlikely case where all the maxes and mins are the same.
            int largestDifference = -1, difference, largestVal, smallestVal;
            // Compute largest 10 minute difference.
            // Hardcoding 10 minutes here because i ran out of good variable names.
            for (int k = 0; k <= 50; k++) {
                // sentinel value of -1 to account for the extremely
                // unlikely case where all the maxes and mins are the same.
                difference = -1;
                largestVal = MIN_TEMP;
                smallestVal = MAX_TEMP;

                // finding the largest and smallest vals
                // for this interval.
                for (int l = 0; l < 10; l++) {
                    if (maxVals[k + l] > largestVal) largestVal = maxVals[k + l];
                    if (minVals[k + l] < smallestVal) smallestVal = minVals[k + l];
                }
                difference = largestVal - smallestVal;
                if (difference > largestDifference) largestDifference = difference;
            }
            System.out.printf("Largest 10-minute interval difference in temperatures is: %d.\n\n", largestDifference);
        }
        // this is so everything can finish executing.
        wait.set(false);

        for (int i = 0; i < NUM_THREADS; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean checkParity(AtomicBoolean[] mark) {
        for (AtomicBoolean b: mark) {
            if (b.get() != (counter.get() % 2 == 0)) return false;
        }
        return true;
    }
}
