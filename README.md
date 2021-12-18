# ID2222DataMining

For the course ID2222 Data Mining, we implement five small projects.

### Finding Similar Items: Textually Similar Documents
We implement the stages of finding textually similar documents based on Jaccard similarity using the shingling, minhashing, and locality-sensitive hashing (LSH) techniques and corresponding algorithms. 

1. A class Shingling that constructs k-shingles of a given length k (e.g., 10) from a given document, computes a hash value for each unique shingle, and represents the document in the form of an ordered set of its hashed k-shingles.
2. A class CompareSets that computes the Jaccard similarity of two sets of integers - two sets of hashed shingles.
3. A class MinHashing that builds a minHash signature (in the form of a vector or a set) of a given length n from a given set of integers (a set of hashed shingles).
4. A class CompareSignatures that estimates similarity of two integer vectors - minhash signatures - as a fraction of components, in which they agree.
5. A class LSH that implements the LSH technique: given a collection of minhash signatures (integer vectors) and a similarity threshold t, the LSH class (using banding and hashing) finds candidate pairs of signatures agreeing on at least fraction t of their components.

To test and evaluate scalability (the execution time versus the size of input dataset) of your implementation, write a program that uses your classes to find similar documents in a corpus of 5-10 documents. Choose a similarity threshold s (e.g., 0,8) that states that two documents are similar if the Jaccard similarity of their shingle sets is at least s. 

To test and evaluate our implementation, we write a program that uses our implementation to find similar documents in a corpus of 5-10 or more documents such as web pages or emails.

### Discovery of Frequent Itemsets and Association Rules
The problem of discovering association rules between itemsets in a sales transaction database (a set of baskets) includes the following two sub-problems:
1. Finding frequent itemsets with support at least s;
2. Generating association rules with confidence at least c from the itemsets found in the first step.
Remind that an association rule is an implication X → Y, where X and Y are itemsets such that X∩Y=∅. Support of the rule X → Y is the number of transactions that contain X⋃Y. Confidence of the rule X → Y the fraction of transactions containing X⋃Y in all transactions that contain X.

First, we solve the first sub-problem by implementing the A-Priori algorithm for finding frequent itemsets with support at least s in a dataset of sales transactions. To test and evaluate our implementation, we write a program that uses our A-Priori algorithm implementation to discover frequent itemsets with support at least s in a given dataset of sales transactions.
Secondly, we solve the second sub-problem, i.e., develop and implement an algorithm for generating association rules between frequent itemsets discovered by using the A-Priori algorithm in a dataset of sales transactions. The rules must have support at least s and confidence at least c, where s and c are given as input parameters.

### Mining Data Streams
We implement a streaming graph processing algorithm described by L. De Stefani, A. Epasto, M. Riondato, and E. Upfal in "TRIEST: Counting Local and Global Triangles in Fully-Dynamic Streams with Fixed Memory Size".

1. First, we implement the reservoir sampling used in the graph algorithm presented in the paper.
2. Second, we implement the streaming graph algorithm presented in the paper that makes use of the algorithm implemented in the first step. 

To ensure that our implementation is correct, we test our implementation with some of the publicly available graph datasets and present our test results in a report.

### Graph Spectra
We study, implement and analyse the spectral graph clustering algorithm as described in the paper "On Spectral Clustering: Analysis and an algorithm" by Andrew Y. Ng, Michael I. Jordan, Yair Weiss. Using our implementation of the K-eigenvector algorithm, we analyse two sample graphs.

### K-way Graph Partitioning Using JaBeJa
The goal of this assignment is to understand distributed graph partitioning using gossip-based peer-to-peer techniques, such as, JaBeJa by F. Rahimian, et al. in "JA-BE-JA: A Distributed Algorithm for Balanced Graph Partitioning".

This assignment consists of the following three tasks.
1. We implement the Ja-Be-Ja algorithm and we tweak different JaBeJa configurations in order to find the smallest edge cuts for the given graphs and analyze how the performance of the algorithm is affected when different parameters are changed, specially the effect of simulated annealing. 
2. Currently, Ja-Be-Ja uses a linear function to decrease the temperature and the temperature is multiplied to the cost function. We will also analyze how changing the simulated annealing parameters and the acceptance probability function affects the performance of Ja-Be-Ja. We implement a different simulated mechanism and observe how this change affects the rate of convergence. 
3. We define our own acceptance probability function or change the Ja-Be-Ja algorithm (in order to improve its performance) and evaluate how our changes affect the performance of graph partitioning. 
