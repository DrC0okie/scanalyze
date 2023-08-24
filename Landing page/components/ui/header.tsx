import Link from 'next/link'
import Image from 'next/image'
import Logo from '@/public/images/scanalyze_two-tone_light-bg_minimal.png'
export default function Header() {
  return (
    <header className="absolute w-full z-30">
      <div className="max-w-6xl mx-auto px-4 sm:px-6">
        <div className="flex items-center  h-20">
          {/* Site branding */}
          <div className="shrink-0 mr-4 mt-4">
            {/* Logo */}
            <Link href="/" className="block" aria-label="Cruip">
             <Image src={Logo} alt="logo" width={100} height={100}></Image>
            </Link>
          </div>
        </div>
      </div>
    </header>
  )
}
