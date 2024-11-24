from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy.orm import Session
from models import Movements, Parking, FollowNotifications
from datetime import datetime
from uuid import UUID
from . import get_db, send_message

router = APIRouter()

class MovementRequest(BaseModel):
    parking: UUID
    datetime: datetime
    type: str
    
# Crear una entrada/salida
@router.post("/movement",
             summary="Registro de una entrada/salida",
            description="Registra la entrada o la salida de un vehículo en un parking")
def add_movement(request: MovementRequest, db: Session = Depends(get_db)):
    # Buscar el parking correspondiente
    parking_entry = db.query(Parking).filter(Parking.id == request.parking).first()
    
    if not parking_entry:
        raise HTTPException(status_code=404, detail="Parking no encontrado")
    
    # Crear el nuevo movimiento
    new_movement = Movements(
        parking=request.parking,
        datetime=request.datetime,
        type=request.type
    )
    
    # Actualizar el campo occupied_places según el tipo de movimiento
    if request.type == "in":
        if parking_entry.occupied_places == parking_entry.total_capacity:
            raise HTTPException(status_code=400, detail="Parking lleno")
        
        if parking_entry.occupied_places == parking_entry.total_capacity -1:
            parking_name = db.query(Parking).filter(Parking.id == request.parking).first()
            parking_name = parking_name.name
            subscriptions = db.query(FollowNotifications).filter(FollowNotifications.parking_id == request.parking).all()
            for suscription in subscriptions:
                print(suscription.notification_id)
                send_message.send_notification(suscription.notification_id,f"El Parking {parking_name} se ha llenado!", "Parece que te has quedado sin sitio :(")
        parking_entry.occupied_places += 1
        
    elif request.type == "out":
        if parking_entry.occupied_places == 0:
            raise HTTPException(status_code=400, detail="No hay vehículos para salir")
        if parking_entry.occupied_places == parking_entry.total_capacity:
            parking_name = db.query(Parking).filter(Parking.id == request.parking).first()
            parking_name = parking_name.name
            subscriptions = db.query(FollowNotifications).filter(FollowNotifications.parking_id == request.parking).all()
            for suscription in subscriptions:
                print(suscription.notification_id)
                send_message.send_notification(suscription.notification_id,f"El Parking {parking_name} tiene un sitio!", "Si llegas rápido puede ser tuyo!")
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
