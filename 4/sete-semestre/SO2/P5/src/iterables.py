import subprocess
import sys
import numpy as np

def pmatrix(matrix):

    h,w = matrix.shape

    latex_str = r'\begin{pmatrix}'
    for i in range(h):
        for j in range(w):
            x, y = matrix[i][j]
            latex_str += f'({x}, {y})'
            if j != w - 1:
                latex_str += ' & '
        latex_str += r' \\ '

    latex_str += r'\end{pmatrix}'
    return latex_str

def h(max_iterations, num_bloques, num_hilos):

    w, h = len(num_bloques), len(num_hilos)
    matrix = np.zeros((h,w),dtype=object)

    for i in range(h):
        for j in range(w):

            time_array = np.array([]) 

            for k in range(max_iterations):
                command = ["./"+sys.argv[1],sys.argv[2],sys.argv[3], str(num_hilos[i]), str(num_bloques[j])]
                output = subprocess.check_output(command, stderr=subprocess.STDOUT)
                time = output.decode('utf-8').split(" ")[5] # obtenemos el tiempo 
                time_array = np.append(time_array,float(time))

            mean = round(np.mean(time_array),4)
            sdev = round(np.std(time_array),4)
            matrix[i,j] = (mean,sdev)

    return matrix

def compare(matrix):
    h,w = matrix.shape
    cThreads, cBlocks = 0,0
    for i in range(h):
        for j in range(i,w):
            if(matrix[i,j][0]-matrix[j,i][0]) > 0:
                cThreads += 1
            elif(matrix[i,j][0]-matrix[j,i][0]) < 0:
                cBlocks += 1
            elif (matrix[i,j][0]-matrix[j,i][0]) == 0 and i != j:
                cThreads += 1
                cBlocks += 1
    
    return(cThreads, cBlocks)

if __name__ == "__main__":

    if len(sys.argv) < 3:
        print("Usage: python3 iterables.py <executable> <airport.csv> <flights.csv> (<num_threads> <num_blocks>)")
        exit

    elif len(sys.argv) >= 3 and len(sys.argv) < 5:
        num_hilos = [1,2,3,4,6,8]
        num_bloques = [1,2,3,4,6,8]
        max_iterations = 3

        matrix = h(max_iterations,num_bloques,num_hilos)
        threads, blocks = compare(matrix)
        print("Lower time threads:", threads, ", lowest time blocks:", blocks)
        print(pmatrix(matrix))

    # hem especificat tot, fils i línies totalment determinades, execució normal
    else:
        command = ["./"+sys.argv[1]]
        for i in range(2,6):
            command.append(sys.argv[i])
        output = subprocess.check_output(command, stderr=subprocess.STDOUT)
        print(output.decode('utf-8'))