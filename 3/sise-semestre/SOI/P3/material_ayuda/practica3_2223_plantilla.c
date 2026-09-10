#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_LEN 100

typedef struct {
  int id;
  char nom[MAX_LEN];
} opcio;

typedef struct {
  int id1;
  int id2;
  char descripcion[MAX_LEN];
} reglas;

int leer_opciones(const char *filename, opcio *array, int max_size);
int leer_reglas(const char *filename, reglas *array, int max_size);

int main(int argc, char *argv[])
{
  char *fichero_opciones, *fichero_reglas;
  int N, num_opciones, num_reglas;
  opcio o_array[100];
  reglas r_array[100];

      // Leer parametros
  if (argc != 4) {
    printf("%s <fichero_opciones> <fichero_reglas> <N>\n", argv[0]);
    exit(1);
  }

  fichero_opciones = argv[1];
  fichero_reglas = argv[2];
  N = atoi(argv[3]);

  if (N < 1) {
    printf("N no puede ser cero o negativo!\n");
    exit(1);
  }

      // Leer fichero de opciones
  num_opciones = leer_opciones(fichero_opciones, o_array, 100);
  if(num_opciones == -1){
    printf("Error leyendo el fichero de opciones!\n");
    exit(1);
  }

      // Leer fichero de reglas
  num_reglas = leer_reglas(fichero_reglas, r_array, 100);
  if(num_reglas == -1){
    printf("Error leyendo el fichero de reglas!\n");
    exit(1);
  }

      // TO-BE-DONE

  return 0;
}

int leer_opciones(const char *filename, opcio *array, int max_size) {
    FILE *fp;
    char line[MAX_LEN];
    char *token;
    int count = 0;

    fp = fopen(filename, "r");
    if (fp == NULL) {
        return -1;
    }

    while (fgets(line, MAX_LEN, fp) != NULL && count < max_size) {
      token = strtok(line, ",");
      array[count].id = atoi(token);
      token = strtok(NULL, ",");
      token[strcspn(token, "\r\n")] = 0;  // remove newline character
      strncpy(array[count].nom, token, MAX_LEN - 1);
      array[count].nom[MAX_LEN - 1] = '\0'; // Ensure null-termination
      count++;
    }

    fclose(fp);
    return count;
}

int leer_reglas(const char *filename, reglas *array, int max_size)
{
    FILE *fp;
    char line[MAX_LEN];
    char *token;
    int count = 0;

    fp = fopen(filename, "r");
    if (fp == NULL) {
        return -1;
    }

    while (fgets(line, MAX_LEN, fp) != NULL && count < max_size) {
        token = strtok(line, ",");
        array[count].id1 = atoi(token);
        token = strtok(NULL, ",");
        array[count].id2 = atoi(token);
        token = strtok(NULL, ",");
        token[strcspn(token, "\r\n")] = 0;  // remove newline character
        strncpy(array[count].descripcion, token, MAX_LEN - 1);
        array[count].descripcion[MAX_LEN - 1] = '\0'; // Ensure null-termination
        count++;
    }

    fclose(fp);
    return count;
}
