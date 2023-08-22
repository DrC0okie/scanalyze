export default function Features() {
  return (
    <section>
      <div className="max-w-6xl mx-auto px-4 sm:px-6">
        <div className="py-12 md:py-20">

          {/* Section header */}
          <div className="max-w-3xl mx-auto text-center pb-12 md:pb-20" data-aos="fade-up" data-aos-delay="300">
            <h2 className="h2 mb-4">Scan, Analyze, Save</h2>
            <div className="flex flex-col items-center gap-4">
              <p className="text-lg border-purple-600 rounded-lg border-2 p-2 text-gray-400 text-center">Simply snap a photo of your grocery receipt using your smartphone. Our cutting-edge Optical Character Recognition (OCR) technology will accurately read the receipt and extract all the important details in seconds. No manual input, no fuss.
                Analyze</p>
              <p className="text-lg border-purple-600 rounded-lg border-2 p-2 text-gray-400 text-center">Unlock the power of analytics. View real-time summaries, detailed expense categories, and insightful charts that help you understand your spending habits. Our app empowers you to make data-driven decisions that could save you money in the long run. Whether it's about cutting down on snacks or finding out which store gives you the best value, we've got you covered.</p>
              <p className="text-lg border-purple-600 rounded-lg border-2 p-2 text-gray-400 text-center">By knowing exactly where your money is going, you can set achievable budget goals and celebrate when you meet them. Make every grocery trip a winning experience!</p>
            </div>
          </div>

          {/* Items */}
          <div className="max-w-sm mx-auto grid gap-8 md:grid-cols-2 lg:grid-cols-3 lg:gap-16 items-start md:max-w-2xl lg:max-w-none">

            {/* 1st item */}
            <div className="relative flex flex-col items-center">

              <svg className="w-16 h-16 mb-4" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                <path className="stroke-purple-600" fill="none" strokeLinejoin="round" strokeWidth="2" d="M10 6v4l3.276 3.276M19 10a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z" />
              </svg>
              <h4 className="h4 mb-2">Real-Time </h4>
              <p className="text-lg text-gray-400 text-center">Scan and know instantly your expenses</p>
            </div>

            {/* 2nd item */}
            <div className="relative flex flex-col items-center">
              <svg className="stroke-purple-600 w-16 h-16 mb-4" strokeWidth="2" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path d="M2 9.376v.786l8 3.925 8-3.925v-.786M1.994 14.191v.786l8 3.925 8-3.925v-.786M10 1.422 2 5.347l8 3.925 8-3.925-8-3.925Z" /></svg>
              <h4 className="h4 mb-2">Multi-Store Compatibility</h4>
              <p className="text-lg text-gray-400 text-center">Every big Swiss supermarket is handled by our app, Coop, Migros, Aldi and Lidl.</p>
            </div>

            {/* 3rd item */}
            <div className="relative flex flex-col items-center">
              <svg className="w-16 h-16 mb-4 stroke-purple-600" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg" fill="none">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M1 1v14h16m0-9-3-2-3 5-3-2-3 4" />
              </svg>
              <h4 className="h4 mb-2">Detailed Analytics</h4>
              <div className="flex flex-col items-center"></div>
              <p className="text-lg text-gray-400 text-center">Get charts, graphs, and real insights of where your money goes</p>
            </div>

            {/* 4th item */}
            <div className="relative flex flex-col items-center" data-aos="fade-up" data-aos-delay="300" data-aos-anchor="[data-aos-id-blocks]">
              <svg className="w-16 h-16 mb-4" viewBox="0 0 64 64" xmlns="http://www.w3.org/2000/svg">
                <rect className="fill-current text-purple-600" width="64" height="64" rx="32" />
                <g transform="translate(22 21)" strokeLinecap="square" strokeWidth="2" fill="none" fillRule="evenodd">
                  <path className="stroke-current text-purple-100" d="M17 22v-6.3a8.97 8.97 0 003-6.569A9.1 9.1 0 0011.262 0 9 9 0 002 9v1l-2 5 2 1v4a2 2 0 002 2h4a5 5 0 005-5v-5" />
                  <circle className="stroke-current text-purple-300" cx="13" cy="9" r="3" />
                </g>
              </svg>
              <h4 className="h4 mb-2">Instant Features</h4>
              <p className="text-lg text-gray-400 text-center">Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat.</p>
            </div>

            {/* 5th item */}
            <div className="relative flex flex-col items-center" data-aos="fade-up" data-aos-delay="400" data-aos-anchor="[data-aos-id-blocks]">
              <svg className="w-16 h-16 mb-4" viewBox="0 0 64 64" xmlns="http://www.w3.org/2000/svg">
                <rect className="fill-current text-purple-600" width="64" height="64" rx="32" />
                <g strokeLinecap="square" strokeWidth="2" fill="none" fillRule="evenodd">
                  <path className="stroke-current text-purple-100" d="M29 42h10.229a2 2 0 001.912-1.412l2.769-9A2 2 0 0042 29h-7v-4c0-2.373-1.251-3.494-2.764-3.86a1.006 1.006 0 00-1.236.979V26l-5 6" />
                  <path className="stroke-current text-purple-300" d="M22 30h4v12h-4z" />
                </g>
              </svg>
              <h4 className="h4 mb-2">Instant Features</h4>
              <p className="text-lg text-gray-400 text-center">Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat.</p>
            </div>

            {/* 6th item */}
            <div className="relative flex flex-col items-center" data-aos="fade-up" data-aos-delay="500" data-aos-anchor="[data-aos-id-blocks]">
              <svg className="w-16 h-16 mb-4" viewBox="0 0 64 64" xmlns="http://www.w3.org/2000/svg">
                <rect className="fill-current text-purple-600" width="64" height="64" rx="32" />
                <g transform="translate(21 22)" strokeLinecap="square" strokeWidth="2" fill="none" fillRule="evenodd">
                  <path className="stroke-current text-purple-300" d="M17 2V0M19.121 2.879l1.415-1.415M20 5h2M19.121 7.121l1.415 1.415M17 8v2M14.879 7.121l-1.415 1.415M14 5h-2M14.879 2.879l-1.415-1.415" />
                  <circle className="stroke-current text-purple-300" cx="17" cy="5" r="3" />
                  <path className="stroke-current text-purple-100" d="M8.86 1.18C3.8 1.988 0 5.6 0 10c0 5 4.9 9 11 9a10.55 10.55 0 003.1-.4L20 21l-.6-5.2a9.125 9.125 0 001.991-2.948" />
                </g>
              </svg>
              <h4 className="h4 mb-2">Instant Features</h4>
              <p className="text-lg text-gray-400 text-center">Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat.</p>
            </div>

          </div>

        </div>
      </div>
    </section>
  )
}
