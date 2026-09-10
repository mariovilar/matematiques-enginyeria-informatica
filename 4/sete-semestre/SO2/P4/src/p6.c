#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/time.h>
#include <pthread.h>

#define MAXCHAR 500

#define LEN_CODE_AIRPORT 3
#define STR_CODE_AIRPORT (LEN_CODE_AIRPORT+1) // Incluimos el caracter de final de palabra '\0'
#define NUM_AIRPORTS 303

#define COL_ORIGIN_AIRPORT 17
#define COL_DESTINATION_AIRPORT 18

#define NUM_THREADS 2
#define MAX_LINES 10000

// Definimos el mutex, a program object that prevents multiple threads from accessing the same shared resource simultaneously
// LO HAREMOS ESTÁTICAMENTE COMO EN LOS PDFs DE LUIS GARRIDO
pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;

// Parámetros que pasamos al thread function
struct params {
  FILE *fp;
  int lines;
  int **num_flights;
  char **airports;
};

/**
 * Reserva espacio para una matriz de tamaño nrow x ncol,
 * donde cada elemento de la matriz tiene size bytes
 */

void **malloc_matrix(int nrow, int ncol, size_t size)
{
  int i;

  void **ptr;

  ptr = calloc(nrow,sizeof(void *));
  for(i = 0; i < nrow; i++)
    ptr[i] = calloc(ncol,size);

  return ptr;
}

/**
 * Libera una matriz de tamaño con nrow filas. Utilizar
 * la funcion malloc_matrix para reservar la memoria 
 * asociada.
 */

void free_matrix(void **matrix, int nrow)
{
  int i;

  for(i = 0; i < nrow; i++)
    free(matrix[i]);
  free(matrix);
}

/**
 * Leer el fichero fname que contiene los codigos
 * IATA (3 caracteres) de los aeropuertos a analizar.
 * En total hay NUM_AIRPORTS a leer, un valor prefijado
 * (para simplificar el código). Los codigos de los
 * aeropuertos se alacenan en la matriz airports, una
 * matriz cuya memoria ha sido previamente reservada.
 */

void read_airports(char **airports, char *fname) 
{
  int i;
  char line[MAXCHAR];

  FILE *fp;

  /*
   * eow es el caracter de fin de palabra
   */
  char eow = '\0';

  fp = fopen(fname, "r");
  if (!fp) {
    printf("ERROR: could not open file '%s'\n", fname);
    exit(1);
  }

  i = 0;
  while (i < NUM_AIRPORTS)
  {
    fgets(line, 100, fp);
    line[3] = eow; 

    /* Copiamos los datos al vector */
    strcpy(airports[i], line);

    i++;
  }

  fclose(fp);
}

/**
 * Dada la matriz de con los codigos de los aeropuertos,
 * así como un código de aeropuerto, esta función retorna
 * la fila asociada al aeropuerto.
 */

int get_index_airport(char *code, char **airports)
{
  int i;

  for(i = 0; i < NUM_AIRPORTS; i++) 
    if (strcmp(code, airports[i]) == 0)
      return i;

  return -1;
}


/**
 * Dada la matriz num_flights, se imprimen por pantalla el
 * numero de destinos diferentes que tiene cada aeropuerto.
 */

void print_num_flights_summary(int **num_flights, char **airports)
{
  int i, j, num;

  for(i = 0; i < NUM_AIRPORTS; i++) 
  {
    num = 0;

    for(j = 0; j < NUM_AIRPORTS; j++)
    {
      if (num_flights[i][j] > 0)
        num++;
    }

    printf("Origin: %s -- Number of different destinations: %d\n", airports[i], num);
  }
}

/**
 * Esta funcion se utiliza para extraer informacion del fichero CSV que
 * contiene informacion sobre los vuelos. En particular, dada una linea
 * leida de fichero, la funcion extra el origen y destino de los vuelos.
 */

