# -*- coding: utf-8 -*-

# This sample demonstrates handling intents from an Alexa skill using the Alexa Skills Kit SDK for Python.
# Please visit https://alexa.design/cookbook for additional examples on implementing slots, dialog management,
# session persistence, api calls, and more.
# This sample is built using the handler classes approach in skill builder.
import logging
import ask_sdk_core.utils as ask_utils
import requests

from ask_sdk_core.skill_builder import SkillBuilder
from ask_sdk_core.dispatch_components import AbstractRequestHandler
from ask_sdk_core.dispatch_components import AbstractExceptionHandler
from ask_sdk_core.handler_input import HandlerInput
from ask_sdk_model.ui import SimpleCard
from ask_sdk_model import Response

logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)

# Respuesta una vez iniciado el "Hablar con"
class LaunchRequestHandler(AbstractRequestHandler):
    """Handler for Skill Launch."""
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool

        return ask_utils.is_request_type("LaunchRequest")(handler_input)

    def handle(self, handler_input):
        # type: (HandlerInput) -> Response
        speak_output = "Bienvenido a Aparca Ya! Que quieres hacer?"

        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask(speak_output)
                .response
        )



#Intent de HelloWorld
class HelloWorldIntentHandler(AbstractRequestHandler):
    """Handler for Hello World Intent."""
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return ask_utils.is_intent_name("HelloWorldIntent")(handler_input) #Nombre del intent en los intents
    
    #Que tiene que hacer
    def handle(self, handler_input):
        # type: (HandlerInput) -> Response 
        speak_output = "Hello World!"

        return (
            handler_input.response_builder
                .speak(speak_output)
                # .ask("add a reprompt if you want to keep the session open for the user to respond")
                .response
        )


class GetParking(AbstractRequestHandler):
    """Handler to get info of a parking, including available spaces."""

    def can_handle(self, handler_input):
        # Check if the intent name is "get_parking"
        return ask_utils.is_intent_name("GetParkingIntent")(handler_input)

    def handle(self, handler_input):
        # Obtener el nombre del parking del slot
        slots = handler_input.request_envelope.request.intent.slots
        parking_name = slots.get("parking_name", None)

        # Verificar si el nombre del parking está presente
        if parking_name is None or not parking_name.value:
            # Si no se recibe el nombre del parking, pedimos que lo indique
            return (
                handler_input.response_builder
                    .speak("¿De qué parking te gustaría saber cuántas plazas libres hay?")
                    .ask("Por favor, dime el nombre del parking.")
                    .response
            )

        # Obtener el valor del nombre del parking
        parking_name_value = parking_name.value
        

        # Realizar la solicitud a la API con el nombre del parking
        try:
            # Realiza la solicitud a la API con el nombre del parking
            response = requests.get(f"https://faithful-sloth-socially.ngrok-free.app/parkings/{parking_name_value}")
            
            # Asegúrate de que la respuesta sea exitosa
            if response.status_code == 200:
                data = response.json()
                
                # Suponemos que la respuesta es un JSON que contiene la información de los parkings
                occupied_places = data.get("occupied_places", 0)  # 0 si no se encuentra información
                total_places = data.get("total_capacity", 0)  # 0 si no se encuentra información
                
                available_spots = total_places - occupied_places
                
                # Crear el mensaje de respuesta para el usuario
                if available_spots > 0:
                    speak_output = f"Hay {available_spots} plazas disponibles en el parking {parking_name_value}."
                else:
                    speak_output = f"Vaya!, no hay plazas disponibles en el parking {parking_name_value} en este momento."
            else:
                # En caso de error al hacer la solicitud
                speak_output = f"No pude obtener la información del parking {parking_name_value}. La llamada a la API devolvió un error: {response.status_code}. Inténtalo de nuevo más tarde.{response.text}"
        
        except requests.exceptions.RequestException as e:
            # En caso de un error de conexión o solicitud
            speak_output = f"Hubo un problema al intentar obtener la información del parking {parking_name_value}. Error de la API: {str(e)}."

        except Exception as e:
            # En caso de un error general
            speak_output = f"Hubo un error inesperado al intentar obtener la información. Error: {str(e)}"

        # Responder con el mensaje de Alexa
        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask("¿Te gustaría preguntar por otro parking o realizar otra acción?")
                .set_card(SimpleCard("Plazas Disponibles", speak_output))
                .response
        )


