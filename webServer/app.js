import express from 'express';
import bodyParser from 'body-parser';
import customEnv from 'custom-env';
import cors from 'cors';
import mongoose from 'mongoose';
import users from './routes/users.js';
import tokens from './routes/tokens.js';
import chats from './routes/chats.js';
import { fileURLToPath } from 'url';
import path from 'path';
import http from 'http';
import { Server } from 'socket.io';
import chatServices from './services/chats.js'
//import admin "firebase-admin";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

var app = express();
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.json());
app.use(cors());

customEnv.env(process.env.NODE_ENV, './webServer/config');

mongoose
  .connect(process.env.CONNECTION_STRING, {
    useNewUrlParser: true,
    useUnifiedTopology: true
  })
  .then(() => {});


//var serviceAccount = require("./my-project-apex3-firebase-adminsdk-swoil-c9736c0352.json");

//admin.initializeApp({
//  credential: admin.credential.cert(serviceAccount)
//});
  

app.use('/Users', users);
app.use('/Tokens', tokens);
app.use('/Chats', chats);

app.use(express.static('build'));
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, '..', 'build', 'index.html'));
});

const server = http.createServer(app);
const io = new Server(server);
const sockets = [];

io.on('connection', (socket) => {
  socket.on('connecting', (username) => {
    if (sockets[username]) {
      // Disconnect the existing socket
      sockets[username].disconnect();
    }
    sockets[username] = socket; // Store the socket object with the username as the key
  });

  socket.on('add-contact', (username) => {
    if (!sockets[username]) {
      return;
    }
    sockets[username].emit('add-contact', username); // not in use
  });
  socket.on('get-message',async (msg) => {
    if (!sockets[msg.recieverUname]) {
      return;
    }
    const chats = await chatServices.getChatsByUsername(msg.recieverUname)
    sockets[msg.recieverUname].emit('get-message', msg);
  });

  socket.emit('message', 'Welcome to our server!');
});


server.listen(process.env.PORT, () => {
  console.log('Listening on port'+process.env.PORT);
});
