import csv
import pandas as pd
import numpy as np

from sklearn import metrics
from sklearn.naive_bayes import GaussianNB
from sklearn.model_selection import train_test_split

class Read_Data():
    '''
    Reads in the data in filename and divides it to a
    train set and a test set.
    '''

    def __init__(self, filename):
        textfile = open(filename, "r")
        lines = textfile.readlines()

        # rstrip() removes \n if it is in the string, then we split the string at \t
        self.data = [line.rstrip('\n').split("\t") for line in lines]
        # making it an array which makes it easier to work with (e.g., for indexing)
        self.data = np.array(self.data)
        print(self.data.shape)

    def get_data(self):
        return self.data

    def get_train_and_test(self):
        # TODO: how  do we want to "feed" the classifier? Should we use trigrams?
        # In that case: at which point do we create the trigrams? (each trigram needs to be
        # associated to the correct label)
        # make separate class for this

        # TODO: make sure train and test set are balanced (50/50 dutch/non-dutch)
        train, test = train_test_split(self.data, train_size = 0.8)
        # labels are column 0
        X_train = train[:,1]
        y_train = train[:,0]
        X_test = test[:,1]
        y_test = test[:,0]

        return X_train, y_train, X_test, y_test


class Naive_Bayes():
    def __init__(self, X_train, y_train, X_test, y_test):
        self.X_train, self.y_train, self.X_test, self.y_test = X_train, y_train, X_test, y_test

    # TODO: train and test classifier
    #def train(self):
        # use GaussianNB?

        #return accuracy?

    #def test(self):

        #return accuracy?

reader = Read_Data('train_data.txt')
X_train, y_train, X_test, y_test = reader.get_train_and_test()


#Just some useful links
#https://dzone.com/articles/naive-bayes-tutorial-naive-bayes-classifier-in-pyt
#https://scikit-learn.org/stable/modules/generated/sklearn.naive_bayes.GaussianNB.html
