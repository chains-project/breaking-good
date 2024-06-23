import json

# Nombre del archivo JSON de entrada
input_json_file = '../explanationStatistics-data.json'

# Nombre del archivo de texto con los commits
commits_file = '/Users/frank/Documents/Work/PHD/Explaining/breaking-good/client_java_version_failure.txt'

# Cargar datos del archivo JSON
with open(input_json_file, 'r') as json_file:
    data = json.load(json_file)

# Obtener la lista de commits desde el archivo de texto
with open(commits_file, 'r') as commits_txt:
    commits = {line.strip() for line in commits_txt}

# Procesar cada objeto en el JSON
for item in data:
    commit_value = item.get('commit')
    if commit_value in commits:
        item['java'] = True
    else:
        item['java'] = False

# Guardar los datos actualizados en el mismo archivo JSON
with open(input_json_file, 'w') as json_file:
    json.dump(data, json_file, indent=4)

print("JSON actualizado con éxito.")
