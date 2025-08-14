import {useQuery} from "@tanstack/react-query";
import * as z from "zod";

const USER_INFO_ENDPOINT = '/api/auth/user/info';

const userSchema = z.object({
    id: z.number(),
    name: z.string(),
    email: z.email(),
    picture: z.url(),
    googleUserId: z.string()
})

type User = z.infer<typeof userSchema>;

export function useUser() {
    const {data: user, isLoading, error} = useQuery<User | null>({
        queryKey: ['user'],
        queryFn: async () => {
            try {
                const response = await fetch(USER_INFO_ENDPOINT, {credentials: "include"});
                if (!response.ok) {
                    return null
                }
                return userSchema.parse(await response.json());
            } catch (error) {
                return null;
            }
        },
    });

    return {user, isLoading, error};
}