import { createFileRoute, redirect } from '@tanstack/react-router'
import API from '#/api/API'

export const Route = createFileRoute('/')({
  beforeLoad: () => {
    const api = new API()
    if (api.obterDadosUsuarioNoNavegador()) {
      throw redirect({ to: '/feed' })
    }
    throw redirect({ to: '/login' })
  },
})
