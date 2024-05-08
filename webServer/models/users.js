import mongoose from 'mongoose'

const Schema = mongoose.Schema
const UserSchema  = new Schema({
        username: {
            type: String,
            required: true
        },
        password: {
            type: String,
            required: true
        },
        displayName: {
            type: String,
            required: true
        },
        profilePic:  {
            type: String,
            required: true
        }
})

export const User = mongoose.model('User', UserSchema);