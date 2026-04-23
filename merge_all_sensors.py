import pandas as pd
import os

# ---- CONFIG ----
FOLDERS = {
    "classroom": r"AutoWeeFee/Classroom",
    "driving": r"AutoWeeFee/Transit"
}

OUTPUT_FILE = "merged_dataset.csv"

def load_sensor_file(path):
    """Load a 1-second sensor CSV and ensure timestamp is datetime index."""
    df = pd.read_csv(path)
    
    # Detect timestamp column
    time_col = None
    for col in df.columns:
        if col.lower() in ["time", "timestamp", "datetime"]:
            time_col = col
            break

    if time_col is None:
        print("❌ No timestamp column in:", path)
        return None

    df[time_col] = pd.to_datetime(df[time_col])
    df = df.set_index(time_col)
    return df


all_data = []

for label, folder in FOLDERS.items():
    print(f"\nProcessing folder: {folder} ({label})")

    # Find all *_1s.csv files
    files = [f for f in os.listdir(folder) if f.endswith("_1s.csv")]

    if not files:
        print("⚠️ No resampled files found in:", folder)
        continue

    merged = None

    for file in files:
        path = os.path.join(folder, file)
        print("  Loading:", file)

        df = load_sensor_file(path)
        if df is None:
            continue

        # Prefix columns with sensor name
        sensor_name = file.replace("_1s.csv", "").lower()
        df = df.add_prefix(sensor_name + "_")

        # Merge into folder-level dataset
        merged = df if merged is None else merged.join(df, how="outer")

    # Add activity label
    merged["activity"] = label

    # Store for final merge
    all_data.append(merged)

# ---- FINAL MERGE ----
final = pd.concat(all_data).sort_index()

# Save
final.to_csv(OUTPUT_FILE)
print("\n✅ Created:", OUTPUT_FILE)
