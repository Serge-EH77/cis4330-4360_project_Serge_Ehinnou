import joblib
import pandas as pd
from skl2onnx import convert_sklearn
from skl2onnx.common.data_types import FloatTensorType

# -----------------------------
# 1. Load trained model
# -----------------------------
model = joblib.load("trained_model.pkl")

# -----------------------------
# 2. Load feature names
# -----------------------------
feature_names = pd.read_csv("feature_names.csv", header=None)[0].tolist()
n_features = len(feature_names)

print("Number of features:", n_features)

# -----------------------------
# 3. Define ONNX input type
# -----------------------------
initial_type = [('input', FloatTensorType([None, n_features]))]

# -----------------------------
# 4. Convert to ONNX
# -----------------------------
onnx_model = convert_sklearn(model, initial_types=initial_type)

# -----------------------------
# 5. Save ONNX model
# -----------------------------
with open("context_model.onnx", "wb") as f:
    f.write(onnx_model.SerializeToString())

print("Saved context_model.onnx")
