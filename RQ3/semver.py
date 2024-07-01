import json
import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns


# Function to load data from JSON file
def load_data_from_json(file_path):
    with open(file_path, 'r') as f:
        data = json.load(f)
    return data


# Function to prepare data for violin plot
def prepare_data_for_plot(data):
    commits = []
    total_changes = []
    change_type = []

    for commit, changes in data.items():
        version_only = changes.get('VERSION_ONLY', 0)
        scope_only = changes.get('SCOPE_ONLY', 0)
        total = version_only + scope_only

        # Add data for total changes (sum of VERSION_ONLY and SCOPE_ONLY)
        # commits.append(commit[:7])  # Take first 7 characters of commit hash as identifier
        # total_changes.append(total)
        # change_type.append('TOTAL')

        # Add data for MAJOR changes
        commits.append(commit[:7])
        total_changes.append(changes.get('MAJOR', 0))
        change_type.append('MAJOR')

        # Add data for MINOR changes
        commits.append(commit[:7])
        total_changes.append(changes.get('MINOR', 0))
        change_type.append('MINOR')

        # Add data for PATCH changes
        commits.append(commit[:7])
        total_changes.append(changes.get('PATCH', 0))
        change_type.append('PATCH')

    # Create a DataFrame for seaborn
    df = pd.DataFrame({'Commit': commits, 'Total Changes': total_changes, 'Change Type': change_type})
    return df


# Function to generate violin plot
def generate_violin_plot(df):
    plt.figure(figsize=(12, 8))
    sns.violinplot(x='Change Type', y='Total Changes', data=df, scale='width')
    plt.title('Distribution of Changes by Type (MAJOR, MINOR, PATCH)')
    plt.xlabel('Change Type')
    plt.ylabel('Total Changes')
    plt.grid(True)
    plt.tight_layout()


# Function to save plot as PDF
def save_plot_as_pdf(file_path):
    plt.savefig(file_path, format='pdf')


# Path to JSON file
json_file = '/Users/frank/Documents/Work/PHD/Explaining/breaking-good/breaking_changes_analysis.json'

# Load data from JSON file
data = load_data_from_json(json_file)

# Prepare data for violin plot
df = prepare_data_for_plot(data)

# Generate and display violin plot
generate_violin_plot(df)

# Save the plot as PDF
pdf_file = 'violin_plot_changes.pdf'
save_plot_as_pdf(pdf_file)

# Show confirmation message
print(f'Violin plot saved as "{pdf_file}".')

# Optionally display the plot (uncomment the line below)
plt.show()
