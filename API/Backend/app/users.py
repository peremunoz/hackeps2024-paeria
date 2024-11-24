from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from models import Parking, FollowNotifications
from uuid import UUID
from pydantic import BaseModel
from . import get_db
from datetime import datetime

router = APIRouter()


class SuscriptionRequest(BaseModel):
    user_id: str
    parking_id: UUID
    notification_id: str
    
#Crear un admin
@router.post("/admin",
             summary="Crear un admin",
            description="Se le asigna un rol de administrador a un usuario")
def get_parkings(db: Session = Depends(get_db)):
    parkings = db.query(Parking).all()
    return {"parkings": parkings}

@router.post("/subscribe_parking",
             summary="Suscribe un usuario a un parking",
            description="Se le asigna un rol de administrador a un usuario")
def get_parkings(request: SuscriptionRequest, db: Session = Depends(get_db)):
    
    # Crear el nuevo movimiento
    new_suscription = FollowNotifications(
        user_id=request.user_id,
        parking_id=request.parking_id,
        notification_id=request.notification_id
    )
    db.add(new_suscription)
    db.commit()
    db.refresh(new_suscription)
    
    return {
        "msg": "Suscripcion añadida",
        "new_suscription": new_suscription
    }

# Obtener las suscripciones de un usuario
@router.get("/subscribed_parkings/{user_id}",
            summary="Obtener las suscripciones de un usuario",
            description="Devuelve los IDs de los parkings suscritos por un usuario")
def get_suscription(user_id: str, db: Session = Depends(get_db)):
    # Obtener todas las suscripciones del usuario
    subscriptions = db.query(FollowNotifications).filter(FollowNotifications.user_id == user_id).all()
    
    # Extraer solo los IDs de los parkings
    parking_ids = [subscription.parking_id for subscription in subscriptions]
    
    return {"parking_ids": parking_ids}