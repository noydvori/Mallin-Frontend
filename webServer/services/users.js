import { User } from '../models/users.js';

// assum all fields are given
const createUser = async (username, password, displayName, profilePic) => {
    //check if user already exists
    
    const user = new User({
        username: username,
        password: password,
        displayName: displayName,
        profilePic: profilePic
    })
    return await user.save()
    // return the object itself
}

const getUser = async (username, password) => {
    const userfind = await User.findOne({username: username, password: password})   
    return userfind
}

const getUserByUsername = async (username) => {
    const user =  await User.findOne({username: username})
    if (user) {
        const userPass ={
            username: user.username,
            displayName: user.displayName,
            profilePic: user.profilePic
        }
        return userPass
    } else {
        return null
    }
    
    // username, display, profilpic 
}

const getUserByUsernameFull = async (username) => {
    return await User.findOne({username: username})

}

export default {createUser, getUser, getUserByUsername, getUserByUsernameFull}