from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from models import Parking
from . import get_db

router = APIRouter()

# Obtener todos los parkings
@router.get("/",
            summary="Obtener todos los parkings",
            description="Devuelve una lista de todos los parkings registrados en la base de datos.")
def get_parkings(db: Session = Depends(get_db)):
    parkings = db.query(Parking).all()
    return {"parkings": parkings}

# Obtener un parking por su nombre
@router.get("/{parking_identifier}",
            summary="Obtener un parking por su nombre",
            description="Devuelve la información de un parking específico, se puede buscar por su nombre")
def get_parking(parking_identifier: str, db: Session = Depends(get_db)):
    parking = db.query(Parking).filter(Parking.name.ilike(f"%{parking_identifier}%")).first()
    
    # Si no se encuentra el parking, se lanza una excepción
    if not parking:
        raise HTTPException(status_code=404, detail="Parking no encontrado")
    
    return {
        "id": parking.id,
        "name": parking.name,
        "latitude": parking.latitude,
        "longitude": parking.longitude,
        "total_capacity": parking.total_capacity,
        "occupied_places": parking.occupied_places,
        "gate_mode": parking.gate_mode
    }

# Obtener un parking por su nombre
@router.get("/get_by_id/{parking_id}",
            summary="Obtener un parking por su nombre",
            description="Devuelve la información de un parking específico, se puede buscar por su nombre")
def get_parking(parking_id: str, db: Session = Depends(get_db)):
    parking = db.query(Parking).filter(Parking.id == parking_id).first()
    
    # Si no se encuentra el parking, se lanza una excepción
    if not parking:
        raise HTTPException(status_code=404, detail="Parking no encontrado")
    
    return {
        "id": parking.id,
        "name": parking.name,
        "latitude": parking.latitude,
        "longitude": parking.longitude,
        "total_capacity": parking.total_capacity,
        "occupied_places": parking.occupied_places,
        "gate_mode": parking.gate_mode
    }
    
#Simulación de sensor
@router.get("/status/{parking_id}",
            summary="--Development, Simulación del sensor en modo entrada o salida",
            description="Para el desarrollo, simulamos el cambio de sensor según si es entrada o salida")
def get_status(parking_id: str, db: Session = Depends(get_db)):
    
    parking = db.query(Parking).filter(Parking.id == parking_id).first()
    
    # Si no se encuentra el parking, se lanza una excepción
    if not parking:
        raise HTTPException(status_code=404, detail="Parking no encontrado")
    
    return {"parking_id": parking_id, "gate_mode": parking.gate_mode}

# Crear un parking
@router.post("/create",
             summary="Crear un parking nuevo",
            description="Crea un nuevo parking en la base de datos, se inicia con 0 plazas ocupadas")
def create_parking(name: str, latitude: str, longitude: str, total_capacity: int, db: Session = Depends(get_db)):
    new_parking = Parking(
        name=name,
        latitude=latitude,
        longitude=longitude,
        total_capacity=total_capacity,
        occupied_places = 0,
        gate_mode = 'in'
    )
    db.add(new_parking)
    db.commit()
    db.refresh(new_parking)
    return {"msg": "Parking creado", "parking": new_parking}


# Cambiar el modo del sensor (gate_mode) para un parking específico
@router.post("/sensor/{parking_id}",
             summary="--Development, Simulación del sensor en modo entrada o salida",
            description="Para el desarrollo, simulamos el cambio de sensor según si es entrada o salida")
def set_sensor(parking_id: str, mode: str, db: Session = Depends(get_db)):
    # Buscar el parking por parking_id
    parking = db.query(Parking).filter(Parking.id == parking_id).first()

    if not parking:
        raise HTTPException(status_code=404, detail="Parking no encontrado")
    
    parking.gate_mode = mode
    db.commit()  
    db.refresh(parking) 
    
    return {"msg": f"Modo del sensor actualizado a {mode}", "parking_id": parking_id, "gate_mode": parking.gate_mode}