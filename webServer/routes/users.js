import { Router } from 'express';
import usersController from '../controllers/users.js';
import tokensController from '../controllers/tokens.js';

const router = Router();

router.post('/', usersController.createUser);

router.get('/:username',tokensController.isAuthorized, usersController.getUser)



export default router;