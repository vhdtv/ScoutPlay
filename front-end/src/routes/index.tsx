import { createFileRoute } from '@tanstack/react-router'
import {RouteComponent as LoginPage} from "./login"
import {RouteComponent as FeedPage} from "./feed"
import { useEffect, useState } from 'react'

export const Route = createFileRoute('/')({ component: RouteComponent })

const loadFeed = async () => {
  throw new Error();
}

function LoadingSpinner() {
  return (
    <div>Loading</div>
  )
}


function RouteComponent() {
  const [status, setStatus] = useState('LOADING');
  useEffect(() => {
    setStatus("LOADING")
    loadFeed()
      .then(() => {
        setStatus("FEED_LOADED")
      })
      .catch(() => {
        setStatus("USER_NOT_LOGGED")
      })
  }, [])
  return status == "LOADING"
    ? <LoadingSpinner />
    : status == "FEED_LOADED"
      ? <FeedPage />
      : <LoginPage />
}