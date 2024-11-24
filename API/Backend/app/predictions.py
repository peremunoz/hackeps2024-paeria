from fastapi import APIRouter
from pydantic import BaseModel
from datetime import datetime
import joblib
import pandas as pd
from sklearn.preprocessing import LabelEncoder

router = APIRouter()

# Cargar el modelo entrenado
class AfluenciaPredictor:
    def __init__(self, model_path):
        self.model = joblib.load(model_path)
    
    def preprocess(self, data):
        # Extraer el día de la semana y la hora
        data["day"] = data["datetime"].dt.day_name()
        data["hour"] = data["datetime"].dt.hour
        label_encoder = LabelEncoder()
        data["day"] = label_encoder.fit_transform(data["day"])
        return data.drop("datetime", axis=1)  # Eliminar la columna datetime después del procesamiento
    
    def predict(self, data):
        data_processed = self.preprocess(data)
        predictions = []
        
        # Iteramos sobre cada fila del DataFrame para hacer la predicción
        for _, row in data_processed.iterrows():
            # Pasamos un solo valor (hora, día) al modelo
            X = [row[["hour", "day"]].values]
            pred = self.model.predict(X)
            predictions.append(pred[0])  # Agregar la predicción al resultado
        
        return predictions

# Definir el modelo de entrada para la API
class PredictionRequest(BaseModel):
    start_datetime: str  # Fecha y hora de inicio
    end_datetime: str    # Fecha y hora de fin

# Instanciar el predictor con el modelo entrenado
predictor = AfluenciaPredictor('modelo_afluencia.pkl')

# Endpoint para predecir la afluencia entre dos fechas
@router.post("/predict")
async def predict_afluencia(request: PredictionRequest):
    # Parsear las fechas de entrada
    start_datetime = datetime.strptime(request.start_datetime, "%Y-%m-%d %H:%M:%S")
    end_datetime = datetime.strptime(request.end_datetime, "%Y-%m-%d %H:%M:%S")
    
    date_range = pd.date_range(start=start_datetime, end=end_datetime, freq='H')

    # Crear DataFrame con las fechas generadas
    input_data = pd.DataFrame(date_range, columns=["datetime"])

    # Realizar predicciones usando el modelo
    predictions = predictor.predict(input_data)

    # Crear un mapa con las predicciones usando el datetime como clave
    prediction_map = {
        str(input_data["datetime"][i]): predictions[i] for i in range(len(input_data))
    }
    
    # Devolver el mapa con las predicciones
    return prediction_map