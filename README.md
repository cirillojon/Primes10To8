# Primes10To8
This program is written in java (openjdk 17.0.4.1)

To run the code use the following commands: 
"javac Primes10To8.java"
"java Primes10To8"

The code then will write out to a file called: 'primes.txt', which will list the time in seconds it took to run, the total number of primes found, the sum of all primes found, and the 10 largest primes found - listed from lowest to highest

The goal of this code is to find all the prime numbers below 100000000 (10^8) using a method called the Sieve of Eratosthenes. It's using 8 pooled threads to do this, so it can work faster. The code is using a special data structure called a ConcurrentHashMap to store the prime numbers it finds, and it's also using another tool called the ExecutorCompletionService to keep track of the progress of the threads. The code is checking if each number is prime or not using a boolean array, and then it's storing the prime numbers in the ConcurrentHashMap, and adding up the total sum of all prime numbers found. Lastly, it's printing out the 10 largest prime numbers found. I have cross referenced these results with mathematics resources on primes online, and they are accurate. 

