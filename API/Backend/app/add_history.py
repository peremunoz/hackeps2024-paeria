import pandas as pd
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Table, MetaData

# Configuración de la base de datos
DATABASE_USER = "admin"
DATABASE_PASSWORD = "admin123"
DATABASE_HOST = "database"
DATABASE_PORT = "5432"
DATABASE_NAME = "mydatabase"

DATABASE_URL = f"postgresql://{DATABASE_USER}:{DATABASE_PASSWORD}@{DATABASE_HOST}:{DATABASE_PORT}/{DATABASE_NAME}"

# Crear el motor de SQLAlchemy
engine = create_engine(DATABASE_URL)

# Crear una sesión para realizar el DELETE
Session = sessionmaker(bind=engine)
session = Session()

# Leer el archivo CSV
csv_file = "/app/app/parking_afluencia_final.csv" 
data = pd.read_csv(csv_file)

# Renombrar las columnas para que coincidan con la tabla
data = data.rename(columns={"afluencia": "occupacy"})

# Seleccionar solo las columnas necesarias para la tabla
data = data[["datetime", "occupacy"]]

try:
    # Eliminar todos los registros de la tabla "history" usando la sesión de SQLAlchemy
    history_table = Table('history', MetaData(), autoload_with=engine)
    session.query(history_table).delete()
    session.commit()
    print("Registros existentes eliminados.")

    # Insertar los datos del CSV en la tabla "history"
    data.to_sql("history", con=engine, if_exists="append", index=False)
    print("Datos cargados exitosamente.")
except Exception as e:
    print(f"Error al cargar los datos: {e}")
finally:
    session.close()
