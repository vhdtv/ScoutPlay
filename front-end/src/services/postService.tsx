export function getPostById(postId: string) {
	// TODO: use API to return
	return new Promise((resolve) =>
		setTimeout(() => resolve({ data: postId }), 1000),
	);
}
