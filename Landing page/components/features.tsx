export default function Features() {
  return (
    <section>
      <div className="max-w-6xl mx-auto px-4 sm:px-6">
        <div className="py-12 md:py-20">

          {/* Section header */}
          <div className="max-w-3xl mx-auto text-center pb-12 md:pb-20" data-aos="fade-up" data-aos-delay="300">
            <h2 className="h2 mb-4 font-title">Scan, Analyze, Save</h2>
            <div className="flex flex-col items-center gap-4 font-text">
              <p className="text-lg border-purple-600 rounded-lg border-2 p-2 text-gray-200 text-center">Simply snap a photo of your grocery receipt using your smartphone. Our cutting-edge Optical Character Recognition (OCR) technology will accurately read the receipt and extract all the important details in seconds. No manual input, no fuss.
                Analyze</p>
              <p className="text-lg border-purple-600 rounded-lg border-2 p-2 text-gray-200 text-center">Unlock the power of analytics. View real-time summaries, detailed expense categories, and insightful charts that help you understand your spending habits. Our app empowers you to make data-driven decisions that could save you money in the long run. Whether it's about cutting down on snacks or finding out which store gives you the best value, we've got you covered.</p>
              <p className="text-lg border-purple-600 rounded-lg border-2 p-2 text-gray-200 text-center">By knowing exactly where your money is going, you can set achievable budget goals and celebrate when you meet them. Make every grocery trip a winning experience!</p>
            </div>
          </div>

          {/* Items */}
          <div className="max-w-sm mx-auto grid gap-8 md:grid-cols-2 lg:grid-cols-2 lg:gap-16 items-start md:max-w-2xl lg:max-w-3xl text-justify">

            {/* 1st item */}
            <div className="relative flex flex-col items-center" data-aos="fade-up" data-aos-delay="0" data-aos-anchor="[data-aos-id-blocks]">

              <svg className="w-16 h-16 mb-4" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                <path className="stroke-purple-600" fill="none" strokeLinejoin="round" strokeWidth="2" d="M10 6v4l3.276 3.276M19 10a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z" />
              </svg>
              <h4 className="h4 mb-2 font-title">Real-Time </h4>
              <p className="text-lg text-gray-200 text-center font-text">Scan and know instantly your expenses.</p>
            </div>

            {/* 2nd item */}
            <div className="relative flex flex-col items-center" data-aos="fade-up" data-aos-delay="100" data-aos-anchor="[data-aos-id-blocks]">
              <svg className="stroke-purple-600 w-16 h-16 mb-4" strokeWidth="2" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path d="M2 9.376v.786l8 3.925 8-3.925v-.786M1.994 14.191v.786l8 3.925 8-3.925v-.786M10 1.422 2 5.347l8 3.925 8-3.925-8-3.925Z" /></svg>
              <h4 className="h4 mb-2 font-title">Multi-Store Compatibility</h4>
              <p className="text-lg text-gray-200 text-center font-text">Our app seamlessly manages major Swiss supermarkets including Coop, Migros, Aldi, and Lidl.</p>
            </div>

            {/* 3rd item */}
            <div className="relative flex flex-col items-center" data-aos="fade-up" data-aos-delay="200" data-aos-anchor="[data-aos-id-blocks]">
              <svg className="w-16 h-16 mb-4 stroke-purple-600" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg" fill="none">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M1 1v14h16m0-9-3-2-3 5-3-2-3 4" />
              </svg>
              <h4 className="h4 mb-2 font-title">Detailed Analytics</h4>
              <div className="flex flex-col items-center"></div>
              <p className="text-lg text-gray-200 font-text text-center">Get charts, graphs, and real insights of where your money goes.</p>
            </div>

            {/* 4th item */}
            <div className="relative flex flex-col items-center" data-aos="fade-up" data-aos-delay="300" data-aos-anchor="[data-aos-id-blocks]">
              <svg className="w-16 h-16 mb-4 stroke-purple-600" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="none">
                <path strokeWidth="2" d="M7 8a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7Zm-2 3h4a4 4 0 0 1 4 4v2H1v-2a4 4 0 0 1 4-4Z"/>
              </svg>
              <h4 className="h4 mb-2 font-title">User-friendly interface</h4>
              <p className="text-lg text-gray-200 text-center font-title">Effortlessly navigate for rapid insights, making informed decisions has never been easier.</p>
            </div>
            

          </div>

        </div>
      </div>
    </section>
  )
}
