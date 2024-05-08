import { Router } from 'express';
import tokensController from '../controllers/tokens.js';
import chatsController from '../controllers/chats.js'

const router = Router();

router.get('/',tokensController.isAuthorized, chatsController.getChats)

// create new chat , in the request the person. the user checks if the user exists
router.post('/',tokensController.isAuthorized, chatsController.createChat )

router.get('/:id', tokensController.isAuthorized, chatsController.getChatById )

// just delete, need to return new chats list?
router.delete('/:id', tokensController.isAuthorized, chatsController.deleteChatById)

// return updated chat opject
router.post('/:id/Messages', tokensController.isAuthorized, chatsController.createMag)

router.get('/:id/Messages',  tokensController.isAuthorized, chatsController.getchatMessages)

export default router;