int extract_fields_airport(char *origin, char *destination, char *line) 
{
  /*Recorre la linea por caracteres*/
  char caracter;
  /* i sirve para recorrer la linea
   * iterator es para copiar el substring de la linea a char
   * coma_count es el contador de comas
   */
  int i, iterator, coma_count;
  /* start indica donde empieza el substring a copiar
   * end indica donde termina el substring a copiar
   * len indica la longitud del substring
   */
  int start, end, len;
  /* invalid nos permite saber si todos los campos son correctos
   * 1 hay error, 0 no hay error 
   */
  int invalid = 0;
  /* found se utiliza para saber si hemos encontrado los dos campos:
   * origen y destino
   */
  int found = 0;
  /*
   * eow es el caracter de fin de palabra
   */
  char eow = '\0';
  /*
   * contenedor del substring a copiar
   */
  char word[STR_CODE_AIRPORT];
  /*
   * Inicializamos los valores de las variables
   */
  start = 0;
  end = -1;
  i = 0;
  coma_count = 0;
  /*
   * Empezamos a contar comas
   */
  do {
    caracter = line[i++];
    if (caracter == ',') {
      coma_count ++;
      /*
       * Cogemos el valor de end
       */
      end = i;
      /*
       * Si es uno de los campos que queremos procedemos a copiar el substring
       */
      if (coma_count ==  COL_ORIGIN_AIRPORT || coma_count == COL_DESTINATION_AIRPORT) {
        /*
         * Calculamos la longitud, si es mayor que 1 es que tenemos 
         * algo que copiar
         */
        len = end - start;

        if (len > 1) {

          if (len > STR_CODE_AIRPORT) {
            printf("ERROR len code airport\n");
            exit(1);
          }

          /*
           * Copiamos el substring
           */
          for(iterator = start; iterator < end-1; iterator ++){
            word[iterator-start] = line[iterator];
          }
          /*
           * Introducimos el caracter de fin de palabra
           */
          word[iterator-start] = eow;
          /*
           * Comprobamos que el campo no sea NA (Not Available) 
           */
          if (strcmp("NA", word) == 0)
            invalid = 1;
          else {
            switch (coma_count) {
              case COL_ORIGIN_AIRPORT:
                strcpy(origin, word);
                found++;
                break;
              case COL_DESTINATION_AIRPORT:
                strcpy(destination, word);
                found++;
                break;
              default:
                printf("ERROR in coma_count\n");
                exit(1);
            }
          }

        } else {
          /*
           * Si el campo esta vacio invalidamos la linea entera 
           */

          invalid = 1;
        }
      }
      start = end;
    }
  } while (caracter && invalid == 0);

  if (found != 2)
    invalid = 1;

  return invalid;
}

/**
 * Función para gestionar nuestros hilos. Va a reciclar bastante código que
 * antes se encontraba en read_airports_data, ya que vamos a leer estos datos
 * con tantos hilos como veamos conveniente.
 */

void *thread_function(void *arg) {
  char origin[STR_CODE_AIRPORT], destination[STR_CODE_AIRPORT];
  int invalid, index_origin, index_destination;
  struct params *data = (struct params *)arg;
  // Creamos la matriz de datos con el malloc
  char **matrix = (char **) malloc_matrix(data->lines, MAXCHAR, sizeof(char));
  char *result;
  int iter = 0;

  do {
    iter = 0;
    pthread_mutex_lock(&mutex);
    // Cuidado, entramos en zona de exclusión mútua, cargando datos a la matriz desde el fichero
    while ((iter < data->lines)) {
      result = fgets(matrix[iter], MAXCHAR, data->fp);
      if(result == NULL) {
        break;
      }
      iter++;
    }
    pthread_mutex_unlock(&mutex);

    for (int i = 0; i<data->lines; i++) {
      invalid = extract_fields_airport(origin, destination, matrix[i]);

      if (!invalid) {
        index_origin = get_index_airport(origin, data->airports);
        index_destination = get_index_airport(destination, data->airports);

        if ((index_origin >= 0) && (index_destination >= 0)) {
          // Cuidado, entramos en zona de exclusión mútua, estamos tocando num_flights compartida
          pthread_mutex_lock(&mutex);
          data->num_flights[index_origin][index_destination]++;
          pthread_mutex_unlock(&mutex);
        }
      }
    }
  }  while(iter == data->lines);

  free_matrix((void **) matrix, data->lines);
  return ((void *)0);
}

