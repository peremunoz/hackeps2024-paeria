from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from models import Parking, FollowNotifications
from uuid import UUID
from pydantic import BaseModel
from . import get_db
from datetime import datetime

router = APIRouter()


class SuscriptionRequest(BaseModel):
    user_id: UUID
    parking_id: UUID
    
#Crear un admin
@router.post("/admin",
             summary="Crear un admin",
            description="Se le asigna un rol de administrador a un usuario")
def get_parkings(db: Session = Depends(get_db)):
    parkings = db.query(Parking).all()
    return {"parkings": parkings}

@router.post("/suscribe_parking",
             summary="Suscribe un usuario a un parking",
            description="Se le asigna un rol de administrador a un usuario")
def get_parkings(request: SuscriptionRequest, db: Session = Depends(get_db)):
    
    # Crear el nuevo movimiento
    new_suscription = FollowNotifications(
        user_id=request.user_id,
        parking_id=request.parking_id
    )
    db.add(new_suscription)
    db.commit()
    db.refresh(new_suscription)
    
    return {
        "msg": "Suscripcion añadida",
        "new_suscription": new_suscription
    }