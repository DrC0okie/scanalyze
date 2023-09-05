const { CognitoJwtVerifier } = require("aws-jwt-verify");
/* 
Middleware that verify that for every request a valid JWT is attached. 
This verify that the request comes from an Authentified user
*/
module.exports = async (req,res,next) => {
    // Verifier that expects valid access tokens:
    const verifier = CognitoJwtVerifier.create({
      userPoolId: process.env.AWS_COGNITO_USER_POOL_ID,
      tokenUse: "access",
      clientId: process.env.AWS_COGNITO_CLIENT_ID,
    });
    
    try {
      const payload = await verifier.verify(
        "eyJraWQeyJhdF9oYXNoIjoidk" // the JWT as string
      );
      console.log("Token is valid. Payload:", payload);
      next();
    } catch {
      res.status(401).json({
        error:"Unauthorized access"
      })
    }
}


