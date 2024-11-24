import RPi.GPIO as GPIO
import requests
from datetime import datetime, timezone

import json


BEAM_PIN = 17

BaseURL = "https://faithful-sloth-socially.ngrok-free.app"
APIMovement = f"{BaseURL}/movements/movement"
ParkingId = "217fa335-bcb3-4aca-8f17-c0fc62dcb8fa"
APIGetStatus = f"{BaseURL}/parkings/status/{ParkingId}"


def break_beam_callback(channel):
    if not GPIO.input(BEAM_PIN):
        GetStatus = requests.get(APIGetStatus).json()

        CurrentDatetime = datetime.now(timezone.utc).isoformat(timespec='milliseconds').replace("+00:00", "Z")
        MovementJSON = {
          "parking": f"{ParkingId}",
          "datetime": f"{str(CurrentDatetime)}",
          "type": f"{GetStatus.get('gate_mode')}"
        }
        print (MovementJSON)
        requests.post(APIMovement, json=MovementJSON)

GPIO.setmode(GPIO.BCM)
GPIO.setup(BEAM_PIN, GPIO.IN, pull_up_down=GPIO.PUD_UP)
GPIO.add_event_detect(BEAM_PIN, GPIO.BOTH, callback=break_beam_callback, bouncetime=200)

message = input("Press enter to quit\n\n")
GPIO.cleanup()

