from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from models import Movements
from datetime import datetime
from . import get_db

router = APIRouter()

# Crear una entrada/salida
@router.post("/movement")
def add_movement(parking: str, datetime: datetime, type: str, db: Session = Depends(get_db)):
    new_movement = Movements(
        parking=parking,
        datetime=datetime,
        type=type
    )
    db.add(new_movement)
    db.commit()
    db.refresh(new_movement)
    return {"msg": "Movimiento creado", "movement": new_movement}
