import mongoose from 'mongoose'

const Schema = mongoose.Schema
const TokenSchema  = new Schema({
        username: {
            type: String,
            required: true
        },
        token: {
            type: String,
            required: true
        },
       
})

export const Token = mongoose.model('Token', TokenSchema);