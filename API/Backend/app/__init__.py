from sqlalchemy.orm import sessionmaker
from sqlalchemy import create_engine

# Configuración de la base de datos
DATABASE_USER = "admin"
DATABASE_PASSWORD = "admin123"
DATABASE_HOST = "database"
DATABASE_PORT = "5432"
DATABASE_NAME = "mydatabase"

DATABASE_URL = f"postgresql://{DATABASE_USER}:{DATABASE_PASSWORD}@{DATABASE_HOST}:{DATABASE_PORT}/{DATABASE_NAME}"

# Crear motor y sesión
engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# Sesión de la base de datos
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
