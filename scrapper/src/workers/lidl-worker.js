import puppeteer from 'puppeteer';
import { workerData, parentPort } from 'worker_threads';
const FOUND_PRODUCT = 6708;
let products = []
console.log("URL : " + workerData.base_url);
console.log("CAT : " + workerData.categories);

const run = async (cat) => {
  let cat_products = [];
  let has_next_page = true;
  let no_page = 1;
  const browser = await puppeteer.launch({
    executablePath: "C:/Program Files/BraveSoftware/Brave-Browser/Application/brave.exe",
    headless: false
  });
  const page = await browser.newPage();

  await page.setViewport({ width: 1000, height: 750 });

  await page.setUserAgent('Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4427.0 Safari/537.36');

  await page.goto(workerData.base_url + "/" + cat, { waitUntil: "networkidle0" });
  await page.click(".cookie-alert-extended-button");
  console.log(page.url());
  //await autoScroll(page);
  //document.getElementsByClassName("product-item-link")[document.getElementsByClassName("product-item-link").length - 1].scrollIntoView()
  const count = await page.evaluate(() => {
    return document.getElementsByClassName("amshopby-filter-parent amshopby-link-selected")[0].children[0].innerHTML
  })
  let scroll_count = 0;
  let new_scroll_count = -1;
  //Scroll to bottom
  await page.evaluate(() => {
    window.scrollTo(0, window.document.body.scrollHeight);
  });
  //Load products
  while (scroll_count != count){
    scroll_count = await page.evaluate(() => {
      return document.getElementsByClassName("product-item-link").length;
    });
    console.log("SC : " + scroll_count);
  }
  //Get products
  cat_products = await page.evaluate(() => {
    let products = [];
    const names =  document.getElementsByClassName("product name product-item-name");
    const prices = document.getElementsByClassName("pricefield__price")
    for(let i= 0 ; i < names.length;i++){
      products.push({
        name : names[i].innerHTML || "Unnamed",
        price : prices[i].getAttribute("content") || 0.00
      })
    }
    return products
  });

  
  browser.close();
  return cat_products;
}

for (let i = 0; i < workerData.categories.length; i++) {
  let prods = await run(workerData.categories[i]).catch(err => {
    console.log(err);
  });
  console.log("Finished fetching  " + prods.length + " product(s) in " + workerData.categories[i] + " category");
  products = products.concat(prods);
}

parentPort.postMessage(products);