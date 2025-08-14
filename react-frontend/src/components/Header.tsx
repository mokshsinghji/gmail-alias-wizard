import {Link} from '@tanstack/react-router'
import {useUser} from "@/hooks/useUser.ts";

export default function Header() {
    const {user, isLoading} = useUser();
    return (
        <header className="p-2 flex gap-2 bg-white text-black justify-between">
            <nav className="flex flex-row justify-between items-center">
                <div className="px-2 font-bold">
                    <Link to="/">Home</Link>
                </div>

            </nav>
            {user && (
                <div className={"flex flex-row gap-2 items-center"}>
                    <img src={user?.picture} className={"h-6 w-6 rounded-full"} alt={user?.name}/>
                    <p>{user?.name}</p>
                </div>
            )}
            {!user && !isLoading && (
                <div className={"flex flex-row gap-2 items-center"}>
                    <a href='/auth/google/login'>Login With Google</a>
                </div>
            )}
        </header>
    )
}
