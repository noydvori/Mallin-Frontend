import mongoose from 'mongoose'

// save one next id
const Schema = mongoose.Schema
const ChatIdSchema  = new Schema({
        id: {
            type: Number,
            required: true,
            default: 0
        },
        
})

export const ChatId = mongoose.model('ChatId', ChatIdSchema);