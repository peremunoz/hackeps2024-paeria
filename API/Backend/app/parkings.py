from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from models import Parking
from . import get_db

router = APIRouter()

# Obtener todos los parkings
@router.get("/")
def get_parkings(db: Session = Depends(get_db)):
    parkings = db.query(Parking).all()
    return {"parkings": parkings}

@router.get("/status/{parking_id}")
def get_status(parking_id: str, db: Session = Depends(get_db)):
    
    parking = db.query(Parking).filter(Parking.id == parking_id).first()
    
    # Si no se encuentra el parking, se lanza una excepción
    if not parking:
        raise HTTPException(status_code=404, detail="Parking no encontrado")
    
    return {"parking_id": parking_id, "gate_mode": parking.gate_mode}

# Crear un parking
@router.post("/create")
def create_parking(name: str, latitude: str, longitude: str, total_capacity: int, db: Session = Depends(get_db)):
    new_parking = Parking(
        name=name,
        latitude=latitude,
        longitude=longitude,
        total_capacity=total_capacity
    )
    db.add(new_parking)
    db.commit()
    db.refresh(new_parking)
    return {"msg": "Parking creado", "parking": new_parking}


# Cambiar el modo del sensor (gate_mode) para un parking específico
@router.post("/sensor/{parking_id}")
def set_sensor(parking_id: str, mode: str, db: Session = Depends(get_db)):
    # Buscar el parking por parking_id
    parking = db.query(Parking).filter(Parking.id == parking_id).first()

    if not parking:
        raise HTTPException(status_code=404, detail="Parking no encontrado")
    
    parking.gate_mode = mode
    db.commit()  
    db.refresh(parking) 
    
    return {"msg": f"Modo del sensor actualizado a {mode}", "parking_id": parking_id, "gate_mode": parking.gate_mode}