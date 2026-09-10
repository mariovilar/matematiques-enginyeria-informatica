import piece
import numpy as np


class Board():
    """
    A class to represent a chess board.

    ...

    Attributes:
    -----------
    board : list[list[Piece]]
        represents a chess board
        
    turn : bool
        True if white's turn

    white_ghost_piece : tup
        The coordinates of a white ghost piece representing a takeable pawn for en passant

    black_ghost_piece : tup
        The coordinates of a black ghost piece representing a takeable pawn for en passant

    Methods:
    --------
    print_board() -> None
        Prints the current configuration of the board

    move(start:tup, to:tup) -> None
        Moves the piece at `start` to `to` if possible. Otherwise, does nothing.
        
    """

    def __init__(self, initState, xinit=True):

        # initstate
        # matrix 8x8

        """
        Initializes the board per standard chess rules
        """
        self.listNames = ['P', 'R', 'H', 'B', 'Q', 'K', 'P', 'R', 'H', 'B', 'Q', 'K']

        self.listSuccessorStates = []
        self.listNextStates = []

        self.board = []

        self.currentStateW = []
        self.currentStateB = []

        self.listVisitedStates = []

        # Board set-up
        for i in range(8):
            self.board.append([None] * 8)

        # assign pieces
        if xinit:

            # White
            self.board[7][0] = piece.Rook(True)
            self.board[7][1] = piece.Knight(True)
            self.board[7][2] = piece.Bishop(True)
            self.board[7][3] = piece.Queen(True)
            self.board[7][4] = piece.King(True)
            self.board[7][5] = piece.Bishop(True)
            self.board[7][6] = piece.Knight(True)
            self.board[7][7] = piece.Rook(True)

            for i in range(8):
                self.board[6][i] = piece.Pawn(True)

            # Black
            self.board[0][0] = piece.Rook(False)
            self.board[0][1] = piece.Knight(False)
            self.board[0][2] = piece.Bishop(False)
            self.board[0][3] = piece.Queen(False)
            self.board[0][4] = piece.King(False)
            self.board[0][5] = piece.Bishop(False)
            self.board[0][6] = piece.Knight(False)
            self.board[0][7] = piece.Rook(False)

            for i in range(8):
                self.board[1][i] = piece.Pawn(False)

        # assign pieces 
        else:

            self.currentState = initState

            # assign pieces
            for i in range(8):
                for j in range(8):

                    # White
                    if initState[i][j] == 1:
                        self.board[i][j] = piece.Pawn(True)
                        # assign AI State

                    elif initState[i][j] == 2:
                        self.board[i][j] = piece.Rook(True)
                    elif initState[i][j] == 3:
                        self.board[i][j] = piece.Knight(True)
                    elif initState[i][j] == 4:
                        self.board[i][j] = piece.Bishop(True)
                    elif initState[i][j] == 5:
                        self.board[i][j] = piece.Queen(True)
                    elif initState[i][j] == 6:
                        self.board[i][j] = piece.King(True)
                    # Blacks
                    elif initState[i][j] == 7:
                        self.board[i][j] = piece.Pawn(False)
                    elif initState[i][j] == 8:
                        self.board[i][j] = piece.Rook(False)
                    elif initState[i][j] == 9:
                        self.board[i][j] = piece.Knight(False)
                    elif initState[i][j] == 10:
                        self.board[i][j] = piece.Bishop(False)
                    elif initState[i][j] == 11:
                        self.board[i][j] = piece.Queen(False)
                    elif initState[i][j] == 12:
                        self.board[i][j] = piece.King(False)

                    # store current state (Whites)
                    if (initState[i][j] > 0 and initState[i][j] < 7):
                        self.currentStateW.append([i, j, int(initState[i][j])])

                    # target state (Blacks)
                    if initState[i][j] > 6 and initState[i][j] > 7:
                        self.currentStateB.append([i, j, int(initState[i][j])])

    def isSameState(self, a, b):

        isSameState1 = True
        # a and b are lists
        for k in range(len(a)):

            if a[k] not in b:
                isSameState1 = False

        isSameState2 = True
        # a and b are lists
        for k in range(len(b)):

            if b[k] not in a:
                isSameState2 = False

        isSameState = isSameState1 and isSameState2
        return isSameState


    def getListNextStatesW(self, mypieces):

        """
        Gets the list of next possible states given the currentStateW
        for each kind of piece
        
        """

        self.listNextStates = []

        # print("mypieces",mypieces)
        # print("len ",len(mypieces))
        for j in range(len(mypieces)):

            self.listSuccessorStates = []

            mypiece = mypieces[j]
            listOtherPieces = mypieces.copy()

            listOtherPieces.remove(mypiece)

            listPotentialNextStates = []

            if (str(self.board[mypiece[0]][mypiece[1]]) == 'K'):

                #      print(" mypiece at  ",mypiece[0],mypiece[1])
                listPotentialNextStates = [[mypiece[0] + 1, mypiece[1], 6], \
                                           [mypiece[0] + 1, mypiece[1] - 1, 6], [mypiece[0], mypiece[1] - 1, 6], \
                                           [mypiece[0] - 1, mypiece[1] - 1, 6], \
                                           [mypiece[0] - 1, mypiece[1], 6], [mypiece[0] - 1, mypiece[1] + 1, 6], \
                                           [mypiece[0], mypiece[1] + 1, 6], [mypiece[0] + 1, mypiece[1] + 1, 6]]
                # check they are empty
                for k in range(len(listPotentialNextStates)):
                    aa = listPotentialNextStates[k]
                    if aa[0] > -1 and aa[0] < 8 and aa[1] > -1 and aa[1] < 8 and listPotentialNextStates[
                        k] not in listOtherPieces:
                        #and listPotentialNextStates[k] not in self.currentStateB:

                        if self.board[aa[0]][aa[1]] == None or not self.board[aa[0]][aa[1]].color:
                            self.listSuccessorStates.append([aa[0], aa[1], aa[2]])


            elif (str(self.board[mypiece[0]][mypiece[1]]) == 'P'):

                #       print(" mypiece at  ",mypiece[0],mypiece[1])
                listPotentialNextStates = [[mypiece[0], mypiece[1], 1], [mypiece[0] + 1, mypiece[1], 1]]
                # check they are empty
                for k in range(len(listPotentialNextStates)):

                    aa = listPotentialNextStates[k]
                    if aa[0] > -1 and aa[0] < 8 and aa[1] > -1 and aa[1] < 8 and listPotentialNextStates[
                        k] not in listOtherPieces:

                        if self.board[aa[0]][aa[1]] == None:
                            self.listSuccessorStates.append([aa[0], aa[1], aa[2]])


            elif (str(self.board[mypiece[0]][mypiece[1]]) == 'R'):

                #         print(" mypiece at  ",mypiece[0],mypiece[1])
                listPotentialNextStates = []

                ix = mypiece[0]
                iy = mypiece[1]

                while (ix > 0):
                    ix = ix - 1
                    #Ocupem la posició si és negra
                    if self.board[ix][iy] != None:
                        if not self.board[ix][iy].color:
                            listPotentialNextStates.append([ix, iy, 2])
                        break

                    elif self.board[ix][iy] == None :
                        listPotentialNextStates.append([ix, iy, 2])

                ix = mypiece[0]
                iy = mypiece[1]
                while (ix < 7):
                    ix = ix + 1
                    if self.board[ix][iy] != None:
                        if not self.board[ix][iy].color:
                            listPotentialNextStates.append([ix, iy, 2])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 2])

                ix = mypiece[0]
                iy = mypiece[1]
                while (iy > 0):
                    iy = iy - 1
                    if self.board[ix][iy] != None:
                        if not self.board[ix][iy].color:
                            listPotentialNextStates.append([ix, iy, 2])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 2])

                ix = mypiece[0]
                iy = mypiece[1]
                while (iy < 7):
                    iy = iy + 1
                    if self.board[ix][iy] != None:
                        if not self.board[ix][iy].color:
                            listPotentialNextStates.append([ix, iy, 2])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 2])

                        # check positions are not occupied - so far cannot kill pieces
                listPotentialNextStates
                for k in range(len(listPotentialNextStates)):

                    pos = listPotentialNextStates[k].copy()
                    pos[2] = 12
                    overlapping = False
                    """
                    if pos in self.currentStateB:
                        overlapping = True
                    """
                    if listPotentialNextStates[k] not in listOtherPieces and listPotentialNextStates[
                        k] and not overlapping:
                        self.listSuccessorStates.append(listPotentialNextStates[k])


            elif (str(self.board[mypiece[0]][mypiece[1]]) == 'H'):

                #         print(" mypiece at  ",mypiece[0]," ",mypiece[1]," ",3)
                listPotentialNextStates = []

                ix = mypiece[0]
                iy = mypiece[1]

                nextS = [ix + 1, iy + 2, 3]
                if nextS[0] > -1 and nextS[0] < 8 and nextS[1] > -1 and nextS[1] < 8:
                    self.listPotentialNextStates.append(nextS)
                nextS = [ix + 2, iy + 1, 3]
                if nextS[0] > -1 and nextS[0] < 8 and nextS[1] > -1 and nextS[1] < 8:
                    self.listPotentialNextStates.append(nextS)

                nextS = [ix + 1, iy - 2, 3]
                if nextS[0] > -1 and nextS[0] < 8 and nextS[1] > -1 and nextS[1] < 8:
                    self.listPotentialNextStates.append(nextS)
                nextS = [ix + 2, iy - 1, 3]
                if nextS[0] > -1 and nextS[0] < 8 and nextS[1] > -1 and nextS[1] < 8:
                    self.listPotentialNextStates.append(nextS)

                nextS = [ix - 2, iy - 1, 3]
                if nextS[0] > -1 and nextS[0] < 8 and nextS[1] > -1 and nextS[1] < 8:
                    self.listPotentialNextStates.append(nextS)
                nextS = [ix - 1, iy - 2, 3]
                if nextS[0] > -1 and nextS[0] < 8 and nextS[1] > -1 and nextS[1] < 8:
                    self.listPotentialNextStates.append(nextS)

                nextS = [ix - 1, iy + 2, 3]
                if nextS[0] > -1 and nextS[0] < 8 and nextS[1] > -1 and nextS[1] < 8:
                    self.listPotentialNextStates.append(nextS)

                nextS = [ix - 2, iy + 1, 3]
                if nextS[0] > -1 and nextS[0] < 8 and nextS[1] > -1 and nextS[1] < 8:
                    self.listPotentialNextStates.append(nextS)

                # check positions are not occupied
                for k in range(len(listPotentialNextStates)):

                    if listPotentialNextStates[k] not in listOtherPieces:
                        self.listSuccessorStates.append(listPotentialNextStates[k])



            elif (str(self.board[mypiece[0]][mypiece[1]]) == 'B'):

                #         print(" mypiece at  ",mypiece[0],mypiece[1], 4)
                listPotentialNextStates = []

                ix = mypiece[0]
                iy = mypiece[1]

                while (ix > 0 and iy > 0):
                    ix = ix - 1
                    iy = iy - 1
                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 4])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 4])

                ix = mypiece[0]
                iy = mypiece[1]
                while (ix < 7 and iy > 0):
                    ix = ix + 1
                    iy = iy + 1
                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 4])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 4])

                ix = mypiece[0]
                iy = mypiece[1]
                while (ix > 0 and iy < 7):
                    ix = ix - 1
                    iy = iy + 1
                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 4])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 4])

                ix = mypiece[0]
                iy = mypiece[1]
                while (ix < 7 and iy < 7):
                    ix = ix + 1
                    iy = iy + 1
                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 4])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 4])

                self.listSuccessorStates = listPotentialNextStates

            elif (str(self.board[mypiece[0]][mypiece[1]]) == 'Q'):

                #       print(" mypiece at  ",mypiece[0],mypiece[1])
                listPotentialNextStates = []

                # bishop wise
                ix = mypiece[0]
                iy = mypiece[1]

                while (ix > 0 and iy > 0):
                    ix = ix - 1
                    iy = iy - 1

                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 5])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 5])

                ix = mypiece[0]
                iy = mypiece[1]
                while (ix < 7 and iy > 0):
                    ix = ix + 1
                    iy = iy + 1
                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 5])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 5])

                ix = mypiece[0]
                iy = mypiece[1]
                while (ix > 0 and iy < 7):
                    ix = ix - 1
                    iy = iy + 1
                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 5])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 5])

                ix = mypiece[0]
                iy = mypiece[1]
                while (ix < 7 and iy < 7):
                    ix = ix + 1
                    iy = iy + 1
                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 5])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 5])

                        # Rook-like
                ix = mypiece[0]
                iy = mypiece[1]

                while (ix > 0):
                    ix = ix - 1
                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 5])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 5])

                ix = mypiece[0]
                iy = mypiece[1]
                while (ix < 7):
                    ix = ix + 1
                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 5])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 5])

                ix = mypiece[0]
                iy = mypiece[1]
                while (iy > 0):
                    iy = iy - 1
                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 5])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 5])

                ix = mypiece[0]
                iy = mypiece[1]
                while (iy < 7):
                    iy = iy + 1
                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 5])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 5])

                        # check positions are not occupied
                for k in range(len(listPotentialNextStates)):

                    if listPotentialNextStates[k] not in listOtherPieces:
                        self.listSuccessorStates.append(listPotentialNextStates[k])

                        # add other state pieces
            for k in range(len(self.listSuccessorStates)):
                self.listNextStates.append([self.listSuccessorStates[k]] + listOtherPieces)

        # for duplicates
        newList = self.listNextStates.copy
        newListNP = np.array(newList)

        # print("list nexts",self.listNextStates)

    def getListNextStatesB(self, mypieces):

        """
        Gets the list of next possible states given the currentStateW
        for each kind of piece

        """

        self.listNextStates = []

        # print("mypieces",mypieces)
        # print("len ",len(mypieces))
        for j in range(len(mypieces)):

            self.listSuccessorStates = []

            mypiece = mypieces[j]
            listOtherPieces = mypieces.copy()

            listOtherPieces.remove(mypiece)

            listPotentialNextStates = []

            #print(str(self.board[mypiece[0]][mypiece[1]]))

            if (self.board[mypiece[0]][mypiece[1]].name == 'K'):

                #print(" mypiece at  ",mypiece[0],mypiece[1])
                listPotentialNextStates = [[mypiece[0] + 1, mypiece[1], 12], \
                                           [mypiece[0] + 1, mypiece[1] - 1, 12], [mypiece[0], mypiece[1] - 1, 12], \
                                           [mypiece[0] - 1, mypiece[1] - 1, 12], \
                                           [mypiece[0] - 1, mypiece[1], 12], [mypiece[0] - 1, mypiece[1] + 1, 12], \
                                           [mypiece[0], mypiece[1] + 1, 12], [mypiece[0] + 1, mypiece[1] + 1, 12]]
                # check they are empty
                for k in range(len(listPotentialNextStates)):

                    aa = listPotentialNextStates[k]
                    if aa[0] > -1 and aa[0] < 8 and aa[1] > -1 and aa[1] < 8 and listPotentialNextStates[
                        k] not in listOtherPieces:
                        #listPotentialNextStates[k] not in self.currentStateW:

                        if self.board[aa[0]][aa[1]] == None or self.board[aa[0]][aa[1]].color:
                            self.listSuccessorStates.append([aa[0], aa[1], aa[2]])


            elif (self.board[mypiece[0]][mypiece[1]].name == 'P'):

                listPotentialNextStates = [[mypiece[0], mypiece[1], 7], [mypiece[0] + 1, mypiece[1], 7]]
                # check they are empty
                for k in range(len(listPotentialNextStates)):

                    aa = listPotentialNextStates[k]
                    if aa[0] > -1 and aa[0] < 8 and aa[1] > -1 and aa[1] < 8 and listPotentialNextStates[
                        k] not in listOtherPieces:

                        if self.board[aa[0]][aa[1]] == None:
                            self.listSuccessorStates.append([aa[0], aa[1], aa[2]])


            elif (self.board[mypiece[0]][mypiece[1]].name == 'R'):

                #         print(" mypiece at  ",mypiece[0],mypiece[1])
                listPotentialNextStates = []

                ix = mypiece[0]
                iy = mypiece[1]

                while (ix > 0):
                    ix = ix - 1
                    if self.board[ix][iy] != None:
                        if self.board[ix][iy].color:
                            listPotentialNextStates.append([ix, iy, 8])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 8])

                ix = mypiece[0]
                iy = mypiece[1]
                while (ix < 7):
                    ix = ix + 1
                    if self.board[ix][iy] != None:
                        if self.board[ix][iy].color:
                            listPotentialNextStates.append([ix, iy, 8])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 8])

                ix = mypiece[0]
                iy = mypiece[1]
                while (iy > 0):
                    iy = iy - 1
                    if self.board[ix][iy] != None:
                        if self.board[ix][iy].color:
                            listPotentialNextStates.append([ix, iy, 8])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 8])

                ix = mypiece[0]
                iy = mypiece[1]
                while (iy < 7):
                    iy = iy + 1
                    if self.board[ix][iy] != None:
                        if self.board[ix][iy].color:
                            listPotentialNextStates.append([ix, iy, 8])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 8])

                        # check positions are not occupied - so far cannot kill pieces
                listPotentialNextStates
                for k in range(len(listPotentialNextStates)):

                    pos = listPotentialNextStates[k].copy()
                    pos[2] = 6
                    overlapping = False
                    """
                    if pos in self.currentStateW:
                        overlapping = True
                    """

                    if listPotentialNextStates[k] not in listOtherPieces and listPotentialNextStates[
                        k] and not overlapping:
                        self.listSuccessorStates.append(listPotentialNextStates[k])





            elif (self.board[mypiece[0]][mypiece[1]].name == 'H'):

                #         print(" mypiece at  ",mypiece[0]," ",mypiece[1]," ",3)
                listPotentialNextStates = []

                ix = mypiece[0]
                iy = mypiece[1]

                nextS = [ix + 1, iy + 2, 9]
                if nextS[0] > -1 and nextS[0] < 8 and nextS[1] > -1 and nextS[1] < 8:
                    self.listPotentialNextStates.append(nextS)
                nextS = [ix + 2, iy + 1, 9]
                if nextS[0] > -1 and nextS[0] < 8 and nextS[1] > -1 and nextS[1] < 8:
                    self.listPotentialNextStates.append(nextS)

                nextS = [ix + 1, iy - 2, 9]
                if nextS[0] > -1 and nextS[0] < 8 and nextS[1] > -1 and nextS[1] < 8:
                    self.listPotentialNextStates.append(nextS)
                nextS = [ix + 2, iy - 1, 9]
                if nextS[0] > -1 and nextS[0] < 8 and nextS[1] > -1 and nextS[1] < 8:
                    self.listPotentialNextStates.append(nextS)

                nextS = [ix - 2, iy - 1, 9]
                if nextS[0] > -1 and nextS[0] < 8 and nextS[1] > -1 and nextS[1] < 8:
                    self.listPotentialNextStates.append(nextS)
                nextS = [ix - 1, iy - 2, 9]
                if nextS[0] > -1 and nextS[0] < 8 and nextS[1] > -1 and nextS[1] < 8:
                    self.listPotentialNextStates.append(nextS)

                nextS = [ix - 1, iy + 2, 9]
                if nextS[0] > -1 and nextS[0] < 8 and nextS[1] > -1 and nextS[1] < 8:
                    self.listPotentialNextStates.append(nextS)

                nextS = [ix - 2, iy + 1, 9]
                if nextS[0] > -1 and nextS[0] < 8 and nextS[1] > -1 and nextS[1] < 8:
                    self.listPotentialNextStates.append(nextS)

                # check positions are not occupied
                for k in range(len(listPotentialNextStates)):

                    if listPotentialNextStates[k] not in listOtherPieces:
                        self.listSuccessorStates.append(listPotentialNextStates[k])



            elif (self.board[mypiece[0]][mypiece[1]].name == 'B'):

                #         print(" mypiece at  ",mypiece[0],mypiece[1], 4)
                listPotentialNextStates = []

                ix = mypiece[0]
                iy = mypiece[1]

                while (ix > 0 and iy > 0):
                    ix = ix - 1
                    iy = iy - 1
                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 10])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 10])

                ix = mypiece[0]
                iy = mypiece[1]
                while (ix < 7 and iy > 0):
                    ix = ix + 1
                    iy = iy + 1
                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 10])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 10])

                ix = mypiece[0]
                iy = mypiece[1]
                while (ix > 0 and iy < 7):
                    ix = ix - 1
                    iy = iy + 1
                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 10])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 10])

                ix = mypiece[0]
                iy = mypiece[1]
                while (ix < 7 and iy < 7):
                    ix = ix + 1
                    iy = iy + 1
                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 10])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 10])

                self.listSuccessorStates = listPotentialNextStates

            elif (self.board[mypiece[0]][mypiece[1]].name == 'Q'):

                #       print(" mypiece at  ",mypiece[0],mypiece[1])
                listPotentialNextStates = []

                # bishop wise
                ix = mypiece[0]
                iy = mypiece[1]

                while (ix > 0 and iy > 0):
                    ix = ix - 1
                    iy = iy - 1

                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 11])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 11])

                ix = mypiece[0]
                iy = mypiece[1]
                while (ix < 7 and iy > 0):
                    ix = ix + 1
                    iy = iy + 1
                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 11])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 11])

                ix = mypiece[0]
                iy = mypiece[1]
                while (ix > 0 and iy < 7):
                    ix = ix - 1
                    iy = iy + 1
                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 11])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 11])

                ix = mypiece[0]
                iy = mypiece[1]
                while (ix < 7 and iy < 7):
                    ix = ix + 1
                    iy = iy + 1
                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 11])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 11])

                        # Rook-like
                ix = mypiece[0]
                iy = mypiece[1]

                while (ix > 0):
                    ix = ix - 1
                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 11])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 11])

                ix = mypiece[0]
                iy = mypiece[1]
                while (ix < 7):
                    ix = ix + 1
                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 11])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 11])

                ix = mypiece[0]
                iy = mypiece[1]
                while (iy > 0):
                    iy = iy - 1
                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 11])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 11])

                ix = mypiece[0]
                iy = mypiece[1]
                while (iy < 7):
                    iy = iy + 1
                    if self.board[ix][iy] != None:
                        listPotentialNextStates.append([ix, iy, 11])
                        break

                    elif self.board[ix][iy] == None:
                        listPotentialNextStates.append([ix, iy, 11])

                        # check positions are not occupied
                for k in range(len(listPotentialNextStates)):

                    if listPotentialNextStates[k] not in listOtherPieces:
                        self.listSuccessorStates.append(listPotentialNextStates[k])

                        # add other state pieces
            for k in range(len(self.listSuccessorStates)):
                self.listNextStates.append([self.listSuccessorStates[k]] + listOtherPieces)

        # for duplicates
        newList = self.listNextStates.copy
        newListNP = np.array(newList)

        # print("list nexts",self.listNextStates)

    def print_board(self):
        """
        Prints the current state of the board.
        """

        buffer = ""
        for i in range(33):
            buffer += "*"
        print(buffer)
        for i in range(len(self.board)):
            tmp_str = "|"
            for j in self.board[i]:
                if j == None or j.name == 'GP':
                    tmp_str += "   |"
                elif len(j.name) == 2:
                    tmp_str += (" " + str(j) + "|")
                else:
                    tmp_str += (" " + str(j) + " |")
            print(tmp_str)
        buffer = ""
        for i in range(33):
            buffer += "*"
        print(buffer)
