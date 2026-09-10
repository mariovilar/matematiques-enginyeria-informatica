
# Heart disease prediction using Random Forests
# 
# This code is accompanied with several tips including classes, functions and methods to use. Please note that you do not have to follow these tips, but they might be handy in some cases.
# 
# Author: Polyxeni Gkontra (polyxeni.gkontra@ub.edu)

import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.utils import shuffle
from sklearn.preprocessing import OrdinalEncoder, MinMaxScaler
from sklearn.compose import ColumnTransformer
from sklearn.ensemble import RandomForestClassifier
from sklearn.pipeline import Pipeline
from sklearn.metrics import classification_report, roc_curve
from sklearn.metrics import accuracy_score
from matplotlib import pyplot as plt

# Read the .csv file with the patient information (TIP: you can use method [read_csv](https://pandas.pydata.org/docs/reference/api/pandas.read_csv.html) from pandas) 


# Explore your data. E.g. Print the data or few lines to see how it looks like (TIP: If you want to see just few lines consider method [head()](https://pandas.pydata.org/docs/reference/api/pandas.DataFrame.head.html#pandas.DataFrame.head) from pandas. Attribute [dtypes](https://pandas.pydata.org/docs/reference/api/pandas.DataFrame.dtypes.html) and function [describe()](https://pandas.pydata.org/docs/reference/api/pandas.DataFrame.describe.html#pandas.DataFrame.describe) from the same library are useful to check the type of the data in each column and statistical properties, respectively)


# Check statistical details on your data like counts, min, max etc

# Check the type of the features

# Check how many patients you have from each category

# Split your dataset into training and testing (TIP: You can use [train_test_split](https://scikit-learn.org/stable/modules/generated/sklearn.model_selection.train_test_split.html) method from scikit-learn. Please note that good ML practices suggest to always shuffle your data. To this end, for dataframes you can use [sample](https://pandas.pydata.org/docs/reference/api/pandas.DataFrame.sample.html) with the parameter frac as 1, but watch out to add reset_index(drop=True) to reset the index)

# Shuffle the data 


# Separate features from output 


# Indices of training subjects and of testing


# Balance the training set (TIP: You can drop patients from the majority class). You can skip this step and use "class_weight" parameter of your classifier to deal with data imbalance
# To make things easier, concatenate X_train and Y_train

# Delete the subjects from the majority class so that the number of subjects in both classes is equal


# Shuffle again as above


# Split the dataframe into X_train and Y_train again: For Y_train just keep column 'HeartDisease', For X_train just drop this column


# Confirm the number of samples you have in each class


# Data pre-processing 
# 
# 1.   Encode categorical variables  (TIP: Check [OrdinalEncoder](https://scikit-learn.org/stable/modules/generated/sklearn.preprocessing.OrdinalEncoder.html#sklearn-preprocessing-ordinalencoder) from sklearn.preprocessing. Another popular approaches is OneHotEncoder but not appropriate for tree based classifiers, can you imagine why?)
# 2.   Scale numerical data (TIP: Check [MinMaxScaler](https://scikit-learn.org/stable/modules/generated/sklearn.preprocessing.MinMaxScaler.html#sklearn.preprocessing.MinMaxScaler))
# 
# TIP: 1. It is very important to treat testing and training data separately to avoid data leakage 2. Methods [fit_transform](https://scikit-learn.org/stable/modules/generated/sklearn.preprocessing.MinMaxScaler.html#sklearn.preprocessing.MinMaxScaler.fit) (for training data) and [tranfrom](https://scikit-learn.org/stable/modules/generated/sklearn.preprocessing.MinMaxScaler.html#sklearn.preprocessing.MinMaxScaler.fit) (for the testing data) can be very helpful. Alternatively you can use Pipeline and ColumnTransformer from sklearn but it will be more complicated to retrieve feature names for the most important features
# 
# 

# Indices of categorical and numerical columns


# Handle categorical variables

# Normalize the numerical valyes


# Train the model (TIP: Check method [fit](https://scikit-learn.org/stable/modules/generated/sklearn.preprocessing.MinMaxScaler.html#sklearn.preprocessing.MinMaxScaler.fit))

# Random forest classifier with 100 trees, random_state is set to be able to reproduce your results

# Apply the model to the testing data and evaluate its perfromance (TIP: You might use [classification_report](https://scikit-learn.org/stable/modules/generated/sklearn.metrics.classification_report.html#sklearn-metrics-classification-report) or [roc_auc_score](https://scikit-learn.org/stable/modules/generated/sklearn.metrics.roc_auc_score.html#sklearn.metrics.roc_auc_score))

# Make the prediction

# Evaluate the performance by comparing the predicted labels with the true ones


# Get the feature importance in the model's estimation and plot the most important features (TIP: To get feature importance and names you can use feature_importances_ and feature_names_in attributes of your model, respectively)

# Sort the features in descending order of importance




