import csv
from datetime import datetime, timedelta
import matplotlib.pyplot as plt
import random

# Configuración de parámetros
start_date = datetime.now() - timedelta(days=7)  # Inicio hace 7 días
end_date = datetime.now()  # Fecha actual
dias_laborales = {0, 1, 2, 3, 4}  # Días laborales (lunes a viernes)

# Variables de control
data = []
current_datetime = start_date.replace(minute=0, second=0, microsecond=0)

# Generar datos por hora
while current_datetime <= end_date:
    is_weekend = current_datetime.weekday() not in dias_laborales
    hora = current_datetime.hour

    # Inicializar afluencia
    afluencia = None

    if is_weekend:
        # Patrón de fin de semana
        if 0 <= hora < 6:  # Madrugada
            afluencia = random.randint(10, 20)
        elif 6 <= hora < 12:  # Mañana
            afluencia = random.randint(20, 40)
        elif 12 <= hora < 18:  # Tarde
            afluencia = random.randint(40, 60)
        else:  # Noche
            afluencia = random.randint(10, 30)
    else:
        # Patrón de día laboral
        if 0 <= hora < 8:  # Madrugada y temprano
            afluencia = random.randint(10, 30)
        elif 8 <= hora <= 9:  # Incremento hasta las 9:00
            afluencia = int((hora - 8) * 40 + 30)  # 30% a las 8:00, 70% a las 9:00
        elif 9 < hora <= 13:  # Mantener afluencia alta hasta las 13:00
            afluencia = 70
        elif 13 < hora <= 18:  # Descenso gradual después de las 13:00
            afluencia = max(70 - (hora - 13) * 10, 30)  # Baja de 70% a 30%
        else:  # Noche
            afluencia = random.randint(20, 40)

    # Agregar registro
    data.append(["217fa335-bcb3-4aca-8f17-c0fc62dcb8fa", afluencia, current_datetime])

    # Avanzar una hora
    current_datetime += timedelta(hours=1)

# Guardar datos en un archivo CSV
filename = "parking_afluencia_final.csv"
with open(filename, "w", newline="") as file:
    writer = csv.writer(file)
    writer.writerow(["parking_id", "afluencia", "datetime"])
    writer.writerows([[row[0], row[1], row[2].strftime("%Y-%m-%d %H:%M:%S")] for row in data])

print(f"Archivo generado y guardado como {filename}")

# Graficar los datos
timestamps = [row[2] for row in data]
afluencias = [row[1] for row in data]

plt.figure(figsize=(14, 7))
plt.plot(timestamps, afluencias, label="Afluencia (%)", color="blue")
plt.title("Afluencia de parking en los últimos 7 días", fontsize=16)
plt.xlabel("Fecha y hora", fontsize=12)
plt.ylabel("Afluencia (%)", fontsize=12)
plt.axhline(100, color="red", linestyle="--", label="Capacidad máxima (100%)")  # Línea de capacidad máxima
plt.xticks(rotation=45, fontsize=10)
plt.yticks(fontsize=10)
plt.grid(True)
plt.legend(fontsize=12)
plt.tight_layout()
plt.show()
