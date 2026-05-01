import { createFileRoute } from '@tanstack/react-router'
import { getPostById } from '../../services/postService'

export const Route = createFileRoute('/post/$post_id')({
  component: RouteComponent,
  loader: async ({params}) => {
    const postData = await getPostById(params.post_id);
    return {
        postId: params.post_id
    }
  }
})

function RouteComponent() {
  const { postId } = Route.useLoaderData();
  return <div>Hello {postId} !</div>
}
