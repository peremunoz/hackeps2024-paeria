from fastapi import FastAPI
from app.users import router as users_router
from app.parkings import router as parkings_router
from app.movements import router as movements_router
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from models import Base

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
Base.metadata.create_all(bind=engine)

app = FastAPI()

# Incluir los routers de diferentes módulos
app.include_router(users_router, prefix="/users", tags=["users"])
app.include_router(parkings_router, prefix="/parkings", tags=["parkings"])
app.include_router(movements_router, prefix="/movements", tags=["movements"])

# Sesión de la base de datos
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
