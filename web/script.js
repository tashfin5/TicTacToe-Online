const loginScreen = document.getElementById('login-screen');
const waitingScreen = document.getElementById('waiting-screen');
const gameScreen = document.getElementById('game-screen');

const usernameInput = document.getElementById('username-input');
const serverIpInput = document.getElementById('server-ip');
const connectBtn = document.getElementById('connect-btn');
const createRoomBtn = document.getElementById('create-room-btn');
const joinRoomBtn = document.getElementById('join-room-btn');
const roomCodeInput = document.getElementById('room-code-input');
const waitingTitle = document.getElementById('waiting-title');
const waitingSubtitle = document.getElementById('waiting-subtitle');
const playAgainBtn = document.getElementById('play-again-btn');
const goHomeBtn = document.getElementById('go-home-btn');
const turnIndicator = document.getElementById('turn-indicator');
const toast = document.getElementById('toast');

const p1Name = document.getElementById('p1-name');
const p1ScoreElem = document.getElementById('p1-score');
const p2Name = document.getElementById('p2-name');
const p2ScoreElem = document.getElementById('p2-score');

const cells = document.querySelectorAll('.cell');

let ws = null;
let mySymbol = '';
let isMyTurn = false;
let myScore = 0;
let oppScore = 0;
let grid = Array(9).fill(null);
let gameActive = false;

connectBtn.addEventListener('click', () => connectToServer('login'));
createRoomBtn.addEventListener('click', () => connectToServer('create_room'));
joinRoomBtn.addEventListener('click', () => connectToServer('join_room'));
playAgainBtn.addEventListener('click', () => {
    if (ws && ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify({ type: 'play_again' }));
        playAgainBtn.innerText = 'Waiting...';
        playAgainBtn.disabled = true;
    }
});
goHomeBtn.addEventListener('click', () => {
    if (ws && ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify({ type: 'leave_match' }));
        ws.close();
    }
    showScreen(loginScreen);
    connectBtn.disabled = false;
    createRoomBtn.disabled = false;
    joinRoomBtn.disabled = false;
    connectBtn.innerText = 'Find Match';
});

cells.forEach(cell => {
    cell.addEventListener('click', () => {
        const index = cell.getAttribute('data-index');
        handleMove(index);
    });
});

function connectToServer(actionType) {
    const username = usernameInput.value.trim();
    const serverUrl = serverIpInput.value.trim();
    const roomCode = roomCodeInput ? roomCodeInput.value.trim() : '';
    
    if (!username) {
        showToast('Please enter a username');
        return;
    }
    if (actionType === 'join_room' && !roomCode) {
        showToast('Please enter a room code');
        return;
    }

    try {
        ws = new WebSocket(`wss://${serverUrl}`);
    } catch (e) {
        showToast('Invalid Server IP');
        return;
    }

    function resetButtons() {
        connectBtn.disabled = false;
        createRoomBtn.disabled = false;
        joinRoomBtn.disabled = false;
        connectBtn.innerText = 'Find Match';
    }

    connectBtn.disabled = true;
    createRoomBtn.disabled = true;
    joinRoomBtn.disabled = true;
    if (actionType === 'login') connectBtn.innerText = 'Connecting...';

    ws.onopen = () => {
        if (actionType === 'login') {
            ws.send(JSON.stringify({ type: 'login', username: username }));
        } else if (actionType === 'create_room') {
            ws.send(JSON.stringify({ type: 'create_room', username: username }));
        } else if (actionType === 'join_room') {
            ws.send(JSON.stringify({ type: 'join_room', username: username, roomCode: roomCode }));
        }
    };

    ws.onmessage = (event) => {
        const data = JSON.parse(event.data);

        if (data.type === 'waiting') {
            waitingTitle.innerText = "Searching for opponent...";
            waitingSubtitle.innerText = "Please wait while we find a match.";
            showScreen(waitingScreen);
        } else if (data.type === 'room_created') {
            waitingTitle.innerText = `Room Code: ${data.roomCode}`;
            waitingSubtitle.innerText = "Share this code with your friend!";
            showScreen(waitingScreen);
        } else if (data.type === 'error') {
            showToast(data.message);
            resetButtons();
            ws.close();
        } else if (data.type === 'match_found') {
            mySymbol = data.symbol;
            isMyTurn = data.turn;
            
            p1Name.innerText = username;
            document.getElementById('p1-symbol').innerText = `(${mySymbol})`;
            
            p2Name.innerText = data.opponent;
            const oppSymbol = mySymbol === 'O' ? 'X' : 'O';
            document.getElementById('p2-symbol').innerText = `(${oppSymbol})`;

            resetBoard();
            showScreen(gameScreen);
            showToast('Match Found!');
        } else if (data.type === 'move') {
            applyMove(data.position, false);
        } else if (data.type === 'opponent_wants_rematch') {
            showToast('Opponent wants to play again!');
            if (!gameActive) {
                turnIndicator.innerText = "Opponent wants Rematch!";
            }
        } else if (data.type === 'reset') {
            showToast(`Match restarted!`);
            resetBoard();
            playAgainBtn.innerText = 'Play Again';
            playAgainBtn.disabled = false;
        } else if (data.type === 'opponent_left' || data.type === 'opponent_disconnected') {
            showToast('Opponent left the match!');
            gameActive = false;
            turnIndicator.innerText = 'Opponent Left';
            turnIndicator.style.color = '#ef4444'; // Red
        setTimeout(() => {
                showScreen(loginScreen);
                resetButtons();
            }, 3000);
        }
    };

    ws.onclose = () => {
        showToast('Disconnected from server');
        showScreen(loginScreen);
        resetButtons();
    };

    ws.onerror = () => {
        showToast('Connection error. Is the server running?');
        resetButtons();
    };
}

