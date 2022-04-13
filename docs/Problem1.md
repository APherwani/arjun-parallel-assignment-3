For this problem I implemented both the fine grain and the course grain lock solutions.

I wanted to get to the lock free implementation too but with senior design and other projects I did not get the time. 

I am ashamed of this but I did not have any other choice given my constraints and I want to apologize to the TA grading this. The runtime is between `9-10 minutes` and the output is in the output directory for this problem. Because of it's highly inefficient nature I expect my solution to not score very well in the efficiency department.

To save the TA some time I'm including a sample output file in the output directory and the time for one sample run was `540929 ms` which is approximately `9 minutes and 1 second`.

I hope that the TA will be a bit understanding, but I will accept whatever your judgement is. Thank you for reading this and have an excellent day!

To run the assignment please `cd` into the `src` directory and type the following commands:
```
javac solution1.java
java solution1
```

The output file will be labeled `output.txt`. This will contain all the chatter of the threads about what is being worked on. The time elapsed will be printed directly to the console.

Note: I have also included the Coarse Grain lock implementation of the linked list even though I don't use it. This is because I worked on it and implemented it so I felt that I should include it too.

Note 2: I haven't fully tested it yet but a possible explanation for the slowness of the algorithm is Java's garbage collection. Of course this is not an excuse, I wanted to reimplement it in Rust but I didn't have the time to do so. If I revisit the problem later I will do this.