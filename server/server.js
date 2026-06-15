const WebSocket = require('ws');

const wss = new WebSocket.Server({ port: 7517 }, () => {
    console.log('Tic Tac Toe Matchmaking Server started on port 7517');
});

let waitingPlayer = null; // For random match
const rooms = new Map();  // For private rooms, keys: roomCode, value: creator ws

function generateRoomCode() {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    let code = '';
    for (let i = 0; i < 5; i++) {
        code += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return code;
}

wss.on('connection', (ws) => {
    ws.on('message', (messageAsString) => {
        let message;
        try {
            message = JSON.parse(messageAsString);
        } catch (e) {
            console.error('Invalid JSON received');
            return;
        }

        if (message.type === 'login') { // Random Match
            ws.username = message.username || 'Anonymous';
            ws.mode = 'random';
            console.log(`Player ${ws.username} searching random match.`);

            if (waitingPlayer) {
                // We have a match!
                const player1 = waitingPlayer;
                const player2 = ws;
                waitingPlayer = null; // Clear queue
                startGame(player1, player2);
            } else {
                waitingPlayer = ws;
                ws.send(JSON.stringify({ type: 'waiting', message: 'Searching for random opponent...' }));
            }
        } else if (message.type === 'create_room') {
            ws.username = message.username || 'Anonymous';
            ws.mode = 'room';
            let roomCode;
            do {
                roomCode = generateRoomCode();
            } while (rooms.has(roomCode));

            ws.roomCode = roomCode;
            rooms.set(roomCode, ws);
            console.log(`Player ${ws.username} created room ${roomCode}.`);

            ws.send(JSON.stringify({ type: 'room_created', roomCode: roomCode }));
        } else if (message.type === 'join_room') {
            ws.username = message.username || 'Anonymous';
            ws.mode = 'room';
            const roomCode = (message.roomCode || '').toUpperCase().trim();
            
            if (rooms.has(roomCode)) {
                const player1 = rooms.get(roomCode);
                const player2 = ws;
                rooms.delete(roomCode); // Room is now full, remove from pending rooms
                console.log(`Player ${ws.username} joined room ${roomCode}.`);
                startGame(player1, player2);
            } else {
                ws.send(JSON.stringify({ type: 'error', message: 'Room not found or already full!' }));
            }
        } else if (message.type === 'move') {
            if (ws.opponent && ws.opponent.readyState === WebSocket.OPEN) {
                ws.opponent.send(JSON.stringify({ type: 'move', position: message.position }));
            }
        } else if (message.type === 'reset') {
            if (ws.opponent && ws.opponent.readyState === WebSocket.OPEN) {
                ws.opponent.send(JSON.stringify({ type: 'reset' }));
            }
        }
    });

    ws.on('close', () => {
        console.log(`Player ${ws.username || 'Unknown'} disconnected.`);
        
        if (waitingPlayer === ws) {
            waitingPlayer = null;
        }
        
        if (ws.mode === 'room' && ws.roomCode && rooms.has(ws.roomCode)) {
            if (rooms.get(ws.roomCode) === ws) {
                rooms.delete(ws.roomCode);
                console.log(`Room ${ws.roomCode} deleted.`);
            }
        }

        if (ws.opponent && ws.opponent.readyState === WebSocket.OPEN) {
            ws.opponent.send(JSON.stringify({ type: 'opponent_disconnected' }));
            ws.opponent.opponent = null; // Break the link
        }
    });
});

function startGame(player1, player2) {
    player1.symbol = 'O'; 
    player2.symbol = 'X'; 
    player1.opponent = player2;
    player2.opponent = player1;

    player1.send(JSON.stringify({
        type: 'match_found',
        opponent: player2.username,
        symbol: 'O',
        turn: true
    }));

    player2.send(JSON.stringify({
        type: 'match_found',
        opponent: player1.username,
        symbol: 'X',
        turn: false
    }));
    console.log(`Match started: ${player1.username} (O) vs ${player2.username} (X)`);
}
