import firebase_admin
from firebase_admin import credentials
from firebase_admin import messaging

# Credenciales de Firebase
cred = credentials.Certificate('./aparca-ya-firebase-adminsdk-3s65b-4704a51d87.json')
firebase_admin.initialize_app(cred)

def send_notification(r_token, title, text):
      # Datos de la notificación
      registration_token = r_token
      message = messaging.Message(
      notification=messaging.Notification(
            title=title,
            body=text
      ),
      token=registration_token
      )

      # Enviar la notificación
      response = messaging.send(message)
      print('Successfully sent message:', response)
