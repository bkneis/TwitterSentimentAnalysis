# Twitter Sentiment Analysis

### Description
Apache spark application built in Java using OpenNLP to analyse the sentiment of tweets. The idea is to compare 2 targets, at the time this was for the 2016 US Election, and determine their social media approval and save this information on a per state basis in a database.

### Details
The sentiment analyser utilizes a bag of words approach with a naives bayes algorithm to train a model to determine how close a string is in terms of sentiment to its training set. My application is extremely naive and a very small data set (only a 100 or 2 tweets). Additionally the training set is either 0 or 1 instead of a scale, as this eases the process of classification and training. Further work to improve this application would be to train the model using a more sophisticated algorithm, such as the one provided by standford NLP toolkit and with a larger database, such as a movie review database.

#### Requirments
Apache Spark for the streaming and batch processing framework

Open NLP for the sentiment analysis

Cassandra or Influx for the database

Java9

A few other small libraries listed in the pom

