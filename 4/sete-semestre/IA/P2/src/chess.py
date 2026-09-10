import board
import piece

import numpy as np


class Chess():
    
    """
    A class to represent the game of chess.
    
    ...

    Attributes:
    -----------
    board : Board
        represents the chess board of the game

    turn : bool
        True if white's turn

    white_ghost_piece : tup
        The coordinates of a white ghost piece representing a takeable pawn for en passant

    black_ghost_piece : tup
        The coordinates of a black ghost piece representing a takeable pawn for en passant

    Methods:
    --------
    promote(pos:stup) -> None
        Promotes a pawn that has reached the other side to another, or the same, piece

    move(start:tup, to:tup) -> None
        Moves the piece at `start` to `to` if possible. Otherwise, does nothing.
    """

    def __init__(self, initboard, myinit=True):
        
        if myinit:
            self.board = board.Board(initboard,False)
            self.boardSim = board.Board(initboard,False)
        else:
            self.board = board.Board([],True)
            self.boardSim = board.Board([],True)
            
        self.turn = True

        self.white_ghost_piece = None
        self.black_ghost_piece = None
        
        # AI current state
        self.currentStateW = [] #self.boardSim.currentStateW.copy()
        self.currentStateB = [] #self.boardSim.currentStateB.copy();


    def newBoardSim(self, initboard):
        self.boardSim = board.Board(initboard, False)

    def promotion(self, pos):
        
        pawn = None
        while pawn == None:
            promote = input("Promote pawn to [Q, R, N, B, P(or nothing)]: ")
            if promote not in ['Q', 'R', 'N', 'B', 'P', '']:
                print("Not a valid promotion piece")
            else:
                if promote == 'Q':
                    pawn = piece.Queen(True)
                elif promote == 'R':
                    pawn = piece.Rook(True)
                elif promote == 'N':
                    pawn = piece.Knight(True)
                elif promote == 'B':
                    pawn = piece.Bishop(True)
                elif promote == 'P' or promote == '': 
                    pawn = piece.Pawn(True)
        self.board.board[pos[0]][pos[1]] = pawn


    def moveSim(self, start, to, verbose=True):
        
        """
        Moves a piece at `start` to `to`. Does nothing if there is no piece at the starting point.
        Does nothing if the piece at `start` belongs to the wrong color for the current turn.
        Does nothing if moving the piece from `start` to `to` is not a valid move.

        start : tup
            Position of a piece to be moved

        to : tup
            Position of where the piece is to be moved
        
        precondition: `start` and `to` are valid positions on the board
        """

        if self.boardSim.board[start[0]][start[1]] == None:
            if verbose:
                print("There is no piece to move at the start place")
            return


        if self.boardSim.board[to[0]][to[1]] != None:
            if verbose:
                print("other piece there")

        target_piece = self.boardSim.board[start[0]][start[1]]
        
        # to ensure alternate moves - ica
        #if self.turn != target_piece.color:
        #    print("That's not your piece to move")
        #    return

        end_piece = self.boardSim.board[to[0]][to[1]]
        is_end_piece = end_piece != None

        # Checks if a player's own piece is at the `to` coordinate
        if is_end_piece and self.boardSim.board[start[0]][start[1]].color == end_piece.color:
            if verbose:
                print("There's a piece in the path.")
            return

        if target_piece.is_valid_move(self.boardSim, start, to):
            
            # Special check for if the move is castling
            # Board reconfiguration is handled in Piece
            if target_piece.name == 'K' and abs(start[1] - to[1]) == 2:
                if verbose:
                    print("castled")
                if self.turn and self.black_ghost_piece:
                    self.boardSim.board[self.black_ghost_piece[0]][self.black_ghost_piece[1]] = None
                elif not self.turn and self.white_ghost_piece:
                    self.boardSim.board[self.white_ghost_piece[0]][self.white_ghost_piece[1]] = None
                self.turn = not self.turn
                return

            if self.boardSim.board[to[0]][to[1]]:
                if verbose:
                    print(str(self.boardSim.board[to[0]][to[1]]) + " taken.")
                # Special logic for ghost piece, deletes the actual pawn that is not in the `to`
                # coordinate from en passant
                if self.boardSim.board[to[0]][to[1]].name == "GP":
                    
                    if self.turn:
                        self.boardSim.board[
                            self.black_ghost_piece[0] + 1
                        ][
                            self.black_ghost_piece[1]
                        ] = None
                        self.black_ghost_piece = None
                    else:
                        self.boardSim.board[self.white_ghost_piece[0] - 1][self.black_ghost_piece[1]] = None
                        self.white_ghost_piece = None

            self.boardSim.board[to[0]][to[1]] = target_piece
            self.boardSim.board[start[0]][start[1]] = None
            if verbose:
                print(str(target_piece) + " moved.")
            if self.turn and self.black_ghost_piece:
                self.boardSim.board[self.black_ghost_piece[0]][self.black_ghost_piece[1]] = None
            elif not self.turn and self.white_ghost_piece:
                self.boardSim.board[self.white_ghost_piece[0]][self.white_ghost_piece[1]] = None

            # alternate player
            self.turn = not self.turn
            
            
            # AI state change - identify change to make in state
            for m in range(len(self.boardSim.currentStateW)):

                # print("piece to move",self.board.currentStateW[m])
                aa = self.boardSim.currentStateW[m]               
                # only the one to move and only for whites so far
                if self.boardSim.listNames[int(aa[2]-1)] == str(target_piece) and target_piece.color:
                    if verbose:
                        print("->piece initial state ",self.boardSim.currentStateW[m])
                    self.boardSim.currentStateW[m][0] = to[0]
                    self.boardSim.currentStateW[m][1] = to[1]
                    if verbose:
                        print("->piece to state ",self.boardSim.currentStateW[m])
                                                       
                   
               #   print("Next States: ",self.board.getListNextStatesW(self.board.currentStateW[m]))

            # AI state change - identify change to make in state
            for m in range(len(self.boardSim.currentStateB)):

                # print("piece to move",self.board.currentStateW[m])
                aa = self.boardSim.currentStateB[m]
                # only the one to move 
                if self.boardSim.listNames[int(aa[2] - 1)] == str(target_piece.name) and not target_piece.color:
                    print("->piece initial state ", self.boardSim.currentStateB[m])
                    self.boardSim.currentStateB[m][0] = to[0]
                    self.boardSim.currentStateB[m][1] = to[1]
                    print("->piece to state ", self.boardSim.currentStateB[m])

    #                   print("Next States: ",self.board.getListNextStatesW(self.board.currentStateW[m]))


    def move(self, start, to):

        """
        Moves a piece at `start` to `to`. Does nothing if there is no piece at the starting point.
        Does nothing if the piece at `start` belongs to the wrong color for the current turn.
        Does nothing if moving the piece from `start` to `to` is not a valid move.

        start : tup
            Position of a piece to be moved

        to : tup
            Position of where the piece is to be moved
        
        precondition: `start` and `to` are valid positions on the board
        """

        if self.board.board[start[0]][start[1]] == None:
            print("There is no piece to move at the start place")
            return

        target_piece = self.board.board[start[0]][start[1]]
        
        # to ensure alternate moves
        #if self.turn != target_piece.color:
        #    print("That's not your piece to move")
        #    return

        end_piece = self.board.board[to[0]][to[1]]
        is_end_piece = end_piece != None

        # Checks if a player's own piece is at the `to` coordinate
        if is_end_piece and self.board.board[start[0]][start[1]].color == end_piece.color:
            print("There's a piece in the path.")
            return

        if target_piece.is_valid_move(self.board, start, to):
            
            # Special check for if the move is castling
            # Board reconfiguration is handled in Piece
            if target_piece.name == 'K' and abs(start[1] - to[1]) == 2:
            
                print("castled")
            
                if self.turn and self.black_ghost_piece:
                    self.board.board[self.black_ghost_piece[0]][self.black_ghost_piece[1]] = None
                elif not self.turn and self.white_ghost_piece:
                    self.board.board[self.white_ghost_piece[0]][self.white_ghost_piece[1]] = None
                self.turn = not self.turn
                return

            if self.board.board[to[0]][to[1]] != None:
                
                print(str(self.board.board[to[0]][to[1]]) + " taken.")
                # Special logic for ghost piece, deletes the actual pawn that is not in the `to`
                # coordinate from en passant
                if self.board.board[to[0]][to[1]].name == "GP":
                    if self.turn:
                        self.board.board[
                            self.black_ghost_piece[0] + 1
                        ][
                            self.black_ghost_piece[1]
                        ] = None
                        self.black_ghost_piece = None
                    else:
                        self.board.board[self.white_ghost_piece[0] - 1][self.black_ghost_piece[1]] = None
                        self.white_ghost_piece = None


            name_end_piece = None
            end_piece_color = None
            if is_end_piece:
                name_end_piece = end_piece.name
                end_piece_color = end_piece.color

            self.board.board[to[0]][to[1]] = target_piece
            self.board.board[start[0]][start[1]] = None
            print(str(target_piece) + " moved.")

            if self.turn and self.black_ghost_piece:
                self.board.board[self.black_ghost_piece[0]][self.black_ghost_piece[1]] = None
            elif not self.turn and self.white_ghost_piece:
                self.board.board[self.white_ghost_piece[0]][self.white_ghost_piece[1]] = None

            # alternate player
            self.turn = not self.turn
            
            stateEndPiece = None
            # AI state change - identify change to make in state
            for m in range(len(self.board.currentStateW)):
   
                # print("piece to move",self.board.currentStateW[m])
                aa = self.board.currentStateW[m]
                # only the one to move and only for whites so far
                if self.board.listNames[int(aa[2]-1)] == str(target_piece) and target_piece.color:

                   print("->piece initial state ",self.board.currentStateW[m])
                   self.board.currentStateW[m][0] = to[0]
                   self.board.currentStateW[m][1] = to[1]
                   print("->piece to state ",self.board.currentStateW[m])

                if is_end_piece and name_end_piece == self.board.listNames[int(aa[2] - 1)] and end_piece_color:
                    stateEndPiece = [self.board.currentStateW[m][0],self.board.currentStateW[m][1],self.board.currentStateW[m][2]]


            if stateEndPiece != None:
               self.board.currentStateW.remove(stateEndPiece)
