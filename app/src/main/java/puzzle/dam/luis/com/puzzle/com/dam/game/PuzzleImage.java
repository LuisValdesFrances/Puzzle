package puzzle.dam.luis.com.puzzle.com.dam.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import puzzle.dam.luis.com.puzzle.com.dam.graphics.Image;
import puzzle.dam.luis.com.puzzle.com.dam.graphics.Screen;
import puzzle.dam.luis.com.puzzle.com.dam.sound.SoundManager;

/**
 * Created by Luis on 04/05/2017.
 */

public class PuzzleImage {

    private int pieces;
    private Piece[][] puzzle;

    private int state;
    public static final int STATE_IDLE = 0;
    public static final int STATE_MOVE = 1;
    public static final int STATE_TARGET = 2;

    private Piece cPiece;
    private int touchOffsetX;
    private int touchOffsetY;
    private int initX;
    private int initY;
    private int targetXY;
    private boolean next;

    public int movements;

    SoundManager soundManager;

    public PuzzleImage(Image image, Screen screen, SoundManager soundManager, int pieces){
        this.pieces = pieces;
        this.soundManager = soundManager;
        int drawOffsetX = Puzzle.WORLD_WIDTH / 2 - image.getWidth() / 2;
        int drawOffsetY = Puzzle.WORLD_HEIGHT / 2 - image.getHeight() / 2;
        puzzle = new Piece[pieces][pieces];
        int sort = 0;
        for(int i = 0; i < pieces; i++){
            for(int j = 0; j < pieces; j++){
                //puzzleMatrix[i][j] = p++;
                Bitmap bm = Bitmap.createScaledBitmap(
                        image.getBitmap(),
                        image.getBitmap().getWidth()/pieces,
                        image.getBitmap().getHeight()/pieces,
                        false);
                Canvas c = new Canvas(bm);
                c.drawBitmap(image.getBitmap(),
                        -image.getBitmap().getWidth()/pieces*j,
                        -image.getBitmap().getHeight()/pieces*i, new Paint());
                Image img = new Image(bm);
                int w = img.getWidth();
                int h = img.getHeight();
                puzzle[i][j] = new Piece(sort++, (w * j) + drawOffsetX, (h * i) +  drawOffsetY, img);
            }
        }
        //Establezco el hueco del puzzle;
        puzzle[pieces-1][pieces-1].setSort(-1);

        //Hago 1000 movimientos legales
        init(1000);
    }

