import * as React from 'react';
import { LineChart } from '@mui/x-charts/LineChart';
import { useEffect, useState } from 'react';

const API_URL = 'https://faithful-sloth-socially.ngrok-free.app'; // Cambia por la URL de tu API

export default function SimpleLineChart() {
  const [chartData, setChartData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      const body = {
        start_datetime: "2024-11-24 08:00:00",
        end_datetime: "2024-11-27 12:00:00",
      };

      try {
        const response = await fetch(`${API_URL}/predictions/predict`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'ngrok-skip-browser-warning': '69420',
          },
          body: JSON.stringify(body),
        });

        const json = await response.json();

        // Convertir los datos en un formato adecuado para el gráfico
        const chartData = Object.keys(json.data).map((key) => {
          const date = new Date(key);
          const formattedDate = `${date.getDate()} - ${date.getHours()}:${date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()}`;
          
          return {
            time: formattedDate, // Usamos la fecha formateada como cadena
            occupancy: json.data[key].occupacy,
          };
        });

        setChartData(chartData);
      } catch (error) {
        console.error("Error fetching data:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return <div>Loading...</div>; // Muestra un mensaje de carga mientras se obtienen los datos
  }

  const xLabels = chartData.map((item) => item.time); // Extraemos las fechas como etiquetas del eje X
  const occupancyData = chartData.map((item) => item.occupancy); // Extraemos los datos de ocupación

  return (
    <LineChart
      width={1500}
      height={500}
      series={[
        { data: occupancyData, label: 'Occupancy %' }, // Datos de ocupación
      ]}
      xAxis={[{ scaleType: 'point', data: xLabels }]} // Utilizamos las fechas como etiquetas en el eje X
    />
  );
}
