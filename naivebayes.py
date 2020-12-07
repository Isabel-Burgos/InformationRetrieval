import re # regular expressions
import numpy as np

from sklearn.model_selection import train_test_split
from sklearn.pipeline import Pipeline
from sklearn.naive_bayes import MultinomialNB
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.metrics import confusion_matrix, classification_report

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
    

# definition for analyzer to tokenize a URL
def words_and_char_trigrams(text):
    words = re.findall(r'(?!www|index|html|htm|http|https)([a-zA-Z]{2,})', text)
    for w in words:
        yield w
        for i in range(len(w) - 3):
            yield w[i:i+3]

# create train and test dataset
reader = Read_Data('train_data.txt')
X_train, y_train, X_test, y_test = reader.get_train_and_test()

# create a CountVectorizer which converts the urls to a vector of term counts 
count_vector = CountVectorizer(analyzer=words_and_char_trigrams)

# define the pipeline such that the urls will be tokenized and classified
pipeline = Pipeline([
    ('vectorizer', count_vector),
    ('model', MultinomialNB())
])

# train and test the model
pipeline.fit(X_train, y_train)
y_pred = pipeline.predict(X_test)

# print the evavluation metrics
print(confusion_matrix(y_test,y_pred))
print(classification_report(y_test,y_pred))

