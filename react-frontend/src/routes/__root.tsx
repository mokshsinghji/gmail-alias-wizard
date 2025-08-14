import {HeadContent, Scripts, createRootRoute} from '@tanstack/react-router'
import {TanStackRouterDevtoolsPanel} from '@tanstack/react-router-devtools'
import {TanstackDevtools} from '@tanstack/react-devtools'
import {QueryClient, QueryClientProvider} from '@tanstack/react-query'

import Header from '../components/Header'

import appCss from '../styles.css?url'

export const Route = createRootRoute({
    head: () => ({
        meta: [
            {
                charSet: 'utf-8',
            },
            {
                name: 'viewport',
                content: 'width=device-width, initial-scale=1',
            },
            {
                title: 'TanStack Start Starter',
            },
        ],
        links: [
            {
                rel: 'stylesheet',
                href: appCss,
            },
        ],
    }),

    shellComponent: RootDocument,
})

const queryClient = new QueryClient();

function RootDocument({children}: { children: React.ReactNode }) {
    return (
        <html lang="en">
        <head>
            <HeadContent/>
        </head>
        <body>
        <QueryClientProvider client={queryClient}>
            <Header/>
            {children}
        </QueryClientProvider>

        <TanstackDevtools
            config={{
                position: 'bottom-left',
                hideUntilHover: true
            }}
            plugins={[
                {
                    name: 'Tanstack Router',
                    render: <TanStackRouterDevtoolsPanel/>,
                },
            ]}
        />
        <Scripts/>
        </body>
        </html>
    )
}
