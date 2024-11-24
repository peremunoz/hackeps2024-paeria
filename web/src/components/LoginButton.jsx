import { CgProfile } from 'react-icons/cg'
import { useState } from 'react'
import {
  getAuth,
  inMemoryPersistence,
  signInWithEmailAndPassword,
} from 'firebase/auth'
import { app } from '../firebase/client'
import { toast } from 'react-toastify'

export function LoginButton() {
  const [isOpen, setIsOpen] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const handleLogin = async () => {
    e.preventDefault()
    setLoading(true)
    setError(null)

    try {
      // Aquí iría la URL de tu API de login
      const response = await fetch('/api/auth/signin', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username: e.target.username.value,
          password: e.target.password.value,
        }),
      })

      if (!response.ok) {
        throw new Error('Error al iniciar sesión')
      }

      // Si el login es exitoso, redirige o realiza alguna acción
      console.log('Sesión iniciada exitosamente')
      // Redirigir al usuario a una página protegida o al dashboard
      // window.location.href = '/dashboard'; // Redirigir al dashboard, por ejemplo
    } catch (error) {
      setError(error.message)
      console.error('Error:', error.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className='absolute top-4 right-4 z-10'>
      <button
        className='px-4 py-2 bg-primary text-white rounded hover:bg-black flex items-center'
        onClick={() => setIsOpen(true)} // Al hacer clic, abrir el modal
      >
        <span className='mr-2'>Entrar</span>
        <CgProfile size={'1.5em'} />
      </button>

      {/* Modal */}
      {isOpen && (
        <div className='fixed inset-0 bg-gray-500 bg-opacity-75 flex justify-center items-center z-50'>
          <div className='bg-white p-6 rounded-lg shadow-lg w-80'>
            <h2 className='text-xl mb-4'>Identifica't</h2>
            <form onSubmit={handleLogin}>
              <div className='mb-4'>
                <label htmlFor='username' className='block text-sm font-medium'>
                  Correu electrònic
                </label>
                <input
                  id='username'
                  type='text'
                  name='username'
                  required
                  className='mt-1 p-2 border border-gray-300 rounded w-full'
                />
              </div>

              <div className='mb-4'>
                <label htmlFor='password' className='block text-sm font-medium'>
                  Contrasenya
                </label>
                <input
                  id='password'
                  type='password'
                  name='password'
                  required
                  className='mt-1 p-2 border border-gray-300 rounded w-full'
                />
              </div>

              {error && <p className='text-red-500 text-sm'>{error}</p>}

              <div className='flex justify-between items-center'>
                <button
                  type='submit'
                  className='px-4 py-2 bg-primary text-white rounded hover:bg-black'
                  disabled={loading}
                >
                  {loading ? 'Carregant...' : 'Entrar'}
                </button>
                <button
                  type='button'
                  onClick={() => setIsOpen(false)} // Cerrar el modal
                  className='px-3 py-2 bg-secondary text-white rounded hover:bg-black'
                >
                  Cancelar
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
