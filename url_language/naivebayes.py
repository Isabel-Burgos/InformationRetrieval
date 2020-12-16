import re # regular expressions
import numpy as np

from sklearn.model_selection import train_test_split
from sklearn.pipeline import Pipeline
from sklearn.naive_bayes import MultinomialNB
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.metrics import confusion_matrix, classification_report

class ReadData():
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

    def get_data(self):
        return self.data

    def get_train_and_test(self):
        train, test = train_test_split(self.data, train_size = 0.8, random_state=42)
        # labels are column 0
        X_train = train[:,1]
        y_train = train[:,0]
        X_test = test[:,1]
        y_test = test[:,0]

        return X_train, y_train, X_test, y_test

class URLParser():
    '''
    Parses the URL(s) to tokens. Implementation is based on Baykan paper, page 178.
    '''

    def __init__(self):
        self.tabu_list = ['www','index','html','htm','http','https']

    # definition for analyzer to tokenize a URL
    def words_and_char_trigrams(self, text):
        '''
        Splits each URL in text to tokens consisting of letter characters only.
        '''

        words = re.findall(r'([a-zA-Z]{2,})', text)
        for w in words:
            if not w in self.tabu_list:
                yield w

            # Add this if we want to split to trigrams
                #for i in range(len(w) - 3):
                #    yield w[i:i+3]

class NaiveBayes():
    '''
    The naive Bayes is implemented to fit for the purpose of this project.
    As part of the process, the urls are parsed to tokens which are used
    when training and predicting.
    '''

    def train(self):
        '''
        Trains the naive Bayes classifier given the whole train_data set.
        Uses a MultinomialNB to classify the urls.
        '''

        reader = ReadData('train_data.txt')
        data = reader.get_data()
        X = data[:,1]
        y = data[:,0]

        parser = URLParser()
        count_vector = CountVectorizer(analyzer=parser.words_and_char_trigrams)

        self.pipeline = Pipeline([
            ('vectorizer', count_vector),
            ('model', MultinomialNB())
        ])

        self.pipeline.fit(X,y)

    def predict(self, url):
        return self.pipeline.predict(url)


# ------------- Code to test if crawling will work ----------- #
### comment these lines when you want to run crawler

# test_url_nd = ['https://www.w3schools.com/python/python_regex.asp']
# test_url_d = ['https://www.ru.nl/opleidingen/masteropleidingen/zoeken-masteropleidingen/']

# nb = NaiveBayes()
# nb.train()

# pred_nd = nb.predict(test_url_nd)
# pred_d = nb.predict(test_url_d)
# print('non dutch is predicted as:',pred_nd)
# print('dutch is predicted as:',pred_d)

# ----------------- Code to test classifier ------------------ #
### uncomment lines below if you want to run test

# # create train and test dataset
# reader = ReadData('train_data.txt')
# X_train, y_train, X_test, y_test = reader.get_train_and_test()
#
# # create a CountVectorizer which converts the urls to a vector of term counts
# parser = URLParser()
# count_vector = CountVectorizer(analyzer=parser.words_and_char_trigrams)
#
# # define the pipeline such that the urls will be tokenized and classified
# pipeline = Pipeline([
#     ('vectorizer', count_vector),
#     ('model', MultinomialNB())
# ])
#
# # train and test the model
# pipeline.fit(X_train, y_train)
# y_pred = pipeline.predict(X_test)
#
# # print the evavluation metrics
# print(confusion_matrix(y_test,y_pred))
# print(classification_report(y_test,y_pred))