/**
 * Dado un fichero CSV que contiene informacion entre multiples aeropuertos,
 * esta funcion lee cada linea del fichero y actualiza la matriz num_flights
 * para saber cuantos vuelos hay entre cada cuidad origen y destino.
 */ 

void read_airports_data(int **num_flights, char **airports, char *fname, int num_lines, int num_threads) 
{
  char line[MAXCHAR];
  FILE *fp;
  pthread_t threads[num_threads];
  int response = 0;
  int lines = num_lines;  

  fp = fopen(fname, "r");
  if (!fp) {
    printf("ERROR: could not open '%s'\n", fname);
    exit(1);
  }

  /* Leemos la cabecera del fichero */
  fgets(line, MAXCHAR, fp);

  // Parámetros para los hilos
  struct params data = {fp, lines, num_flights, airports};

  // Creamos los hilos
  for(int i = 0; i < num_threads; i++) {
    response = pthread_create(&threads[i], NULL, thread_function, (void*) &data);
    if(response != 0) {
      printf("ERROR: could not create a thread");
      exit(1);
    }
  }

  response = 0;

  // Esperamos a recibir una respuesta de los hilos que estamos ejecutando
  for(int j = 0; j < num_threads; j++) {
    // Cuidado con el primer argumento, en el create es puntero, aquí no
    response = pthread_join(threads[j], NULL);
    if(response != 0) {
      printf("ERROR: could not join a thread");
      exit(1);
    }
  }
  fclose(fp);
}

/**
 * Esta es la funcion principal que realiza los siguientes procedimientos
 * a) Lee los codigos IATA del fichero de aeropuertos
 * b) Lee la informacion de los vuelos entre diferentes aeropuertos y
 *    actualiza la matriz num_flights correspondiente.
 * c) Se imprime para cada aeropuerto origen cuantos destinos diferentes
 *    hay.
 * d) Se imprime por pantalla lo que ha tardado el programa para realizar
 *    todas estas tareas.
 */

int main(int argc, char **argv)
{
  char **airports;
  int **num_flights;

  int fourth_argument;
  int fifth_argument;

  // inválido
  if (argc < 3) {
    printf("%s <airport.csv> <flights.csv> (<threads> <lines>)\n", argv[0]);
    exit(1);
  }
  // estamos ejecutando sin personalizar líneas e hilos
  else if(argc >= 3 && argc < 5) {
    fourth_argument = NUM_THREADS;
    fifth_argument = MAX_LINES;
  }
  // en nuestro script python pasamos num hilos y num líneas por parámetro
  else {
    fourth_argument = atoi(argv[3]);
    fifth_argument = atoi(argv[4]);
  }

  struct timeval tv1, tv2;

  // Tiempo cronologico 
  gettimeofday(&tv1, NULL);

  // Reserva espacio para las matrices
  airports    = (char **) malloc_matrix(NUM_AIRPORTS, STR_CODE_AIRPORT, sizeof(char));
  num_flights = (int **) malloc_matrix(NUM_AIRPORTS, NUM_AIRPORTS, sizeof(int));

  // Lee los codigos de los aeropuertos 
  read_airports(airports, argv[1]);

  // Lee los datos de los vuelos
  read_airports_data(num_flights, airports, argv[2], fourth_argument, fifth_argument);

  // Imprime un resumen de la tabla
  print_num_flights_summary(num_flights, airports);

  // Libera espacio
  free_matrix((void **) airports, NUM_AIRPORTS);
  free_matrix((void **) num_flights, NUM_AIRPORTS);

  // Tiempo cronologico 
  gettimeofday(&tv2, NULL);

  // Tiempo para la creacion del arbol 
  printf("Tiempo para procesar el fichero: %f segundos\n",
      (double) (tv2.tv_usec - tv1.tv_usec) / 1000000 +
      (double) (tv2.tv_sec - tv1.tv_sec));

  return 0;
}
