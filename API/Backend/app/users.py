from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from models import Parking
from . import get_db

router = APIRouter()

# Obtener todos los parkings
@router.post("/admin")
def get_parkings(db: Session = Depends(get_db)):
    parkings = db.query(Parking).all()
    return {"parkings": parkings}