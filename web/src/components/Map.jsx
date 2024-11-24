import { useState, useEffect } from 'react'
import mapboxgl from 'mapbox-gl'
import 'mapbox-gl/dist/mapbox-gl.css'
import Modal from 'react-modal'
import { getAuth, onAuthStateChanged } from 'firebase/auth' // Import Firebase authentication
import Chart from './Chart.jsx'
import ProgressBar from './ProgressBar.jsx'

export function Map() {
  const API_URL = 'https://faithful-sloth-socially.ngrok-free.app' // Cambia por la URL de tu API
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [chartXAxis, setChartXAxis] = useState(null)
  const [chartData, setChartData] = useState(null)
  const [selectedParking, setSelectedParking] = useState(null)
  const [isLoggedIn, setIsLoggedIn] = useState(false) // Track login status (change to actual logic)
  var selectedParkingInterval = null

  useEffect(() => {
    const auth = getAuth() // Initialize Firebase Auth
    const unsubscribe = onAuthStateChanged(auth, (user) => {
      if (user) {
        console.log(user)
        setIsLoggedIn(true) // Set logged in if user is authenticated
      } else {
        setIsLoggedIn(false) // Set logged out if no user
      }
    })

    return () => unsubscribe() // Clean up subscription
  }, [])

  // Este useEffect se ejecuta cuando se abre el modal
  useEffect(() => {
    let intervalId = -1
    const updateParking = (parking) => {
      fetch(`${API_URL}/parkings/get_by_id/${parking.id}`, {
        headers: {
          'ngrok-skip-browser-warning': '69420',
        },
      })
        .then((response) => response.json())
        .then((data) => {
          setSelectedParking(data)
          
        })
        .catch((error) => {
          console.error('Error al obtener los detalles del parking:', error)
        })
    }

    if (isModalOpen && selectedParking) {
      return
      // Llamamos a updateParking inmediatamente
      updateParking(selectedParking)
      
      if (intervalId == -1) {
        return
      }
      console.log('Intervalo creado')
      // Configuramos el intervalo para actualizar los datos cada segundo
      intervalId = setInterval(() => {
        console.log('Intervalo ejecutado')
        if (selectedParking) {
          console.log('Actualizando parking...')
          updateParking(selectedParking)
        }
      }, 3000)
    }

    // Limpiar el intervalo cuando el modal se cierre
    return () => {
      console.log('Intervalo limpiado')
      if (intervalId) clearInterval(intervalId)
    }
  }, [isModalOpen, selectedParking]) // Ejecuta cuando el modal se abre y cuando el parking seleccionado cambia

  useEffect(() => {
    // Configuración de Mapbox con tu clave de API
    mapboxgl.accessToken =
      'pk.eyJ1IjoicGVyZW11bm96IiwiYSI6ImNtM3VkanI0ODBpcmkybHM3ZmNlZHRjY2oifQ.gI4DbFUU7K4GeMEmnZ7AoQ'

    // Inicializar el mapa
    const map = new mapboxgl.Map({
      container: 'map', // El contenedor donde se mostrará el mapa
      style: 'mapbox://styles/mapbox/outdoors-v12', // Estilo del mapa
      center: [0.62, 41.61], // Coordenadas iniciales [longitud, latitud]
      zoom: 11, // Nivel de zoom
    })

    var currentMarkers = []

    // Función para obtener los parkings de la API
    const fetchParkings = async () => {
      try {
        const response = await fetch(`${API_URL}/parkings/`, {
          headers: {
            'ngrok-skip-browser-warning': '69420',
          },
        }) // Cambia por la URL de tu API
        const data = await response.json()

        // Eliminar los parkings anteriores
        currentMarkers.forEach((marker) => {
          marker.remove()
        })

        currentMarkers = []

        // Marcar los parkings en el mapa
        data.parkings.forEach((parking) => {
          const el = document.createElement('div')
          const width = 32
          const height = 32
          const availablePlaces =
            parking.total_capacity - parking.occupied_places
          const percentageAvailable =
            (availablePlaces / parking.total_capacity) * 100
          var imageUrl =
            'https://hackeps2024-paeria.vercel.app/parking-blau.png'
          if (percentageAvailable < 50) {
            imageUrl = 'https://hackeps2024-paeria.vercel.app/parking-groc.png'
          }
          if (percentageAvailable == 0) {
            imageUrl = 'https://hackeps2024-paeria.vercel.app/parking-roig.png'
          }
          el.className = 'marker'
          el.style.backgroundImage = `url(${imageUrl})`
          el.style.width = `${width}px`
          el.style.height = `${height}px`
          el.style.backgroundSize = '100%'
          el.style.display = 'block'
          el.style.border = 'none'
          el.style.borderRadius = '50%'
          el.style.cursor = 'pointer'
          el.style.padding = 0
          el.addEventListener('click', () => {
            // Mostrar detalles del parking en el modal
            setSelectedParking(parking)
            openModal(parking)
          })
          const actualMarker = new mapboxgl.Marker(el)
            .setLngLat([parking.longitude, parking.latitude]) // Usa las coordenadas de cada parking
            .addTo(map)
          currentMarkers.push(actualMarker)
        })
      } catch (error) {
        console.error('Error al obtener los parkings:', error)
      }
    }

    fetchParkings()

    // Llamar a fetchParkings cada segundo
    const intervalId = setInterval(() => {
      fetchParkings()
    }, 10000)

    // Limpieza de recursos cuando el componente se desmonte
    return () => {
      clearInterval(intervalId)
      map.remove()
    }
  }, [])

  const closeModal = () => {
    setIsModalOpen(false)
    setSelectedParking(null)
  }

  const openModal = (parking) => {
    setSelectedParking(parking)
    setIsModalOpen(true)
  }

  return (
    <div>
      <div
        id='map'
        style={{
          width: '100vw', // El ancho será el 100% de la vista
          height: '100vh', // La altura será el 100% de la vista
          margin: 0, // Elimina márgenes que podrían afectar el tamaño
          padding: 0,
        }}
      ></div>

      {/* Modal con detalles del parking */}
      <Modal
        isOpen={isModalOpen}
        onRequestClose={closeModal}
        contentLabel='Detalls del Parking'
        className={`fixed top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 p-6 bg-white rounded-lg shadow-lg w-[80vw] h-[80vh] transition-all duration-500 ease-in-out ${
          isModalOpen ? 'opacity-100 scale-100' : 'opacity-0 scale-95'
        }`}
        overlayClassName={`fixed inset-0 bg-black bg-opacity-50 transition-opacity duration-500 ease-in-out ${
          isModalOpen ? 'opacity-100' : 'opacity-0'
        }`}
      >
        {selectedParking && (
          <div>
            <h2 className='text-2xl font-bold mb-4 flex items-center justify-between'>
              <span>Parking {selectedParking.name}</span>
            </h2>

          <ProgressBar percentage={(selectedParking.total_capacity - selectedParking.occupied_places) / selectedParking.total_capacity * 100} />

            <p>
              Places:{' '}
              {selectedParking.total_capacity - selectedParking.occupied_places}{' '}
              de {selectedParking.total_capacity}
            </p>
            <Chart />
            <button
              onClick={closeModal}
              className='mt-4 px-4 py-2 bg-primary text-white rounded-lg hover:bg-black transition-colors'
            >
              Cerrar
            </button>
          </div>
        )}
      </Modal>
    </div>
  )
}
