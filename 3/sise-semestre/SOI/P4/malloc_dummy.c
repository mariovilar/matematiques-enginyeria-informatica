#include <assert.h>
#include <string.h>
#include <sys/types.h>
#include <unistd.h>
#include <stdio.h>

void *malloc(size_t mida) {
  void *p = sbrk(0);

  fprintf(stderr, "Malloc\n");

  if (mida <= 0)
    return NULL;

  if (sbrk(mida) == (void*) -1)
    return NULL; // sbrk failed.

  return p;
}

void free(void *p)
{
  fprintf(stderr, "Free\n");
}

