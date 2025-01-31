public class Algorithm {
    public Player[] players;
    public Rules rules;
    public Board board;
    public Game game;
    public int nodes = 0;
    public int leafeval;
    public int leaf;

    public Algorithm(Player[] players, Rules rules, Board board, Game game) {
        this.players = players;
        this.rules = rules;
        this.board = board;
        this.game = game;
    }

    public class State {
        public OnePiece piece;
        public int oldIndex;
        public char oldCell;
        public String oldBlock;
        public int newIndex;
        public char newCell;
        public String newBlock;
        public int[] g;
        public boolean proceed = false;
    
        public State(int player, int i, int die) {
            this.piece = game.players[player].pieces[i];
            if (!rules.isMoveValid(piece, die, board)) {
                this.proceed = true;
            }
            if (!this.proceed) {
                this.oldIndex = piece.i;
                this.oldCell = board.cells[oldIndex];
                this.oldBlock = board.blocks.get(oldCell);
                this.newIndex = board.newIndex(piece, die);
                this.newCell = board.cells[newIndex];
                this.newBlock = board.blocks.get(newCell);
                this.g = game.botPlayMove(piece, die);
            }
        }

        public void reState(int player) {
            int newIndex = this.newIndex;
            int[] g = this.g;

            this.piece.i = this.oldIndex;
            game.currentPlayer = player;
            char newcell2 = board.cells[newIndex];
            board.cells[this.oldIndex] = this.oldCell;
            board.cells[newIndex] = this.newCell;

            if (this.newBlock != null) {
                board.blocks.put(this.newCell, this.newBlock);
            } else {
                board.blocks.remove(newcell2);
            }
            if (this.oldBlock != null) {
                board.blocks.put(this.oldCell, this.oldBlock);
            }
            if (g[0] != -1) {
                OnePiece piece2 = board.pieces[board.cells[g[0]] - 'a'];
                board.cells[g[0]] = ' ';
                piece2.i = newIndex;
                if (g.length > 1) {
                    OnePiece piece3 = board.pieces[board.cells[g[1]] - 'a'];
                    board.cells[g[1]] = ' ';
                    piece3.i = newIndex;
                } 
                if (g.length > 2) {
                    OnePiece piece4 = board.pieces[board.cells[g[2]] - 'a'];
                    board.cells[g[2]] = ' ';
                    piece4.i = newIndex;
                } 
                if (g.length > 3) {
                    OnePiece piece5 = board.pieces[board.cells[g[3]] - 'a'];
                    board.cells[g[3]] = ' ';
                    piece5.i = newIndex;
                }
            }
        }
    }

    public int[] expectiminimax(int depth, boolean chance, int die) {
        int[] val = { 0, 0, 0, 0, 0, 0, 0 };
        if (depth == 0 || this.game.isGameOver()) {
            for (int l = 0; l < this.players.length; l++) {
                for (int i = 0; i < 4; i++) {
                    OnePiece piece = this.players[l].pieces[i];
                    if (piece.i != piece.base) {
                        val[l] += 99;
                    } else {
                        continue;
                    }
                    if (piece.i >= piece.homeStart) {
                        val[l] += 73;
                        continue;
                    }
                    if (piece.i % 13 == 0 || this.board.blocks.containsKey(this.board.cells[piece.i])) {
                        val[l] += 37;
                    }
                    for (int j = 1; j < 7; j++) {
                        int back = piece.i - j;
                        if (back < 0) {
                            back += 52;
                        }
                        char cellUI = this.board.cells[back];
                        if (cellUI == ' ') {
                            val[l] += 3;
                            continue;
                        }
                        String block = this.board.blocks.get(cellUI);
                        if (block == null) {
                            OnePiece piece2 = this.board.pieces[cellUI - 'a'];
                            if (piece2.end == piece.end) {
                                val[l] += 7;
                            } else {
                                val[l] -= 30; 
                            }
                        } else for (int k = 0; k < block.length(); k++) {
                            OnePiece piece2 = this.board.pieces[block.charAt(k) - 'a'];
                            if (piece2.end == piece.end) {
                                val[l] += 13;
                            } else {
                                val[l] -= 70; 
                            }
                        }
                    }
                    for (int j = 1; j < 7; j++) {
                        int front = piece.i + j;
                        if (front > 51) {
                            front -= 52;
                        }
                        if (this.board.cells[front] != ' ') {
                            val[l] += 17;
                        }
                    }
                }
            }
            for (int i = 0; i < this.players.length; i++) {
                for (int j = 0; j < 4; j++) {
                    if (this.players[i].pieces[j].i == this.players[i].pieces[j].base) {
                        for (int k = 0; k < this.players.length; k++) {
                            if (k != i) {
                                val[k] += 99;
                            }
                        }
                    }
                }
            }
            leaf = this.game.currentPlayer;
            leafeval = val[leaf];
            return val;
        }
        if (chance) {
            for (int i = 1; i < 19; i++) {
                if (i == 6 || i == 12) {
                    continue;
                }
                int[] emm = expectiminimax(depth - 1, false, i);
                if (i < 6) {
                    for (int j = 0; j < 4; j++) {
                        val[j] += emm[j] / 6;
                    }
                } else if (i < 12) {
                    for (int j = 0; j < 4; j++) {
                        val[j] += emm[j] / (6 * 6);
                    }
                } else {
                    for (int j = 0; j < 4; j++) {
                        val[j] += emm[j] / (6 * 6 * 6);
                    }
                }
            }
            return val;
        }
        int player = this.game.currentPlayer;
        int[][][][] vals = new int[4][4][4][7];
        if (die < 6) {
            for (int i = 0; i < 4; i++) {
                State state = new State(player, i, die);
                if (state.proceed) {
                    continue;
                }
                nodes++;
                if (state.g[0] == -1) {
                   this.game.currentPlayer = (player + 1) % this.players.length;
                }
                int[] emm = expectiminimax(depth - 1, true, die);
                for (int j = 0; j < 4; j++) {
                    vals[0][0][i][j] = emm[j];
                }
                vals[0][0][i][4] = (int) state.piece.ui;
                state.reState(player);
            }
        } else if (die < 12) {
            for (int y = 0; y < 4; y++) {
                State state2 = new State(player, y, 6);
                if (state2.proceed) {
                    continue;
                }
                for (int i = 0; i < 4; i++) {
                    State state = new State(player, i, die - 6);
                    if (state.proceed) {
                        continue;
                    }
                    nodes++;
                    if (state.g[0] == -1) {
                       this.game.currentPlayer = (player + 1) % this.players.length;
                    }
                    int[] emm = expectiminimax(depth - 1, true, die - 6);
                    for (int j = 0; j < 4; j++) {
                        vals[0][y][i][j] = emm[j];
                    }
                    vals[0][y][i][4] = (int) state2.piece.ui;
                    vals[0][y][i][5] = (int) state.piece.ui;
                    state.reState(player);
                }
                state2.reState(player);
            }
        } else {
            for (int x = 0; x < 4; x++) {
                State state3 = new State(player, x, 6);
                if (state3.proceed) {
                    continue;
                }
                for (int y = 0; y < 4; y++) {
                    State state2 = new State(player, y, 6);
                    if (state2.proceed) {
                        continue;
                    }
                    for (int i = 0; i < 4; i++) {
                        State state = new State(player, i, die - 12);
                        if (state.proceed) {
                            continue;
                        }
                        nodes++;
                        if (state.g[0] == -1) {
                           this.game.currentPlayer = (player + 1) % this.players.length;
                        }
                        int[] emm = expectiminimax(depth - 1, true, die - 12);
                        for (int j = 0; j < 4; j++) {
                            vals[x][y][i][j] = emm[j];
                        }
                        vals[x][y][i][4] = (int) state3.piece.ui;
                        vals[x][y][i][5] = (int) state2.piece.ui;
                        vals[x][y][i][6] = (int) state.piece.ui;
                        state.reState(player);
                    }
                    state2.reState(player);
                }
                state3.reState(player);
            }
        }
        int kk = 4;
        int jj = 4;
        if (die > 12) {
            kk = 1;
        }
        if (die > 6) {
            jj = 1;
        }
        for (int k = 0; k < 4; k += kk) {
            for (int j = 0; j < 4; j += jj) {
                for (int i = 0; i < 4; i++) {
                    if (vals[k][j][i][player] >= val[player]) {
                        val = vals[k][j][i];
                    }
                }
            }
        }
        return val;
    }
}