function handleMove(index) {
    if (!gameActive || !isMyTurn || grid[index] !== null) return;
    
    applyMove(index, true);
    ws.send(JSON.stringify({ type: 'move', position: parseInt(index) }));
}

function applyMove(index, isMe) {
    const symbol = isMe ? mySymbol : (mySymbol === 'O' ? 'X' : 'O');
    grid[index] = symbol;
    
    const cell = document.querySelector(`.cell[data-index="${index}"]`);
    cell.innerText = symbol;
    cell.classList.add(symbol.toLowerCase());
    
    isMyTurn = !isMe;
    updateTurnIndicator();
    checkWinCondition();
}

function updateTurnIndicator() {
    if (!gameActive) return;
    if (isMyTurn) {
        turnIndicator.innerText = 'Your Turn!';
        turnIndicator.style.color = '#8b5cf6';
    } else {
        turnIndicator.innerText = "Opponent's Turn";
        turnIndicator.style.color = '#94a3b8';
    }
}

function checkWinCondition() {
    const winPatterns = [
        [0, 1, 2], [3, 4, 5], [6, 7, 8], // Rows
        [0, 3, 6], [1, 4, 7], [2, 5, 8], // Cols
        [0, 4, 8], [2, 4, 6]             // Diagonals
    ];

    let won = false;
    let winningSymbol = null;

    for (const pattern of winPatterns) {
        const [a, b, c] = pattern;
        if (grid[a] && grid[a] === grid[b] && grid[a] === grid[c]) {
            won = true;
            winningSymbol = grid[a];
            break;
        }
    }

    if (won) {
        gameActive = false;
        if (winningSymbol === mySymbol) {
            turnIndicator.innerText = 'You Won! 🎉';
            turnIndicator.style.color = '#10b981'; // Green
            myScore++;
            p1ScoreElem.innerText = myScore;
        } else {
            turnIndicator.innerText = 'You Lost 😢';
            turnIndicator.style.color = '#ef4444'; // Red
            oppScore++;
            p2ScoreElem.innerText = oppScore;
        }
        playAgainBtn.classList.remove('hidden');
        goHomeBtn.classList.remove('hidden');
    } else if (!grid.includes(null)) {
        gameActive = false;
        turnIndicator.innerText = "It's a Tie! 🤝";
        turnIndicator.style.color = '#f59e0b'; // Yellow
        playAgainBtn.classList.remove('hidden');
        goHomeBtn.classList.remove('hidden');
    }
}

function resetBoard() {
    grid = Array(9).fill(null);
    gameActive = true;
    playAgainBtn.classList.add('hidden');
    goHomeBtn.classList.add('hidden');
    playAgainBtn.innerText = 'Play Again';
    playAgainBtn.disabled = false;
    
    cells.forEach(cell => {
        cell.innerText = '';
        cell.className = 'cell'; // reset classes
    });

    updateTurnIndicator();
}

function showScreen(screenElem) {
    document.querySelectorAll('.screen').forEach(s => s.classList.remove('active'));
    screenElem.classList.add('active');
}

function showToast(message) {
    toast.innerText = message;
    toast.classList.add('show');
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}
