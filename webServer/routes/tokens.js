import { Router } from 'express';
import tokensController from '../controllers/tokens.js';

const router = Router();

router.post('/', tokensController.createToken);

router.post('/UserToken',tokensController.isAuthorized, tokensController.addUserToken);

export default router;