from multiprocessing import current_process
from tabnanny import check
import time
import chess
import board
import numpy as np
import copy
import ast
from itertools import permutations


class Aichess():
    def __init__(self, TA, myinit=True):

        if myinit:
            self.chess = chess.Chess(TA, True)
        else:
            self.chess = chess.Chess([], False)

        self.listNextStates = []
        self.listVisitedStates = []
        self.listVisitedSituations = []
        self.pathToTarget = []

        self.qTable = {}
        
        self.currentStateW = self.chess.boardSim.currentStateW
        self.depthMax = 8
        self.checkMate = False

    # Copy two states
    def copyState(self, state):
        copyState = []
        for piece in state:
            copyState.append(piece.copy())
        return copyState
    
    # Get the states of the white
    def getCurrentStateW(self):
        return self.currentStateW

    # Returns the next state of whites
    def getListNextStatesW(self, myState):

        self.chess.boardSim.getListNextStatesW(myState)
        listNextStates = self.chess.boardSim.listNextStates.copy()

        lst = []

        for state in listNextStates:
            if (not (([0,3,2] in state) or ([0,4,2] in state) or ([0,5,2] in state) or
                 ([1,3,2] in state) or ([1,4,2] in state) or ([1,5,2] in state) or
                 ([0,3,6] in state) or ([0,4,6] in state) or ([0,5,6] in state) or
                 ([1,3,6] in state) or ([1,4,6] in state) or ([1,5,6] in state))):
                lst.append(state)

        
        return lst
    
    # Create a new borad
    def newBoardSim(self, listStates):
        # Create a new boardSim
        TA = np.zeros((8, 8))
        for state in listStates:
            TA[state[0]][state[1]] = state[2]

        self.chess.newBoardSim(TA)

    # Get the movement done
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
        
    # Compute the maximum Q value
    def maxQValue(self, currentState):
        maxQ = -1000000
        # No trobem l'estat
        if str(currentState) not in self.qTable.keys():
            return 0
        currentDict = self.qTable[str(currentState)]
        # Per totes les possibles accions agafem la de màxim Q-value
        for nextString in currentDict.keys():
            maxQ = max(maxQ, currentDict[nextString])
        return maxQ
    
    # Move to a random state
    def randomState(self, currentState, listStates):
        # A random number between zero and the length of the list of states
        n = np.random.choice(len(listStates))
        nextState = listStates[n]

        # State has been visited?
        currentDict = self.qTable[str(currentState)]
        if str(nextState) not in currentDict.keys():
            currentDict[str(nextState)] = 0

        return nextState
    
    # Compute the next move
    def getNextMovement(self, epsilon, listStates, currentState, obey):
        
        x = np.random.rand()

        # Exploration with epsilon probability
        if x < epsilon:
            return self.randomState(currentState, listStates)
        
        # Exploration with 1-epsilon probabilty
        else:
            maxValue = -1000000
            maxState = None
            currentDict = self.qTable[str(currentState)]
            visitedStates = currentDict.keys()

            if(np.random.rand() > obey):
                return self.randomState(currentState, listStates)

            # First loop to find the maximum value
            for state in listStates:
                # If the state has not been visited, we initialize it
                if str(state) not in visitedStates:
                    currentDict[str(state)] = 0
                qValue = currentDict[str(state)]

                if qValue > maxValue:
                    maxState = state
                    maxValue = qValue
            
            return maxState
    
    # Reconstruct the path done
    def reconstructPath(self, initialState):

        currentState = initialState
        checkMate = False
        self.chess.board.print_board()

        path = [initialState]

        while not checkMate:

            maxQ = -1000000
            maxState = None
            currentDict = self.qTable[str(currentState)]

            for stateString in currentDict.keys():

                qValue = currentDict[stateString]
                if maxQ < qValue:
                    maxState = ast.literal_eval(stateString)
                    maxQ = qValue

            path.append(maxState)
            movement = self.getMovement(currentState, maxState)

            # We make the corresponding move
            self.chess.move(movement[0],movement[1])
            self.chess.board.print_board()

            currentState = maxState

            # When check mate is achieved, the execution ends
            if self.isCheckMate(currentState):
                checkMate = True

        print("Path sequence: ", path)

    # Q-learning algorithm
    def Qlearning(self, epsilon, gamma, alpha, obey):

        currentState = self.getCurrentStateW()
        initialState = self.copyState(currentState)
        self.qTable[str(currentState)] = {}

        N = 5
        deltaRange = 0.000001

        convergencia = 0
        iterations = 0

        while convergencia < N:

            checkMate = False
            movements, error = 0, 0

            while not checkMate:
                 # If we haven't visited the state, we add it to the Q-table
                if str(currentState) not in self.qTable.keys():
                    self.qTable[str(currentState)] = {}
                
                listNextStates = self.getListNextStatesW(currentState)

                nextState = self.getNextMovement(epsilon, listNextStates, currentState, obey)
                qValue = self.qTable[str(currentState)][str(nextState)]
                
                reward = self.h(nextState+[[0,4,12]])

                if self.isCheckMate(nextState):
                    qValue = 100
                    checkMate = True
                    reward = 100
                else:
                    reward = -reward
                    maxQVal = self.maxQValue(nextState)
                    prod = reward + gamma*maxQVal - qValue
                    error += prod
                    qValue = qValue + alpha*prod

                self.qTable[str(currentState)][str(nextState)] = qValue

                if not checkMate:
                    # To the actual position
                    self.newBoardSim(nextState+[[0,4,12]])
                    currentState = nextState
                    movements += 1
            
            # Mirem si convergeix
            if movements != 0:
                if -deltaRange < error/movements < deltaRange:
                    convergencia += 1
                else:
                    convergencia = 0
                
            if convergencia in [N-1, N-2]:
                print("Iteration", iterations, "with error", error/movements, "and total movements", movements)
            
            iterations += 1

            self.newBoardSim(initialState+[[0,4,12]])
            currentState = self.copyState(initialState)

        print("Convergeixen", convergencia, "del total de", iterations, "iteracions")
        self.reconstructPath(initialState)

    # Check if is checkMate
    def isCheckMate(self, mystate):
        # Check mate for exercise 1 (the black king is fixed at position [0,4]
        # we put the possible states where check mate occurs
        listCheckMateStates = [[[0,0,2],[2,4,6]],[[0,1,2],[2,4,6]],[[0,2,2],[2,4,6]],[[0,6,2],[2,4,6]],[[0,7,2],[2,4,6]]]

        # We look at all state permutations and see if they match the CheckMates list
        for permState in list(permutations(mystate)):
            if list(permState) in listCheckMateStates:
                return True

        return False


    # Heuristics
    def h(self, state):
        if state[0][2] == 2:
            posicioRei = state[1]
            posicioTorre = state[0]
        else:
            posicioRei = state[0]
            posicioTorre = state[1]
        # With the king we wish to reach configuration (2,4), calculate Manhattan distance
        fila = abs(posicioRei[0] - 2)
        columna = abs(posicioRei[1]-4)
        # Pick the minimum for the row and column, this is when the king has to move in diagonal
        # We calculate the difference between row an colum, to calculate the remaining movements
        # which it shoudl go going straight        
        hRei = min(fila, columna) + abs(fila-columna)
        # with the tower we have 3 different cases
        if posicioTorre[0] == 0 and (posicioTorre[1] < 3 or posicioTorre[1] > 5):
            hTorre = 0
        elif posicioTorre[0] != 0 and posicioTorre[1] >= 3 and posicioTorre[1] <= 5:
            hTorre = 2
        else:
            hTorre = 1
        # In our case, the heuristics is the real cost of movements
        return hRei + hTorre
    
# Set the params for the execution
def setParams():
    # Gamma value for further states
    gamma = 0.9

    # Alpha value for learning
    alpha = 0.9

    # Epsilon value for exporation/expotation
    epsilon = 0.3
    
    # Percentage for randomness
    obey = 1

    return gamma, alpha, epsilon, obey

if __name__ == "__main__":
    # Initialize board
    TA = np.zeros((8, 8))

    # Initial board configuration
    TA[7][0] = 2
    TA[7][4] = 6
    TA[0][4] = 12

    print("starting AI chess... ")
    aichess = Aichess(TA, True)

    gamma, alpha, epsilon, obey = setParams()

    start_time = time.time()
    qLearning = aichess.Qlearning(epsilon, gamma, alpha, obey)

    print("--- It took %s seconds ---" % "{:.2f}".format(time.time() - start_time))