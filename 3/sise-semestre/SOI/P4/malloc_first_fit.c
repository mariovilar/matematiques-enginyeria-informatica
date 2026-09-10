#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "struct.h"

p_meta_data first_element = NULL;
p_meta_data last_element  = NULL;

#define ALIGN8(x) (((((x)-1)>>3)<<3)+8)
#define MAGIC     0x12345678


p_meta_data search_available_space(size_t size_bytes);
p_meta_data request_space(size_t size_bytes);
void free(void *ptr);
void *malloc(size_t size_bytes);
void *calloc(size_t nelem, size_t elsize);
void *calloc_with_metadata(size_t nelem, size_t elsize);
void *realloc(void* ptr, size_t size_bytes);

/*
* Función para encontrar un bloque libre de un tamaño especificado
* @param medida del bloque que queremos buscar
* @return puntero a la primera posición de memoria del bloque seleccionado
*/
p_meta_data search_available_space(size_t size_bytes) {
    // Guardamos en un puntero la dirección del primer elemento
    p_meta_data current = first_element;

    // Buscamos un espacio de memoria libre y del tamaño adecuado. Utilizamos las siguientes condiciones:
    // 1. El puntero no sea nulo
    // 2. El bloque de memoria esté disponible (available = 1)
    // 3. La medida del bloque sea mayor que la que pasamos por parámetro    
    while (current && !(current->available && current->size_bytes >= size_bytes)) 
        current = current->next;

    // Retornamos el puntero a la primera posición de memoria del bloque seleccionado
    return current;
}

/*
* Función para crear un bloque nuevo con un tamaño especificado
* @param medida del bloque que queremos crear
* @return puntero a la primera posición de memoria del bloque creado
*/
p_meta_data request_space(size_t size_bytes) {
    // Creamos un puntero y lo asignamos a la cabeza del heap
    p_meta_data meta_data;

    meta_data = (void *) sbrk(0);

    // Si el bloque no tiene la medida que necesitamos retornamos null
    if (sbrk(SIZE_META_DATA + size_bytes) == (void *) -1)
        return (NULL);

    // En caso que el bloque sea correcto configuramos los valores
    meta_data->size_bytes = size_bytes;
    meta_data->available = 0;
    meta_data->magic = MAGIC;
    meta_data->next = NULL;
    meta_data->previous = NULL;

    // Retornamos un puntero a la primera posición de memoria del bloque creado
    return meta_data;
}

/*
* Función para liberar espacio de memoria
* @param puntero a la primera dirección del bloque de memoria que queremos liberar
*/
void free(void *ptr) {
    // Comprobamos que el puntero no sea nulo
    if(ptr == NULL) return;
    
    // Tiramos hacia atrás para encontrar los metadatos
    ptr -= SIZE_META_DATA;  // tirem enrere, per a trobar la metadata

    // Asignamos el puntero como nos interesa
    p_meta_data meta_data = ptr;
    
    // Miramos si el valor magic es correcto
    if(meta_data->magic == MAGIC) {
        // Hacemos que el trozo de memoria esté libre e imprimimos cuantos bytes hemos liberado
        meta_data->available = 1;
        fprintf(stderr, "Freed %zu bytes\n", meta_data->size_bytes);
    } else {
        // En caso que el magic no coincida salta un error
        fprintf(stderr, "ERROR MAGIC\n");
    }

    return;
}

/*
* Función para reservar espacio de memoria
* @param medida del bloque que queremos reservar
* @return puntero a la primera posición de memoria del bloque reservado
*/
void *malloc(size_t size_bytes) {
    // Creamos varios punteros
    void *p;
    p_meta_data meta_data;

    // Si pedimos una medida negativa o nula retornamos null
    if (size_bytes <= 0) {
        return NULL;
    }

    // La variable size_bytes ha de ser un múltiplo de 8
    size_bytes = ALIGN8(size_bytes);
    fprintf(stderr, "Malloc %zu bytes\n", size_bytes);

    // Buscamos un bloque libre en la memoria y lo guardamos a un puntero
    meta_data = search_available_space(size_bytes);

    // Si encontramos un bloque libre
    if (meta_data) { 
        // Dejamos constancia de que ha quedado ocupado
        meta_data->available = 0;
    } 
    // En caso de no haber encontrado un bloque libre
    else {
        // Creamos uno nuevo utilizando request_space()
        meta_data = request_space(size_bytes);

        // En caso de seguir sin tener un bloque disponible retornamos null
        if (!meta_data)
            return (NULL);

        // Si el último elemento no es null le podemos asignar 
        // como next el primer elemento del nuevo bloque
        if (last_element)
            last_element->next = meta_data;

        // Ahora meta_data será el último bloque, por lo tanto le asignamos
        // el previo a last_element
        meta_data->previous = last_element;

        // Ahora el último elemento será meta_data
        last_element = meta_data;

        // En caso que además no tengamos primer elemento, meta_data hará esta función
        if (first_element == NULL)
            first_element = meta_data;
    }

    // Asignamos a p el puntero al primer elemento del bloque
    p = (void *) meta_data;

    // Retornamos un puntero que pueda utilizarse.
    // Para ello le sumamos el tamaño de los meta datos
    return (p + SIZE_META_DATA); 
}

