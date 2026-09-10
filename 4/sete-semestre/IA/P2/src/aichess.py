#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Data d'entrega: 11/2023

@author: ignasi
"""
import copy
import math
import time
import chess
import board
import numpy as np
import sys
import queue
from typing import List

RawStateType = List[List[List[int]]]

from itertools import permutations


class Aichess():
    """
    A class to represent the game of chess.

    ...

    Attributes:
    -----------
    chess : Chess
        represents the chess game

    Methods:
    --------
    startGame(pos:stup) -> None
        Promotes a pawn that has reached the other side to another, or the same, piece

    """

    def __init__(self, TA, myinit=True):

        if myinit:
            self.chess = chess.Chess(TA, True)
        else:
            self.chess = chess.Chess([], False)

        self.listNextStates = []
        self.listVisitedStates = []
        self.listVisitedSituations = []
        self.pathToTarget = []
        self.currentStateW = self.chess.boardSim.currentStateW;
        self.depthMax = 8;
        self.checkMate = False

    def copyState(self, state):
        copyState = []
        for piece in state:
            copyState.append(piece.copy())
        return copyState
    
    def isVisitedSituation(self, color, mystate):
        if (len(self.listVisitedSituations) > 0):
            perm_state = list(permutations(mystate))

            isVisited = False
            for j in range(len(perm_state)):

                for k in range(len(self.listVisitedSituations)):
                    if self.isSameState(list(perm_state[j]), self.listVisitedSituations.__getitem__(k)[1]) and color == \
                            self.listVisitedSituations.__getitem__(k)[0]:
                        isVisited = True

            return isVisited
        else:
            return False

    def getCurrentStateW(self):

        return self.myCurrentStateW

    # Returns the next state of whites 
    def getListNextStatesW(self, myState):

        self.chess.boardSim.getListNextStatesW(myState)
        self.listNextStates = self.chess.boardSim.listNextStates.copy()

        return self.listNextStates
    
    # Returns the next state of blacks 
    def getListNextStatesB(self, myState):
        self.chess.boardSim.getListNextStatesB(myState)
        self.listNextStates = self.chess.boardSim.listNextStates.copy()

        return self.listNextStates

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

    def isVisited(self, mystate):

        if (len(self.listVisitedStates) > 0):
            perm_state = list(permutations(mystate))

            isVisited = False
            for j in range(len(perm_state)):

                for k in range(len(self.listVisitedStates)):

                    if self.isSameState(list(perm_state[j]), self.listVisitedStates[k]):
                        isVisited = True

            return isVisited
        else:
            return False

    def newBoardSim(self, listStates):
        # Create a new boardSim
        TA = np.zeros((8, 8))
        for state in listStates:
            TA[state[0]][state[1]] = state[2]

        self.chess.newBoardSim(TA)

    def getPieceState(self, state, piece):
        pieceState = None
        for i in state:
            if i[2] == piece:
                pieceState = i
                break
        return pieceState

    def getCurrentState(self):
        # Return the current state of the pieces of the board
        listStates = []
        for i in self.chess.board.currentStateW:
            listStates.append(i)
        for j in self.chess.board.currentStateB:
            listStates.append(j)
        return listStates

    def getNextPositions(self, state):
        # Return the next positions of a determinated state of the pieces of the board
        if state == None:
            return None
        if state[2] > 6:
            nextStates = self.getListNextStatesB([state])
        else:
            nextStates = self.getListNextStatesW([state])
        nextPositions = []
        for i in nextStates:
            nextPositions.append(i[0][0:2])
        return nextPositions

    def getWhiteState(self, currentState):
        whiteState = []
        wkState = None
        wrState = None
        for i in currentState:
            if i[2] == 6:
                wkState = i
        whiteState.append(wkState)
        for i in currentState:
            if i[2] == 2:
                wrState = i
        if wrState != None:
            whiteState.append(wrState)
        return whiteState

    def getBlackState(self, currentState):
        blackState = []
        black_king_state = None
        brState = None
        
        for i in currentState:
            if i[2] == 12:
                black_king_state = i
        blackState.append(black_king_state)
        for i in currentState:
            if i[2] == 8:
                brState = i
        if brState != None:
            blackState.append(brState)
        return blackState

    def getMovement(self, state, nextState):
        # Given a state and a successor state, return the position of the moved piece in both states
        pieceState = None
        pieceNextState = None
        pieceNext = None

        for piece in state:
            if piece not in nextState:
                movedPiece = piece[2]
                for i in nextState:
                    if i[2] == movedPiece:
                        pieceNext = i
                
                if pieceNext == None:
                    continue
                
                pieceState = piece
                pieceNextState = pieceNext
                break

        return [pieceState, pieceNextState]

    #################################
    # Funcions per veure el risc del rei
    #################################
    # Rei negre
    def isWatchedBk(self, currentState):
        self.newBoardSim(currentState)

        bkPosition = self.getPieceState(currentState, 12)[0:2]
        wkState = self.getPieceState(currentState, 6)
        wrState = self.getPieceState(currentState, 2)

        # Si les negres maten el rei blanc, no és una configuració correcta
        if wkState == None:
            return False
        # Mirem les possibles posicions del rei blanc i mirem si en alguna pot "matar" al rei negre
        for wkPosition in self.getNextPositions(wkState):
            if bkPosition == wkPosition:
                # Tindríem un checkMate
                return True
        if wrState != None:
            # Mirem les possibles posicions de la torre blanca i mirem si en alguna pot "matar" al rei negre
            for wrPosition in self.getNextPositions(wrState):
                if bkPosition == wrPosition:
                    return True
        return False

    def canNotMoveBk(self, currentState):
        # The future states of the black king are threatened by the whites?
        self.newBoardSim(currentState)
        
        bkState = self.getPieceState(currentState, 12)
        
        allWatched = False
        
        # It's important to check if the king is located on a wall, a situation that determines its state of threat
        if bkState[0] == 0 or bkState[0] == 7 or bkState[1] == 0 or bkState[1] == 7:

            wrState = self.getPieceState(currentState, 2)
            whiteState = self.getWhiteState(currentState)
            nextBStates = self.getListNextStatesB(self.getBlackState(currentState))
            allWatched = True

            for state in nextBStates:

                newWhiteState = whiteState.copy()
                # Check if white rook has been eliminated, in case of it remove it from the state
                if wrState != None and wrState[0:2] == state[0][0:2]:
                    newWhiteState.remove(wrState)

                state = state + newWhiteState
                # Move black pieces to the new state
                self.newBoardSim(state)

                # Is the black king threatened or not?
                if not self.isWatchedBk(state):
                    allWatched = False
                    break

        self.newBoardSim(currentState)
        return allWatched

    # Mirem si el rei negre està en escac i mat
    def checkMateBk(self,state):
        # Estem en taules si totes les possibles posicions estan amenaçades (això és canMoveBk == true)
        #   i a més el rei està en perill (això és isWatchedBk == true)
        return self.canNotMoveBk(state) and self.isWatchedBk(state)

    # Mirem si estem en taules pels negres
    def taulesBk(self, state):
        # Estem en taules si totes les possibles posicions estan amenaçades (això és canNotMoveBk == true)
        #   pero el rei no està en perill (això és isWatchedBk == false)
        return self.canNotMoveBk(state) and not self.isWatchedBk(state)

    # Rei blanc
    def isWatchedWk(self, currentState):
        self.newBoardSim(currentState)

        wkPosition = self.getPieceState(currentState, 6)[0:2]
        bkState = self.getPieceState(currentState, 12)
        brState = self.getPieceState(currentState, 8)

        # If whites kill the black king , it is not a correct configuration
        if bkState == None:
            return False
        # We check all possible positions for the black king, and chck if in any of them it may kill the white king
        for bkPosition in self.getNextPositions(bkState):
            if wkPosition == bkPosition:
                # That would be checkMate
                return True
        if brState != None:
            # We check the possible positions of the black tower, and we chck if in any o them it may killt he white king
            for brPosition in self.getNextPositions(brState):
                if wkPosition == brPosition:
                    return True

        return False

    def canNotMoveWk(self, currentState):
        
        self.newBoardSim(currentState)
        # The future states of the white king are threatened by the blacks?
        brState = self.getPieceState(currentState, 8)
        wkState = self.getPieceState(currentState, 6)

        allWatched = False

        # It's important to check if the king is located on a wall, a situation that determines its state of threat
        if wkState[0] == 0 or wkState[0] == 7 or wkState[1] == 0 or wkState[1] == 7:
            blackState = self.getBlackState(currentState)
            allWatched = True
            nextWStates = self.getListNextStatesW(self.getWhiteState(currentState))
            for state in nextWStates:
                newBlackState = blackState.copy()
                # Check if black rook has been eliminated, in case of it remove it from the state
                if brState != None and brState[0:2] == state[0][0:2]:
                    newBlackState.remove(brState)
                state = state + newBlackState
                # Move white pieces to the new state
                self.newBoardSim(state)
                # Is the white king threatened or not?
                if not self.isWatchedWk(state):
                    allWatched = False
                    break
        self.newBoardSim(currentState)
        return allWatched

    # Mirem si el rei blanc està en escac i mat
    def checkMateWk(self,state):
        # Estem en taules si totes les possibles posicions estan amenaçades (això és canMoveWk == true)
        #   i a més el rei està en perill (això és isWatchedWk == true)
        return self.canNotMoveWk(state) and self.isWatchedWk(state)

    # Mirem si estem en taules pels blancs
    def taulesWk(self, state):
        # Estem en taules si totes les possibles posicions estan amenaçades (això és canNotMoveWk == true)
        #   pero el rei no està en perill (això és isWatchedWk == false)
        return self.canNotMoveWk(state) and not self.isWatchedWk(state)


    #################################
    # Funció per calcular l'heuristica
    #################################
    def heuristica(self, currentState, color):
        #In this method, we calculate the heuristics for both the whites and black ones
        #The value calculated here is for the whites, 
        # but finally from verything, as a function of the color parameter, we multiply the result by -1
        value = 0

        bkState = self.getPieceState(currentState, 12)
        wkState = self.getPieceState(currentState, 6)
        wrState = self.getPieceState(currentState, 2)
        brState = self.getPieceState(currentState, 8)

        filaBk = bkState[0]
        columnaBk = bkState[1]
        filaWk = wkState[0]
        columnaWk = wkState[1]

        if wrState != None:
            filaWr = wrState[0]
            columnaWr = wrState[1]
        if brState != None:
            filaBr = brState[0]
            columnaBr = brState[1]

        # We check if they killed the black tower
        if brState == None:
            value += 50
            fila = abs(filaBk - filaWk)
            columna = abs(columnaWk - columnaBk)
            distReis = min(fila, columna) + abs(fila - columna)
            if distReis >= 3 and wrState != None:
                filaR = abs(filaBk - filaWr)
                columnaR = abs(columnaWr - columnaBk)
                value += (min(filaR, columnaR) + abs(filaR - columnaR))/10
            # If we are white white, the closer our king from the oponent, the better
            # we substract 7 to the distance between the two kings, since the max distance they can be at in a board is 7 moves
            value += (7 - distReis)
            # If they black king is against a wall, we prioritize him to be at a corner, precisely to corner him
            if bkState[0] == 0 or bkState[0] == 7 or bkState[1] == 0 or bkState[1] == 7:
                value += (abs(filaBk - 3.5) + abs(columnaBk - 3.5)) * 10
            #If not, we will only prioritize that he approahces the wall, to be able to approach the check mate
            else:
                value += (max(abs(filaBk - 3.5), abs(columnaBk - 3.5))) * 10

        # They killed the black tower. Within this method, we consider the same conditions than in the previous condition
        # Within this method we consider the same conditions than in the previous section, but now with reversed values.
        if wrState == None:
            value += -50
            fila = abs(filaBk - filaWk)
            columna = abs(columnaWk - columnaBk)
            distReis = min(fila, columna) + abs(fila - columna)

            if distReis >= 3 and brState != None:
                filaR = abs(filaWk - filaBr)
                columnaR = abs(columnaBr - columnaWk)
                value -= (min(filaR, columnaR) + abs(filaR - columnaR)) / 10
            # If we are white, the close we have our king from the oponent, the better
            # If we substract 7 to the distance between both kings, as this is the max distance they can be at in a chess board
            value += (-7 + distReis)

            if wkState[0] == 0 or wkState[0] == 7 or wkState[1] == 0 or wkState[1] == 7:
                value -= (abs(filaWk - 3.5) + abs(columnaWk - 3.5)) * 10
            else:
                value -= (max(abs(filaWk - 3.5), abs(columnaWk - 3.5))) * 10

        # We are checking blacks
        if self.isWatchedBk(currentState):
            value += 20

        # We are checking whites
        if self.isWatchedWk(currentState):
            value += -20

        # If black, values are negative, otherwise positive
        if not color:
            value = (-1) * value

        return value

    #################################
    # Implementació del MiniMax
    #################################
    def minimaxGame(self, depthWhite, depthBlack):
        currentState = self.getCurrentState()        

        # A la variable color win guardarem qui ha guanyat, utilitzem:
        #   1. -1 => Guanya blanques
        #   2. 0 => Taules
        #   3. 1 => Guanya negres
        winnerMessage = {-1: 'Han guanyat les blanques', 0: 'Han quedat empat', 1: 'Han guanyat les negres'}
        winnerPlayer = 0

        # Si les blanques estan en escac i mat guanyen les negres
        # if self.checkMateWk(currentState):
            # return winnerMessage[1]
        
        # Si el rei negre està en escac i mat guanyen les blanques
        # if self.isWatchedBk(currentState):
            # return winnerMessage[-1]

        # Guardem l'estat actual i l'afegim a la llista de visitats
        copyState = self.copyState(currentState)
        self.listVisitedSituations.append((False, copyState))

        # Fem un màxim de 50 moviments. Es per tenir l'execució acotada
        for i in range(50):
            currentState = self.getCurrentState()

            # Comencen jugant les blances
            if i % 2 == 0:
                # Mouen la fitxa utilitzan l'algorisme minimax
                if not self.minimaxWhite(currentState, depthWhite):
                    break
                # Si un cop moguda la peça el rei negre està en escat i mat, han guanyat
                if self.checkMateBk(currentState):
                    winnerPlayer = -1
                    break
                elif self.taulesBk(currentState):
                    winnerPlayer = 0
                    break

            # Després juguen les negres
            else:
                # Mouen la fitxa utilitzan l'algorisme minimax per a les negres
                if not self.minimaxBlack(currentState, depthBlack):
                    break

                # Si un cop moguda la peça el rei blanc està en escat i mat, han guanyat
                if self.checkMateWk(currentState):
                    winnerPlayer = 1
                    break
                elif self.taulesWk(currentState):
                    winnerPlayer = 0
                    break

            # Print the board
            self.chess.board.print_board()

        # Print the final board
        self.chess.board.print_board()

        # Mostrem el missatge de qui ha guanyat
        return winnerMessage[winnerPlayer]

    # Moviment de les blanques
    def minimaxWhite(self, state, depthWhite):
        # Fem el moviment segons el màxim
        nextState = self.maximumWhites(state, 0, depthWhite)

        # Si no hem pogut trobar un estat següent, és que el rei blanc està amenaçat a tot arreu
        if nextState == None:
            return False

        copyState = self.copyState(nextState)

        # Si encara no l'haviem visitat, l'afegim a la llista
        self.listVisitedSituations.append((True, copyState))
        
        # Fem el moviment
        movement = self.getMovement(state, nextState)
        self.chess.move(movement[0], movement[1])

        # Retornem que tot ha anat bé
        return True

    def maximumWhites(self, currentState, depth, depthWhite):
        # Mirem si el rei blanc està en escat i mat
        if self.checkMateWk(currentState):
            return -1000000
        
        # Mirem si el rei blanc està en taules
        if self.taulesWk(currentState):
            return 0

        # Si hem arribat a la profunditat màxima
        if depth == depthWhite:
            return self.heuristica(currentState, True)

        # Com volem trobar el màxim hem d'inicialitzar la variable amb un valor petit
        maximumValue = -float('inf')

        # Guardem l'últim estat que hem pogut visitar, es per si arribem al màxim de profunditat
        maximumState = None

        # Guardem les posicions de les blanques i les negres
        whiteState = self.getWhiteState(currentState)
        blackState = self.getBlackState(currentState)

        # Mirem si la torre negra esta viva
        brState = self.getPieceState(currentState, 8)
            
        # Iterem per tots els estats futurs
        for state in self.getListNextStatesW(whiteState):
            # Guardem les peces negres
            newBlackState = blackState.copy()

            # Si ens hem menjat la torre l'hem d'eliminar
            if brState != None and brState[0:2] == state[0][0:2]:
                newBlackState.remove(brState)

            # Actualiztem l'estat
            state = state + newBlackState

            # No podem moure el rei a una posició en perill
            if not self.isWatchedWk(state) and not self.isVisitedSituation(True,state):
                # Calculem el valor, que és el mínim per les blanques
                valueState = self.minimumWhites(state, depth + 1, depthWhite)

                # En cas positiu actualitzem els valors
                if valueState > maximumValue:
                    maximumValue = valueState
                    maximumState = state

        # Si arribem al final de l'arbre retornem l'últim estat que hem vist
        if depth == 0:
            return maximumState
        
        return maximumValue

    def minimumWhites(self, currentState, depth, depthMax):
        # Mirem si el rei negre està en escat i mat
        if self.checkMateBk(currentState):
            return 10000000 - depth
        
        # Mirem si el rei negre està en taules
        if self.taulesBk(currentState):
            return 0

        # Si hem arribat a la profunditat màxima
        if depth == depthMax:
            return self.heuristica(currentState, True)

        # Guardem les posicions de les blanques i les negres
        whiteState = self.getWhiteState(currentState)
        blackState = self.getBlackState(currentState)

        # Mirem si la torre blanca esta viva
        wrState = self.getPieceState(currentState, 2)

        # Com volem trobar el mínim hem d'inicialitzar la variable amb un valor gran
        minimumValue = float('inf')

        # Iterem per tots els estats futurs
        for state in self.getListNextStatesB(blackState):
            # Guardem les peces blanques
            newWhiteState = whiteState.copy()

            # Si ens hem menjat la torre l'hem d'eliminar
            if wrState != None and wrState[0:2] == state[0][0:2]:
                newWhiteState.remove(wrState)
                
            # Actualiztem l'estat
            state = state + newWhiteState

            # No podem moure el rei a una posició en perill
            if not self.isWatchedBk(state) and not self.isVisitedSituation(True,state):
                # Actualizem el valor mínim
                minimumValue = min(minimumValue, self.maximumWhites(state, depth + 1, depthMax))

        # Retornem el valor
        return minimumValue

    # Moviment de les negres
    def minimaxBlack(self, state, depthMax):
        # Fem el moviment segons el màxim
        nextState = self.maximumBlacks(state, 0, depthMax)
        
        # No hem pogut trobar un estat en què la fitxa negra no estigui amenaçada
        if nextState == None:
            return False

        copyState = self.copyState(nextState)

        # Si ja l'hem visitat retornem false, ja que ens podem quedar enganxats 
        # en un bucle infinit.     
        #if self.isVisitedSituation(True, copyState):
            #return False

        # Si encara no l'haviem visitat, l'afegim a la llista
        self.listVisitedSituations.append((True, copyState))
        
        # Fem el moviment
        movement = self.getMovement(state, nextState)
        self.chess.move(movement[0], movement[1])

        # Retornem que tot ha anat bé
        return True

    def maximumBlacks(self, currentState, depth, depthMax):
        # Mirem si el rei blanc està en escat i mat
        if self.checkMateBk(currentState):
            return -1000000
        
        # Mirem si el rei blanc està en taules
        if self.taulesBk(currentState):
            return 0

        # Si hem arribat a la profunditat màxima
        if depth == depthMax:
            return self.heuristica(currentState, False)

        # Com volem trobar el màxim hem d'inicialitzar la variable amb un valor petit
        maximumValue = -float('inf')

        # Guardem l'últim estat que hem pogut visitar, es per si arribem al màxim de profunditat
        maximumState = None

        # Guardem les posicions de les blanques i les negres
        whiteState = self.getWhiteState(currentState)
        blackState = self.getBlackState(currentState)
        
        # Mirem si la torre blanca esta viva
        wrState = self.getPieceState(currentState, 2)
                
        # Iterem per tots els estats futurs
        for state in self.getListNextStatesB(blackState):
            # Guardem les peces blanques
            newWhiteState = whiteState.copy()

            # Si ens hem menjat la torre l'hem d'eliminar
            if wrState != None and wrState[0:2] == state[0][0:2]:
                newWhiteState.remove(wrState)

            # Actualiztem l'estat
            state = state + newWhiteState

            # No podem moure el rei a una posició en perill
            if not self.isWatchedBk(state) and not self.isVisitedSituation(True,state):
                # Calculem el valor
                valueState = self.minimumBlacks(state, depth + 1, depthMax)

                # En cas positiu actualitzem els valors
                if valueState > maximumValue:
                    maximumValue = valueState
                    maximumState = state

        # Si arribem al final de l'arbre retornem l'últim estat que hem vist
        if depth == 0:
            return maximumState

        return maximumValue

    def minimumBlacks(self, currentState, depth, depthMax):
        # Mirem si el rei blanc està en escat i mat
        if self.checkMateWk(currentState):
            return 10000000 - depth
        
        # Mirem si el rei blanc està en taules
        if self.taulesWk(currentState):
            return 0

        # Si hem arribat a la profunditat màxima
        if depth == depthMax:
            return self.heuristica(currentState, False)

        # Com volem trobar el mínim hem d'inicialitzar la variable amb un valor gran
        minimumValue = float('inf')

        # Guardem les posicions de les blanques i les negres
        whiteState = self.getWhiteState(currentState)
        blackState = self.getBlackState(currentState)

        # Mirem si la torre negra esta viva
        brState = self.getPieceState(currentState, 8)

        # Iterem per tots els estats futurs
        for state in self.getListNextStatesW(whiteState):
            # Guardem les peces negres
            newBlackState = blackState.copy()

            # Si ens hem menjat la torre l'hem d'eliminar
            if brState != None and brState[0:2] == state[0][0:2]:
                newBlackState.remove(brState)

            # Actualiztem l'estat
            state = state + newBlackState

            # No podem moure el rei a una posició en perill
            if not self.isWatchedWk(state) and not self.isVisitedSituation(True,state):
                # Actualizem el valor mínim
                minimumValue = min(minimumValue, self.maximumBlacks(state, depth + 1, depthMax))

        return minimumValue


    #################################
    # Implementació de la AlphaBetaPoda
    #################################
    def alphaBetaPoda(self, depthWhite, depthBlack):
        currentState = self.getCurrentState()

        # A la variable color win guardarem qui ha guanyat, utilitzem:
        #   1. -1 => Guanya blanques
        #   2. 0 => Taules
        #   3. 1 => Guanya negres
        winnerMessage = {-1: 'Han guanyat les blanques', 0: 'Han quedat empat', 1: 'Han guanyat les negres'}
        winnerPlayer = 0

        # Si les blanques estan en escac i mat guanyen les negres
        if self.checkMateWk(currentState):
            return winnerMessage[1]
        
        # Si el rei negre està en escac i mat guanyen les blanques
        if self.checkMateBk(currentState):
            return winnerMessage[-1]

        # Guardem l'estat actual i l'afegim a la llista de visitats
        copyState = self.copyState(currentState)
        self.listVisitedSituations.append((False, copyState))

        # Fem un màxim de 50 moviments. Es per tenir l'execució acotada
        for i in range(50):
            currentState = self.getCurrentState()

            # Comencen jugant les blances
            if i % 2 == 0:
                # Mouen la fitxa utilitzan l'algorisme alphabetapoda
                if not self.podaWhite(currentState, depthWhite):
                    break
                # Si un cop moguda la peça el rei negre està en escat i mat, han guanyat
                if self.checkMateBk(currentState):
                    winnerPlayer = -1
                    break
                elif self.taulesBk(currentState):
                    winnerPlayer = 0
                    break

            # Després juguen les negres
            else:
                # Mouen la fitxa utilitzan l'algorisme alphabetapoda
                if not self.podaBlack(currentState, depthBlack):
                    break

                # Si un cop moguda la peça el rei blanc està en escat i mat, han guanyat
                if self.checkMateWk(currentState):
                    winnerPlayer = 1
                    break
                elif self.taulesWk(currentState):
                    winnerPlayer = 0
                    break
            
            # Print the board
            self.chess.board.print_board()

        # Print the final board
        self.chess.board.print_board()

        # Mostrem el missatge de qui ha guanyat
        return winnerMessage[winnerPlayer]

    # Moviment de les blanques
    def podaWhite(self, state, depthMax):
        # La variable alpha és un valor molt petit i beta molt gran
        alpha = -float('inf')
        beta = float('inf')

        # Fem el moviment segons el màxim
        nextState = self.podaMaxValueWhite(state, 0, depthMax, alpha, beta)

        # Si no hem pogut trobar un estat següent, és que el rei blanc està amenaçat a tot arreu
        if nextState == None:
            return False

        copyState = self.copyState(nextState)

        # Si encara no l'haviem visitat, l'afegim a la llista
        self.listVisitedSituations.append((True, copyState))

        # Fem el moviment
        movement = self.getMovement(state, nextState)
        self.chess.move(movement[0], movement[1])

        # Retornem que tot ha anat bé
        return True

    def podaMaxValueWhite(self, currentState, depth, depthMax, alpha, beta):
        # Mirem si el rei blanc està en escat i mat
        if self.checkMateWk(currentState):
            return -999999
        
        # Mirem si el rei blanc està en taules
        if self.taulesWk(currentState):
            return 0

        # Si hem arribat a la profunditat màxima
        if depth == depthMax:
            return self.heuristica(currentState, True)

        # Com volem trobar el màxim hem d'inicialitzar la variable amb un valor petit
        maximumValue = -float('inf')

        # Guardem l'últim estat que hem pogut visitar, es per si arribem al màxim de profunditat
        maximumState = None

        # Guardem les posicions de les blanques i les negres
        whiteState = self.getWhiteState(currentState)
        blackState = self.getBlackState(currentState)

        # Mirem si la torre negra esta viva
        brState = self.getPieceState(currentState, 8)
        
        # Iterem per tots els estats futurs
        for state in self.getListNextStatesW(whiteState):
            # Guardem les peces negres
            newBlackState = blackState.copy()

            # Si ens hem menjat la torre l'hem d'eliminar
            if brState != None and brState[0:2] == state[0][0:2]:
                newBlackState.remove(brState)

            # Actualiztem l'estat
            state = state + newBlackState

            # No podem moure el rei a una posició en perill
            if not self.isWatchedWk(state) and not self.isVisitedSituation(True,state):
                # Calculem el valor
                valueState = self.podaMinValueWhite(state, depth + 1, depthMax, alpha, beta)

                # En cas positiu actualitzem els valors
                if valueState > maximumValue:
                    maximumValue = valueState
                    maximumState = state

                # En cas que el valor sigui superior a beta no ens interesa
                if maximumValue >= beta:
                    break

                # Actualitzem alpha
                alpha = max(alpha, maximumValue)

        # Si arribem al final de l'arbre retornem l'últim estat que hem vist
        if depth == 0:
            return maximumState
        
        return maximumValue

    def podaMinValueWhite(self, currentState, depth, depthMax, alpha, beta):
        # Mirem si el rei negre està en escat i mat
        if self.checkMateBk(currentState):
            return 100000 - depth
        
        # Mirem si el rei negre està en taules
        if self.taulesBk(currentState):
            return 0

        # Si hem arribat a la profunditat màxima
        if depth == depthMax:
            return self.heuristica(currentState, True)

        # Guardem les posicions de les blanques i les negres
        whiteState = self.getWhiteState(currentState)
        blackState = self.getBlackState(currentState)

        # Mirem si la torre blanca esta viva
        wrState = self.getPieceState(currentState, 2)

        # Com volem trobar el mínim hem d'inicialitzar la variable amb un valor gran
        minimumValue = float('inf')

        # Iterem per tots els estats futurs
        for state in self.getListNextStatesB(blackState):
            # Guardem les peces blanques
            newWhiteState = whiteState.copy()
            
            # Si ens hem menjat la torre l'hem d'eliminar
            if wrState != None and wrState[0:2] == state[0][0:2]:
                newWhiteState.remove(wrState)

            # Actualiztem l'estat
            state = state + newWhiteState

            # No podem moure el rei a una posició en perill
            if not self.isWatchedBk(state) and not self.isVisitedSituation(True,state):
                # Actualizem el valor mínim
                minimumValue = min(minimumValue, self.podaMaxValueWhite(state, depth + 1, depthMax, alpha, beta))

                # En cas que el valor sigui inferior a alpha no ens interesa
                if minimumValue <= alpha:
                    break

                # Actualitzem el valor de beta
                beta = min(beta, minimumValue)

        # Retornem el valor
        return minimumValue

    # Moviment de les blanques
    def podaBlack(self, state, depthMax):
        # La variable alpha és un valor molt petit i beta molt gran
        alpha = -float('inf')
        beta = float('inf')

        # Fem el moviment segons el màxim
        nextState = self.podaMaxValueBlack(state, 0, depthMax, alpha, beta)

        # Si no hem pogut trobar un estat següent, és que el rei blanc està amenaçat a tot arreu
        if nextState == None:
            return False

        copyState = self.copyState(nextState)

        # Si encara no l'haviem visitat, l'afegim a la llista
        self.listVisitedSituations.append((True, copyState))

        # Fem el moviment
        movement = self.getMovement(state, nextState)
        self.chess.move(movement[0], movement[1])

        # Retornem que tot ha anat bé
        return True

    def podaMaxValueBlack(self, currentState, depth, depthMax, alpha, beta):
        # Mirem si el rei blanc està en escat i mat
        if self.checkMateBk(currentState):
            return -999999
        
        # Mirem si el rei negre està en taules
        if self.taulesBk(currentState):
            return 0

        # Si hem arribat a la profunditat màxima
        if depth == depthMax:
            return self.heuristica(currentState, False)

        # Com volem trobar el màxim hem d'inicialitzar la variable amb un valor petit
        maximumValue = -float('inf')

        # Guardem l'últim estat que hem pogut visitar, es per si arribem al màxim de profunditat
        maximumState = None

        # Guardem les posicions de les blanques i les negres
        whiteState = self.getWhiteState(currentState)
        blackState = self.getBlackState(currentState)

        # Mirem si la torre blanca esta viva
        wrState = self.getPieceState(currentState, 2)
            
        # Iterem per tots els estats futurs
        for state in self.getListNextStatesB(blackState):
            # Guardem les peces blanques
            newWhiteState = whiteState.copy()

            # Si ens hem menjat la torre l'hem d'eliminar
            if wrState != None and wrState[0:2] == state[0][0:2]:
                newWhiteState.remove(wrState)

            # Actualiztem l'estat
            state = state + newWhiteState

            # No podem moure el rei a una posició en perill
            if not self.isWatchedBk(state) and not self.isVisitedSituation(True,state):
                # Calculem el valor
                valueState = self.podaMinValueBlack(state, depth + 1, depthMax, alpha, beta)

                # En cas positiu actualitzem els valors
                if valueState > maximumValue:
                    maximumValue = valueState
                    maximumState = state

                # En cas que el valor sigui superior a beta no ens interesa
                if maximumValue >= beta:
                    break

                # Actualitzem alpha
                alpha = max(alpha, maximumValue)

        # Si arribem al final de l'arbre retornem l'últim estat que hem vist
        if depth == 0:
            return maximumState
        
        return maximumValue

    def podaMinValueBlack(self, currentState, depth, depthMax, alpha, beta):
        # Mirem si el rei blanc està en escat i mat
        if self.checkMateWk(currentState):
            return 100000 - depth
        
        # Mirem si el rei blanc està en taules
        if self.taulesWk(currentState):
            return 0

        # Si hem arribat a la profunditat màxima
        if depth == depthMax:
            return self.heuristica(currentState, False)
        
        # Com volem trobar el mínim hem d'inicialitzar la variable amb un valor gran
        minimumValue = float('inf')

        # Guardem les posicions de les blanques i les negres
        whiteState = self.getWhiteState(currentState)
        blackState = self.getBlackState(currentState)

        # Mirem si la torre negra esta viva
        brState = self.getPieceState(currentState, 8)

        # Iterem per tots els estats futurs
        for state in self.getListNextStatesW(whiteState):
            # Guardem les peces negres
            newBlackState = blackState.copy()

            # Si ens hem menjat la torre l'hem d'eliminar
            if brState != None and brState[0:2] == state[0][0:2]:
                newBlackState.remove(brState)

            # Actualiztem l'estat
            state = state + newBlackState

            # No podem moure el rei a una posició en perill
            if not self.isWatchedWk(state) and not self.isVisitedSituation(True,state):
                # Actualizem el valor mínim
                minimumValue = min(minimumValue, self.podaMaxValueBlack(state, depth + 1, depthMax, alpha, beta))

                # En cas que el valor sigui inferior a alpha no ens interesa
                if minimumValue <= alpha:
                    break

                # Actualitzem el valor de beta
                beta = min(beta, minimumValue)

        return minimumValue
    
    #################################
    # Implementació del ExpectiMax
    #################################
    def expectimax(self, depthWhite, depthBlack):
        currentState = self.getCurrentState()        

        # A la variable color win guardarem qui ha guanyat, utilitzem:
        #   1. -1 => Guanya blanques
        #   2. 0 => Taules
        #   3. 1 => Guanya negres
        winnerMessage = {-1: 'Han guanyat les blanques', 0: 'Han quedat empat', 1: 'Han guanyat les negres'}
        winnerPlayer = 0

        # Guardem l'estat actual i l'afegim a la llista de visitats
        copyState = self.copyState(currentState)
        self.listVisitedSituations.append((False, copyState))

        # Fem un màxim de 50 moviments. Es per tenir l'execució acotada
        for i in range(50):
            currentState = self.getCurrentState()

            # Comencen jugant les blances
            if i % 2 == 0:
                # Mouen la fitxa utilitzan l'algorisme expectimax
                if not self.expectimaxWhite(currentState, depthWhite):
                    break
                # Si un cop moguda la peça el rei negre està en escat i mat, han guanyat
                if self.checkMateBk(currentState):
                    winnerPlayer = -1
                    break

            # Després juguen les negres
            else:
                # Mouen la fitxa utilitzan l'algorisme expectimax
                if not self.expectimaxBlack(currentState, depthBlack):
                    break

                # Si un cop moguda la peça el rei blanc està en escat i mat, han guanyat
                if self.checkMateWk(currentState):
                    winnerPlayer = 1
                    break

            # Print the board
            self.chess.board.print_board()

        # Print the final board
        self.chess.board.print_board()

        # Mostrem el missatge de qui ha guanyat
        return winnerMessage[winnerPlayer]

    # Moviment de les blanques
    def expectimaxWhite(self, state, depthMax):
        # Fem el moviment segons el màxim
        nextState = self.expMaxValueWhite(state, 0, depthMax)

        # Si no tenim estat següent no podem moure, checkmate
        if nextState == None:
            return False
        
        copyState = self.copyState(nextState)

        # Si encara no l'haviem visitat, l'afegim a la llista
        self.listVisitedSituations.append((True, copyState))

        # Fem el moviment
        movement = self.getMovement(state, nextState)
        self.chess.move(movement[0], movement[1])

        # Retornem que tot ha anat bé
        return True

    def expMaxValueWhite(self, currentState, depth, depthMax):
        # Mirem si el rei blanc està en escat i mat
        if self.checkMateWk(currentState):
            return -999999
        
        # Mirem si el rei blanc està en taules
        if self.taulesWk(currentState):
            return 0

        # Si hem arribat a la profunditat màxima
        if depth == depthMax:
            return self.heuristica(currentState, True)

        # Com volem trobar el màxim hem d'inicialitzar la variable amb un valor petit
        maximumValue = -float('inf')

        # Guardem l'últim estat que hem pogut visitar, es per si arribem al màxim de profunditat
        maximumState = None

        # Guardem les posicions de les blanques i les negres
        whiteState = self.getWhiteState(currentState)
        blackState = self.getBlackState(currentState)

        # Mirem si la torre negra esta viva
        brState = self.getPieceState(currentState, 8)
            
        # Iterem per tots els estats futurs
        for state in self.getListNextStatesW(whiteState):
            # Guardem les peces negres
            newBlackState = blackState.copy()

            # Si ens hem menjat la torre l'hem d'eliminar
            if brState != None and brState[0:2] == state[0][0:2]:
                newBlackState.remove(brState)

            # Actualiztem l'estat
            state = state + newBlackState

            # No podem moure el rei a una posició en perill
            if not self.isWatchedWk(state) and not self.isVisitedSituation(True,state):
                # Calculem el valor
                valueState = self.expValueWhite(state, depth + 1, depthMax)

                # En cas positiu actualitzem els valors
                if valueState > maximumValue:
                    maximumValue = valueState
                    maximumState = state

        # Si arribem al final de l'arbre retornem l'últim estat que hem vist
        if depth == 0:
            return maximumState
        
        return maximumValue

    def expValueWhite(self, currentState, depth, depthMax):
        # Mirem si el rei negre està en escat i mat
        if self.checkMateBk(currentState):
            return 1000000 - depth
        
        # Mirem si el rei negre està en taules
        if self.taulesBk(currentState):
            return 0

        # Si hem arribat a la profunditat màxima
        if depth == depthMax:
            return self.heuristica(currentState, True)

        # Guardem les posicions de les blanques i les negres
        whiteState = self.getWhiteState(currentState)
        blackState = self.getBlackState(currentState)

        # Mirem si la torre blanca esta viva
        wrState = self.getPieceState(currentState, 2)

        # Guardem una llista de valors
        values = []

        # Iterem per tots els estats futurs
        for state in self.getListNextStatesB(blackState):
            # Guardem les peces blanques
            newWhiteState = whiteState.copy()

            # Si ens hem menjat la torre l'hem d'eliminar
            if wrState != None and wrState[0:2] == state[0][0:2]:
                newWhiteState.remove(wrState)

            # Actualiztem l'estat
            state = state + newWhiteState
            
            # No podem moure el rei a una posició en perill
            if not self.isWatchedBk(state) and not self.isVisitedSituation(True,state):
                # Actualizem el valor i l'afegim a la llista
                values.append(self.expMaxValueWhite(state, depth + 1, depthMax))

        # Asignem les probabilitats i calculem el valor esperat
        return self.calculateValue(values)

    # Moviment de les negres
    def expectimaxBlack(self, state, depthMax):
        # Fem el moviment segons el màxim
        nextState = self.expMaxValueBlack(state, 0, depthMax)

        # Si no tenim estat següent no podem moure, checkmate
        if nextState == None:
            return False
        
        copyState = self.copyState(nextState)

        # Si encara no l'haviem visitat, l'afegim a la llista
        self.listVisitedSituations.append((True, copyState))
        
        # Fem el moviment
        movement = self.getMovement(state, nextState)
        self.chess.move(movement[0], movement[1])

        # Retornem que tot ha anat bé
        return True

    def expMaxValueBlack(self, currentState, depth, depthMax):
        # Mirem si el rei blanc està en escat i mat
        if self.checkMateBk(currentState):
            return -999999
        
        # Mirem si el rei negre està en taules
        if self.taulesBk(currentState):
            return 0

        # Si hem arribat a la profunditat màxima
        if depth == depthMax:
            return self.heuristica(currentState, False)

        # Com volem trobar el màxim hem d'inicialitzar la variable amb un valor petit
        maximumValue = -float('inf')

        # Guardem l'últim estat que hem pogut visitar, es per si arribem al màxim de profunditat
        maximumState = None

        # Guardem les posicions de les blanques i les negres
        whiteState = self.getWhiteState(currentState)
        blackState = self.getBlackState(currentState)

        # Mirem si la torre blanca esta viva
        wrState = self.getPieceState(currentState, 2)
            
        # Iterem per tots els estats futurs
        for state in self.getListNextStatesB(blackState):
            # Guardem les peces blanques
            newWhiteState = whiteState.copy()

            # Si ens hem menjat la torre l'hem d'eliminar
            if wrState != None and wrState[0:2] == state[0][0:2]:
                newWhiteState.remove(wrState)

            # Actualiztem l'estat
            state = state + newWhiteState

            # No podem moure el rei a una posició en perill
            if not self.isWatchedBk(state) and not self.isVisitedSituation(True,state):
                # Calculem el valor
                valueState = self.expValueBlack(state, depth + 1, depthMax)

                # En cas positiu actualitzem els valors
                if valueState > maximumValue:
                    maximumValue = valueState
                    maximumState = state

        # Si arribem al final de l'arbre retornem l'últim estat que hem vist
        if depth == 0:
            return maximumState
        
        return maximumValue

    def expValueBlack(self, currentState, depth, depthMax):
        # Mirem si el rei blanc està en escat i mat
        if self.checkMateWk(currentState):
            return 1000000 - depth
        
        # Mirem si el rei blanc està en taules
        if self.taulesWk(currentState):
            return 0

        # Si hem arribat a la profunditat màxima
        if depth == depthMax:
            return self.heuristica(currentState, False)
            
        # Guardem les posicions de les blanques i les negres
        whiteState = self.getWhiteState(currentState)
        blackState = self.getBlackState(currentState)

        # Mirem si la torre negra esta viva
        brState = self.getPieceState(currentState, 8)

        # Guardem una llista de valors
        values = []

        # Iterem per tots els estats futurs
        for state in self.getListNextStatesW(whiteState):
            # Guardem les peces negres
            newBlackState = blackState.copy()
            
            # Si ens hem menjat la torre l'hem d'eliminar
            if brState != None and brState[0:2] == state[0][0:2]:
                newBlackState.remove(brState)

            # Actualiztem l'estat
            state = state + newBlackState

            # No podem moure el rei a una posició en perill
            if not self.isWatchedWk(state) and not self.isVisitedSituation(True,state):
                # Actualizem el valor i l'afegim a la llista
                values.append(self.expMaxValueBlack(state, depth + 1, depthMax))

        # Asignem les probabilitats i calculem el valor esperat
        return self.calculateValue(values)

    #################################
    # Funcions auxiliars pel ExpectiMax
    #################################
    def mitjana(self, values):
        sum = 0
        N = len(values)
        for i in range(N):
            sum += values[i]

        return sum / N

    def desviacio(self, values, mitjana):
        sum = 0
        N = len(values)

        for i in range(N):
            sum += pow(values[i] - mitjana, 2)

        return pow(sum / N, 1 / 2)

    def calculateValue(self, values):
        
        if len(values) == 0:
            return 0
        mitjana = self.mitjana(values)
        desviacio = self.desviacio(values, mitjana)
        # If deviation is 0, we cannot standardize values, since they are all equal, thus probability willbe equiprobable
        if desviacio == 0:
            # We return another value
            return values[0]

        esperanca = 0
        sum = 0
        N = len(values)
        for i in range(N):
            #Normalize value, with mean and deviation - zcore
            normalizedValues = (values[i] - mitjana) / desviacio
            # make the values positive with function e^(-x), in which x is the standardized value
            positiveValue = pow(1 / math.e, normalizedValues)
            # Here we calculate the expected value, which in the end will be expected value/sum            
            # Our positiveValue/sum represent the probabilities for each value
            # The larger this value, the more likely
            esperanca += positiveValue * values[i]
            sum += positiveValue

        return esperanca / sum
     

    #################################
    # Prova 1: blacks using alpha-beta pruning and whites using minimax
    #################################
    def podaBlackMiniMaxWhite(self, depthWhite, depthBlack):
        currentState = self.getCurrentState()

        # A la variable color win guardarem qui ha guanyat, utilitzem:
        #   1. -1 => Guanya blanques
        #   2. 0 => Taules
        #   3. 1 => Guanya negres
        winnerMessage = {-1: 'Han guanyat les blanques', 0: 'Han quedat empat', 1: 'Han guanyat les negres'}
        winnerPlayer = 0

        # Si les blanques estan en escac i mat guanyen les negres
        if self.checkMateWk(currentState):
            return winnerMessage[1]
        
        # Si el rei negre està en escac i mat guanyen les blanques
        if self.checkMateBk(currentState):
            return winnerMessage[-1]

        # Guardem l'estat actual i l'afegim a la llista de visitats
        copyState = self.copyState(currentState)
        self.listVisitedSituations.append((False, copyState))

        # Fem un màxim de 50 moviments. Es per tenir l'execució acotada
        for i in range(50):
            currentState = self.getCurrentState()

            # Comencen jugant les blances
            if i % 2 == 0:
                # Mouen la fitxa utilitzan l'algorisme minimax
                if not self.minimaxWhite(currentState, depthWhite):
                    break
                # Si un cop moguda la peça el rei negre està en escat i mat, han guanyat
                if self.checkMateBk(currentState):
                    winnerPlayer = -1
                    break
                elif self.taulesBk(currentState):
                    winnerPlayer = 0
                    break

            # Després juguen les negres
            else:
                # Mouen la fitxa utilitzan l'algorisme alphabetapoda
                if not self.podaBlack(currentState, depthBlack):
                    break

                # Si un cop moguda la peça el rei blanc està en escat i mat, han guanyat
                if self.checkMateWk(currentState):
                    winnerPlayer = 1
                    break
                elif self.taulesWk(currentState):
                    winnerPlayer = 0
                    break
            
            # Print the board
            self.chess.board.print_board()

        # Print the final board
        self.chess.board.print_board()

        # Mostrem el missatge de qui ha guanyat
        return winnerMessage[winnerPlayer]

    #################################
    # Prova 2: blacks using alpha-beta pruning and whites using expectimax
    #################################
    def podaBlackExpectiMaxWhite(self, depthWhite, depthBlack):
        currentState = self.getCurrentState()

        # A la variable color win guardarem qui ha guanyat, utilitzem:
        #   1. -1 => Guanya blanques
        #   2. 0 => Taules
        #   3. 1 => Guanya negres
        winnerMessage = {-1: 'Han guanyat les blanques', 0: 'Han quedat empat', 1: 'Han guanyat les negres'}
        winnerPlayer = 0

        # Si les blanques estan en escac i mat guanyen les negres
        if self.checkMateWk(currentState):
            return winnerMessage[1]
        
        # Si el rei negre està en escac i mat guanyen les blanques
        if self.checkMateBk(currentState):
            return winnerMessage[-1]

        # Guardem l'estat actual i l'afegim a la llista de visitats
        copyState = self.copyState(currentState)
        self.listVisitedSituations.append((False, copyState))

        # Fem un màxim de 50 moviments. Es per tenir l'execució acotada
        for i in range(50):
            currentState = self.getCurrentState()

            # Comencen jugant les blances
            if i % 2 == 0:
                # Mouen la fitxa utilitzan l'algorisme expectimax
                if not self.expectimaxWhite(currentState, depthWhite):
                    break
                # Si un cop moguda la peça el rei negre està en escat i mat, han guanyat
                if self.checkMateBk(currentState):
                    winnerPlayer = -1
                    break
                elif self.taulesBk(currentState):
                    winnerPlayer = 0
                    break

            # Després juguen les negres
            else:
                # Mouen la fitxa utilitzan l'algorisme alphabetapoda
                if not self.podaBlack(currentState, depthBlack):
                    break

                # Si un cop moguda la peça el rei blanc està en escat i mat, han guanyat
                if self.checkMateWk(currentState):
                    winnerPlayer = 1
                    break
                elif self.taulesWk(currentState):
                    winnerPlayer = 0
                    break
            
            # Print the board
            self.chess.board.print_board()

        # Print the final board
        self.chess.board.print_board()

        # Mostrem el missatge de qui ha guanyat
        return winnerMessage[winnerPlayer]

#################################
# Plot 1: blacks using alpha-beta pruning and whites using expectimax
#################################
def plotWhiteMiniMaxBlackMiniMax(max_iter, max_depth):
    results = []
    for depthWhite in range(1,max_depth+1):
        for depthBlack in range(1,max_depth+1):
            win = 0
            for i in range(1,max_iter+1):
                start_time = time.time()

                # intiialize board
                TA = np.zeros((8, 8))

                # Initial board configuration
                TA[7][0] = 2
                TA[7][4] = 6
                TA[0][7] = 8
                TA[0][4] = 12

                # initialise board
                print("stating AI chess... ")
                aichess = Aichess(TA, True)

                print("printing board")
                aichess.chess.boardSim.print_board()

                winner = aichess.podaBlackExpectiMaxWhite(depthWhite,depthBlack)
                    
                time_elapsed = (time.time() - start_time)
                    
                if(winner == 'Han guanyat les blanques'): 
                    win += 1/max_iter

                result = (depthWhite, depthBlack, win, time_elapsed)

            results.append(result)
            print(result)

    return results

if __name__ == "__main__":
    #   if len(sys.argv) < 2:
    #       sys.exit(usage())

    question = 'Qué algoritmo quieres utilizar? \n 1. MiniMax \n 2. AlphaBeta poda \n 3. ExpectiMax \n 4. Blanques amb minimax i negres amb poda alfa-beta \n 5. Blanques amb expectimax i negres amb poda alfa-beta \n 6. Time test \n'
    option = input(question)

    start_time = time.time()

    # intiialize board
    TA = np.zeros((8, 8))

    # Initial board configuration
    TA[7][0] = 2
    TA[7][4] = 6
    TA[0][7] = 8
    TA[0][4] = 12

    if option == '1':
        # initialise board
        print("stating AI chess... ")
        aichess = Aichess(TA, True)

        print("printing board")
        aichess.chess.boardSim.print_board()

        winner = aichess.minimaxGame(2,2)
        print(winner)

    elif option == '2':
        # initialise board
        print("stating AI chess... ")
        aichess = Aichess(TA, True)

        print("printing board")
        aichess.chess.boardSim.print_board()

        winner = aichess.alphaBetaPoda(2,2)
        print(winner)

    elif option == '3':
        # initialise board
        print("stating AI chess... ")
        aichess = Aichess(TA, True)

        print("printing board")
        aichess.chess.boardSim.print_board()

        winner = aichess.expectimax(2,2)
        print(winner)

    elif option == '4':
        # initialise board
        print("stating AI chess... ")
        aichess = Aichess(TA, True)

        print("printing board")
        aichess.chess.boardSim.print_board()

        winner = aichess.podaBlackMiniMaxWhite(2,2)
        print(winner)

    elif option == '5':
        # initialise board
        print("stating AI chess... ")
        aichess = Aichess(TA, True)

        print("printing board")
        aichess.chess.boardSim.print_board()

        winner = aichess.podaBlackExpectiMaxWhite(2,2)
        print(winner)

    elif option == '6':
        result = plotWhiteMiniMaxBlackMiniMax(2,4)
        print(result)

    else:
        print('La opción que has puesto no es válida')

    print("--- %s seconds ---" % (time.time() - start_time))