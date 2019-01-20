/*************************************************************************************/
//gcc newtons_cradle.cpp -o newtons_cradle -lGL -lGLU -lglut -lm -lstdc++
//./spring
/*************************************************************************************/
#ifdef __unix
#define fopen_s(pFile,filename,mode) ((*(pFile))=fopen((filename),  (mode)))==NULL
#endif
#include <iostream>
#include <ctime>
#include <cmath>
#include <GL/gl.h>
#include <GL/glut.h>
#include <stdlib.h>
#include <math.h>
#include <zconf.h>

using namespace std;

#define N 100
typedef float point2[2];
double PI = M_PI;
float skala = 1.0f;
double tab1[N][N][3], tabN1[N][N][3];
point2 tex[N][N];
static GLfloat theta[2] = { 0.0f, 0.0f };  //azymut
static GLfloat pix2angley, pix2angle;   //przelicznik pikseli na stopnie
static GLfloat viewer[] = { 0.0, 0.0, 25.0 };
static int x_pos_old = 0, y_pos_old = 0;    // poprzednia pozycja kursora myszy
static int delta_x = 0, delta_y = 0;        // różnica pomiędzy pozycją bieżącą i poprzednią kursora myszy
static GLint status = 0;    // stan klawiszy myszy (0- nie naciśnięto żadnego klawisza, 1-naciśnięty został lewy klawisz)
int slices=40, stacks=40;

float maxAngle = 50; // maksymalny kąt wychylenia kul
float maxInc = 6.5;  // maksymalna inkrementacja kąta
float angle = -maxAngle;
bool clockwise = false;

// Zmienne zfer
int spheres = 5; // Licznik kul
int spheresMoving = 1; // Licznik poruszających się kul
float sphereRadius = 1.0; // Promień sfery

// Zmienne podstawy
float woodx = 8;
float woody = 0.5;
float woodz = 5;
float sphereWoodDist = 1.0; // Odleglosc miedzy kula a podstawą, gdy kąt jest równy 0

// Zmienne rur
float pipeRadius = 0.2;
float pipex = 10;
float pipey = 5;
float pipez = 5;

float strand = pipey-pipeRadius-sphereWoodDist-sphereRadius;

double toRadians(double degree) {
    return degree*PI/180;
}

/*************************************************************************************/

GLbyte *LoadTGAImage(const char *FileName, GLint *ImWidth, GLint *ImHeight, GLint *ImComponents, GLenum *ImFormat)
{
    // Struktura dla nagłówka pliku TGA
#pragma pack(1)
    typedef struct
    {
        GLbyte    idlength;
        GLbyte    colormaptype;
        GLbyte    datatypecode;
        unsigned short    colormapstart;
        unsigned short    colormaplength;
        unsigned char     colormapdepth;
        unsigned short    x_orgin;
        unsigned short    y_orgin;
        unsigned short    width;
        unsigned short    height;
        GLbyte    bitsperpixel;
        GLbyte    descriptor;
    }TGAHEADER;
#pragma pack(8)
    FILE *pFile;
    TGAHEADER tgaHeader;
    unsigned long lImageSize;
    short sDepth;
    GLbyte    *pbitsperpixel = NULL;
    //  Wartości domyślne zwracane w przypadku błędu
    *ImWidth = 0;
    *ImHeight = 0;
    *ImFormat = GL_BGR_EXT;
    *ImComponents = GL_RGB8;
    fopen_s(&pFile, FileName, "rb");
    if (pFile == NULL)
        return NULL;

    /*************************************************************************************/
    //  Przeczytanie nagłówka pliku
    fread(&tgaHeader, sizeof(TGAHEADER), 1, pFile);
    /*************************************************************************************/
    //  Odczytanie szerokości, wysokości i głębi obrazu
    *ImWidth = tgaHeader.width;
    *ImHeight = tgaHeader.height;
    sDepth = tgaHeader.bitsperpixel / 8;
    /*************************************************************************************/
    //  Sprawdzenie czy głębia spełnia założone warunki (8, 24 lub 32 bity)
    if (tgaHeader.bitsperpixel != 8 && tgaHeader.bitsperpixel != 24 && tgaHeader.bitsperpixel != 32)
        return NULL;
    /*************************************************************************************/
    // Obliczenie rozmiaru bufora w pamięci
    lImageSize = tgaHeader.width * tgaHeader.height * sDepth;
    /*************************************************************************************/
    // Alokacja pamięci dla danych obrazu
    pbitsperpixel = (GLbyte*)malloc(lImageSize * sizeof(GLbyte));
    if (pbitsperpixel == NULL)
        return NULL;
    if (fread(pbitsperpixel, lImageSize, 1, pFile) != 1)
    {
        free(pbitsperpixel);
        return NULL;
    }
    /*************************************************************************************/
    // Ustawienie formatu OpenGL
    switch (sDepth)
    {
        case 3:
            *ImFormat = GL_BGR_EXT;
            *ImComponents = GL_RGB8;
            break;
        case 4:
            *ImFormat = GL_BGRA_EXT;
            *ImComponents = GL_RGBA8;
            break;
        case 1:
            *ImFormat = GL_LUMINANCE;
            *ImComponents = GL_LUMINANCE8;
            break;
    };
    fclose(pFile);
    return pbitsperpixel;
}
/*************************************************************************************/
void timer(int) {
    float inc = maxInc-abs(angle)/maxAngle*maxInc*0.85;

    if(clockwise and angle<=-maxAngle) {
        clockwise=false;
    }
    else if(!clockwise and angle>=maxAngle) {
        clockwise=true;
    }

    if(clockwise)
        angle -= inc;
    else
        angle += inc;

    glutPostRedisplay();
    glutTimerFunc(20, timer, 0);
}
/*************************************************************************************/