    public void update(Screen screen, float delta){

        switch(state){
            case STATE_IDLE:
                if(screen.isTouchDown() || screen.isTouchDrag()) {
                    cPiece =
                            getPiece(screen,
                                    screen.getTouchX(),
                                    screen.getTouchY());
                    if (cPiece != null && isLegal(cPiece) != -1) {
                        //changePositions(p, getPieceFromId(-1));
                        initX = cPiece.getX();
                        initY = cPiece.getY();
                        touchOffsetX =  (int)screen.getTouchX() - cPiece.getX();
                        touchOffsetY =  (int)screen.getTouchY() - cPiece.getY();
                        state = STATE_MOVE;
                    }
                }
                break;
            case STATE_MOVE:
                if(screen.isTouchUp()){
                    //La piezas se recolocan si se ha movido lo suficiente
                    if (isLegal(cPiece) == RIGHT || isLegal(cPiece) == LEFT) {
                        if(Math.abs(cPiece.getX()-getPieceFromId(-1).getX()) < cPiece.getWidth()/2){
                            targetXY = getPieceFromId(-1).getX();
                            next = true;
                            movements++;
                        }else{
                            targetXY = initX;
                            next = false;
                        }
                    }
                    else if (isLegal(cPiece) == DOWN || isLegal(cPiece) == UP) {
                        if(Math.abs(cPiece.getY()-getPieceFromId(-1).getY()) < cPiece.getHeight()/2){
                            targetXY = getPieceFromId(-1).getY();
                            next = true;
                            movements++;
                        }else{
                            targetXY = initY;
                            next = false;
                        }
                    }
                    state = STATE_TARGET;
                }else {
                    if (screen.isTouching()) {
                        //Limito el movimiento
                        if (isLegal(cPiece) == RIGHT) {
                            cPiece.setX(getCompress(
                                    initX,
                                    getPieceFromId(-1).getX(),
                                    (int) screen.getTouchX() - touchOffsetX
                            ));
                        }
                        else if (isLegal(cPiece) == LEFT) {
                            cPiece.setX(getCompress(
                                    getPieceFromId(-1).getX(),
                                    initX,
                                    (int) screen.getTouchX() - touchOffsetX
                            ));
                        }
                        else if (isLegal(cPiece) == DOWN) {
                            cPiece.setY(getCompress(
                                    initY,
                                    getPieceFromId(-1).getY(),
                                    (int) screen.getTouchY() - touchOffsetY
                            ));
                        }
                        else if (isLegal(cPiece) == UP) {
                            //Limito hacia arriba
                            cPiece.setY(getCompress(
                                    getPieceFromId(-1).getY(),
                                    initY,
                                    (int) screen.getTouchY() - touchOffsetY
                            ));
                        }
                    }
                }
                break;
            case STATE_TARGET:

                if (isLegal(cPiece) == RIGHT) {
                    if((next && cPiece.getX() > targetXY) || (!next && cPiece.getX() < targetXY)){
                        cPiece.setX(targetXY);
                        nextState();
                    }else{
                        int dist = Math.abs(cPiece.getX() - targetXY);
                        if(next) cPiece.setX(cPiece.getX() + (int)(dist*(delta*16))+1);
                        else  cPiece.setX(cPiece.getX() - (int)(dist*(delta*16))-1);
                    }
                }
                else if (isLegal(cPiece) == LEFT) {
                    if((next && cPiece.getX() < targetXY) || (!next && cPiece.getX() > targetXY)){
                        cPiece.setX(targetXY);
                        nextState();
                    }else{
                        int dist = Math.abs(cPiece.getX() - targetXY);
                        if(next) cPiece.setX(cPiece.getX() - (int)(dist*(delta*16))-1);
                        else  cPiece.setX(cPiece.getX() + (int)(dist*(delta*16))+1);
                    }
                }
                else if (isLegal(cPiece) == DOWN) {
                    if((next && cPiece.getY() > targetXY) || (!next && cPiece.getY() < targetXY)){
                        cPiece.setY(targetXY);
                        nextState();
                    }else{
                        int dist = Math.abs(cPiece.getY() - targetXY);
                        if(next) cPiece.setY(cPiece.getY() + (int)(dist*(delta*16))+1);
                        else  cPiece.setY(cPiece.getY() - (int)(dist*(delta*16))-1);
                    }
                }
                else if (isLegal(cPiece) == UP) {
                    if((next && cPiece.getY() < targetXY) || (!next && cPiece.getY() > targetXY)){
                        cPiece.setY(targetXY);
                        nextState();
                    }else{
                        int dist = Math.abs(cPiece.getY() - targetXY);
                        if(next) cPiece.setY(cPiece.getY() - (int)(dist*(delta*16))-1);
                        else  cPiece.setY(cPiece.getY() + (int)(dist*(delta*16))+1);
                    }
                }
                break;


        }
    }

    public void draw(Screen screen, Canvas canvas, Paint paint){
       for(int i = 0; i < pieces; i++) {
            for (int j = 0; j < pieces; j++) {
                if(puzzle[i][j].getSort() != -1) {
                    Piece p;
                    if(state == STATE_MOVE || state == STATE_TARGET){
                        //Es la pieza activa
                        if(cPiece.getSort() == puzzle[i][j].getSort()){
                            drawPiece(screen, canvas, paint, cPiece);
                        }else{
                            drawPiece(screen, canvas, paint, puzzle[i][j]);
                        }
                    }else{
                        drawPiece(screen, canvas, paint, puzzle[i][j]);
                    }

                }
            }
        }
    }

    private void nextState(){
        if(next){
            changePositions(cPiece, getPieceFromId(-1));
        }else{
            cPiece.setX(initX);
            cPiece.setY(initY);
        }
        state = STATE_IDLE;
        soundManager.playFX(SoundManager.FX_PIECE);
    }

    /**
     * Desordena el puzze mediante fuerza bruta. Habría que optimizrlo un poco escribiendo un mas
     * codigo para que, en vez de buscar una pieza valida de forma aleatoria mediante un while, lo
     * hiciese iterando una a una mediante un if.
     *
     * @param movements: Numero de movimientos legales para desordenarlo
     */
    private void init(int movements){

        while(movements > 0){
            Piece p = new Piece(puzzle[getRandom(0, pieces-1)][getRandom(0, pieces-1)]);
            if(isLegal(p)!= -1){
                changePositions(p, getPieceFromId(-1));
                movements--;
            }
        }
    }

    /**
     * Genera un numero aleatorio comprendido entre los dos parametros, ambos incluidos
     * @param min
     * @param max
     * @return
     */
    public static int getRandom(int min, int max){
        return min + (int)(Math.random() * (max+1));
    }

