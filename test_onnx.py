import pandas as pd

feature_names = pd.read_csv("feature_names.csv", header=None)[0].tolist()

print("Number of features:", len(feature_names))
for i, f in enumerate(feature_names):
    print(i, f)
