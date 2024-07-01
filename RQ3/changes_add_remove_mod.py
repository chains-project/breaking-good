# import json
# import pandas as pd
# import matplotlib.pyplot as plt
# import seaborn as sns
#
# # Lee los datos del archivo JSON
# with open('/Users/frank/Documents/Work/PHD/Explaining/breaking-good/transitive_changes_analysis.json', 'r') as file:
#     data = json.load(file)
#
# # Preparación de los datos en un formato adecuado para pandas
# keys = []
# adds = []
# modifies = []
# removes = []
#
# for key, values in data.items():
#     keys.append(key)
#     adds.append(values['ADD'])
#     modifies.append(values['MODIFY'])
#     removes.append(values['REMOVED'])
#
# # Creación de un DataFrame de pandas
# df = pd.DataFrame({
#     'Key': keys,
#     'Added': adds,
#     'Modify': modifies,
#     'Removed': removes
# })
#
# # Preparación de los datos para el gráfico de violín
# data_for_violin = pd.melt(df, id_vars=['Key'], var_name='Operation', value_name='Count')
#
# # Creación del gráfico de violín
# plt.figure(figsize=(10, 6))
# # plt.title('Distribución de operaciones por llave')
# sns.violinplot(x='Operation', y='Count', data=data_for_violin)
# plt.xlabel('Tipo de Operación')
# plt.ylabel('Cantidad')
# plt.grid(True)
# plt.show()


import json
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# Leer datos desde el archivo JSON
with open('/Users/frank/Documents/Work/PHD/Explaining/breaking-good/transitive_changes_analysis.json', 'r') as file:
    data = json.load(file)

# Preparar los datos en un formato adecuado para pandas
keys = []
adds = []
modifies = []
removes = []

for key, values in data.items():
    keys.append(key)
    adds.append(values['ADD'])
    modifies.append(values['MODIFY'])
    removes.append(values['REMOVED'])

# Crear un DataFrame de pandas
df = pd.DataFrame({
    'Key': keys,
    'Added': adds,
    'Modify': modifies,
    'Removed': removes
})

# Filtrar valores para mostrar solo valores mayores que 0
df = df[(df['Added'] > 0) | (df['Modify'] > 0) | (df['Removed'] > 0)]

# Preparar los datos para el gráfico de violín
data_for_violin = pd.melt(df, id_vars=['Key'], var_name='Operation', value_name='Count')

# Filtrar nuevamente para mostrar solo valores mayores que 0 en el gráfico de violín
data_for_violin = data_for_violin[data_for_violin['Count'] > 0]

# Calcular las medianas por tipo de operación
medians = data_for_violin.groupby('Operation')['Count'].median().to_dict()

# Crear el gráfico de violín usando seaborn
plt.figure(figsize=(12, 8))
plt.title('Distribución de operaciones por llave', fontsize=16)
sns.violinplot(x='Operation', y='Count', data=data_for_violin, palette='muted', cut=0)
plt.xlabel('Tipo de Operación', fontsize=14)
plt.ylabel('Cantidad', fontsize=14)
plt.xticks(fontsize=12)
plt.yticks(fontsize=12)
plt.grid(True)

# Añadir leyenda con medianas fuera del gráfico
legend_text = []
for operation, median in medians.items():
    legend_text.append(f'{operation}: {median}')

plt.legend(legend_text, title='Median value for operation', title_fontsize='large', fontsize='large', loc='upper left')

plt.tight_layout()
plt.show()
