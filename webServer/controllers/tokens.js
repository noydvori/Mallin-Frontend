import usersService from '../services/users.js'
import jwt from 'jsonwebtoken'
import tokenService from '../services/token.js'

const key = "Some super secret key shhhhhhhhhhhhhhhhh!!!!!"

const createToken = async (req, res) => {
    if ( await usersService.getUser(req.body.username, req.body.password) ){
        const username = req.body.username
        const user = {username: username}
        const key = "Some super secret key shhhhhhhhhhhhhhhhh!!!!!"
        const accessToken =  jwt.sign(user, key)
        res.json({ accessToken: accessToken})
        //res.json(accessToken);
        
    } else {
        res.status(404).send('Invalid username and/or password')
        
    }
    
}


/// 
const isAuthorized = async (req, res, next) => {
    const key= "Some super secret key shhhhhhhhhhhhhhhhh!!!!!"
    if (req.headers.authorization){
        // Extract the token from that header
        
        const token = req.headers.authorization.split(" ")[1];
        
        try {
            // Verify the token is valid
                
            const data =  jwt.verify(token, key);
            
            // Token validation was successful. Continue to the actual function (index)
            req.username= data.username
            return next( )
        } catch (err) {
                return res.status(401).send("Invalid Token");
        } 
    } else {
        return res.status(403).send('Token required');
    }

}

const addUserToken = async (req, res) => {
    tokenService.addUserToken(req.username,req.body.token).then((s)=> res.sendStatus(200)).catch((s)=>sendStatus(s))
}

export default {createToken, isAuthorized, addUserToken}