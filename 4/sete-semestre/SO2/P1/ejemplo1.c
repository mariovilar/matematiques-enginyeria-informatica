#include <stdio.h>
#include <string.h>

int main(void)
{
    char buff[15];
    int pass = 0;

    printf("\n Introduce la contraseña: \n");
    gets(buff);

    if(strcmp(buff, "CafeConLeche"))
    {
        printf ("\n Contraseña Incorrecta \n");
    }
    else
    {
        printf ("\n Contraseña Correcta! \n");
        pass = 1;
    }

    if(pass)
    {
       /* Ahora imagina que damos permisos de administrador o root al usuario*/
        printf ("\n Privilegios de root asignados al usuario \n");
    }

    return 0;
}