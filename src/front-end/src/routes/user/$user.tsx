import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/user/$user')({
  component: RouteComponent,
})

function RouteComponent() {
  return <div>Hello "/user/$user"!</div>
}