void drawWood() {
    glPushMatrix();
    
    // Środek geometryczny
    glTranslatef(0.0f, -(sphereRadius/2+sphereWoodDist+woody), 0.0f);

    glEnable(GL_TEXTURE_2D);

    glColor3f(1.0f,1.0f,1.0f);

    glBegin(GL_QUADS);

    glNormal3f(0.0, 1.0f, 0.0f);
    glTexCoord2f(0.0f, 0.0f); glVertex3f(-woodx, woody, woodz);
    glTexCoord2f(1.0f, 0.0f); glVertex3f(woodx, woody, woodz);
    glTexCoord2f(1.0f, 1.0f); glVertex3f(woodx, woody, -woodz);
    glTexCoord2f(0.0f, 1.0f); glVertex3f(-woodx, woody, -woodz);

    glNormal3f(0.0, -1.0f, 0.0f);
    glTexCoord2f(0.0f, 0.0f); glVertex3f(-woodx, -woody, woodz);
    glTexCoord2f(1.0f, 0.0f); glVertex3f(woodx, -woody, woodz);
    glTexCoord2f(1.0f, 1.0f); glVertex3f(woodx, -woody, -woodz);
    glTexCoord2f(0.0f, 1.0f); glVertex3f(-woodx, -woody, -woodz);

    glNormal3f(-1.0, 0.0f, 0.0f);
    glTexCoord2f(0.0f, 0.0f); glVertex3f(-woodx, -woody, -woodz);
    glTexCoord2f(1.0f, 0.0f); glVertex3f(-woodx, -woody, woodz);
    glTexCoord2f(1.0f, 1.0f); glVertex3f(-woodx, woody, woodz);
    glTexCoord2f(0.0f, 1.0f); glVertex3f(-woodx, woody, -woodz);

    glNormal3f(1.0, 0.0f, 0.0f);
    glTexCoord2f(0.0f, 0.0f); glVertex3f(woodx, -woody, -woodz);
    glTexCoord2f(1.0f, 0.0f); glVertex3f(woodx, -woody, woodz);
    glTexCoord2f(1.0f, 1.0f); glVertex3f(woodx, woody, woodz);
    glTexCoord2f(0.0f, 1.0f); glVertex3f(woodx, woody, -woodz);

    glNormal3f(0.0, 0.0f, 1.0f);
    glTexCoord2f(0.0f, 0.0f); glVertex3f(-woodx, -woody, woodz);
    glTexCoord2f(1.0f, 0.0f); glVertex3f(woodx, -woody, woodz);
    glTexCoord2f(1.0f, 1.0f); glVertex3f(woodx, woody, woodz);
    glTexCoord2f(0.0f, 1.0f); glVertex3f(-woodx, woody, woodz);

    glNormal3f(0.0, 0.0f, -1.0f);
    glTexCoord2f(0.0f, 0.0f); glVertex3f(-woodx, -woody, -woodz);
    glTexCoord2f(1.0f, 0.0f); glVertex3f(woodx, -woody, -woodz);
    glTexCoord2f(1.0f, 1.0f); glVertex3f(woodx, woody, -woodz);
    glTexCoord2f(0.0f, 1.0f); glVertex3f(-woodx, woody, -woodz);
    glEnd();

    glDisable(GL_TEXTURE_2D);
    glPopMatrix();
}

