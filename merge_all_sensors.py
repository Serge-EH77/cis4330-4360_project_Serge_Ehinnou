import pandas as pd
import os

# ---- CONFIG ----
FOLDERS = {
    "classroom": r"AutoWeeFee/Classroom",
    "driving": r"AutoWeeFee/Transit"
}

OUTPUT_FILE = "merged_dataset.csv"

script_dir = os.path.dirname(os.path.abspath(__file__))

def load_sensor_file(path):
    """Load a 1-second sensor CSV and ensure timestamp is datetime index."""
    df = pd.read_csv(path)
    
    if df.columns[0].lower() == 'time':
        # Normal file
        time_col = 'time'
    else:
        # Malformed file with extra headers
        # Read again skipping the extra rows
        df_temp = pd.read_csv(path, header=None, skiprows=3)
        # Get column names from first two rows
        row1 = pd.read_csv(path, header=None, nrows=1).iloc[0]
        row2 = pd.read_csv(path, header=None, skiprows=1, nrows=1).iloc[0]
        columns = ['time']
        for i in range(1, len(row1)):
            col_name = str(row1[i]).lower() + '_' + str(row2[i]).lower()
            columns.append(col_name)
        df_temp.columns = columns
        df = df_temp
        time_col = 'time'
    
    df[time_col] = pd.to_datetime(df[time_col])
    df = df.set_index(time_col)
    return df


all_data = []

for label, folder in FOLDERS.items():
    folder = os.path.join(script_dir, folder)
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
