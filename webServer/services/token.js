import {Token} from '../models/tokensFireBase.js'

const addUserToken = async (username, token) =>{
    const tokenUser = new Token({
        username: username,
        token: token
    })

    return await tokenUser.save()

}

const getUserToken = async (username) => {
    return await Token.findOne({username: username})
}

export default {addUserToken, getUserToken}
