import csv
import pandas as pd
import numpy as np

from sklearn import metrics
from sklearn.naive_bayes import GaussianNB


textfile = open("train_data.txt", "r")
lines = textfile.readlines()
lines = [lines.split("\t" for line in lines)]
df = pd.DataFrame(lines)

# data = []
# for line in textfile:
#     line.split("\t")
#     data.append(line)

#Just some useful links
#https://dzone.com/articles/naive-bayes-tutorial-naive-bayes-classifier-in-pyt
#https://scikit-learn.org/stable/modules/generated/sklearn.naive_bayes.GaussianNB.html