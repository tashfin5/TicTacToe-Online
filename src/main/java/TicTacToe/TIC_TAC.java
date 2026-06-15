package TicTacToe;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

public class TIC_TAC extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // IMPORTANT: Change this to your hosted server URL once you host it online!
    // Example: "tictactoe-server.onrender.com"
    private static final String SERVER_IP = "tictactoe-online-vx1d.onrender.com";
    
    private JTextField usernameField;
    private JTextField roomCodeField;
    private JButton connectBtn;
    private JButton createRoomBtn;
    private JButton joinRoomBtn;
    
    private JLabel waitTitle;
    private JLabel waitSub;
    private JLabel queueTimerLabel;
    private RoundedButton cancelQueueBtn;
    private javax.swing.Timer queueTimer;
    private int queueSeconds = 0;
    
    private RoundedButton[] buttons = new RoundedButton[9];
    private JLabel statusLabel;
    private JLabel myScoreLabel;
    private JLabel oppScoreLabel;
    private JLabel p1NameLabel;
    private JLabel p2NameLabel;
    private OutlinedRoundedButton playAgainBtn;
    private RoundedButton goHomeBtn;
    
    private WebSocket webSocket;
    private String mySymbol;
    private boolean myTurn = false;
    private boolean gameActive = false;
    private String[] grid = new String[9];
    private int myScore = 0;
    private int oppScore = 0;
    private String opponentName = "Opponent";

    public TIC_TAC() {
        super("Tic Tac Toe Global");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 680);
        getContentPane().setBackground(new Color(15, 23, 42));
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setOpaque(false);
        
        mainPanel.add(createLoginPanel(), "login");
        mainPanel.add(createWaitingPanel(), "waiting");
        mainPanel.add(createGamePanel(), "game");
        
        add(mainPanel);
        setLocationRelativeTo(null);
    }
    
    private JPanel createLoginPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(15, 23, 42));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        
        RoundedPanel box = new RoundedPanel(24);
        box.setBackground(new Color(30, 41, 59));
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel title = new JLabel("Tic Tac Toe", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(new Color(139, 92, 246));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel sub = new JLabel("Play globally against anyone", SwingConstants.CENTER);
        sub.setForeground(Color.GRAY);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        usernameField = new JTextField();
        usernameField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 25)), "Username", 0, 0, null, Color.GRAY));
        usernameField.setBackground(new Color(15, 23, 42));
        usernameField.setForeground(Color.WHITE);
        usernameField.setCaretColor(Color.WHITE);
        usernameField.setPreferredSize(new Dimension(250, 50));
        usernameField.setMaximumSize(new Dimension(300, 50));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        connectBtn = new RoundedButton("Find Random Match", 15);
        connectBtn.setBackground(new Color(139, 92, 246));
        connectBtn.setForeground(Color.WHITE);
        connectBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        connectBtn.setPreferredSize(new Dimension(250, 45));
        connectBtn.setMaximumSize(new Dimension(300, 45));
        connectBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        connectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        connectBtn.addActionListener(e -> connectToServer("login"));

        JLabel orLabel = new JLabel("— OR —", SwingConstants.CENTER);
        orLabel.setForeground(new Color(255, 255, 255, 128));
        orLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        createRoomBtn = new RoundedButton("Create Room", 15);
        createRoomBtn.setBackground(new Color(59, 130, 246));
        createRoomBtn.setForeground(Color.WHITE);
        createRoomBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        createRoomBtn.setPreferredSize(new Dimension(250, 45));
        createRoomBtn.setMaximumSize(new Dimension(300, 45));
        createRoomBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        createRoomBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createRoomBtn.addActionListener(e -> connectToServer("create_room"));
        
        JPanel joinPanel = new JPanel();
        joinPanel.setLayout(new BoxLayout(joinPanel, BoxLayout.X_AXIS));
        joinPanel.setOpaque(false);
        joinPanel.setPreferredSize(new Dimension(250, 45));
        joinPanel.setMaximumSize(new Dimension(300, 45));
        joinPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        roomCodeField = new JTextField();
        roomCodeField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 25)), "Room Code", 0, 0, null, Color.GRAY));
        roomCodeField.setBackground(new Color(15, 23, 42));
        roomCodeField.setForeground(Color.WHITE);
        roomCodeField.setCaretColor(Color.WHITE);
        
        joinRoomBtn = new RoundedButton("Join", 15);
        joinRoomBtn.setBackground(new Color(16, 185, 129));
        joinRoomBtn.setForeground(Color.WHITE);
        joinRoomBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        joinRoomBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        joinRoomBtn.addActionListener(e -> connectToServer("join_room"));
        
        joinPanel.add(roomCodeField);
        joinPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        joinPanel.add(joinRoomBtn);
        
        box.add(title);
        box.add(Box.createRigidArea(new Dimension(0, 5)));
        box.add(sub);
        box.add(Box.createRigidArea(new Dimension(0, 25)));
        box.add(usernameField);
        box.add(Box.createRigidArea(new Dimension(0, 20)));
        box.add(connectBtn);
        box.add(Box.createRigidArea(new Dimension(0, 15)));
        box.add(orLabel);
        box.add(Box.createRigidArea(new Dimension(0, 15)));
        box.add(createRoomBtn);
        box.add(Box.createRigidArea(new Dimension(0, 10)));
        box.add(joinPanel);
        
        p.add(box, gbc);
        return p;
    }
    
    private JPanel createWaitingPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(15, 23, 42));
        
        RoundedPanel box = new RoundedPanel(24);
        box.setBackground(new Color(30, 41, 59));
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        waitTitle = new JLabel("Searching for opponent...", SwingConstants.CENTER);
        waitTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        waitTitle.setForeground(Color.WHITE);
        waitTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        waitSub = new JLabel("Please wait while we find a match.", SwingConstants.CENTER);
        waitSub.setForeground(Color.GRAY);
        waitSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        queueTimerLabel = new JLabel("00:00", SwingConstants.CENTER);
        queueTimerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        queueTimerLabel.setForeground(new Color(59, 130, 246));
        queueTimerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        cancelQueueBtn = new RoundedButton("Cancel", 15);
        cancelQueueBtn.setBackground(new Color(239, 68, 68)); // Red
        cancelQueueBtn.setForeground(Color.WHITE);
        cancelQueueBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        cancelQueueBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelQueueBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelQueueBtn.addActionListener(e -> {
            stopQueueTimer();
            if (webSocket != null) {
                webSocket.abort();
            }
            cardLayout.show(mainPanel, "login");
            resetButtons();
        });
        
        box.add(waitTitle);
        box.add(Box.createRigidArea(new Dimension(0, 10)));
        box.add(queueTimerLabel);
        box.add(Box.createRigidArea(new Dimension(0, 10)));
        box.add(waitSub);
        box.add(Box.createRigidArea(new Dimension(0, 20)));
        box.add(cancelQueueBtn);
        
        p.add(box);
        return p;
    }
    
    private JPanel createGamePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(15, 23, 42)); // App BG
        
        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        inner.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        // Scoreboard
        RoundedPanel scorePanel = new RoundedPanel(20);
        scorePanel.setBackground(new Color(30, 41, 59));
        scorePanel.setLayout(new GridLayout(2, 3));
        scorePanel.setBorder(new EmptyBorder(15, 10, 15, 10));
        
        p1NameLabel = new JLabel("You", SwingConstants.CENTER);
        p1NameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        JLabel vsLabel = new JLabel("VS", SwingConstants.CENTER);
        vsLabel.setForeground(Color.GRAY);
        vsLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        p2NameLabel = new JLabel("Opponent", SwingConstants.CENTER);
        p2NameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        myScoreLabel = new JLabel("0", SwingConstants.CENTER);
        myScoreLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        
        JLabel emptyLabel = new JLabel("", SwingConstants.CENTER); 
        
        oppScoreLabel = new JLabel("0", SwingConstants.CENTER);
        oppScoreLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        
        scorePanel.add(p1NameLabel);
        scorePanel.add(vsLabel);
        scorePanel.add(p2NameLabel);
        scorePanel.add(myScoreLabel);
        scorePanel.add(emptyLabel);
        scorePanel.add(oppScoreLabel);
        
        // Status
        statusLabel = new JLabel("Status", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        statusLabel.setForeground(new Color(139, 92, 246));
        statusLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
        
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);
        topContainer.add(scorePanel, BorderLayout.NORTH);
        topContainer.add(statusLabel, BorderLayout.SOUTH);
        
        // Grid
        JPanel gridPanel = new JPanel(new GridLayout(3, 3, 10, 10)); 
        gridPanel.setOpaque(false);
        gridPanel.setBorder(new EmptyBorder(10, 0, 20, 0));
        
        for (int i = 0; i < 9; i++) {
            buttons[i] = new RoundedButton("", 20);
            buttons[i].setFont(new Font("SansSerif", Font.BOLD, 64));
            buttons[i].setBackground(new Color(30, 41, 59));
            buttons[i].setForeground(Color.WHITE);
            buttons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            int index = i;
            buttons[i].addActionListener(e -> handleMove(index));
            gridPanel.add(buttons[i]);
        }
        
        // Bottom
        playAgainBtn = new OutlinedRoundedButton("Play Again", 15, new Color(139, 92, 246));
        playAgainBtn.setBackground(new Color(15, 23, 42)); // Transparent essentially
        playAgainBtn.setForeground(new Color(139, 92, 246));
        playAgainBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        playAgainBtn.setPreferredSize(new Dimension(140, 40));
        playAgainBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        playAgainBtn.setVisible(false);
        playAgainBtn.addActionListener(e -> {
            if (webSocket != null) {
                webSocket.sendText("{\"type\":\"play_again\"}", true);
                playAgainBtn.setText("Waiting...");
                playAgainBtn.setEnabled(false);
            }
        });
        
        goHomeBtn = new RoundedButton("Go Home", 15);
        goHomeBtn.setBackground(new Color(239, 68, 68));
        goHomeBtn.setForeground(Color.WHITE);
        goHomeBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        goHomeBtn.setPreferredSize(new Dimension(140, 40));
        goHomeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        goHomeBtn.setVisible(false);
        goHomeBtn.addActionListener(e -> {
            stopQueueTimer();
            if (webSocket != null) {
                webSocket.sendText("{\"type\":\"leave_match\"}", true);
                webSocket.abort();
            }
            cardLayout.show(mainPanel, "login");
            resetButtons();
        });
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.add(playAgainBtn);
        bottomPanel.add(goHomeBtn);
        
        inner.add(topContainer, BorderLayout.NORTH);
        inner.add(gridPanel, BorderLayout.CENTER);
        inner.add(bottomPanel, BorderLayout.SOUTH);
        
        p.add(inner, BorderLayout.CENTER);
        return p;
    }
    
    private void resetButtons() {
        connectBtn.setEnabled(true);
        createRoomBtn.setEnabled(true);
        joinRoomBtn.setEnabled(true);
        connectBtn.setText("Find Random Match");
    }

    private void startQueueTimer() {
        queueSeconds = 0;
        queueTimerLabel.setText("00:00");
        if (queueTimer != null) {
            queueTimer.stop();
        }
        queueTimer = new Timer(1000, e -> {
            queueSeconds++;
            int m = queueSeconds / 60;
            int s = queueSeconds % 60;
            queueTimerLabel.setText(String.format("%02d:%02d", m, s));
        });
        queueTimer.start();
    }

    private void stopQueueTimer() {
        if (queueTimer != null) {
            queueTimer.stop();
        }
    }

    private void connectToServer(String actionType) {
        String user = usernameField.getText().trim();
        String roomCode = roomCodeField.getText().trim();
        String ip = SERVER_IP;
        
        if (user.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username!");
            return;
        }
        if (actionType.equals("join_room") && roomCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter room code!");
            return;
        }
        
        connectBtn.setEnabled(false);
        createRoomBtn.setEnabled(false);
        joinRoomBtn.setEnabled(false);
        if (actionType.equals("login")) connectBtn.setText("Connecting...");
        
        try {
            HttpClient client = HttpClient.newHttpClient();
            client.newWebSocketBuilder()
                  .buildAsync(URI.create("wss://" + ip), new WSListener(actionType, user, roomCode))
                  .thenAccept(ws -> {
                      this.webSocket = ws;
                  })
                  .exceptionally(ex -> {
                      SwingUtilities.invokeLater(() -> {
                          JOptionPane.showMessageDialog(this, "Connection failed!");
                          resetButtons();
                      });
                      return null;
                  });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid server URL!");
            resetButtons();
        }
    }
    
    private void handleMove(int index) {
        if (!gameActive || !myTurn || grid[index] != null) return;
        
        applyMove(index, mySymbol);
        webSocket.sendText("{\"type\":\"move\",\"position\":" + index + "}", true);
        myTurn = false;
        updateStatus();
        checkWin();
    }
    
    private void applyMove(int index, String symbol) {
        grid[index] = symbol;
        buttons[index].setText(symbol);
        if (symbol.equals("O")) {
            buttons[index].setForeground(new Color(59, 130, 246));
        } else {
            buttons[index].setForeground(new Color(236, 72, 153));
        }
    }
    
    private void updateStatus() {
        if (!gameActive) return;
        if (myTurn) {
            statusLabel.setText("Your Turn!");
            statusLabel.setForeground(new Color(139, 92, 246));
        } else {
            statusLabel.setText("Opponent's Turn");
            statusLabel.setForeground(Color.GRAY);
        }
    }
    
    private void resetBoard() {
        gameActive = true;
        playAgainBtn.setVisible(false);
        goHomeBtn.setVisible(true);
        goHomeBtn.setText("Leave Match");
        playAgainBtn.setText("Play Again");
        playAgainBtn.setEnabled(true);
        for (int i = 0; i < 9; i++) {
            grid[i] = null;
            buttons[i].setText("");
        }
        updateStatus();
    }
    
    private void checkWin() {
        int[][] winPatterns = {
            {0,1,2}, {3,4,5}, {6,7,8}, // Rows
            {0,3,6}, {1,4,7}, {2,5,8}, // Cols
            {0,4,8}, {2,4,6}           // Diagonals
        };
        
        boolean won = false;
        String winningSymbol = null;
        
        for (int[] p : winPatterns) {
            if (grid[p[0]] != null && grid[p[0]].equals(grid[p[1]]) && grid[p[0]].equals(grid[p[2]])) {
                won = true;
                winningSymbol = grid[p[0]];
                break;
            }
        }
        
        if (won) {
            gameActive = false;
            if (winningSymbol.equals(mySymbol)) {
                statusLabel.setText("You Won! 🎉");
                statusLabel.setForeground(new Color(16, 185, 129)); // Green
                myScore++;
                myScoreLabel.setText(String.valueOf(myScore));
            } else {
                statusLabel.setText("You Lost 😢");
                statusLabel.setForeground(new Color(239, 68, 68)); // Red
                oppScore++;
                oppScoreLabel.setText(String.valueOf(oppScore));
            }
            playAgainBtn.setVisible(true);
            goHomeBtn.setText("Go Home");
        } else {
            boolean tie = true;
            for (String s : grid) if (s == null) tie = false;
            if (tie) {
                gameActive = false;
                statusLabel.setText("It's a Tie! 🤝");
                statusLabel.setForeground(new Color(245, 158, 11)); // Yellow
                playAgainBtn.setVisible(true);
                goHomeBtn.setText("Go Home");
            }
        }
    }
    
    private String extractJsonValue(String json, String key) {
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) return null;
        int start = idx + search.length();
        if (json.charAt(start) == '"') {
            start++;
            int end = json.indexOf('"', start);
            return json.substring(start, end);
        } else {
            int end = json.indexOf(',', start);
            if (end == -1) end = json.indexOf('}', start);
            return json.substring(start, end).trim();
        }
    }
    
    private class WSListener implements WebSocket.Listener {
        StringBuilder messageBuffer = new StringBuilder();
        String actionType, username, roomCode;

        public WSListener(String actionType, String username, String roomCode) {
            this.actionType = actionType;
            this.username = username;
            this.roomCode = roomCode;
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            String payload = "";
            if (actionType.equals("login")) {
                payload = "{\"type\":\"login\",\"username\":\"" + username + "\"}";
            } else if (actionType.equals("create_room")) {
                payload = "{\"type\":\"create_room\",\"username\":\"" + username + "\"}";
            } else if (actionType.equals("join_room")) {
                payload = "{\"type\":\"join_room\",\"username\":\"" + username + "\",\"roomCode\":\"" + roomCode + "\"}";
            }
            webSocket.sendText(payload, true);
            WebSocket.Listener.super.onOpen(webSocket);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            messageBuffer.append(data);
            if (last) {
                String msg = messageBuffer.toString();
                messageBuffer.setLength(0); // Clear
                
                SwingUtilities.invokeLater(() -> {
                    String type = extractJsonValue(msg, "type");
                    if ("waiting".equals(type)) {
                        waitTitle.setText("Searching for opponent...");
                        waitSub.setText("Please wait while we find a match.");
                        startQueueTimer();
                        cardLayout.show(mainPanel, "waiting");
                    } else if ("room_created".equals(type)) {
                        String code = extractJsonValue(msg, "roomCode");
                        waitTitle.setText("Room Code: " + code);
                        waitSub.setText("Share this code with your friend!");
                        startQueueTimer();
                        cardLayout.show(mainPanel, "waiting");
                    } else if ("error".equals(type)) {
                        stopQueueTimer();
                        JOptionPane.showMessageDialog(TIC_TAC.this, extractJsonValue(msg, "message"));
                        resetButtons();
                        webSocket.abort();
                    } else if ("match_found".equals(type)) {
                        stopQueueTimer();
                        opponentName = extractJsonValue(msg, "opponent");
                        mySymbol = extractJsonValue(msg, "symbol");
                        myTurn = "true".equals(extractJsonValue(msg, "turn"));
                        
                        // Setup colors and text for score board
                        Color myCol = mySymbol.equals("O") ? new Color(59, 130, 246) : new Color(236, 72, 153);
                        p1NameLabel.setText(usernameField.getText().trim() + " (" + mySymbol + ")");
                        p1NameLabel.setForeground(myCol);
                        myScoreLabel.setForeground(myCol);
                        
                        String oppSym = mySymbol.equals("O") ? "X" : "O";
                        Color oppCol = oppSym.equals("O") ? new Color(59, 130, 246) : new Color(236, 72, 153);
                        p2NameLabel.setText(opponentName + " (" + oppSym + ")");
                        p2NameLabel.setForeground(oppCol);
                        oppScoreLabel.setForeground(oppCol);
                        
                        resetBoard();
                        cardLayout.show(mainPanel, "game");
                    } else if ("move".equals(type)) {
                        int pos = Integer.parseInt(extractJsonValue(msg, "position"));
                        String oppSym = mySymbol.equals("O") ? "X" : "O";
                        applyMove(pos, oppSym);
                        myTurn = true;
                        updateStatus();
                        checkWin();
                    } else if ("opponent_wants_rematch".equals(type)) {
                        if (!gameActive) {
                            statusLabel.setText("Opponent wants Rematch!");
                            statusLabel.setForeground(new Color(139, 92, 246));
                        }
                    } else if ("reset".equals(type)) {
                        resetBoard();
                    } else if ("opponent_left".equals(type) || "opponent_disconnected".equals(type)) {
                        gameActive = false;
                        statusLabel.setText("Opponent Left!");
                        statusLabel.setForeground(new Color(239, 68, 68));
                        JOptionPane.showMessageDialog(TIC_TAC.this, "Your opponent left the match.");
                        cardLayout.show(mainPanel, "login");
                        resetButtons();
                    }
                });
            }
            return WebSocket.Listener.super.onText(webSocket, data, last);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            SwingUtilities.invokeLater(() -> {
                stopQueueTimer();
                JOptionPane.showMessageDialog(TIC_TAC.this, "Disconnected from server.");
                cardLayout.show(mainPanel, "login");
                resetButtons();
            });
            return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
        }
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            new TIC_TAC().setVisible(true);
        });
    }
}

// Custom UI Components
class RoundedPanel extends JPanel {
    private int radius;
    public RoundedPanel(int radius) {
        super();
        this.radius = radius;
        setOpaque(false);
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        super.paintComponent(g);
        g2.dispose();
    }
}

class RoundedButton extends JButton {
    private int radius;
    public RoundedButton(String text, int radius) {
        super(text);
        this.radius = radius;
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        super.paintComponent(g);
        g2.dispose();
    }
}

class OutlinedRoundedButton extends JButton {
    private int radius;
    private Color borderColor;
    public OutlinedRoundedButton(String text, int radius, Color borderColor) {
        super(text);
        this.radius = radius;
        this.borderColor = borderColor;
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, radius, radius);
        
        super.paintComponent(g);
        g2.dispose();
    }
}
