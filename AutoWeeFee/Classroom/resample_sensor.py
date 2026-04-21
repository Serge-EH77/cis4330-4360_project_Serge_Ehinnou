import pandas as pd
import numpy as np
import os

# Get directory of this script
script_dir = os.path.dirname(os.path.abspath(__file__))

# List all CSV files in the folder
csv_files = [f for f in os.listdir(script_dir) if f.lower().endswith(".csv")]

print("Found CSV files:", csv_files)

for file in csv_files:
    file_path = os.path.join(script_dir, file)
    print("\nProcessing:", file)

    df = pd.read_csv(file_path)

    # Detect timestamp column
    time_col = None
    for col in df.columns:
        if col.lower() in ["time", "timestamp", "date", "datetime"]:
            time_col = col
            break

    if time_col is None:
        print("❌ No timestamp column found, skipping:", file)
        continue

    # Convert timestamp to datetime
    df[time_col] = pd.to_datetime(df[time_col])
    df = df.set_index(time_col)

    # ---- Pre-compute derived columns before resampling ----
    if ("accel" in file.lower() or "gyro" in file.lower() or "gyroscope" in file.lower()) and all(c in df.columns for c in ["x", "y", "z"]):
        df["magnitude"] = np.sqrt(df["x"]**2 + df["y"]**2 + df["z"]**2)

    # Resample into 1-second windows
    grouped = df.resample("1s")

    # ---- ACCELEROMETER ----
    if "accel" in file.lower() and all(c in df.columns for c in ["x", "y", "z"]):
        out = grouped.agg({
            "x": "mean",
            "y": "mean",
            "z": "mean",
            "magnitude": ["mean", "std", "max"]
        })

    # ---- MICROPHONE ----
    elif "mic" in file.lower() or "audio" in file.lower() or "microphone" in file.lower():
        # Find the amplitude-like column
        amp_col = None
        for col in df.columns:
            if "db" in col.lower() or "amp" in col.lower() or "sound" in col.lower():
                amp_col = col
                break

        if amp_col is None:
            print("❌ No amplitude-like column found, skipping:", file)
            continue

        out = grouped.agg({
            amp_col: ["mean", "max"]
        })

    # ---- WIFI ----
    elif "wifi" in file.lower():
        # RSSI column is named "level"
        rssi_col = None
        for col in df.columns:
            if "level" in col.lower() or "rssi" in col.lower() or "signal" in col.lower():
                rssi_col = col
                break

        if rssi_col is None:
            print("❌ No RSSI-like column found, skipping:", file)
            continue

        # Count APs per second
        wifi_count = grouped.size().rename("wifi_count")

        # Strongest RSSI per second
        wifi_rssi = grouped[rssi_col].max().rename("wifi_max_rssi")

        # Combine into one output
        out = pd.concat([wifi_count, wifi_rssi], axis=1)

        
    # ---- GYROSCOPE ----
    elif "gyro" in file.lower() or "gyroscope" in file.lower():
        # Ensure x, y, z exist
        if all(c in df.columns for c in ["x", "y", "z"]):
            out = grouped.agg({
                "x": "mean",
                "y": "mean",
                "z": "mean",
                "magnitude": ["mean", "std", "max"]
            })
        else:
            print("❌ Gyroscope missing x,y,z columns, skipping:", file)
            continue


    # ---- ORIENTATION (quaternions + euler angles) ----
    elif "orient" in file.lower() or "rotation" in file.lower():
        # Normalize quaternion column names
        col_map = {}
        for col in df.columns:
            if col.lower() == "seconds_qz":
                col_map[col] = "qz"
            elif col.lower() == "qx":
                col_map[col] = "qx"
            elif col.lower() == "qy":
                col_map[col] = "qy"
            elif col.lower() == "qw":
                col_map[col] = "qw"

        df = df.rename(columns=col_map)

        # Check quaternion columns
        quat_cols = ["qx", "qy", "qz", "qw"]
        euler_cols = ["roll", "pitch", "yaw"]

        if all(c in df.columns for c in quat_cols):
            # Aggregate quaternions and Euler angles
            agg_dict = {c: "mean" for c in quat_cols}

            # Add Euler angle means
            for c in euler_cols:
                if c in df.columns:
                    agg_dict[c] = "mean"

            out = grouped.agg(agg_dict)

        else:
            print("❌ Missing quaternion columns, skipping:", file)
            continue

    else:
        print("⚠️ Unknown sensor type, skipping:", file)
        continue
    
    
    # Save output
    out_file = file.replace(".csv", "_1s.csv")
    out.to_csv(os.path.join(script_dir, out_file))
    print("✅ Created:", out_file)
