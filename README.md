# Tic Tac Toe Online 🎮

A beautiful, real-time multiplayer Tic Tac Toe game featuring both a **Web Version** and a **Standalone Java Desktop App**. Challenge random players around the world, or create private rooms to play with your friends instantly!

### 🌍 **[Play Now in your Browser!](https://tictactoe-tashfin.vercel.app/)**

---

## ✨ Features

- **Global Matchmaking:** Click "Find Random Match" to instantly pair up with another player searching for a game anywhere in the world.
- **Private Rooms:** Click "Create Room" to generate a secure 5-character Room Code. Share it with a friend, they enter it via "Join Room", and you start playing immediately!
- **Real-Time Sync:** Powered by ultra-fast WebSockets. Your opponent's moves show up on your screen the exact millisecond they make them.
- **Modern UI:** Features a sleek, modern glassmorphism design with vibrant colors, rounded edges, and smooth micro-interactions across both the Web and Java platforms.
- **Cross-Platform Play:** Someone on the Web App can seamlessly play against someone using the Java Desktop App. It's fully integrated!

---

## 🛠️ Technology Stack

1. **Matchmaking Server (Backend):**
   - Node.js
   - `ws` (WebSockets)
   - Hosted on Render.com
2. **Web Client (Frontend):**
   - Vanilla HTML, CSS, JavaScript
   - Native WebSockets API
   - Hosted on Vercel
3. **Desktop Client:**
   - Java (JDK 20)
   - Java Swing (Custom UI Components for Modern Look)
   - `java.net.http.WebSocket` API

---

## 🚀 How to Run Locally

If you want to run or modify this project on your own computer:

### 1. Start the Server
```bash
cd server
npm install
node server.js
```
*(The server will start running on port 7517).*

### 2. Run the Web App
Simply open the `web/index.html` file in any modern web browser.
*(Make sure to change the `server-ip` hidden input in `index.html` to `localhost:7517` if you want to test your local server instead of the live hosted one).*

### 3. Run the Java App
Ensure you have the JDK installed. Compile the project and run it:
```bash
javac -d target\classes src\main\java\TicTacToe\TIC_TAC.java
java -cp target\classes TicTacToe.TIC_TAC
```
Alternatively, just double-click the included `Play_TicTacToe.bat` file to silently launch the game without a terminal window! *(Requires the game to be compiled in `target\classes` first).*

---

## 📸 Screenshots

<img width="1919" height="940" alt="image" src="https://github.com/user-attachments/assets/a76fca11-704c-42c9-8614-e928fdb2d513" />
<img width="1919" height="936" alt="image" src="https://github.com/user-attachments/assets/f4aa843a-3a9d-466d-bb7b-eaa614139840" />
<img width="1919" height="934" alt="image" src="https://github.com/user-attachments/assets/c8ed1b09-6df5-41f1-a403-27e42700ccbb" />





---
Developed as an online multiplayer project!
