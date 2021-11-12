#CLASSES
import random, math
import io
from itertools import chain
import pandas as pd

#QUESTION 1
class Shingling:

    def __init__(self, input_text, input_k):
        self.text = input_text
        self.k = input_k


    def shingles(self):
        for line in self.text:
            return {line[i:i + self.k] for i in range(len(line) - self.k + 1)}


    def ordered_hash(self, input_sh):
        a = random.randint(0,100)
        b = random.randint(0,100)        #convert string into int
        return set(sorted(set((7*(int.from_bytes(x.encode(), 'little')) +13)%1039205197 for x in input_sh)))

#QUESTION 2
'''
class CompareSets:

    @staticmethod
    def Jaccard(set1, set2):
        #return len(set1)/len(set2)
        return len(set1.intersection(set2)) / len(set1.union(set2))
'''
class CompareSetsInput:
    def __init__(self, set1, set2):
        self.seta = set1
        self.setb = set2

    def Jac_Sim(self):
        return len(self.seta.intersection(self.setb)) / len(self.seta.union(self.setb))


#Question 3
class MinHashing:

    def __init__(self, input_set, input_n):
        self.s = input_set
        self.n = input_n

    def hashing(self):
        min_hash = [None] * self.n # Vector of length n
        random.seed(111)
        for n in range(self.n):
            a = random.randint(0, 2**32-1) # Max shingle id
            b = random.randint(0, 2**32-1)
            vector = [None] * len(self.s) # Vector of length set hashed shingles
            for i, value in enumerate(self.s):
                hashcode = ((a * value + b) % 4294967311) # Make sure that prime > max shingle id
                vector[i] = hashcode
            min_hash[n] = min(vector)
        return min_hash

#Question 4
class CompareSignatures:
    def __init__(self, vector1, vector2):
        self.v1 = vector1
        self.v2 = vector2

    def estimate(self):
        count = 0
        for i, elem in enumerate(self.v1):
            if self.v1[i] == self.v2[i]: # Assumed that length of both vectors are the same
                count += 1

        return count/len(self.v1)

#Qustion 5

class LHS:
    def __init__(self, signatures, n, threshold):
        self.signatures = signatures
        self.n = n
        self.threshold = threshold

        self.cand_count = 0


    def candidates(self):
        b, total_rows, total_docs = self.n, self.signatures.count().min(), len(self.signatures.columns)
        r = math.floor(total_rows / b)

        bucket = {} #dictionary
        cand = []

        for i in range(b):
            band = self.signatures[r*i:r*(i+1)]   #bands

            for j in range(total_docs):
                bucket[j] = hash(tuple(band[j].values)) #hash content of bands-->same bands will have same hash value


            for f in range(total_docs):
                for s in range(f+1, total_docs):
                    if bucket[f]==bucket[s]:
                        cand.append([f,s])    #get pairs of columns with same hash value (identical bands)

        self.final_cand = [list(x) for x in set(tuple(x) for x in cand)]
        print("number of candidates=",len(self.final_cand))
        return(self.final_cand)
    
    def check_similarity(self):
        if self.final_cand:
            rows = self.signatures.count().min()
            for pair in self.final_cand:
                col1 = pair[0]
                col2 = pair[1]
                mh = []
                for i in [col1,col2]:
                    ordered_set = set(self.signatures[i].iloc[:rows])
                    mh.append(MinHashing(ordered_set,20).hashing())

                if CompareSignatures(mh[0],mh[1]).estimate() <= self.threshold:
                    print("Column",col1,"and",col2,"have Jaccard similarity:",
                         CompareSignatures(mh[0],mh[1]).estimate(),"which is lower than",self.threshold,
                          "so they are not similar")
                else:
                    self.cand_count+=1
                    print("Column",col1,"and",col2,"have Jaccard similarity:",
                          CompareSignatures(mh[0],mh[1]).estimate(),"which is higher than",self.threshold,"so they are similar")
        else:
            print("No candidates")

        rows_min = self.signatures.count().min()
        count = 0
        similar = []
        for f in range(len(self.signatures.columns)):
            for s in range(f+1, len(self.signatures.columns)):
                mh = []
                for i in [f,s]:
                    ordered_set2 = set(self.signatures[i].iloc[:rows_min])
                    mh.append(MinHashing(ordered_set2,20).hashing())
                comp = CompareSignatures(mh[0],mh[1]).estimate()
                print("Column",f,"and",s,"have Jaccard similarity:",
                        comp)
                if comp >= self.threshold:
                    count+=1
                    similar.append([s,f])
        print("#similar=",count,"specifically:", similar,"from them",self.cand_count,"were found by candidates (for finding errors in candidates)")