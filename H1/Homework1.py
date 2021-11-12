from numpy import unique
from pandas import read_csv
import copy, io
import random, math
from itertools import chain
import pandas as pd

import classesFile as cl
import time


def main_func():
    url = 'https://storage.googleapis.com/dataset-uploader/bbc/bbc-text.csv'
    df = read_csv(url)
    print(df.head())


    #get the first 10 columns
    df_len_10 = copy.deepcopy(df)
    df_len_10 = df['text'].iloc[:100]

    num_docs = input("Input number of documents to be compared (max:100):")
    start_time = time.time()

    sets = []
    for i in range(int(num_docs)):
        sample_text = io.StringIO(df_len_10[i])
        # Construct 10-shingles
        sh = cl.Shingling(sample_text,3) 
        shingles = sh.shingles() 
        # Ordered set of hashed 100-shingles
        ordered_set = sh.ordered_hash(shingles)
        sets.append(ordered_set)

    df = pd.DataFrame(sets).transpose()


    l = cl.LHS(df, 200, 0.6)
    l.candidates()
    l.check_similarity()


    print("--- %s seconds ---" % (time.time() - start_time))
    '''
    #get shingles and compute Jaccard similarity
    text1 = io.StringIO(df_len_10[0])
    text2 = io.StringIO(df_len_10[1])
    sh1 = cl.Shingling(text1,10)
    shingles1 = sh1.shingles()
    ordered = sh1.ordered_hash(shingles1)
    #print("ordered=",ordered)
    sh2 = cl.Shingling(text2,10)
    shingles2 = sh2.shingles()
    Jac = cl.CompareSetsInput(shingles1, shingles2)
    Sim = Jac.Jac_Sim()
    print("Jaccars Similarity=",Sim)
    '''

    '''
    #q3
    sample_text = io.StringIO(df_len_10[0])
    # Construct 10-shingles
    sh = cl.Shingling(sample_text,10) 
    shingles = sh.shingles() 
    # Ordered set of hashed 100-shingles
    ordered_set = sh.ordered_hash(shingles) 
    mh = cl.MinHashing(ordered_set,20)
    minhash = mh.hashing()
    print("MinHash of first document:",minhash)
    '''

    '''
    #q4
    sets = []
    for i in range(2):
        sample_text = io.StringIO(df_len_10[i])
        # Construct 10-shingles
        sh = cl.Shingling(sample_text,10) 
        shingles = sh.shingles() 
        # Ordered set of hashed 100-shingles
        ordered_set = sh.ordered_hash(shingles)
        sets.append(ordered_set)
    mh1 = cl.MinHashing(sets[0],20).hashing()
    mh2 = cl.MinHashing(sets[1],20).hashing()
    print(cl.CompareSignatures(mh1,mh1).estimate())
    print(cl.CompareSignatures(mh1,mh2).estimate())
    '''

    '''
    #input signatures in LHS
    df = pd.DataFrame(sets).transpose()   #sets from q4


    #q5 candidates and their similarity
    l = cl.LHS(df, 200, 0.8)
    l.candidates()
    l.check_similarity()

    #validation of LHS
    df_with_duplicates = copy.deepcopy(df) 
    df_with_duplicates[2] = df_with_duplicates[0]
    df_with_duplicates[3] = df_with_duplicates[1]
    print(df_with_duplicates.head())
    print(df.head())

    l_test = cl.LHS(df_with_duplicates, 200, 0.8)
    l_test.candidates()
    l_test.check_similarity()
    '''

if __name__=="__main__":
    main_func()


