import type { Metadata } from 'next'
import './styles/globals.css'
import PageTransitionWrapper from "@/components/PageTransitionWrapper";


export const metadata: Metadata = {
    title: 'Pingo: Uma Aventura Na Internet',}
export default function RootLayout({children}: {
    children: React.ReactNode
}) {
    return (
        <html lang="en" suppressHydrationWarning={true}>
        <body>
            <PageTransitionWrapper>
                {children}
            </PageTransitionWrapper>
        </body>
        </html>
    )
}