/*
* Función para reservar varios espacios de memoria distintos. 
* En esta función suponemos que son adjacentes. De esta forma solo
* tiene metada el primer elemento.
* @param cantidad de bloques que queremos reservar
* @param medida de los bloques que queremos 
* @return puntero a la primera posición de memoria del bloque reservado
*/
void *calloc(size_t nelem, size_t elsize) {

    // Si alguno de los valores es cero paramos la ejecución
    if (nelem <= 0 || elsize <= 0) {
        return NULL;
    }

    // El tamaño del bloque que vamos a reservar va a ser la multiplicación 
    // del número de bloques a reservar por su medida
    size_t size = nelem * elsize;

    // Llamamos a la función malloc que hemos implementado anteriormente.
    void *ptr = malloc(size);

    // Si el puntero es nulo, salimos y retornamos NULL
    if(ptr == NULL) {
        fprintf(stderr,"Nuestro calloc ha fallado");
        return NULL;
    }

    // La función memset inicializa el bloque de datos a cero. 
    // Esta función recibe tres parámetros:
    // 1. El puntero a la dirección del primer elemento del bloque
    // 2. Valor al cual queremos inicializar todo el bloque
    // 3. Medida del bloque
    memset(ptr, 0, size);
    fprintf(stderr, "Calloc done with %zu bytes\n",size);

    // Retornamos un puntero que apunta a la primera
    // dirección de memoria del bloque reservado
    return ptr;
}

/*
* Función para reservar varios espacios de memoria distintos.
* En esta función suponemos que no tienen porque ser adjacentes. De esta forma
* hay que tratar cada bloque por separado.
* @param cantidad de bloques que queremos reservar
* @param medida de los bloques que queremos 
* @return puntero a la primera posición de memoria del bloque reservado
*/
void *calloc_with_metadata(size_t nelem, size_t elsize) {
    
    // Inicializamos los punteros necesarios
    p_meta_data meta_data = NULL;
    p_meta_data meta_prev = NULL;
    p_meta_data meta_next = NULL;

    // Si alguno de los valores es cero paramos la ejecución
    if (nelem == 0 || elsize == 0) {
        return NULL;
    }

    // Llamamos a la función malloc que hemos implementado anteriormente
    // y lo asiganamos el primer puntero
    void *previous = malloc(elsize);

    // La función memset inicializa el bloque de datos a cero. 
    // Esta función recibe tres parámetros:
    // 1. El puntero a la dirección del primer elemento del bloque
    // 2. Valor al cual queremos inicializar todo el bloque
    // 3. Medida del bloque
    memset(previous, 0, elsize);

    // Iteamos tantas veces como bloques que queremos
    for (int i=1; i<nelem; i++) {
        // Llamamos a malloc y lo asignamos al siguiente puntero
        void *next = malloc(elsize);

        // Si alguno de punteros es null, retornamos null
        if(previous == NULL || next == NULL) {
            return NULL;
        }

        // En caso contrario lo configuramos todo
        meta_prev = (previous - SIZE_META_DATA);
        meta_next = (next - SIZE_META_DATA);
        meta_prev->next = meta_next;
        meta_next->previous = meta_prev;
        
        // Inicializamos el bloque de datos a cero
        memset(next, 0, elsize);

        previous = next;
    }

    // Retornamos un puntero
    return previous;
}

/*
* Función para reajustar el tamaño de un bloque de memoria.
* @param puntero a la primera dirección del bloque de memoria que queremos modificar
* @param nueva medida que queremos darle al bloque
*/
void *realloc(void* ptr, size_t size_bytes) {
    // Si el puntero es null la función realloc actúa como un malloc
    if(ptr == NULL) {
        // Liberamos espacio
        free(ptr);

        // Llamamos a malloc
        return malloc(size_bytes);
    }

    // Si la nueva medida es 0 entonces liberamos espacio y retornamos null
    if (size_bytes <= 0) {
        free(ptr);
        return NULL;
    }

    // Movemos el puntero para tener en cuenta los meta datos    
    ptr -= SIZE_META_DATA;
    p_meta_data meta_data = ptr;

    // Si el valor magic es incorrecto salta un error y para el programa
    if (meta_data->magic != MAGIC) {
        fprintf(stderr, "Error\n");
        return NULL;
    }

    // Hacemos que la medida del bloque sea múltiplo de 8
    size_bytes = ALIGN8(size_bytes);

    // Si la medida del bloque es superior a la nueva medida
    if(meta_data->size_bytes >= size_bytes) {

        // Imprimimos, a modo de guía, el tamaño solicitado y el tamaño real
        fprintf(stderr,"METADATA SIZE: %d, SIZE_BYTES: %d\n", meta_data->size_bytes,size_bytes);

        // Retornamos un puntero que pueda utilizarse.
        // Para ello le sumamos el tamaño de los meta datos
        return (ptr + SIZE_META_DATA); 
    }

    // En caso que la medida del bloque sea inferior a la nueva medida
    // tenemos que llamar a malloc con la nueva medida
    void* new = malloc(size_bytes);
    
    // Si el puntero obtenido del malloc es null, retornamos null
    if (new == NULL) {
        fprintf(stderr, "Realloc failed");
        return NULL;
    }
    

    // La función memcpy copia el contenido de un bloque a otro.
    // Esta función recibe tres parámetros:
    // 1. El puntero a la dirección del primer elemento del bloque donde queremos copiar 
    // 2. El puntero a la dirección del primer elemento del bloque que queremos copiar 
    // 3. Cantidad de datos que queremos copiar
    memcpy(new, ptr, meta_data->size_bytes);

    // Liberamos el espacio
    free(ptr);

    // Retornamos un puntero que pueda utilizarse.
    // Para ello le sumamos el tamaño de los meta datos
    return new;
}