#                   print("Next States: ",self.board.getListNextStatesW(self.board.currentStateW[m]))
            stateEndPiece = None
            # AI state change - identify change to make in state
            for m in range(len(self.board.currentStateB)):

               # print("piece to move",self.board.currentStateW[m])
               aa = self.board.currentStateB[m]
               # only the one to move 
               if self.board.listNames[int(aa[2] - 1)] == target_piece.name and not target_piece.color:
                   print("->piece initial state ", self.board.currentStateB[m])
                   self.board.currentStateB[m][0] = to[0]
                   self.board.currentStateB[m][1] = to[1]
                   print("->piece to state ", self.board.currentStateB[m])

#                   print("Next States: ",self.board.getListNextStatesW(self.board.currentStateW[m]))

               if is_end_piece and name_end_piece == self.board.listNames[int(aa[2] - 1)] and not end_piece_color:
                   stateEndPiece = [self.board.currentStateB[m][0],self.board.currentStateB[m][1],self.board.currentStateB[m][2]]

            if stateEndPiece != None:
               self.board.currentStateB.remove(stateEndPiece)


def getListNextStatesW(self):

        """
        Gets the list of next possible states given the currentStateW
        
        """
       
                


def translate(s):
    """
    Translates traditional board coordinates of chess into list indices
    """
    try:
        row = int(s[0])
        col = s[1]
        if row < 1 or row > 8:
            print(s[0] + "is not in the range from 1 - 8")
            return None
        if col < 'a' or col > 'h':
            print(s[1] + "is not in the range from a - h")
            return None
        dict = {'a': 0, 'b': 1, 'c': 2, 'd': 3, 'e': 4, 'f': 5, 'g': 6, 'h': 7}
        return (8 - row, dict[col])
    except:
        print(s + "is not in the format '[number][letter]'")
        return None