void drawPipes() {
    glPushMatrix();
    glTranslatef(0.0f, pipey/2-sphereRadius/2-sphereWoodDist, 0.0f);

    glColor3f(0.8f, 0.8f, 0.8f);

    GLUquadric *cylinder = gluNewQuadric();

    glPushMatrix();
    glTranslatef(-pipex/2, pipey/2-pipeRadius, pipez/2-pipeRadius);
    glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
    gluCylinder(cylinder,pipeRadius,pipeRadius,pipex,slices, stacks);
    glPopMatrix();

    glPushMatrix();
    glTranslatef(-pipex/2, pipey/2-pipeRadius, -(pipez/2-pipeRadius));
    glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
    gluCylinder(cylinder,pipeRadius,pipeRadius,pipex,slices, stacks);
    glPopMatrix();

    glPushMatrix();
    glTranslatef(-(pipex/2-pipeRadius), pipey/2-pipeRadius, pipez/2-pipeRadius);
    glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
    gluCylinder(cylinder,pipeRadius,pipeRadius,pipey-pipeRadius,slices, stacks);
    glPopMatrix();

    glPushMatrix();
    glTranslatef(-(pipex/2-pipeRadius), pipey/2-pipeRadius, -(pipez/2-pipeRadius));
    glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
    gluCylinder(cylinder,pipeRadius,pipeRadius,pipey-pipeRadius,slices, stacks);
    glPopMatrix();

    glPushMatrix();
    glTranslatef((pipex/2-pipeRadius), pipey/2-pipeRadius, pipez/2-pipeRadius);
    glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
    gluCylinder(cylinder,pipeRadius,pipeRadius,pipey-pipeRadius,slices, stacks);
    glPopMatrix();

    glPushMatrix();
    glTranslatef((pipex/2-pipeRadius), pipey/2-pipeRadius, -(pipez/2-pipeRadius));
    glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
    gluCylinder(cylinder,pipeRadius,pipeRadius,pipey-pipeRadius,slices, stacks);
    glPopMatrix();

    glPopMatrix();
}

void renderMySphere(float angle) {
    glPushMatrix();

    glRotatef(angle, 0.0f, 0.0f, 1.0f);
    glTranslatef(0.0f, -strand, 0.0f);

    glColor3f(0.8, 0.8, 0.8);

    GLfloat mat_ambient[] = { 0.0, 0.0, 0.0, 0.0 };
    GLfloat mat_diffuse[] = { 0.5, 0.5, 0.5, 1.0 };
    GLfloat mat_specular[] = { 1.0, 1.0, 1.0, 1.0 };
    GLfloat mat_shininess[] = { 300.0 };
    glMaterialfv(GL_FRONT, GL_AMBIENT, mat_ambient);
    glMaterialfv(GL_FRONT, GL_DIFFUSE, mat_diffuse);
    glMaterialfv(GL_FRONT, GL_SPECULAR, mat_specular);
    glMaterialfv(GL_FRONT, GL_SHININESS, mat_shininess);

    glPushMatrix();
    glTranslatef(0.0f, -(sphereRadius/2), 0.0f);
    glutSolidSphere(sphereRadius/2, 20, 20);
    glPopMatrix();

    glRotatef(-angle, 0.0f, 0.0f, 1.0f);

    float distX = sin(toRadians(angle))*strand;
    float distY = cos(toRadians(angle))*strand;
    float distZ = pipez/2-pipeRadius;

    glColor3f(0.72f, 0.54f, 0.0f);
    glBegin(GL_LINES);
    glVertex3f(0.0f, 0.0f, 0.0f);
    glVertex3f(-distX, distY, -distZ);
    glVertex3f(0.0f, 0.0f, 0.0f);
    glVertex3f(-distX, distY, distZ);
    glEnd();

    glPopMatrix();
}