class HelpIntentHandler(AbstractRequestHandler):
    """Handler for Help Intent."""
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return ask_utils.is_intent_name("AMAZON.HelpIntent")(handler_input)

    def handle(self, handler_input):
        # type: (HandlerInput) -> Response
        speak_output = "Puedes pedirme información sobre los parkings de tu ciudad"

        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask(speak_output)
                .response
        )


class CancelOrStopIntentHandler(AbstractRequestHandler):
    """Single handler for Cancel and Stop Intent."""
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return (ask_utils.is_intent_name("AMAZON.CancelIntent")(handler_input) or
                ask_utils.is_intent_name("AMAZON.StopIntent")(handler_input))

    def handle(self, handler_input):
        # type: (HandlerInput) -> Response
        speak_output = "Espero haber podido ayudar!"

        return (
            handler_input.response_builder
                .speak(speak_output)
                .response
        )

class FallbackIntentHandler(AbstractRequestHandler):
    """Single handler for Fallback Intent."""
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return ask_utils.is_intent_name("AMAZON.FallbackIntent")(handler_input)

    def handle(self, handler_input):
        # type: (HandlerInput) -> Response
        logger.info("In FallbackIntentHandler")
        speech = "Hmmm Parece que no te he entendido bien"
        reprompt = "Como puedo ayudarte?"

        return handler_input.response_builder.speak(speech).ask(reprompt).response

class SessionEndedRequestHandler(AbstractRequestHandler):
    """Handler for Session End."""
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return ask_utils.is_request_type("SessionEndedRequest")(handler_input)

    def handle(self, handler_input):
        # type: (HandlerInput) -> Response

        # Any cleanup logic goes here.

        return handler_input.response_builder.response


class IntentReflectorHandler(AbstractRequestHandler):
    """The intent reflector is used for interaction model testing and debugging.
    It will simply repeat the intent the user said. You can create custom handlers
    for your intents by defining them above, then also adding them to the request
    handler chain below.
    """
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return ask_utils.is_request_type("IntentRequest")(handler_input)

    def handle(self, handler_input):
        # type: (HandlerInput) -> Response
        intent_name = ask_utils.get_intent_name(handler_input)
        speak_output = "You just triggered " + intent_name + "."

        return (
            handler_input.response_builder
                .speak(speak_output)
                # .ask("add a reprompt if you want to keep the session open for the user to respond")
                .response
        )


class CatchAllExceptionHandler(AbstractExceptionHandler):
    """Generic error handling to capture any syntax or routing errors. If you receive an error
    stating the request handler chain is not found, you have not implemented a handler for
    the intent being invoked or included it in the skill builder below.
    """
    def can_handle(self, handler_input, exception):
        # type: (HandlerInput, Exception) -> bool
        return True

    def handle(self, handler_input, exception):
        # type: (HandlerInput, Exception) -> Response
        logger.error(exception, exc_info=True)

        speak_output = "Sorry, I had trouble doing what you asked. Please try again."

        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask(speak_output)
                .response
        )

# The SkillBuilder object acts as the entry point for your skill, routing all request and response
# payloads to the handlers above. Make sure any new handlers or interceptors you've
# defined are included below. The order matters - they're processed top to bottom.


sb = SkillBuilder()

sb.add_request_handler(LaunchRequestHandler())
sb.add_request_handler(HelloWorldIntentHandler())
sb.add_request_handler(GetParking())
sb.add_request_handler(HelpIntentHandler())
sb.add_request_handler(CancelOrStopIntentHandler())
sb.add_request_handler(FallbackIntentHandler())
sb.add_request_handler(SessionEndedRequestHandler())
sb.add_request_handler(IntentReflectorHandler()) # make sure IntentReflectorHandler is last so it doesn't override your custom intent handlers

sb.add_exception_handler(CatchAllExceptionHandler())

lambda_handler = sb.lambda_handler()