    private static int RIGHT = 0;
    private static int LEFT = 1;
    private static int DOWN = 2;
    private static int UP = 3;
    private int isLegal(Piece piece){
        int matrixX = getMatrixPosition(piece)[0];
        int matrixY = getMatrixPosition(piece)[1];

        //Derecha
        if(matrixX < pieces-1 && puzzle[matrixY][matrixX+1].getSort()==-1)
            return RIGHT;
        //Izquierda
        if(matrixX > 0 && puzzle[matrixY][matrixX-1].getSort()==-1)
            return LEFT;
        //Abajo
        if(matrixY < pieces-1 && puzzle[matrixY+1][matrixX].getSort()==-1)
            return DOWN;
        //Ariba
        if(matrixY > 0 && puzzle[matrixY-1][matrixX].getSort()==-1)
            return UP;
        return -1;
    }

    private int[] getMatrixPosition(Piece piece){
        int[] p = new int[2];
        for(int i = 0; i < pieces; i++) {
            for (int j = 0; j < pieces; j++) {
                if(puzzle[i][j].getSort() == piece.getSort()){
                    p[0] = j;
                    p[1] = i;
                    break;
                }
            }
        }
        return p;
    }

    /**
     * Devuelve la pieza con el id que se pasa por parametro
     *
     * @return Una copia de la pieza que se busca
     */
    private Piece getPieceFromId(int sort){
        for(int i = 0; i < pieces; i++){
            for(int j = 0; j < pieces; j++){
                if(puzzle[i][j].getSort() == sort){
                    return new Piece(puzzle[i][j]);
                }
            }
        }
        return null;
    }

    /**
     * Devuelve la posicion de la matriz de la pieza con el id que se pasa por parametro
     *
     * @return Un array de tamanyo dos, 0: x, 1: y
     */
    private int[] getPositionMatrixFromId(int sort){
        for(int i = 0; i < pieces; i++){
            for(int j = 0; j < pieces; j++){
                if(puzzle[i][j].getSort() == sort){
                    return new int[]{j, i};
                }
            }
        }
        return null;
    }

    private Piece getPiece(Screen screen, int screenX, int screenY){
        Piece p;
        for(int i = 0; i < pieces; i++) {
            for (int j = 0; j < pieces; j++) {
                p = puzzle[i][j];
                if(screenX > p.getX() && screenX < p.getX() + p.getWidth() &&
                        screenY > p.getY() && screenY < p.getY() + p.getHeight()){
                    return new Piece(p);
                }
            }
        }
        return null;
    }

    private void changePositions(Piece piece1, Piece piece2){
        //Guardo los datos de posicion de la pieza que voy a sustituir
        Piece p = getPieceFromId(piece1.getSort());
        int[] pMatrix = getPositionMatrixFromId(piece1.getSort());

        //Sustituyo la primera pieza
        for(int i = 0; i < pieces; i++) {
            for (int j = 0; j < pieces; j++) {
                if(puzzle[i][j].getSort() == piece2.getSort()){
                    piece1.setX(puzzle[i][j].getX());
                    piece1.setY(puzzle[i][j].getY());
                    puzzle[i][j] = piece1;
                    break;
                }
            }
        }
        //Sustituyo la primera segunda pieza
        piece2.setX(p.getX());
        piece2.setY(p.getY());
        puzzle[pMatrix[1]][pMatrix[0]] = piece2;
    }

    private int getCompress(int min, int max, int val){
        if(val < min) return min;
        else if(val > max) return max;
        else return val;
    }

    public boolean checkVictory(){
        int p = 0;
        for(int i = 0; i < pieces; i++){
            for(int j = 0; j < pieces; j++){
                if (puzzle[i][j].getSort() != p) {
                    break;
                }else{
                    p++;
                }
            }
        }
        return p == (pieces*pieces-1);
    }

    private void drawPiece(Screen screen, Canvas canvas, Paint paint, Piece piece){
        canvas.drawBitmap(piece.getImage().getBitmap(),piece.getX(), piece.getY(), paint);
    }

    public class Piece{

        private int sort;
        private int x;
        private int y;
        private Image image;

        public Piece(int sort, int x, int y, Image image) {
            this.sort = sort;
            this.x = x;
            this.y = y;
            this.image = image;
        }

        public Piece(Piece piece){
            this(piece.getSort(), piece.getX(), piece.getY(), piece.getImage());
        }

        public int getWidth(){
            return image.getWidth();
        }

        public int getHeight(){
            return image.getHeight();
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public Image getImage() {
            return image;
        }

        public void setImage(Image image) {
            this.image = image;
        }
    }





}
