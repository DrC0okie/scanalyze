const sc = require('string-comparison')
const jw = require('jaro-winkler');

const single_levenshtein = (p1,p2) =>{
    return sc.levenshtein.similarity(p1.name,p2.name);
}
const single_cosine = (p1,p2) =>{
    return sc.cosine.similarity(p1.name,p2.name);
}
const single_dice = (p1,p2) =>{
    return sc.diceCoefficient.similarity(p1.name,p2.name);
}
const single_jaccard = (p1,p2) =>{
    return sc.jaccardIndex.similarity(p1.name,p2.name);
}
const single_jarowinkler = (p1,p2) =>{
    return jw(p1.name,p2.name);
}
const single_dice_price_weighted = (p1,p2) =>{
    const PRICE_WEIGHT = 1
    const ALGO_WEIGHT = 2
    const price_similarity = p1.price == p2.actual_price
    return (ALGO_WEIGHT * sc.diceCoefficient.similarity(p1.name,p2.name) + (price_similarity * PRICE_WEIGHT)) / (PRICE_WEIGHT + ALGO_WEIGHT)
}
const combo_dice_jaro_price_weighted = (p1,p2) =>{
    const PRICE_WEIGHT = 0.3
    const DICE_WEIGHT = 1
    const JARO_WEIGHT = 0.2
    const price_similarity = p1.price == p2.actual_price
    return (DICE_WEIGHT * sc.diceCoefficient.similarity(p1.name,p2.name) + (JARO_WEIGHT * jw(p1.name,p2.name)) + (price_similarity * PRICE_WEIGHT)) / (PRICE_WEIGHT + DICE_WEIGHT + JARO_WEIGHT)
}
const combo_dice_jaro = (p1,p2) =>{
    const DICE_WEIGHT = 1
    const JARO_WEIGHT = 0.5
    return (DICE_WEIGHT * sc.diceCoefficient.similarity(p1.name,p2.name) + (JARO_WEIGHT * jw(p1.name,p2.name))) / (DICE_WEIGHT + JARO_WEIGHT)
}
const combo_jaro_levenshtein = (p1,p2) =>{
    const LEVEN_WEIGHT = 1
    const JARO_WEIGHT = 1
    return (LEVEN_WEIGHT * sc.levenshtein.similarity(p1.name,p2.name) + (JARO_WEIGHT * jw(p1.name,p2.name))) / (LEVEN_WEIGHT + JARO_WEIGHT)
}
const combo_jaro_levenshtei_price = (p1,p2) =>{
    const PRICE_WEIGHT = 0.4
    const LEVEN_WEIGHT = 1
    const JARO_WEIGHT = 0.5
    const price_similarity = p1.price == p2.actual_price
    return (LEVEN_WEIGHT * sc.levenshtein.similarity(p1.name,p2.name) + (JARO_WEIGHT * jw(p1.name,p2.name)) + (price_similarity * PRICE_WEIGHT)) / (PRICE_WEIGHT + LEVEN_WEIGHT + JARO_WEIGHT)
}
const triple_jaro_levenshtei_dice_price = (p1,p2) =>{
    const PRICE_WEIGHT = 0.5
    const LEVEN_WEIGHT = 0.7
    const JARO_WEIGHT = 0
    const DICE_WEIGHT = 1.5
    const price_similarity = p1.price == p2.actual_price
    return (DICE_WEIGHT * sc.diceCoefficient.similarity(p1.name,p2.name) + (LEVEN_WEIGHT * sc.levenshtein.similarity(p1.name,p2.name)) + (JARO_WEIGHT * jw(p1.name,p2.name)) + (price_similarity * PRICE_WEIGHT)) / (PRICE_WEIGHT + LEVEN_WEIGHT + JARO_WEIGHT + DICE_WEIGHT)
}
const execute = (algo_name,algo_funct,targets,products,isFiltered) => {
    let correct = 0 ;
    let best_products = [];
    for(const target of targets){
        let best_product;
        let best_product_value = 0;
        //Filter only if parameter is true
        let filtered_products = isFiltered?  products.filter((el)=>{
            return el.actual_price == target.price || el.actual_price == 0
        }) : products

        for(let product of filtered_products){
            const rating = algo_funct(target,product)
            if(best_product_value < rating){
                best_product = {...product,rating}
                best_product_value = rating
            }
        }
        // if(best_product_value < CORRECT_THRESHOLD){
        //     let filtered_products = isFiltered?  products.filter((el)=>{
        //         return el.actual_price == 0
        //     }) : products
        //     for(let product of filtered_products){
        //         const rating = algo_funct(target.name,product.name)
        //         if(best_product_value < rating){
        //             best_product = {...product,rating}
        //             best_product_value = rating
        //         }
        //     }

        // }
        if(target.correct == best_product.id){
            correct+= 1;
            best_product.found = true;
        }else{
            best_product.found = false;
        }
        best_products.push(best_product);
    }
    return {
        name:algo_name,
        performance: correct * 1.0 /targets.length * 100,
        //best_products
    };
}

module.exports = {
    single_cosine,
    single_levenshtein,single_dice,
    single_jaccard,
    single_jarowinkler,
    single_dice_price_weighted,
    combo_dice_jaro,
    combo_dice_jaro_price_weighted,
    combo_jaro_levenshtein,
    combo_jaro_levenshtei_price,
    triple_jaro_levenshtei_dice_price,
    execute
}