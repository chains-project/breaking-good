import os
import json

def search_line_in_files(folder, search_line):
    file_names = set()  # Conjunto para almacenar nombres de archivos sin extensión

    # Check if the folder exists
    if not os.path.isdir(folder):
        print(f"The folder '{folder}' does not exist.")
        return file_names

    # Iterate over each file in the folder
    for filename in os.listdir(folder):
        file_path = os.path.join(folder, filename)

        # Check if it is a file
        if os.path.isfile(file_path):
            # Guardar el nombre de archivo sin extensión
            file_name_without_extension = os.path.splitext(filename)[0]
            file_names.add(file_name_without_extension)

    return file_names

def add_werror_property_to_json(input_file, logs_folder, search_line):
    # Leer el JSON de entrada desde el archivo
    with open(input_file, 'r') as f:
        json_data = json.load(f)

    # Obtener nombres de archivo sin extensión de los logs
    commit_files = search_line_in_files(logs_folder, search_line)

    # Agregar la propiedad 'werror' al JSON según los archivos de commit encontrados
    for item in json_data:
        commit = item.get('commit', '')
        if commit in commit_files:
            item['werror'] = True
        else:
            item['werror'] = False

    return json_data

def save_json_to_file(json_data, output_file):
    # Escribir el JSON actualizado en un archivo
    with open(output_file, 'w') as f:
        json.dump(json_data, f, indent=4)

if __name__ == "__main__":
    # Rutas de los archivos y carpetas
    input_file = '/Users/frank/Documents/Work/PHD/Explaining/breaking-good/explanationStatistics-data.json'
    logs_folder = '/Users/frank/Documents/Work/PHD/chains-project/paper/bump/reproductionLogs/successfulReproductionLogs'
    search_line = 'warnings found and -Werror specified'
    output_file = 'output.json'

    # Agregar la propiedad 'werror' al JSON de entrada
    updated_json = add_werror_property_to_json(input_file, logs_folder, search_line)

    # Guardar el JSON actualizado en un archivo de salida
    save_json_to_file(updated_json, output_file)

    print(f"Updated JSON saved to '{output_file}'.")
