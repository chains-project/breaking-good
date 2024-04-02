import os
import json
import csv

def process_json_files(folder_path):
    data_list = []
    for filename in os.listdir(folder_path):
        if filename.endswith('.json'):
            file_path = os.path.join(folder_path, filename)
            with open(file_path, 'r') as file:
                try:
                    data = json.load(file)
                    change_count = len(data.get('change', []))
                    breaking_change_count = len(data.get('breakingChange', []))
                    data_list.append((os.path.splitext(filename)[0], change_count, breaking_change_count))
                except json.decoder.JSONDecodeError:
                    print(f"Skipping {filename}: Unable to parse as JSON")

    write_to_csv(data_list)


def write_to_csv(data_list):
    # Open a CSV file in write mode
    with open('output.csv', mode='w', newline='') as file:
        # Create a CSV writer object
        writer = csv.writer(file)
        # Write the header row
        writer.writerow(['Filename', 'Number Broken use in client ', 'Breaking Change Count'])
        # Write data rows
        for item in data_list:
            writer.writerow(item)

# Change the folder path according to your configuration
folder_path = "/Users/frank/Documents/Work/PHD/Tools/bump_experiments/src/main/java/maracas/breaking_changes_compilation_errors"
process_json_files(folder_path)
