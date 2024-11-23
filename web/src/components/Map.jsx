import { useEffect } from 'react';
import mapboxgl from 'mapbox-gl';
import 'mapbox-gl/dist/mapbox-gl.css';
const Map = () => {
  useEffect(() => {
    // Configuración de Mapbox con tu clave de API
    mapboxgl.accessToken = 'pk.eyJ1IjoicGVyZW11bm96IiwiYSI6ImNtM3VkanI0ODBpcmkybHM3ZmNlZHRjY2oifQ.gI4DbFUU7K4GeMEmnZ7AoQ';

    // Inicializar el mapa
    const map = new mapboxgl.Map({
      container: 'map', // El contenedor donde se mostrará el mapa
      style: 'mapbox://styles/mapbox/streets-v11', // Estilo del mapa
      center: [-74.5, 40], // Coordenadas iniciales [longitud, latitud]
      zoom: 9, // Nivel de zoom
    });

    // Añadir un marcador en el mapa
    new mapboxgl.Marker()
      .setLngLat([-74.5, 40])
      .addTo(map);
  }, []);

  return (
    <div>
      <div
        id="map"
        style={{ width: '100%', height: '500px' }} // Ajusta el tamaño del mapa
      ></div>
    </div>
  );
};

export default Map;
