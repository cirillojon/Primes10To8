import java.util.Arrays;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.*;

/* --- COP4520 Assignment 1         */
/* --- Jonathan Cirillo  1/26/2023  */

/* Finds all prime numbers below 10^8 using multi-threading 
with 8 pooled threads and the sieve of eratosthenes algorithm */

//implements 'callable' instead of 'runnable --
//so we can return the sum of the prime numbers that each thread calculates 
public class Primes10To8 extends Thread implements Callable<Void> 
{
    //initialize variables
    long limit;
    long sum;
    long startIndex;
    ConcurrentHashMap<Long, Integer> primes;
    static boolean[] arr;
    
    //constructor 
    public Primes10To8(long limit, long startIndex, ConcurrentHashMap<Long, Integer> primes) 
    {
        this.limit = limit;
        this.startIndex = startIndex;
        this.primes = primes;
    }

    //method that finds prime numbers using sieve of eratosthenes algorithm
    public static void findPrimes(long limit) 
    {
        //creates new array of booleans with the size of the limit
        arr = new boolean[(int)limit+1];

        //fills the array with true values
        Arrays.fill(arr, true);

        //set 0 and 1 to false because they aren't prime
        arr[0] = false;
        arr[1] = false;

        for(int i = 2; i <= Math.sqrt(limit); i++) 
        {
            //if the current number is prime
            if(arr[i]) 
            {
                //loop through the array starting at i*i and add i to the index
                for(int j = i*i; j <= limit; j += i) 
                {
                    //set the current index to false (not prime)
                    arr[j] = false;
                }
            }
        }
    }

    //returns the sum of the prime numbers
    public long getSum() 
    {
        return sum;
    }

    public static long[] TenLargest(ConcurrentHashMap<Long, Integer> primes) 
    {
        //convert primes hashmap to array
        long[] primesArray = new long[primes.size()];
        int i = 0;

        for (long prime : primes.keySet()) 
        {
            primesArray[i] = prime;
            i++;
        }

        Arrays.sort(primesArray);
        // return the ten largest primes
        return Arrays.copyOfRange(primesArray, primesArray.length - 10, primesArray.length);
    }
    
    //this method is called when each thread is started
    //'call' instead of run() so we are able to return the sum that each thread calculates
    public Void call() 
    {
        for (long i = startIndex; i < limit; i++) 
        {
            if (arr[(int)(i)])
            {
                primes.put(i, -1);
                sum = sum + i;
            }
        }
        return null;
    }
    public static void main(String[] args) throws InterruptedException, ExecutionException 
    {
            long start = System.currentTimeMillis();

            //# of threads we will use
            int threadCount = 8;
    
            //upper limit of range of numbers to find primes in
            int limit = 100000000;
    
            //variable to store the sum of all primes found below the limit
            long sum = 0;
    
            //we use a 'ConcurrentHashMap' to store the prime numbers found and ensure no duplicates
            //we use this instead of a regular hashmap, because it is thread safe
            ConcurrentHashMap<Long, Integer> primes = new ConcurrentHashMap<>();
    
            //generates the prime numbers using the sieve algorithm
            findPrimes(limit);
    
            //creates a thread pool with the given number of threads to use (8)
            Executor executor = Executors.newFixedThreadPool(threadCount);
    
            //array to store the threads
            Primes10To8[] threads = new Primes10To8[threadCount];
    
            //we need the 'ExecutorCompletionServic'e so that when the threads are finished running,
            //we can find the sum of all the primes found by each thread
            ExecutorCompletionService<Void> ecs = new ExecutorCompletionService<>(executor);
    
            //creates and starts the threads
            //each thread will find primes in a range of 12500000 (10^8/8)
            limit = 0;
            for (int i = 0; i < threadCount; i++) 
            {
                //creates a new thread and adds it to the thread array
                Primes10To8 temp = new Primes10To8(limit+12500000, limit, primes);
                threads[i] = temp;

                //starts the thread
                ecs.submit(threads[i]);
                limit += 12500000;
            }

            //waits for all threads to finish before continuing the code
            for (int i = 0; i < threadCount; i++) 
            {
                ecs.take();
            }

            //calculates the sum of all the prime numbers found
            for (int i = 0; i < threadCount; i++) 
            {
                sum = sum + threads[i].getSum();
            }

            long[] TenLargestPrimes = Primes10To8.TenLargest(primes);

            //stores the time when the threads are finished running and the 
            //necessary values have been calcualted 
            long end = System.currentTimeMillis();
            
            //writes the total number of primes, sum of primes, and total time taken to a 'primes.txt' file
            try (BufferedWriter buff = new BufferedWriter(new FileWriter("primes.txt", true))) 
            {
                //writes the total time taken in seconds, number of primes, sum of primes, and top 10 primes to the file
                buff.write((end - start)/1000.0 + "s" +" " + primes.size() + " " + sum + " " + Arrays.toString(TenLargestPrimes));
            } 
            catch (IOException e) 
            {
                System.out.println("Error writing to file");
            }

        //print statements for testing
        //System.out.println("Total number of prime numbers: " + primes.size());
        //System.out.println("Sum of prime numbers: " + totalSum);
        //System.out.println("Top 10 primes: " + Arrays.toString(topTenPrimes));
    }
}            


