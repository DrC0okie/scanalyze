const sc = require('string-comparison')
const jw = require('jaro-winkler');
/*  Every function below use a certain string matching algorithm. 
    All single function use one algorithm
    All combo function use two algorithm and the functions suffixed with "price"
    also use the price to determine the best match
    All combo and triple function use a weighted average as result.
    The triple function is currently being used in the index action as it is the most efficient function.
*/
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
    const price_similarity = p1.unit_price == p2.actual_price
    return (ALGO_WEIGHT * sc.diceCoefficient.similarity(p1.product_name,p2.name) + (price_similarity * PRICE_WEIGHT)) / (PRICE_WEIGHT + ALGO_WEIGHT)
}
const combo_dice_jaro_price_weighted = (p1,p2) =>{
    const PRICE_WEIGHT = 0.3
    const DICE_WEIGHT = 1
    const JARO_WEIGHT = 0.2
    const price_similarity = p1.unit_price == p2.actual_price
    return (DICE_WEIGHT * sc.diceCoefficient.similarity(p1.product_name,p2.name) + (JARO_WEIGHT * jw(p1.product_name,p2.name)) + (price_similarity * PRICE_WEIGHT)) / (PRICE_WEIGHT + DICE_WEIGHT + JARO_WEIGHT)
}
const combo_dice_jaro = (p1,p2) =>{
    const DICE_WEIGHT = 1
    const JARO_WEIGHT = 0.5
    return (DICE_WEIGHT * sc.diceCoefficient.similarity(p1.product_name,p2.name) + (JARO_WEIGHT * jw(p1.product_name,p2.name))) / (DICE_WEIGHT + JARO_WEIGHT)
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
const combo_dice_levenshtein_price = (p1,p2) =>{
    const PRICE_WEIGHT = 0.5
    const LEVEN_WEIGHT = 0.7
    const DICE_WEIGHT = 1.5
    const price_similarity = p1.price == p2.actual_price
    return (DICE_WEIGHT * sc.diceCoefficient.similarity(p1.product_name,p2.name) + (LEVEN_WEIGHT * sc.levenshtein.similarity(p1.name,p2.name)) + (price_similarity * PRICE_WEIGHT)) / (PRICE_WEIGHT + LEVEN_WEIGHT + DICE_WEIGHT)
}
const triple_jaro_levenshtei_dice_price = (p1,p2) =>{
    const PRICE_WEIGHT = 0.5
    const LEVEN_WEIGHT = 0.7
    const JARO_WEIGHT = 0.2
    const DICE_WEIGHT = 1.5
    const price_similarity = p1.unit_price == p2.actual_price
    return (DICE_WEIGHT * sc.diceCoefficient.similarity(p1.product_name,p2.name) + (LEVEN_WEIGHT * sc.levenshtein.similarity(p1.product_name,p2.name)) + (JARO_WEIGHT * jw(p1.product_name,p2.name)) + (price_similarity * PRICE_WEIGHT)) / (PRICE_WEIGHT + LEVEN_WEIGHT + JARO_WEIGHT + DICE_WEIGHT)
}
/* This function mesure an algorithm's performance it was used when testing
   the different algorithms.
*/
const test = (algo_name,algo_funct,targets,products,isFiltered) => {
    let correct = 0 ;
    let best_products = [];
    for(const target of targets){
        let best_product;
        let best_product_value = 0;
        //Filter only if parameter is true
        let filtered_products = isFiltered?  products.filter((el)=>{
            return el.actual_price == target.unit_price || el.actual_price == 0
        }) : products
        //Loop trough all products and save the product if it has a better rating than current best product
        for(let product of filtered_products){
            const rating = algo_funct(target,product)
            if(best_product_value < rating){
                best_product = {...product,rating}
                best_product_value = rating
            }
        }
        //If the algorithm found the correct product add 1 to the score
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
        best_products
    };
}
/* This function  run the indexation algorithm on a target with a set of products */
const run = (target,products) =>{
    
    let best_product;
    let best_product_value = 0;
    
    for(let product of products){
        const rating = triple_jaro_levenshtei_dice_price(target,product)
        if(best_product_value < rating){
            best_product = {...product,rating}
            best_product_value = rating
        }
    }
    return best_product
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
    combo_dice_levenshtein_price,
    triple_jaro_levenshtei_dice_price,
    test,
    run
}