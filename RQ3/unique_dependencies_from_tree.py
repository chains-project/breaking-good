import json

# Define the path to the input file
file_path = 'treeFile2.txt'

# Read the file content
with open(file_path, 'r') as file:
    lines = file.readlines()

# Initialize a dictionary to store the dependencies and their details
dependencies_dict = {}

# Helper function to clean and split the dependency line
def parse_dependency(line):
    line = line.strip()
    parts = line.split(':')
    return parts if len(parts) >= 4 else None

# Process the lines to fill the dictionary
for line in lines:
    line = line.strip()
    # Consider only lines that represent dependencies and not start with tree characters
    if not line.startswith(('+', '|', '\\', ' ')):
        parts = parse_dependency(line)
        if parts:
            group = parts[0]
            artifact = parts[1]
            packaging = parts[2]
            version = parts[3]
            scope = ':'.join(parts[4:]) if len(parts) > 4 else ''

            # Creating the key and value for the dictionary
            dependency_key = f"{group}:{artifact}:{packaging}"
            dependency_value = {
                "group": group,
                "artifact": artifact,
                "packaging": packaging,
                "version": version,
                "scope": scope
            }

            dependencies_dict[dependency_key] = dependency_value
    # Handle indented dependency lines
    elif line.startswith(('+', '|', '\\', ' ')):
        line = line.lstrip('+-| \\ ')
        parts = parse_dependency(line)
        if parts:
            group = parts[0]
            artifact = parts[1]
            packaging = parts[2]
            version = parts[3]
            scope = ':'.join(parts[4:]) if len(parts) > 4 else ''

            # Creating the key and value for the dictionary
            dependency_key = f"{group}:{artifact}:{packaging}"
            dependency_value = {
                "group": group,
                "artifact": artifact,
                "packaging": packaging,
                "version": version,
                "scope": scope
            }

            dependencies_dict[dependency_key] = dependency_value

# Convert the dictionary to a JSON string
dependencies_json = json.dumps(dependencies_dict, indent=4)

# Save the JSON to a file
output_path = 'dependencies_2.json'
with open(output_path, 'w') as output_file:
    output_file.write(dependencies_json)

dependencies_json
