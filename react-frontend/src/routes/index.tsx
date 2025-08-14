import { createFileRoute, useNavigate } from '@tanstack/react-router'
import { useUser } from "@/hooks/useUser.ts";
import { useEffect } from "react";

export const Route = createFileRoute('/')({
  component: App,
})

function App() {
  const { user, isLoading } = useUser();
  const navigate = useNavigate();

  useEffect(() => {
    if (user != null) {
      navigate({
        to: '/dashboard'
      });
    }
  }, [isLoading, user])

  return (
    <div className="text-center">
      <h1 className="text-4xl font-bold">Welcome to the Gmail Alias Wizard</h1>
      <p className="mt-4">Please log in to continue</p>
      <a href="/auth/google/login" className="mt-4 inline-block bg-blue-500 text-white py-2 px-4 rounded">
        Login With Google
      </a>
    </div>
  )
}
