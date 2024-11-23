from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from models import Parking
from . import get_db

router = APIRouter()

#Crear un admin

@router.post("/admin",
             summary="Crear un admin",
            description="Se le asigna un rol de administrador a un usuario")
def get_parkings(db: Session = Depends(get_db)):
    parkings = db.query(Parking).all()
    return {"parkings": parkings}