void drawSpheres() {
    glPushMatrix();
    glTranslatef(0.0f, pipey-pipeRadius-sphereWoodDist-sphereRadius/2, 0.0f);

    for(int i=1; i<=spheres; i++) {
        glPushMatrix();
        glTranslatef(-spheres/2.0f-sphereRadius/2.0f+i*sphereRadius,0.0f,0.0f);
        if(i<=spheresMoving && angle<0)
            renderMySphere(angle);
        else if(i>spheres-spheresMoving && angle>0)
            renderMySphere(angle);
        else
            renderMySphere(0);
        glPopMatrix();
    }

    glPopMatrix();
}

// Funkcja określająca co ma być rysowane (zawsze wywoływana gdy trzeba przerysować scenę)
void RenderScene(void)
{
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glLoadIdentity();
    gluLookAt(viewer[0], viewer[1], viewer[2], 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
    if (status == 1)                    // jeśli lewy klawisz myszy wciśnięty
    {
        theta[0] += delta_x*pix2angle * 5;
        theta[1] += delta_y*pix2angley * 5;
    }
    if (status == 2)
    {
        viewer[2] += delta_y*pix2angley / 5;
        if (viewer[2] < 7.0) viewer[2] -= delta_y*pix2angley * 5;
    }
    glRotatef(theta[0], 0.0, 1.0, 0.0);
    glRotatef(theta[1], 1.0, 0.0, 0.0);

    drawWood();
    drawPipes();
    drawSpheres();

    glFlush();  // Przekazanie poleceń rysujących do wykonania
    glutSwapBuffers();
}

/*************************************************************************************/
// Funkcja ustalająca stan renderowania
void Mouse(int btn, int state, int x, int y) {
    if (btn == GLUT_LEFT_BUTTON && state == GLUT_DOWN) {
        x_pos_old = x;        // przypisanie aktualnie odczytanej pozycji kursora
        y_pos_old = y;        // jako pozycji poprzedniej
        status = 1;           // wciśnięty został lewy klawisz myszy
    }
    else if (btn == GLUT_RIGHT_BUTTON && state == GLUT_DOWN) {
        x_pos_old = x;        // przypisanie aktualnie odczytanej pozycji kursora
        y_pos_old = y;        // jako pozycji poprzedniej
        status = 2;			  // wciśnięty został prawy klawisz myszy
    }
    else
        status = 0;           // nie został wciśnięty żaden klawisz
}

void Motion(GLsizei x, GLsizei y)
{
    delta_x = x - x_pos_old;     // obliczenie różnicy położenia kursora myszy
    x_pos_old = x;               // podstawienie bieżącego położenia jako poprzednie
    delta_y = y - y_pos_old;     // obliczenie różnicy położenia kursora myszy
    y_pos_old = y;               // podstawienie bieżącego położenia jako poprzednie
    glutPostRedisplay();         // przerysowanie obrazu sceny
}

void MyInit(void)
{
    glClearColor(0.1f, 0.1f, 0.1f, 1.0f);   // Kolor czyszczący (wypełnienia okna) ustawiono na czarny

    // Zmienne dla obrazu tekstury
    GLbyte *pBytes;
    GLint ImWidth, ImHeight, ImComponents;
    GLenum ImFormat;

    // Definicja materiału z jakiego zrobiony jest przedmiot
    //-------------------------------------------------------
    GLfloat mat_ambient[] = { 0.5f, 0.5f, 0.5f, 1.0f };
    // wspłczynniki ka =[kar,kag,kab] dla światła otoczenia
    GLfloat mat_diffuse[] = { 1.0, 1.0, 1.0, 1.0 };
    // wspłczynniki kd =[kdr,kdg,kdb] świata rozproszonego
    GLfloat mat_specular[] = { 1.0, 1.0, 1.0, 1.0 };
    // wspłczynniki ks =[ksr,ksg,ksb] dla światła odbitego
    GLfloat mat_shininess = { 100.0 };
    // wspłczynnik n opisujący połysk powierzchni
    // Definicja źródła światła
    //-------------------------------------------------------
    GLfloat light_position[2][4] = { { 1.0, 1.0, 0.0, 1.0 }, { 0.0, 0.0, 0.0, 1.0 } };
    // położenie źródła
    GLfloat light_ambient[] = { 0.2f, 0.2f, 0.2f, 1.0f };
    // składowe intensywności świecenia źródła światła otoczenia
    // Ia = [Iar,Iag,Iab]
    GLfloat light_diffuse[2][4] = { { 1.0, 0.5, 0.0, 0.0 }, { 0.0, 0.5, 1.0, 1.0 } };
    // składowe intensywności świecenia źródła światła powodującego
    // odbicie dyfuzyjne Id = [Idr,Idg,Idb]
    GLfloat light_specular[2][4] = { { 1.0f, 1.0f, 0.0f, 1.0f }, { 0.7f, 0.7f, 1.0f, 1.0f } };
    // składowe intensywności świecenia źródła światła powodującego
    // odbicie kierunkowe Is = [Isr,Isg,Isb]
    GLfloat att_constant = { 0.01f };
    // składowa stała ds dla modelu zmian oświetlenia w funkcji
    // odległości od źródła
    GLfloat att_linear = { 0.001f };
    // składowa liniowa dl dla modelu zmian oświetlenia w funkcji
    // odległości od źródła
    GLfloat att_quadratic = { 0.001f };
    // składowa kwadratowa dq dla modelu zmian oświetlenia w funkcji odległości od źródła
    // Ustawienie patrametrów materiału*/
    //-------------------------------------------------------
    glMaterialfv(GL_FRONT, GL_SPECULAR, mat_specular);
    glMaterialfv(GL_FRONT, GL_AMBIENT, mat_ambient);
    glMaterialfv(GL_FRONT, GL_DIFFUSE, mat_diffuse);
    glMaterialf(GL_FRONT, GL_SHININESS, mat_shininess);
    // Ustawienie parametrów źródła światła
    //-------------------------------------------------------
    glLightfv(GL_LIGHT0, GL_AMBIENT, light_ambient);
    glLightfv(GL_LIGHT0, GL_DIFFUSE, light_diffuse[0]);
    glLightfv(GL_LIGHT0, GL_SPECULAR, light_specular[0]);
    glLightfv(GL_LIGHT0, GL_POSITION, light_position[0]);
    glLightf(GL_LIGHT0, GL_CONSTANT_ATTENUATION, att_constant);
    glLightf(GL_LIGHT0, GL_LINEAR_ATTENUATION, att_linear);
    glLightf(GL_LIGHT0, GL_QUADRATIC_ATTENUATION, att_quadratic);
    glLightfv(GL_LIGHT1, GL_AMBIENT, light_ambient);
    glLightfv(GL_LIGHT1, GL_DIFFUSE, light_diffuse[1]);
    glLightfv(GL_LIGHT1, GL_SPECULAR, light_specular[1]);
    glLightfv(GL_LIGHT1, GL_POSITION, light_position[1]);
    glLightf(GL_LIGHT1, GL_CONSTANT_ATTENUATION, att_constant);
    glLightf(GL_LIGHT1, GL_LINEAR_ATTENUATION, att_linear);
    glLightf(GL_LIGHT1, GL_QUADRATIC_ATTENUATION, att_quadratic);
    // Ustawienie opcji systemu oświetlania sceny
    //-------------------------------------------------------
    glShadeModel(GL_SMOOTH); // włączenie łagodnego cieniowania
    glEnable(GL_LIGHTING);   // włączenie systemu oświetlenia sceny
    glEnable(GL_LIGHT0);     // włączenie źródła o numerze 0
    glEnable(GL_LIGHT1);     // włączenie źródła o numerze 1
    glEnable(GL_DEPTH_TEST); // włączenie mechanizmu z-bufora
    // Przeczytanie obrazu tekstury z pliku o nazwie tekstura.tga
    pBytes = LoadTGAImage("P4_t.tga", &ImWidth, &ImHeight, &ImComponents, &ImFormat);
    // Zdefiniowanie tekstury 2-D
    glTexImage2D(GL_TEXTURE_2D, 0, ImComponents, ImWidth, ImHeight, 0, ImFormat, GL_UNSIGNED_BYTE, pBytes);
    // Zwolnienie pamięci
    free(pBytes);
    // Włączenie mechanizmu teksturowania
    glEnable(GL_TEXTURE_2D);
    // Ustalenie trybu teksturowania
    glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
    // Określenie sposobu nakładania tekstur
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
}
/*************************************************************************************/
// Funkcja ma za zadanie utrzymanie stałych proporcji rysowanych
// w przypadku zmiany rozmiarów okna.
// Parametry vertical i horizontal (wysokość i szerokość okna) są
// przekazywane do funkcji za każdym razem gdy zmieni się rozmiar okna.
void ChangeSize(GLsizei horizontal, GLsizei vertical) {
    pix2angle = 360.0*0.1 / (float)horizontal; // przeliczenie pikseli na stopnie
    pix2angley = 360.0*0.1 / (float)vertical;
    glMatrixMode(GL_PROJECTION);
    // Przełączenie macierzy bieżącej na macierz projekcji
    glLoadIdentity();
    // Czyszcznie macierzy bieżącej
    gluPerspective(45.0, 1.0, 1.0, 200.0);
    // Ustawienie parametrów dla rzutu perspektywicznego
    if (horizontal <= vertical)
        glViewport(0, (vertical - horizontal) / 2, horizontal, horizontal);
    else
        glViewport((horizontal - vertical) / 2, 0, vertical, vertical);
    // Ustawienie wielkości okna okna widoku (viewport) w zależności
    // relacji pomiędzy wysokością i szerokością okna
    glMatrixMode(GL_MODELVIEW);
    // Przełączenie macierzy bieżącej na macierz widoku modelu
    glLoadIdentity();
    // Czyszczenie macierzy bieżącej
}
/*************************************************************************************/

void Click(unsigned char key, int x, int y) {
    switch(key) {
        case '+':
            if(maxAngle<90) {
                maxAngle++;
            }
            break;
        case '-':
            if(maxAngle>10) {
                maxAngle--;
            }
            break;
        case 's':
            spheres++;
            break;
        case 'd':
            spheres--;
            break;
        case 'w':
            if(spheresMoving<spheres) {
                spheresMoving++;
            }
            break;
        case 'e':
            if(spheresMoving>0) {
                spheresMoving--;
            }
            break;
        default:
            break;
    }
}

/*************************************************************************************/

int main(int argc, char *argv[])
{
    glutInit( & argc, argv );
    srand(time(NULL));
    glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB | GLUT_DEPTH);
    glutInitWindowSize(1200, 900);
    glutCreateWindow("Newton's cradle");
    glutTimerFunc(20, timer, 0);
    glutDisplayFunc(RenderScene);
    // Określenie, że funkcja RenderScene będzie funkcją zwrotną
    // (callback function).  Bedzie ona wywoływana za każdym razem
    // gdy zajdzie potrzba przerysowania okna
    glutReshapeFunc(ChangeSize);
    // Dla aktualnego okna ustala funkcję zwrotną odpowiedzialną
    // za zmiany rozmiaru okna
    glutMouseFunc(Mouse);
    // Ustala funkcję zwrotną odpowiedzialną za badanie stanu myszy
    glutKeyboardFunc(Click);

    glutMotionFunc(Motion);
    // Ustala funkcję zwrotną odpowiedzialną za badanie ruchu myszy
    MyInit();
    // Wykonuje wszelkie inicjalizacje konieczne przed przystąpieniem do renderowania
    glEnable(GL_DEPTH_TEST);
    // Włączenie mechanizmu usuwania powierzchni niewidocznych
    glutMainLoop();
    // Funkcja uruchamia szkielet biblioteki GLUT
    return 0;
}
