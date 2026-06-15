const WebSocket = require('ws');

const wss = new WebSocket.Server({ port: 7517 }, () => {
    console.log('Tic Tac Toe Matchmaking Server started on port 7517');
});

let waitingPlayer = null;
const games = new Map();

wss.on('connection', (ws) => {
    ws.on('message', (messageAsString) => {
        let message;
        try {
            message = JSON.parse(messageAsString);
        } catch (e) {
            console.error('Invalid JSON received');
            return;
        }

        if (message.type === 'login') {
            ws.username = message.username || 'Anonymous';
            console.log(`Player ${ws.username} joined.`);

            if (waitingPlayer) {
                // We have a match!
                const player1 = waitingPlayer;
                const player2 = ws;
                waitingPlayer = null; // Clear queue

                // Assign symbols
                player1.symbol = 'O'; // Player 1 plays first with O
                player2.symbol = 'X'; // Player 2 plays second with X

                // Store game reference in both sockets
                player1.opponent = player2;
                player2.opponent = player1;

                // Notify both players
                player1.send(JSON.stringify({
                    type: 'match_found',
                    opponent: player2.username,
                    symbol: 'O',
                    turn: true // Player 1's turn
                }));

                player2.send(JSON.stringify({
                    type: 'match_found',
                    opponent: player1.username,
                    symbol: 'X',
                    turn: false
                }));

                console.log(`Match started: ${player1.username} (O) vs ${player2.username} (X)`);
            } else {
                // Wait for another player
                waitingPlayer = ws;
                ws.send(JSON.stringify({
                    type: 'waiting'
                }));
            }
        } else if (message.type === 'move') {
            // Relay the move to the opponent
            if (ws.opponent && ws.opponent.readyState === WebSocket.OPEN) {
                ws.opponent.send(JSON.stringify({
                    type: 'move',
                    position: message.position
                }));
            }
        } else if (message.type === 'reset') {
            // Player requested a reset, tell opponent
            if (ws.opponent && ws.opponent.readyState === WebSocket.OPEN) {
                ws.opponent.send(JSON.stringify({
                    type: 'reset'
                }));
            }
        }
    });

    ws.on('close', () => {
        console.log(`Player ${ws.username || 'Unknown'} disconnected.`);
        
        // If they were waiting, remove them
        if (waitingPlayer === ws) {
            waitingPlayer = null;
        }

        // If they were in a game, notify the opponent
        if (ws.opponent && ws.opponent.readyState === WebSocket.OPEN) {
            ws.opponent.send(JSON.stringify({
                type: 'opponent_disconnected'
            }));
            ws.opponent.opponent = null; // Break the link
        }
    });
});
