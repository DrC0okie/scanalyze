import './css/style.css'


import Header from '@/components/ui/header'
import Banner from '@/components/banner'




export const metadata = {
  title: 'Create Next App',
  description: 'Generated by create next app',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en">
      <body className=" font-inter antialiased bg-gray-900 text-gray-200 tracking-tight`">
        <div className="flex flex-col min-h-screen overflow-hidden">
          <Header />
          {children}
          <Banner />
        </div>
      </body>
    </html>
  )
}
 