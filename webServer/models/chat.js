import mongoose from 'mongoose'
import {Msg} from './massage.js'
import {User} from './users.js'

const Schema = mongoose.Schema
const ChatSchema  = new Schema({
        id: {
            type: Number,
            required: true
        },
        users: [
            {
              type: mongoose.ObjectId,
              ref: 'User',
              required: true
            },
          ],
        messages: [
            {
              type: Schema.Types.ObjectId,
              ref: 'Msg',
            },
          ],
        lastMessage: {
          type: Schema.Types.ObjectId,
          ref: 'Msg',
          nullable: true,
        }
})

export const Chat = mongoose.model('Chat', ChatSchema);