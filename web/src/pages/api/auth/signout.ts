import type { APIRoute } from 'astro'

export const POST: APIRoute = async ({ redirect, cookies }) => {
  cookies.delete('__session', {
    path: '/',
  })

  return new Response('OK', {
    status: 200,
  })
}
