//gcc triangles.cpp -o triangles -lGL -lGLU -lglut -lm
//./triangles

#include <GL/gl.h>
#include <GL/glut.h>
#include <stdlib.h>
#include <math.h>

float curChange=0.1;
float rotAngle = 0;
int rotDir0 = 1;
int rotDir1 = -1;
int rotDir2 = 1;
int d0=1;
int d1=-1;
int d2=1;
bool o0=false;
bool o1=false;
bool o2=false;
float away0=0;
float away1=0;
float away2=0;
float rotAngle0=0;
float rotAngle1=0;
float rotAngle2=0;

float a=50.0f;

void timer(int) {
    rotAngle++;
    if(rotDir0==1) {
        rotAngle0++;
    }
    if(rotDir0==-1) {
        rotAngle0--;
    }
    if(rotDir1==1) {
        rotAngle1++;
    }
    if(rotDir1==-1) {
        rotAngle1--;
    }
    if(rotDir2==1) {
        rotAngle2++;
    }
    if(rotDir2==-1) {
        rotAngle2--;
    }
    glutPostRedisplay();
    if(o0) away0 += curChange;
    if(o1) away1 += curChange;
    if(o2) away2 += curChange;
}

void drawTriangle(float x, float y, int d, bool o, float away) {
    
    float awayAngle = atan(x/y);
    glTranslated(away * cos(awayAngle), away * sin(awayAngle), 0);

    glTranslated(a/3, a/3, 0);
    glRotatef(d*rotAngle*3, 0.0, 0.0, 1.0);
    glTranslated(-a/3, -a/3, 0);
    glBegin(GL_TRIANGLE_STRIP);
    glVertex2f(0.0, 0.0);
    glVertex2f(a, 0.0);
    glVertex2f(0.0, a);
    glEnd();
}

