import Image from 'next/image'

import Tim from '@/public/images/tim.jpg'
import David from '@/public/images/david.jpg'
import Jarod from '@/public/images/jarod.jpg'

export default function Testimonials() {
  return (
    <section>
      <div className="max-w-6xl mx-auto px-4 sm:px-6">
        <div className="py-12 md:py-20 border-t border-gray-800">

          {/* Section header */}
          <div className="max-w-3xl mx-auto text-center pb-12 md:pb-20">
            <h2 className="h2 mb-4 font-title">Our beloved team</h2>
            <p className="text-xl text-gray-200 font-text">Vitae aliquet nec ullamcorper sit amet risus nullam eget felis semper quis lectus nulla at volutpat diam ut venenatis tellus—in ornare.</p>
          </div>

          {/* Testimonials */}
          <div className="max-w-sm mx-auto grid gap-8 lg:grid-cols-3 lg:gap-6 items-start lg:max-w-none">

            {/* 1st testimonial */}
            <div className="flex flex-col h-full p-6 bg-black border-purple-600  border-2" data-aos="fade-up" data-aos-delay="400">
              <div>
                <div className="relative flex justify-center">
                <Image className="rounded-full mb-2" src={Jarod} width={200} height={200} alt="Testimonial 02" />
                  
                </div>
              </div>
              <div className="text-gray-700 font-medium mt-6 pt-5 border-t border-gray-700">
                <cite className="text-gray-200 not-italic font-title">Jarod Streckeisen</cite> - <a className="text-purple-600 font-text hover:text-gray-200 transition duration-150 ease-in-out" href="#0">CTO</a>
              </div>
            </div>

            {/* 2nd testimonial */}
            <div className="flex flex-col h-full p-6 bg-black border-purple-600  border-2" data-aos="fade-up" data-aos-delay="400">
              <div>
                <div className="relative flex justify-center">
                <Image className="rounded-full mb-2" src={Tim} width={200} height={200} alt="Testimonial 02" />
                  
                </div>
              </div>
              <div className="text-gray-700 font-medium mt-6 pt-5 border-t border-gray-700">
                <cite className="text-gray-200 not-italic font-title">Timothée Van Hove</cite> - <a className="text-purple-600 font-text hover:text-gray-200 transition duration-150 ease-in-out" href="#0">CEO</a>
              </div>
            </div>

            {/* 3rd testimonial */}
            <div className="flex flex-col h-full p-6 bg-black border-purple-600  border-2" data-aos="fade-up" data-aos-delay="400">
              <div>
                <div className="relative flex justify-center">
                <Image className="rounded-full mb-2" src={David} width={200} height={200} alt="Testimonial 02" />
                  
                </div>
              </div>
              <div className="text-gray-700 font-medium mt-6 pt-5 border-t border-gray-700">
                <cite className="text-gray-200 not-italic font-title">Anthony David</cite> - <a className="text-purple-600 font-text hover:text-gray-200 transition duration-150 ease-in-out" href="#0">COO</a>
              </div>
            </div>

          </div>

        </div>
      </div>
    </section>
  )
}
