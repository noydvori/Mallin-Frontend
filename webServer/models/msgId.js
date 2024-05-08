import mongoose from 'mongoose'

// save one next id
const Schema = mongoose.Schema
const MsgIdSchema  = new Schema({
        id: {
            type: Number,
            required: true,
            default: 0
        },
        
})

export const MsgId = mongoose.model('MsgId', MsgIdSchema);