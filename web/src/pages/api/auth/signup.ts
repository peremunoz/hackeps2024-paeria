import type { APIRoute } from 'astro'
import { getAuth } from 'firebase-admin/auth'
import { app } from '../../../firebase/server'

export const POST: APIRoute = async ({ request, redirect }) => {
  const auth = getAuth(app)

  /* Get form data */
  const formData = await request.formData()
  const email = formData.get('email')?.toString()
  const password = formData.get('password')?.toString()
  const confirmPassword = formData.get('confirm_password')?.toString()
  const username = formData.get('email')?.toString().split('@')[0]

  /* Validate form data */
  if (password !== confirmPassword) {
    return new Response('Passwords do not match', { status: 400 })
  }

  if (!email || !password) {
    return new Response('Missing form data', { status: 400 })
  }

  /* Create user */
  try {
    await auth.createUser({
      email,
      password,
      displayName: username,
    })
  } catch (error: any) {
    return new Response('Something went wrong, try again later', {
      status: 400,
    })
  }
  return redirect('/signin')
}
