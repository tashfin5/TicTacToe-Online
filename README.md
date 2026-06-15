# TicTacToe-Online 🎮

A completely cross-platform, universal multiplayer Tic Tac Toe game. 

This project allows players using a **Modern Web Application** (in their browser) to seamlessly play against players using the **Java Desktop Application**, no matter where they are in the world! 🌍

## Features 🚀
*   **Universal Matchmaking**: Powered by a lightweight Node.js WebSocket server that dynamically pairs players who are looking for a match.
*   **Modern Web UI**: A stunning HTML/CSS/JS frontend featuring glassmorphism, dynamic animations, and neon styling.
*   **Upgraded Java UI**: A custom-built modern Java Swing UI that ditches the boring standard layout for perfectly rounded buttons, sleek dark-mode panels, and matching neon colors without requiring external `.jar` dependencies (utilizing native Java 11 WebSockets).
*   **Live Game State Sync**: Instant updates for moves, disconnects, and rematches.

---

## 🛠️ Project Structure
*   `/server` - The Node.js WebSocket server that handles all matchmaking and game state relay.
*   `/web` - The HTML, CSS, and JS files for the Web Client.
*   `/src` - The Java source code for the Desktop Client.

---

## 💻 How to Run Locally

To play locally on your own machine, you need to run the Matchmaking Server, and then launch the clients of your choice.

### 1. Start the Matchmaking Server
Make sure you have [Node.js](https://nodejs.org) installed.
```bash
cd server
npm install
node server.js
```

### 2. Start a Web Player
Simply open `web/index.html` in any modern web browser (Chrome, Edge, Firefox, etc.).
Enter a username, leave the server IP as `localhost:7517`, and click **Find Match**.

### 3. Start a Java Player
Make sure you have the Java JDK installed (Java 11 or higher).
Compile and run the code from the project root:
```bash
javac -d target\classes src\main\java\TicTacToe\TIC_TAC.java
java -cp target\classes TicTacToe.TIC_TAC
```
Enter a username, click **Find Match**, and the server will instantly pair you with the web player!

---

## 🌍 How to Host Online

To let your friends play with you globally, you need to host the Node.js server so they can connect to it.

### Hosting the Server (Free with Render)
1. Push this repository to your GitHub account.
2. Go to [Render.com](https://render.com) and sign in.
3. Click **New +** -> **Web Service**.
4. Connect this GitHub repository.
5. Set the **Root Directory** to `server`.
6. Set the **Build Command** to `npm install` and **Start Command** to `node server.js`.
7. Deploy! Render will give you a public URL (e.g., `tictactoe-server.onrender.com`).

### Connecting Players
Once hosted, you can hardcode this new public URL into your apps:
*   **Web App:** Edit `web/index.html` and change the hidden `<input id="server-ip">` value to your Render URL.
*   **Java App:** Edit `src/main/java/TicTacToe/TIC_TAC.java`, change `SERVER_IP` to your Render URL, and recompile.

Send the `web/index.html` file to your friends (or host it as a Static Site on Render/GitHub Pages), and enjoy!
