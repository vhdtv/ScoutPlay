import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/feed')({
  component: RouteComponent,
})

export function RouteComponent() {
  return <div>Hello "/feed"!</div>
}
