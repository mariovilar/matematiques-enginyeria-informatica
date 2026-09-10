from math import e
import numpy as np 
import time

def left(x,y):
    if y == 0:
        return (x,y)
    return (x,y-1) 

def right(x,y):
    if y == 3:
        return (x,y)
    return (x,y+1)

def up(x,y):
    if x == 0:
        return (x,y)
    return (x-1,y)

def down(x,y):
    if x == 2:
        return (x,y)
    return (x+1,y)

def move(position, move):
    x,y = position
    if move == "L":
        if position != (1, 2):
            return left(x,y)

    elif move == "R":
        if position != (1, 0):
            return right(x,y)

    elif move == "U":
        if position != (2, 1):
            return up(x,y)

    elif move == "D":
        if position != (0, 1):
            return down(x,y)
    
    return False

def setEpsilon(epsilon, iteracion, N):
    # Modificamos el valor de epsilon según las iteraciones que llevamos
    if(iteracion < 1/3*N):
        return epsilon/1
    elif(iteracion < 1/2*N):
        return epsilon/1.5
    elif(iteracion < 3/4*N):
        return epsilon/2
    else:
        return epsilon/2.5

def epsilonGreedy(qmatrix, actions, position, epsilon):
    idx = -1

    # Generamos un movimiento random
    if np.random.rand() <= epsilon:    
        idx = np.random.choice(len(actions))
    # Obtenemos el movimiento con mayor Q
    else:
        idx = np.argmax(qmatrix[position])
    
    return idx

def getNextAction(actions, obey, i):
    idx = i

    # Hacemos un movimiento random
    if np.random.rand() >= obey:
        idx = np.random.choice(len(actions))

    return (actions[idx], idx)

def Q_learning(qmatrix, rmatrix, actions, start_position, target, alpha, gamma, epsilon, obey):
    # Guardamos la posición final
    position = start_position

    # Guardamos el total de iteraciones
    N = 1000000

    first_print = np.random.randint(0, N/2 + 1)
    second_print = np.random.randint(N/2, N) 

    # Definimos los valores para la convergencia
    conv_seen = False
    conv_value = 1/N

    for pp in range(N):

        # Si hemos llegado al final volvemos a empezar
        if position == target:
            position = start_position
            continue
        
        # El valor de epsilon depende del número de iteraciones hechas
        epsilon = setEpsilon(epsilon, pp, N)

        # Obtenemos el siguiente movimiento utilizando episilon greedy
        i = epsilonGreedy(qmatrix, actions, position, epsilon)

        action, i = getNextAction(actions, obey, i)

        # Movemos la ficha
        nextPos = move(position, action)
        
        # En caso de ser un movimiento válido lo ejecutamos
        if nextPos:
            x,y = nextPos

            # Obtenemos la recompensa
            reward = rmatrix[x,y]
            
            # Obtenemos el Q-value
            actQ = qmatrix[position[0],position[1],i]
            maxQVal = -float('inf')
                
            # Buscamos el máximo habiendo aplicado el movimiento
            for j in range(len(qmatrix[x,y])):
                if qmatrix[x,y,j] > maxQVal:
                    maxQVal = qmatrix[x,y,j]
            
            # Reasignamos valores
            qmatrix[position[0],position[1],i] = actQ + alpha*(reward+gamma*maxQVal-actQ)
            position = (x,y)

            # Utilizamos el valor de delta para ver cuando converge
            delta = reward - (actQ - maxQVal)
            
            if((0 < delta < conv_value) and not conv_seen):
                conv_seen = True
                print('El algoritmo converge en la iteración ' + str(pp+1))

        # En caso de tener el primer o dos intermedios los mostramos
        if pp == 0 or pp == first_print or pp == second_print:
            print('\nITERACIÓN: ' + str(pp+1))
            showQMatrix(qmatrix)

    return qmatrix

def qMatrixPrint(matrix):
    x,y,z = matrix.shape
    # Per a persones amb dificultats imaginant les dimensions
    for i in range(x):
        app = []
        for j in range(y):
            app.append(np.mean(matrix[i,j]))
        print(app)

def printHBar():
    print('-'*5*13)     

def printOne(row):
    for element in row:
        print('|' + ' '*5 + element + ' '*5, end='') 

    print('|')

def printTwo(row):
    for i in range(0, len(row), 2):
        print('|' + row[i] + ' '*5 + row[i+1], end='') 

    print('|')

def showQMatrix(matrix):
    x,y,z = matrix.shape

    new_matrix = []

    # Trasponemos la matriz
    for i in range(x):
        prov = []
        for j in range(y):
            provisional = ["{:05.2f}".format(row[j]) if row[j] != 100 else '100.0' for row in matrix[i]]

            prov.append(provisional)

        new_matrix.append(prov)

    mat = []

    # Agrupamos la fila de left y right
    for i in range(x):
        prov = []
        for j in range(z):
            aux = []
            
            if(j == 0):
                for k in range(y):
                    aux.append(new_matrix[i][j][k])
                    aux.append(new_matrix[i][j+1][k])
            elif(j == 1):
                continue
            else:  
                aux = new_matrix[i][j]

            prov.append(aux)
        mat.append(prov)

    # Mostramos como hace falta
    for i in range(x):
        printHBar()
        printOne(mat[i][1])
        printTwo(mat[i][0])
        printOne(mat[i][2])
    
    printHBar()

def setStochasticParams():
    # Valor gamma para el descuento en estados muy lejanos
    gamma = 0.9

    # Valor de alpha para tener en cuenta el aprendizaje anterior
    alpha = 0.9

    # Valor de epsilon para definir la cantidad de iteraciones aleatorias
    epsilon = 0.8
    
    # Porcentaje (tanto por uno) de determinación en las acciones
    obey = 0.99

    return gamma, alpha, epsilon, obey

def setDeterministicParams():
    # Valor gamma para el descuento en estados muy lejanos
    gamma = 0.9

    # Valor de alpha para tener en cuenta el aprendizaje anterior
    alpha = 0.9

    # Valor de epsilon para definir la cantidad de iteraciones aleatorias
    epsilon = 0.8
    
    # Porcentaje (tanto por uno) de determinación en las acciones
    obey = 1

    return gamma, alpha, epsilon, obey

if __name__ == "__main__":
    star_time = time.time()

    # Matriz de valores Q. Se inicializa a 0 y se actualiza con el calculo de Q
    qmatrix = np.zeros((3,4,4)) # [L,R,U,D]

    # Matriz de recompensas. Da la recompensa de llegar a cada estado.
    #rmatrix = np.matrix([[-1,-1,-1,100], [-1,-float('inf'),-1,-1], [-1,-1,-1,-1]])
    rmatrix = np.matrix([[-3,-2,-1,100], [-4,-float('inf'),-2,-1], [-5,-4,-3,-2]])

    gamma, alpha, epsilon, obey = setStochasticParams()

    # Acciones que se puede hacer
    actions = ["L", "R", "U", "D"]

    # Posiciones inicial y objetivo
    start_position = (2,0)
    target = (0,3)

    try:
        if(0 <= gamma <= 1 and 0 <= alpha <= 1 and 0 <= epsilon <= 1 and 0 <= obey <= 1):
            mat = Q_learning(qmatrix, rmatrix, actions, start_position, target, alpha, gamma, epsilon, obey)
            print('\nRESULTADO FINAL:')
            showQMatrix(mat)            
        else:
            raise ValueError('Algún parámetro no es válido')
    except ValueError as e:
        print(f"{e}")

    finish_time = time.time()

    print('Ha tardado ' + "{:.2f}".format(finish_time-star_time) + ' segundos')