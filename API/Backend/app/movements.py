from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from models import Movements, Parking
from datetime import datetime
import uuid
from . import get_db

router = APIRouter()

# Crear una entrada/salida
@router.post("/movement")
def add_movement(parking: str, datetime: datetime, type: str, db: Session = Depends(get_db)):
    # Buscar el parking correspondiente
    parking_entry = db.query(Parking).filter(Parking.id == parking).first()
    
    if not parking_entry:
        raise HTTPException(status_code=404, detail="Parking no encontrado")
    
    # Crear el nuevo movimiento
    new_movement = Movements(
        parking=parking,
        datetime=datetime,
        type=type
    )
    
    # Actualizar el campo occupied_places según el tipo de movimiento
    if type == "in":
        if parking_entry.occupied_places >= parking_entry.total_capacity:
            raise HTTPException(status_code=400, detail="Parking lleno")
        parking_entry.occupied_places += 1
    elif type == "out":
        if parking_entry.occupied_places <= 0:
            raise HTTPException(status_code=400, detail="No hay vehículos para salir")
        parking_entry.occupied_places -= 1
    else:
        raise HTTPException(status_code=400, detail="Tipo de movimiento inválido. Debe ser 'in' o 'out'")
    
    # Guardar el nuevo movimiento y actualizar el parking en la base de datos
    db.add(new_movement)
    db.commit()
    db.refresh(new_movement)
    db.refresh(parking_entry)
    
    return {
        "msg": "Movimiento creado",
        "movement": new_movement,
        "occupied_places": parking_entry.occupied_places
    }