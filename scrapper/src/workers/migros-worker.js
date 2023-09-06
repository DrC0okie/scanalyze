import {workerData, parentPort} from 'worker_threads';
import puppeteer from 'puppeteer';
import {v4} from 'uuid';

let products = []
console.log("URL : " + workerData.base_url);
// console.log("CAT : " + workerData.categories);


const run = async (cat) => {
    let has_more_product = true;
    const browser = await puppeteer.launch({
        executablePath: "C:/Program Files/Google/chrome/Application/chrome.exe",
        headless: "new"
    });
    const page = await browser.newPage();

    await page.setViewport({width: 1500, height: 1000});
    await page.setUserAgent('Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4427.0 Safari/537.36');
    await page.goto(workerData.base_url + "/" + cat)
    page.waitForNavigation();
    await page.exposeFunction("v4", v4);

    // Display all the product of the category (by default only 100 are displayed)
    while (has_more_product) {
        await page.waitForSelector(".primary.m-button.mat-mdc-button-base:not([disabled]", {timeout: 10000}).then(async (el) => {
            has_more_product = true;
            await page.evaluate(async () => {
                await document.querySelector(".primary.m-button.mat-mdc-button-base").click();
            })

        }).catch((error) => {
            has_more_product = false;
        });
    }

    let actual_products = await page.evaluate(async () => {
        const items = document.getElementsByClassName("product-show-details");
        let actual_products = [];

        for (let i = 0; i < items.length; i++) {
            const item = items[i];
            const actual_price = item.getElementsByClassName("actual").length ? parseFloat(item.getElementsByClassName("actual")[0].innerHTML.trim()) : 0
            const regular_price = item.getElementsByClassName("regular-price").length ? item.getElementsByClassName("regular-price")[0].children[2].innerHTML : 0
            let name = item.getElementsByClassName("name").length ? item.getElementsByClassName("name")[0].innerHTML + " " : "";
            console.log(name);
            for (let j = 0; j < item.getElementsByClassName("desc")[0].children.length; j++) {
                name = name.concat(" ", item.getElementsByClassName("desc")[0].children[j].innerHTML || " ");
            }
            actual_products.push(
                {
                    id: await v4(),
                    actual_price,
                    regular_price,
                    name
                }
            )

        }
        return actual_products;
    })
    browser.close();

    return actual_products;
}

for (let i = 0; i < workerData.categories.length; i++) {
    let prods = await run(workerData.categories[i].url);
    console.log("Finished fetching  " + prods.length + " product(s) in " + workerData.categories[i].name + " category");
    products = products.concat(prods);
}

parentPort.postMessage(products);