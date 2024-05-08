import chatsService from '../services/chats.js'

const getChats = async(req, res) => {
    console.log("hi");
    const chats = await chatsService.getChatsByUsername(req.username)
    res.json(chats)
    
}

const createChat = async(req, res) => {
    console.log(res.body)
    chatsService.createChat(req.body.username, req.username).then((chat)=> res.json(chat)).catch((e)=>res.sendStatus(e))
}

const getChatById = async(req, res) => {
   const chat = await chatsService.getChatById(req.params.id,req.username )
    if (chat){
        res.json(chat)
    } else {
        res.sendStatus(401)
    }
       
}

const deleteChatById = async(req, res) => {
    chatsService.deleteChatById(req.params.id).then((s)=> res.sendStatus(200)).catch((s)=>sendStatus(s))
}

const createMag = async(req, res) => {
    const chat = await chatsService.createMag(req.body.msg, req.username, req.params.id)
    if (chat){
        res.json(chat)
    } else {
        res.sendStatus(401)
    }
    // if id not exists 
}

const getchatMessages = async(req, res) => {
    const chatMsg = await chatsService.getchatMessages(req.params.id)
    if (chatMsg) {
        res.json(chatMsg)
    } else {
        res.sendStatus(401)
    }
}

export default {getChats, createChat, getChatById, deleteChatById, createMag, getchatMessages}