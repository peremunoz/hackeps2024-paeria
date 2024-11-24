import { LineChart } from '@mui/x-charts'
import {SparkLineChart} from '@mui/x-charts'
import { useEffect, useState } from 'react'

const API_URL = 'https://faithful-sloth-socially.ngrok-free.app' // Cambia por la URL de tu API

export default function Chart() {
  // Estados para los datos del gráfico
  const [chartData, setChartData] = useState([])
  const [historyData, setHistoryData] = useState([])
  const [predictionData, setPredictionData] = useState([])
  const [loading, setLoading] = useState(true) // Estado de carga

  useEffect(() => {
    const fetchData = async () => {
      const body = {
        start_datetime: "2024-11-24 08:00:00",
        end_datetime: "2024-11-27 12:00:00"
      }
      
      try {
        const data = await fetch(`${API_URL}/predictions/predict`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'ngrok-skip-browser-warning': '69420',
          },
          body: JSON.stringify(body)
        })
  
        const json = await data.json()
  
        // Convertir los datos en un formato adecuado para el gráfico
        const chartData = Object.keys(json.data).map((key) => {
            // Crear una nueva fecha a partir de la cadena de fecha y hora
            const date = new Date(key);
          
            // Extraer solo el número del día del mes (1 a 31)
            const day = date.getDate(); // Obtiene el número del día del mes
          
            return {
              time: day, // Guardamos solo el número del día
              occupancy: json.data[key].occupacy,
              type: json.data[key].type,
            };
          });
        console.log("Chart data:", chartData); // Imprime los datos completos
  
        // Filtrar y separar los datos por tipo
        const history = chartData.filter((item) => item.type === 'history')
        const prediction = chartData.filter((item) => item.type === 'prediction')
  
        // Actualizar el estado con los datos cargados
        setChartData(chartData)
        setHistoryData(history)
        setPredictionData(prediction)
  
      } catch (error) {
        console.error("Error fetching data:", error)
      } finally {
        setLoading(false) // Datos cargados
      }
    }
  
    fetchData()
  }, [])
  

  if (loading) {
    return <div>Loading...</div> // Muestra un mensaje de carga mientras se obtienen los datos
  }

  return (
    <SparkLineChart
      width={500}
      height={300}
      xAxis={[
        {
          data: chartData.map((item) => item.time), // Usamos la fecha como eje X
        },
      ]}
      series={[
        {
          data: historyData.map((item) => item.occupancy), // Datos de ocupación históricos
          stroke: '#8884d8', // Color de la línea para "history"
          strokeDasharray: '4 4', // Línea discontinua para "history"
          name: 'History',
        },
        {
          data: predictionData.map((item) => item.occupancy), // Datos de ocupación para predicciones
          stroke: '#82ca9d', // Color de la línea para "prediction"
          strokeDasharray: '', // Línea continua para "prediction"
          name: 'Prediction',
        },
      ]}
    />
  )
  
}
