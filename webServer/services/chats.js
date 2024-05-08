import { Chat } from '../models/chat.js';
import { Msg } from '../models/massage.js';
import { MsgId } from '../models/msgId.js';
import { ChatId } from '../models/chatId.js';
import userService from './users.js'
import { ReturnDocument } from 'mongodb';


const getChatsByUsername = async (username) => {
    const userChats = await Chat.find().populate({
        path: 'users',
        model: 'User'
      })
      .populate({
        path: 'messages',
        populate: {
          path: 'sender',
          model: 'User'
        }
      })
      .populate({
        path: 'lastMessage',
        populate: {
          path: 'sender',
          model: 'User'
        }
      })
      .exec();
    const filterChats = userChats.filter(chat => { return chat.users.some(user => user.username === username)})
    
    const chatsPass = []

    filterChats.forEach(chat =>{
        var id = chat.id
        var user
        // only the other us
        if (chat.users[0].username=== username){
            user = {
                username: chat.users[1].username,
                displayName: chat.users[1].displayName,
                profilePic: chat.users[1].profilePic
            } 
        } else {
            user = {
                username: chat.users[0].username,
                displayName: chat.users[0].displayName,
                profilePic: chat.users[0].profilePic
            } 
        }

        var lastMessage = null
        if (chat.lastMessage){
            lastMessage = chat.lastMessage
            
        }
        chatsPass.push({id: id, user: user, lastMessage: lastMessage})
    })
    // list of chat 
    // chat: id, user, lastMessage 
    // user: username, display, profilpic
    return chatsPass
}

async function createChatId (){
    const currentId = await ChatId.findOne()
    if (currentId) {
        // not first
        const newId = currentId.id +1
        await ChatId.findOneAndReplace({id:currentId.id}, {id:newId})
        return currentId.id;
    }
    // first
    const currId = 0;
    const newId = 1;
    const id = new ChatId({ id: newId})
    await id.save()
    return currId
}
async function createMsgId (){
    const currentId = await MsgId.findOne()
    if (currentId) {
        // not first
        const newId = currentId.id +1
        await MsgId.findOneAndReplace({id:currentId.id}, {id:newId})
        return currentId.id;
    }
    // first
    const currId = 0;
    const newId = 1;
    const id = new MsgId({ id: newId})
    await id.save()
    return currId
}

const createChat = async (usernameToAdd, thisUser ) => {
    return new Promise (async (resolve, reject) => {
        // chat: id, user, lastMessage 
        // user: username, display, profilpic
        const userToAdd = await userService.getUserByUsernameFull(usernameToAdd)
        const currentUser = await userService.getUserByUsernameFull(thisUser)
        
        
        if(userToAdd){
            const chatId = await createChatId()
            const chat = new Chat({
                id: chatId,
                users: [userToAdd,currentUser],
                messages: [],
                lastMassage: null
            })
            await chat.save()
            
            const chatToPass = {
                id: chat.id,
                user: { username: usernameToAdd,
                        displayName: userToAdd.displayName,
                        profilePic: userToAdd.profilePic
                        },
                lastMassage: null
            }
            resolve(chatToPass )
        
            
        } else {
            reject(401)

        }  
    })
      
}

const getChatByIdFull = async (id) => {
    // chat: id, user, lastMessage 
    // user: username, display, profilpic
    const chat =  await Chat.findOne({ id: id })
    .populate({
      path: 'users',
      model: 'User'
    })
    .populate({
      path: 'messages',
      populate: {
        path: 'sender',
        model: 'User'
      }
    })
    .populate({
      path: 'lastMessage',
      populate: {
        path: 'sender',
        model: 'User'
      }
    })
    .exec();
    return chat
}

const getChatById = async (id, myUserName) => {
    // chat: id, user, lastMessage 
    // user: username, display, profilpic
    const chat =await getChatByIdFull(id)
        if (chat){
            var otherUser;
        if (chat.users[0].username === myUserName){
            otherUser = chat.users[1]
        } else {
            otherUser = chat.users[0]
        }
        const otherUserPass = {
            username: otherUser.username,
            displayName: otherUser.displayName,
            profilePic: otherUser.profilePic
        }
        const chatPass = {
            id: chat.id,
            user: otherUserPass,
            lastMessage: chat.lastMessage
        }
        return chatPass
    } else {
        return null
    }
    
}

const deleteChatById = async (id) =>{
    Chat.deleteOne({id: id}).then(()=> 200).catch(()=> 401)
}

function messagesToPass(messagesList){
    const messagesPass = []
    messagesList.forEach(msg => {
        var id = msg.id
        var content = msg.content
        var created = msg.created
        var sender = { username: msg.sender.username}
        messagesPass.push({id:id, content:content, created: created, sender: sender})
    })
    return messagesPass
}

const createMag = async (message, senderUsername, chatId) => {
    // return list mag 
    // msg: id, content, created, sender
    // sender: { username }
    if (message){
        const sender = await userService.getUserByUsernameFull(senderUsername);
    const id = await createMsgId();
    const newMessage = new Msg({
      id: id,
      sender: sender,
      content: message
    });
    await newMessage.save();

    const chat = await getChatByIdFull(chatId);
    if (chat) {
      chat.messages.push(newMessage);
      chat.lastMessage = newMessage;
      await chat.save();

      var messageList = chat.messages

      // Retrieve the updated chat with all messages populated

        
      const messagesPass = messagesToPass(messageList);
      return messagesPass;
    }
       

    }

  return null;
    
}

const getchatMessages = async (chatId) => {
    // return list mag 
    // msg: id, content, created, sender
    // sender: { username }
    const chat =await getChatByIdFull(chatId)
    if (chat){
        const messagesPass = messagesToPass(chat.messages)
        return messagesPass
    
    } else {
        return null
    }
    
}
export default {getChatsByUsername, createChat, getChatById, deleteChatById, createMag, getchatMessages}