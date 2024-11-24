import { LuLogIn } from 'react-icons/lu'
import { useState } from 'react'
import { toast } from 'react-toastify'
import { getAuth } from 'firebase/auth'

export function LogoutButton() {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const handleLogout = async () => {
    setLoading(true)
    setError(null)

    try {
      // Aquí iría la URL de tu API para cerrar sesión
      const response = await fetch('/api/auth/signout', {
        method: 'POST', // o 'GET', dependiendo de cómo maneje tu API el cierre de sesión
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include', // Si necesitas enviar cookies o sesiones
      })

      if (!response.ok) {
        throw new Error('Error al cerrar sesión')
      }

      if (response.ok) {
        getAuth().signOut()
        toast.success(`Desconnectat correctament!`, {
          position: 'top-right',
          autoClose: 1000,
          hideProgressBar: false,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
          theme: 'light',
        })
        setTimeout(() => {
          window.location.assign('/ca/dashboard')
        }, 1000)
      }
    } catch (error) {
      setError(error.message)
      console.error('Error:', error.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className='absolute top-4 right-4 z-10 flex items-center space-x-2'>
      <button
        className='px-4 py-2 bg-primary text-white rounded hover:bg-black'
        onClick={handleLogout}
        disabled={loading} // Deshabilitar el botón mientras se está haciendo la solicitud
      >
        {loading ? (
          <span>Loading...</span>
        ) : (
          <span className='flex items-center'>
            <span>Desconnecta't</span>
            <LuLogIn size={'1.5em'} className='ml-2' />
          </span>
        )}
      </button>
      {error && <p className='text-red-500'>{error}</p>}
    </div>
  )
}
