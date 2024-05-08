import mongoose from 'mongoose'
import {User} from './users.js'

const Schema = mongoose.Schema
const MsgSchema  = new Schema({
        id: {
            type: Number,
            required: true
        },
        created: {
            type: Date,
            default: Date.now,
            required: true
        },
        sender: {
            type: Schema.Types.ObjectId,
            ref: 'User',
            required: true
        },
        content:  {
            type: String,
            nullable: true,
            required: true
        }
})

export const Msg = mongoose.model('Msg', MsgSchema);