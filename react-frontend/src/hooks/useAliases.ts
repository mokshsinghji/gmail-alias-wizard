import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import z from "zod";

const GET_CURRENT_ALIASES_ENDPOINT = "/api/aliases";

const aliasSchema = z.object({
    sendAsEmail: z.string(),
    displayName: z.string().optional().nullable(),
    isDefault: z.boolean(),
    replyToAddress: z.string().optional().nullable(),
    verificationStatus: z.string().optional().nullable(),
})

export function useAliases() {
    const { data, error } = useQuery({
        queryKey: ['aliases'],
        queryFn: async () => {
            const response = await fetch(GET_CURRENT_ALIASES_ENDPOINT);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const data = await response.json();

            return z.array(aliasSchema).parse(data);
        }
    });

    const qc = useQueryClient();

    const addAlias = useMutation({
        mutationKey: ["addAlias"],
        mutationFn: async ({ newAlias, newAliasName }: { newAlias: string, newAliasName: string }) => {
            const response = await fetch(GET_CURRENT_ALIASES_ENDPOINT, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    displayName: newAliasName,
                    emailAlias: newAlias
                })
            });
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            console.log(data);
            return aliasSchema.parse(data);
        },
        onSettled: () => {
            qc.cancelQueries({ queryKey: ['aliases'] });
            qc.invalidateQueries({ queryKey: ['aliases'] });
            qc.refetchQueries({ queryKey: ['aliases'] });
        }
    })

    return {
        aliases: data,
        addAlias,
        isLoading: !error && !data,
        isError: error
    };
}
