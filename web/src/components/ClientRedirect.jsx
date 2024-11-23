import { useEffect } from 'react'

const RedirectToDashboard = () => {
  useEffect(() => {
    window.location.href = '/ca/dashboard'
  }, [])

  return null
}

export default RedirectToDashboard