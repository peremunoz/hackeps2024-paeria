from sqlalchemy import Column, String, Integer, TIMESTAMP, ForeignKey, text
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.ext.declarative import declarative_base
import uuid

Base = declarative_base()

class Parking(Base):
    __tablename__ = "parking"
    id = Column(UUID(as_uuid=True), primary_key=True, default=str(uuid.uuid4()), server_default=text("gen_random_uuid()"))
    name = Column(String)
    latitude = Column(String)
    longitude = Column(String)
    total_capacity = Column(Integer)
    occupied_places = Column(Integer)
    gate_mode = Column(String)
    
class Admins(Base):
    __tablename__ = "admins"
    id = Column(UUID, primary_key=True)

class Movements(Base):
    __tablename__ = "movements"
    id = Column(UUID(as_uuid=True), primary_key=True, server_default=text("gen_random_uuid()"))
    parking = Column(UUID(as_uuid=True), ForeignKey("parking.id"))
    datetime = Column(TIMESTAMP)
    type = Column(String)

class FollowNotifications(Base):
    __tablename__ = "follow_notifications"
    user_id = Column(UUID(as_uuid=True), primary_key=True, default=str(uuid.uuid4()), server_default="gen_random_uuid()")
    parking_id = Column(UUID(as_uuid=True), ForeignKey("parking.id"))
    
class Incidents(Base):
    __tablename__ = "incidents"
    user_id = Column(UUID(as_uuid=True), primary_key=True, default=str(uuid.uuid4()), server_default="gen_random_uuid()")
    parking_id = Column(UUID(as_uuid=True), ForeignKey("parking.id"))
    name = Column(String)
    description = Column(String)
    occupied_places = Column(Integer)