void MyDisplay(void){
    glLoadIdentity();
    glClear(GL_COLOR_BUFFER_BIT);
    glColor3f(0.0, 0.0, 1.0);

    glPushMatrix();
    glRotatef(rotAngle0, 0.0, 0.0, 1.0);

    glPushMatrix();
    drawTriangle(a, a, d0, o0, away0);
    glPopMatrix();

    glPushMatrix();
    glRotated(90, 0.0, 0.0, 1.0);
    drawTriangle(a, a, d0, o0, away0);
    glPopMatrix();

    glPushMatrix();
    glRotated(180, 0.0, 0.0, 1.0);
    drawTriangle(a, a, d0, o0, away0);
    glPopMatrix();

    glPushMatrix();
    glRotated(270, 0.0, 0.0, 1.0);
    drawTriangle(a, a, d0, o0, away0);
    glPopMatrix();
    glPopMatrix();

    glPushMatrix();
    glRotatef(rotAngle1, 0.0, 0.0, 1.0);

    glTranslated(0.0, a, 0);
    glPushMatrix();
    glColor3f(0.0, 1.0, 0.0);
    drawTriangle(2*a, a, d1, o1, away1);
    glPopMatrix();

    glPushMatrix();
    glRotated(90, 0.0, 0.0, 1.0);
    glColor3f(1, 0.5, 0.0);
    drawTriangle(a, 2*a, d1, o1, away1);
    glPopMatrix();

    glTranslated(a, -a, 0);
    glPushMatrix();
    glColor3f(1, 0.5, 0.0);
    drawTriangle(a, 2*a, d1, o1, away1);
    glPopMatrix();

    glPushMatrix();
    glRotated(90, 0.0, 0.0, 1.0);
    glTranslated(0, 2*a, 0);
    glColor3f(0.0, 1.0, 0.0);
    drawTriangle(2*a, a, d1, o1, away1);
    glPopMatrix();

    glRotated(270, 0.0, 0.0, 1.0);
    glPushMatrix();
    glColor3f(0.0, 1.0, 0.0);
    drawTriangle(2*a, a, d1, o1, away1);
    glPopMatrix();

    glPushMatrix();
    glRotated(270, 0.0, 0.0, 1.0);
    glTranslated(2*a, 0, 0);
    glColor3f(1, 0.5, 0.0);
    drawTriangle(a, 2*a, d1, o1, away1);
    glPopMatrix();

    glTranslated(a, -a, 0);
    glPushMatrix();
    drawTriangle(a, 2*a, d1, o1, away1);
    glPopMatrix();

    glPushMatrix();
    glColor3f(0.0, 1.0, 0.0);
    glRotated(270,0,0,1);
    drawTriangle(2*a, a, d1, o1, away1);
    glPopMatrix();
    glPopMatrix();

    glPushMatrix();
    glRotatef(rotAngle2, 0.0, 0.0, 1.0);
    glTranslated(2*a, 0, 0);
    glPushMatrix();
    glColor3f(1, 0, 0.7);
    drawTriangle(a, 3*a, d2, o2, away2);
    glPopMatrix();

    glPushMatrix();
    glRotated(270,0,0,1);
    glColor3f(1.0, 0.0, 0.0);
    drawTriangle(3*a, a, d2, o2, away2);
    glPopMatrix();

    glTranslated(-a, a, 0);
    glPushMatrix();
    glColor3f(1.0, 1.0, 0.0);
    drawTriangle(a, a, d2, o2, away2);
    glPopMatrix();

    glPushMatrix();
    glRotated(270,0,0,1);
    glTranslated(2*a,0,0);
    drawTriangle(a, a, d2, o2, away2);
    glPopMatrix();

    glTranslated(-a, a,0);
    glPushMatrix();
    glColor3f(1.0, 0.0, 0.0);
    drawTriangle(3*a, a, d2, o2, away2);
    glPopMatrix();

    glPushMatrix();
    glColor3f(1, 0, 0.7);
    glRotated(270,0,0,1);
    glTranslated(4*a, 0, 0);
    drawTriangle(a, 3*a, d2, o2, away2);
    glPopMatrix();

    glRotated(90,0,0,1);
    glPushMatrix();
    drawTriangle(a, 3*a, d2, o2, away2);
    glPopMatrix();

    glPushMatrix();
    glColor3f(1.0, 0.0, 0.0);
    glRotated(90,0,0,1);
    glTranslated(0, 4*a, 0);
    drawTriangle(3*a, a, d2, o2, away2);
    glPopMatrix();

    glTranslated(-a, a, 0);
    glPushMatrix();
    glColor3f(1.0, 1.0, 0.0);
    drawTriangle(a, a, d2, o2, away2);
    glPopMatrix();

    glPushMatrix();
    glRotated(90,0,0,1);
    glTranslated(0,2*a,0);
    drawTriangle(a, a, d2, o2, away2);
    glPopMatrix();

    glTranslated(-a, a, 0);
    glPushMatrix();
    glColor3f(1.0, 0.0, 0.0);
    drawTriangle(3*a, a, d2, o2, away2);
    glPopMatrix();

    glPushMatrix();
    glColor3f(1, 0, 0.7);
    glRotated(90,0,0,1);
    drawTriangle(a, 3*a, d2, o2, away2);
    glPopMatrix();
    glPopMatrix();

    glFlush();
    glutTimerFunc(20, timer, 0);
}

void MyInit(void){
    glClearColor (0.0, 0.0, 0.0, 0.0);
    glViewport(0, 0, 300, 300);
    glMatrixMode(GL_PROJECTION);
    gluOrtho2D(-300, 300, -300, 300);
    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
}

void Click(unsigned char key, int x, int y) {
    switch(key) {
        case 'q':
            rotDir0 = -rotDir0;
            break;
        case 'w':
            rotDir1 = -rotDir1;
            break;
        case 'e':
            rotDir2 = -rotDir2;
            break;
        case 'a':
            d0 = -d0;
            break;
        case 's':
            d1 = -d1;
            break;
        case 'd':
            d2 = -d2;
            break;
        case 'z':
            o0 = !o0;
            break;
        case 'x':
            o1 = !o1;
            break;
        case 'c':
            o2 = !o2;
        default:
            break;
    }
}

int main(int argc, char** argv){
    glutInit(&argc, argv);
    glutInitDisplayMode(GLUT_SINGLE | GLUT_RGB);
    glutInitWindowSize(1000, 1000);
    glutInitWindowPosition(300, 300);
    glutCreateWindow("Triangles");
    MyInit();
    glutDisplayFunc(MyDisplay);
    glutKeyboardFunc(Click);
    glutMainLoop();
    return 0;
} 