if __name__ == "__main__":

    # intiialize board
    # current state initialization
    TA = np.zeros((8,8))
    #white pieces
    TA[[6,]]=1 # white pawn
    TA[7][0]=2
    TA[7][1]=3
    TA[7][2]=4
    TA[7][3]=6
    TA[7][4]=5
    TA[7][5]=4
    TA[7][6]=3
    TA[7][7]=2
    #black pieces
    TA[[1,]]=7 # black pawn
    TA[0][0]=8
    TA[0][1]=9
    TA[0][2]=10
    TA[0][3]=11
    TA[0][4]=12
    TA[0][5]=10
    TA[0][6]=9
    TA[0][7]=8
    
    # initialize board
    chess = Chess(TA)
#  chess = Chess([],False)
        
    
    # print board
    chess.board.print_board()

    while True:
        
        start = input("From: ")
        to = input("To: ")
        
        start = translate(start)
        to = translate(to)

        if start == None or to == None:
            continue

        chess.move(start, to)

        # check for promotion pawns
        i = 0
        while i < 8:
            if not chess.turn and chess.board.board[0][i] != None and \
                chess.board.board[0][i].name == 'P':
                chess.promotion((0, i))
                break
            elif chess.turn and chess.board.board[7][i] != None and \
                chess.board.board[7][i].name == 'P':
                chess.promotion((7, i))
                break
            i += 1

        chess.board.print_board()
