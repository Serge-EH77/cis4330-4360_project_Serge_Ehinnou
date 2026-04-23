import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import classification_report
import joblib

# 1. Load dataset
df = pd.read_csv("merged_dataset.csv")

# --- FIX: Remove accidental index column(s) ---
df = df.loc[:, ~df.columns.str.contains("^Unnamed")]
df = df.drop(columns=["0"], errors="ignore")

# Drop time column (not a feature)
df = df.drop(columns=["time"], errors="ignore")

# 2. Fill missing values instead of dropping rows
df = df.fillna(df.mean(numeric_only=True))

# 3. Split features and labels
X = df.drop(columns=["activity"])
y = df["activity"]

print("Samples:", len(df))
print("Features:", X.shape[1])
print("Classes:", y.unique())

# 4. Train/test split (no stratify because dataset is tiny)
X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size=0.33, random_state=42
)

# 5. Train model
model = RandomForestClassifier(
    n_estimators=200,
    max_depth=10,
    random_state=42
)
model.fit(X_train, y_train)

# 6. Evaluate
preds = model.predict(X_test)
print("\nClassification Report:\n")
print(classification_report(y_test, preds))

# 7. Save model + feature order
joblib.dump(model, "trained_model.pkl")
X.columns.to_series().to_csv("feature_names.csv", index=False)

print("\nSaved trained_model.pkl and feature_names.csv")
