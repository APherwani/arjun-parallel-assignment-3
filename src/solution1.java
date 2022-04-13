import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class solution1 {
    final static int N = 4;
    static int NUM_GIFTS = 500_000;
    static int CUR_NUM = 0;
    static Semaphore mutex = new Semaphore(1);
    static Random rand = new Random();
    static FineList<Integer> list = new FineList<Integer>();

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        String path = "../output/output.txt";
        File f = new File(path);
        PrintWriter out = new PrintWriter(f);

        Thread[] threads = new Thread[N];

        for (int i = 0; i < N; i++) {
            final int x = i;
            threads[i] = new Thread(() -> {
                while (CUR_NUM < NUM_GIFTS || !list.isEmpty()) {
                    int action = rand.nextInt()%3;

                    if (action == 0) {
                        if (CUR_NUM < NUM_GIFTS) {
                                try {
                                mutex.acquire();
                                int giftId = CUR_NUM;
                                CUR_NUM++;
                                mutex.release();
                                list.add(Integer.valueOf(giftId));
                                out.printf("Thread #%d add %d.\n", x, giftId);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (action == 1) {
                        try {
                            Integer giftPopped = list.pop();
                            out.printf("Thread #%d remove %d.\n", x, giftPopped.intValue());
                        } catch (NoSuchElementException e) {
                            // No gift to remove.
                        }
                    } else {
                        int giftToFind = rand.nextInt(NUM_GIFTS);
                        try {
                            out.printf("Thread #%d found %d.\n", x, giftToFind);
                            list.get(giftToFind);
                        } catch (NoSuchElementException e) {
                            // This gift was not found.
                        }
                    }
                    if (CUR_NUM >= NUM_GIFTS && list.isEmpty())  {
                        break;
                    }
                }
            });
        }
        long before = System.currentTimeMillis();
        for (Thread t: threads) {
            t.start();
        }
        for (Thread t: threads) {
            t.join();
        }
        long after = System.currentTimeMillis();
        System.out.printf("Time taken %d ms.", (after - before));
        out.flush();
        out.close();
    }
}