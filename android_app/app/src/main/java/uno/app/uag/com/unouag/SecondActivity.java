package uno.app.uag.com.unouag;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Random;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView centercard;                       //imagen de la carta que esta en el centro

    ImageView[] IVclock = new ImageView[4];     //array de las flechas en direccion de las manesillas del reloj
    ImageView[] IVanticlock = new ImageView[4]; //array de las flechas que van al contrario de las manecillas del reloj
    ImageView[] hands = new ImageView[4];       //array de las manitas que indican turno del jugador
    boolean swapArrow = false;                  //bandera de direccion, si es verdadero va en direccion de las manecillas del reloj
    Button botonT;                              //boton temporal para cambiar de direccion (direccion del reloj o encontra)
    Button btnhand;                             //boton temporal para cambiar mano de posicion

    int dirHand;                                //direccion de la mano = turno del  jugador
    int[] turnos = new int[4];                  //array de turnos
    int turno;                                  //variable que indica que jugador sigue

    String[] letras = new String[4];            //array de las letras de los colores de las cartas
    String[] especiales = new String[3];        //array de letras de cartas especiales
    String[] cartas = new String[108];          //array de los nombres de las cartas

    ImageView[] player1 = new ImageView[100];   //imagenes de kas cartas del jugador1
    ImageView[] player2 = new ImageView[100];   //imagenes de las cartas del jugador2
    ImageView[] player3 = new ImageView[100];   //imagenes de las cartas del jugador3
    ImageView[] player4 = new ImageView[100];   //imagenes de kas cartas del jugador4

    String[] splayer1 = new String[100];        //nombres de las cartas del jugador1
    String[] splayer2 = new String[100];        //nombres de las cartas del jugador2
    String[] splayer3 = new String[100];        //nombres de las cartas del jugador3
    String[] splayer4 = new String[100];        //nombres de las cartas del jugador4

    int cntcartasp1 = 7;                        //contador de cartas del jugador 1
    int cntcartasp2 = 7;                        //contador de cartas del jugador 2
    int cntcartasp3 = 7;                        //contador de cartas del jugador 3
    int cntcartasp4 = 7;                        //contador de cartas del jugador 4

    String cartaenelcentro;                     //nombre de la carta del centro

    int players;                                //numero de jugadores en el juego
    int xs=0;                                   //incrementador auxiliar
    int ixs = 0;                                //incrementador auxilia     r
    int otrai=-1;                               //otro incrementador

    TextView mensajeT;                          //para presentar mensajes auxiliares de compilacion

    public class Carta
    {
        int numCarta;
        String colorCarta;
        ImageView[] imCarta;

        public Carta(int nCarta, String colCarta, ImageView[] imgCarta)
        {
            numCarta=nCarta;
            colorCarta=colCarta;
            imCarta=imgCarta;
        }
    }

    public class Jugador
    {
        public int idJugador;
        public List<Carta> lCartas;

        public Jugador(int id,List<Carta> cartas) {
            idJugador = id;
            lCartas = cartas;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mensajeT = (TextView) findViewById(R.id.messageT);
        Intent intent = getIntent();                            //obtener dato guardado de main activity
        Bundle extra = intent.getExtras();                      //
        if (extra != null) {
            players = extra.getInt("PLAYERS");                  //guardarlo en variable players
            Toast.makeText(getApplicationContext(), "players= " + players, Toast.LENGTH_SHORT).show();
        }

        if(players == 2){                                       //si solo son dos jugadores
            player2[0] = (ImageView) findViewById(R.id.p31);
            player2[1] = (ImageView) findViewById(R.id.p32);
            player2[2] = (ImageView) findViewById(R.id.p33);
            player2[3] = (ImageView) findViewById(R.id.p34);
            player2[4] = (ImageView) findViewById(R.id.p35);
            player2[5] = (ImageView) findViewById(R.id.p36);
            player2[6] = (ImageView) findViewById(R.id.p37);
            for(int i=0;i<7;i++)
                player2[i].setVisibility(View.VISIBLE);         //mostramos las cartas volteadas del jugador 2
            turnos[0] = 1; turnos[1] = 0;                       //array para colocar la mano que señala al jugador en turno
            turnos[2] = 2; turnos[3] = 0;                       //array para colocar la mano que señala al jugador en turno
        } else if(players == 3){
            player2[0] = (ImageView) findViewById(R.id.p21);
            player2[1] = (ImageView) findViewById(R.id.p22);
            player2[2] = (ImageView) findViewById(R.id.p23);
            player2[3] = (ImageView) findViewById(R.id.p24);
            player2[4] = (ImageView) findViewById(R.id.p25);
            player2[5] = (ImageView) findViewById(R.id.p26);
            player2[6] = (ImageView) findViewById(R.id.p27);
            for(int i=0;i<7;i++)
                player2[i].setVisibility(View.VISIBLE);         //mostramos las cartas volteadas del jugador 2
            player3[0] = (ImageView) findViewById(R.id.p41);
            player3[1] = (ImageView) findViewById(R.id.p42);
            player3[2] = (ImageView) findViewById(R.id.p43);
            player3[3] = (ImageView) findViewById(R.id.p44);
            player3[4] = (ImageView) findViewById(R.id.p45);
            player3[5] = (ImageView) findViewById(R.id.p46);
            player3[6] = (ImageView) findViewById(R.id.p47);
            for(int i=0;i<7;i++)
                player3[i].setVisibility(View.VISIBLE);         //mostramos las cartas volteadas del jugador 3
            turnos[0] = 1; turnos[1] = 2;                       //array para colocar la mano que señala al jugador en turno
            turnos[2] = 0; turnos[3] = 3;                       //array para colocar la mano que señala al jugador en turno
        } else if(players == 4){
            player2[0] = (ImageView) findViewById(R.id.p21);
            player2[1] = (ImageView) findViewById(R.id.p22);
            player2[2] = (ImageView) findViewById(R.id.p23);
            player2[3] = (ImageView) findViewById(R.id.p24);
            player2[4] = (ImageView) findViewById(R.id.p25);
            player2[5] = (ImageView) findViewById(R.id.p26);
            player2[6] = (ImageView) findViewById(R.id.p27);
            for(int i=0;i<7;i++)
                player2[i].setVisibility(View.VISIBLE);         //mostramos las cartas volteadas del jugador 3
            player3[0] = (ImageView) findViewById(R.id.p31);
            player3[1] = (ImageView) findViewById(R.id.p32);
            player3[2] = (ImageView) findViewById(R.id.p33);
            player3[3] = (ImageView) findViewById(R.id.p34);
            player3[4] = (ImageView) findViewById(R.id.p35);
            player3[5] = (ImageView) findViewById(R.id.p36);
            player3[6] = (ImageView) findViewById(R.id.p37);
            for(int i=0;i<7;i++)
                player3[i].setVisibility(View.VISIBLE);
            player4[0] = (ImageView) findViewById(R.id.p41);    //mostramos las cartas volteadas del jugador 3
            player4[1] = (ImageView) findViewById(R.id.p42);
            player4[2] = (ImageView) findViewById(R.id.p43);
            player4[3] = (ImageView) findViewById(R.id.p44);
            player4[4] = (ImageView) findViewById(R.id.p45);
            player4[5] = (ImageView) findViewById(R.id.p46);
            player4[6] = (ImageView) findViewById(R.id.p47);
            for(int i=0;i<7;i++)
                player4[i].setVisibility(View.VISIBLE);         //mostramos las cartas volteadas del jugador 3
            turnos[0] = 1; turnos[1] = 2;                       //array para colocar la mano que señala al jugador en turno
            turnos[2] = 3; turnos[3] = 4;                       //array para colocar la mano que señala al jugador en turno
        }

        dirHand = 0;                                            //la mano comienza con el jugador 1
        letras[0] = "b"; letras[1] = "g";                       //color b=azul, color g=verde
        letras[2] = "r"; letras[3] = "y";                       //color r=rojo, color y=amarillo
        especiales[0] = "d";                                    //carta draw - tomar 2
        especiales[1] = "r";                                    //carta reverse - cambio de direccion
        especiales[2] = "s";                                    //carta skipped - saltar al jugador en turno
        xs = 0;                                                 //indice de array de nombre de cartas
        for(int i=0;i<4;i++)                                    //recorre todas las letras b,g,r,y
            for(int j=0;j<2;j++)                                //recorre dos veces cada letra
                for(int k=1;k<10;k++)                           //del 1 al 9
                    cartas[xs++] = letras[i] + k;               //asigna el nombre al array de nombres

        for(int i=0;i<4;i++) {                                  //recorre todas las letras  b,g,r,y
            cartas[xs++] = letras[i] + "0";                     //asignamos el cero para las cartas b0,g0,r0,y0
            cartas[xs++] = "wc";                                //asignamos la wild card normal
            cartas[xs++] = "w4";                                //asignamos la wild card 4
        }

        for(int i=0;i<4;i++)                                    //recorre todas las letras
            for(int j=0;j<2;j++)                                //recorre dos veces cada letra
                for(int k=0;k<3;k++)                            //recorre todas las cartas especiales
                    cartas[xs++] = letras[i] + especiales[k];   //asignamos cartas especiales draw, reverse, skipped


        centercard = (ImageView) findViewById(R.id.tiradas);    //asignamos carta cualquiera al centro
        IVclock[0] = (ImageView) findViewById(R.id.clock1);     //asignamos imagenes de flecha
        IVclock[1] = (ImageView) findViewById(R.id.clock2);     //asignamos imagenes de flecha
        IVclock[2] = (ImageView) findViewById(R.id.clock3);     //asignamos imagenes de flecha
        IVclock[3] = (ImageView) findViewById(R.id.clock4);     //asignamos imagenes de flecha
        IVanticlock[0] = (ImageView) findViewById(R.id.anticlock1);     //asignamos imagenes de flecha al reves
        IVanticlock[1] = (ImageView) findViewById(R.id.anticlock2);     //asignamos imagenes de flecha al reves
        IVanticlock[2] = (ImageView) findViewById(R.id.anticlock3);     //asignamos imagenes de flecha al reves
        IVanticlock[3] = (ImageView) findViewById(R.id.anticlock4);     //asignamos imagenes de flecha al reves
        hands[0] = (ImageView) findViewById(R.id.hand1);        //asignamos imagenes de las manos
        hands[1] = (ImageView) findViewById(R.id.hand2);
        hands[2] = (ImageView) findViewById(R.id.hand3);
        hands[3] = (ImageView) findViewById(R.id.hand4);
        swapArrow = true;                                       //asignamos la direccion manecillas del reloj
        botonT = (Button) findViewById(R.id.buttonT);           //creamos boton cambiar direccion
        botonT.setOnClickListener(this);                        //que sea clickeable
        btnhand = (Button) findViewById(R.id.btnhand);          //creamos boton para mano
        btnhand.setOnClickListener(this);                       //que sea clickeable

        player1[0] = (ImageView) findViewById(R.id.p11);        //asignamos la imagen al array de imagenes
        player1[1] = (ImageView) findViewById(R.id.p12);        //asignamos la imagen al array de imagenes
        player1[2] = (ImageView) findViewById(R.id.p13);        //asignamos la imagen al array de imagenes
        player1[3] = (ImageView) findViewById(R.id.p14);        //asignamos la imagen al array de imagenes
        player1[4] = (ImageView) findViewById(R.id.p15);        //asignamos la imagen al array de imagenes
        player1[5] = (ImageView) findViewById(R.id.p16);        //asignamos la imagen al array de imagenes
        player1[6] = (ImageView) findViewById(R.id.p17);        //asignamos la imagen al array de imagenes

        AsignarCartasInicio(players);                            //repartimos las cartas a todos los jugadores que estan jugando


        //****NO SE TE OLVIDE HACER FUNCION EN CASO DE QUE UNA CARTA ESPECIAL SE PONGA AL INICIO DEL JUEGO***
        //STARTING A DISCARD PILE
        //Well, if a word card is the first one turned up from the DRAW pile, this is what happens.
        //Wild Draw Four - It's put back in the deck and another one is chosen.
        //Wild Card - The player to the left of the dealer calls out a color - then plays.
        //Draw Two Card - The player to the left of dealer must pick two cards and then the next player goes (sad, but true).
        //Reverse Card - The dealer plays first, but then goes to the right instead of the left.
        //Skip Card - The player to the left of dealer doesn't start (get it, he's skipped). The player to the left of him starts.

        AcomodarCartasJugador1();                                 //poner las cartas (imagenes) del jugador 1
        //myCountDown.start();                                    //activar el timer para ver las cartas

        player1[0].setOnTouchListener(new View.OnTouchListener() {//si se toco la primera imagen
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: { ImageView view = (ImageView) v; view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate(); break; }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: { ImageView view = (ImageView) v; view.getDrawable().clearColorFilter(); view.invalidate();
                        //*********************************************************************************
                        //AQUI DEBES HACER UNA FUNCION PARA VALIDAR SI LA CARTA QUE TOCASTE SE PUEDE ECHAR AL CENTRO
                        //******************************************************************
                        tirarcarta(splayer1[0],0);                      //tirar carta "nombre", posicion del indice
                        break; } } return true; }
        });


        player1[1].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: { ImageView view = (ImageView) v; view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate(); break; }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: { ImageView view = (ImageView) v; view.getDrawable().clearColorFilter(); view.invalidate();
                        tirarcarta(splayer1[1],1);  break; } } return true; }
        });

        player1[2].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: { ImageView view = (ImageView) v; view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate(); break; }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: { ImageView view = (ImageView) v; view.getDrawable().clearColorFilter(); view.invalidate();
                        tirarcarta(splayer1[2], 2); break; } } return true; }
        });

        player1[3].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v; view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate(); break; }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v; view.getDrawable().clearColorFilter();  view.invalidate();
                        tirarcarta(splayer1[3], 3); break; } } return true; }
        });

        player1[4].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        view.getDrawable().clearColorFilter();
                        view.invalidate();
                        tirarcarta(splayer1[4], 4);
                        break;
                    }
                }
                return true;
            }
        });

        player1[5].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        view.getDrawable().clearColorFilter();
                        view.invalidate();
                        tirarcarta(splayer1[5], 5);
                        break;
                    }
                }
                return true;
            }
        });
        //si toco la carta 7
        player1[6].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        view.getDrawable().clearColorFilter();
                        view.invalidate();
                        tirarcarta(splayer1[6], 6);
                        break;
                    }
                }
                return true;
            }
        });
    }

    void tirarcarta(String s, int n){                   //funcion para poner carta en el centro y quitar la que se tiro
        //shownumberT(splayer1[0]);                     //mostramos el texto de la carta seleccionada
        cambiarimagendelcentro(s);                      //cambiamos la imagen del centro
        splayer1[n] = "0";                              //limpiamos el campo string
        cntcartasp1--;                                  //restamos 1 al contador de cartas del jugador1
        reordenarcartasjugador1(n);                     //reorganizamos el array de string de los nombres de las cartas del jugador1
        AcomodarCartasJugador1();                       //mostramos las cartas que restan
    }

    void AsignarCartasInicio(int p){
        Resources res = getResources();                 //obtener la clase actual de recursos
        int n;                                          //para guardar el numero aleatorio
        for(int i=0;i<7;i++){                           //Asignar cartas aleatorias para el jugador 1 al inicio del juego
            do{
                Random rand = new Random();             //comenzar random
                n = rand.nextInt(108);                  //entre 0 y 107
            }while(cartas[n] == "0");                   //mientras sea un 0 buscar otro valor, por que 0 significa que ya no existe
            String cname = cartas[n]; cartas[n] = "0";  //guardamos el nombre y la casilla le ponemos 0 para ya no usarla
            int resID = res.getIdentifier(cname, "drawable", getPackageName()); //buscamos la carta en la carpeta drawable
            player1[i].setImageResource(resID);         //asignamos la imagen en la carta que se visualiza
            splayer1[i] = cname;                        //guardamos el nombre en el array de los nombres de las cartas del jugador 1
        }
        //asignamos cartas a los demas jugadores
        for(int h=0;h<(p-2);h++){                       //reapartir cartas para los otros jugadores, p = numero de jugadores
            for(int i=0;i<7;i++) {                      //Asignar 7 cartas a cada jugadore
                do {
                    Random rand = new Random();         //comenzar random
                    n = rand.nextInt(108);              //entre 0 y 107
                } while (cartas[n] == "0");             //mientras sea un 0 buscar otro valor
                String cname = cartas[n]; cartas[n] = "0";                          //guardamos el nombre y la casilla le ponemos 0
                int resID = res.getIdentifier(cname, "drawable", getPackageName()); //buscamos la carta en la carpeta drawable
                if (h == 0)
                    splayer2[i] = cname;                //guardamos el nombre de las cartas para jugador 2
                else if (h == 1)
                    splayer3[i] = cname;                //guardamos el nombre de las cartas para jugador 3
                else if (h == 2)
                    splayer4[i] = cname;                //guardamos el nombre de las cartas para jugador 3
            }
        }
        //ahora mostramos una carta en el centro para comenzar el juego
        do {
            Random rand = new Random();                                     //comenzar random
            n = rand.nextInt(108);                                          //entre 0 y 107
        } while ((cartas[n] == "0") || (cartas[n] == "w4"));                //mientras sea un 0 buscar otro valor
        String cname = cartas[n]; cartas[n] = "0";                          //guardamos el nombre y la casilla le ponemos 0
        int resID = res.getIdentifier(cname, "drawable", getPackageName()); //buscamos la carta en el drawable
        centercard.setImageResource(resID);                                 //asignamos una carta aleatoria al centro
        cartaenelcentro = cname;                                            //guardamos el nombre en una variable globar para compara cada carta
    }

    void AcomodarCartasJugador1(){
        Resources res = getResources();                                     //obtener la clase actual de recursos
        int startpos = 0, i = 0;                                            //posicion inicial de la primera carta
        if ((cntcartasp1 > 0) && (cntcartasp1 < 18)) {                      //si son menos de 18 cartas, se deben acomodar cada 40puntos de distancia una de otra
            startpos = (((cntcartasp1 * 40) / 2) - 15);                     //sacamos la posicion mas a la izquierda para saber desde donde poner las cartas
            while (i < cntcartasp1) {                                       //mientras hay cartas en el jugador 1
                int resID = res.getIdentifier(splayer1[i], "drawable", getPackageName());   //buscamos la carta la carpeta drawable
                player1[i].setImageResource(resID);                         //asignamos la imagen en la carta que se visualiza
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) player1[i].getLayoutParams();      //marco donde se acomodan las cartas
                params.rightMargin = startpos;                              //asignamos posicion
                player1[i].setLayoutParams(params);                         //ubicamos posicion en base al marco donde se encuentra la imagen
                startpos -= 40;                                             //decrementamos 40 puntos en posicion
                i++;                                                        //incrementamos el numero de cartas puestas
            }
        }
    }

    void reordenarcartasjugador1(int id)  {
            for (int i = 0; i <= cntcartasp1; i++)                          //debemos limpiar el contenido (visual) de las cartas
                player1[i].setImageDrawable(null);                          //eliminamos las cartas actuales visualmente

            //****ATENCION: POR AQUI ESTA EL PUTO ERROR*****
            int j = id;                                                     //indice de apartir de cual se van a acomodar
            while (j < cntcartasp1) {                                       //recorriendo desde la carta que se toco hasta la ultima
                splayer1[j] = splayer1[j + 1];                              //recorrer las cadenas una posicion para ocupar el espacio ocupado
                j++;
            }
            splayer1[cntcartasp1] = "0";                                    //asignamos 0 al final para no tener un null
            //Toast.makeText(getApplicationContext(), "id= " + id + ", cc= " + cntcartasp1, Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(), "c= " + cntcartasp1 + ", sp= " + splayer1[cntcartasp1], Toast.LENGTH_SHORT).show();
    }

    void cambiarimagendelcentro(String vs){
        Resources res = getResources();                                     //obtener la clase actual de recursos
        int resID = res.getIdentifier(vs, "drawable", getPackageName());    //buscamos la carta en el drawable
        centercard.setImageResource(resID);                                 //asignamos la imagen en la carta del centro
    }

    void shownumberT(String vs){
        Toast.makeText(getApplicationContext(), "p= " + vs, Toast.LENGTH_SHORT).show();
    }

    CountDownTimer myCountDown = new CountDownTimer(10000, 1000) {          //timer cada segundo hasta 10 segundos
        public void onTick(long millisUntilFinished) {                      //cada segundo ejecutar esta funcion
            if(ixs < 100){                                                  //las 100 cartas del jugador 1
                mensajeT.setText("p= " + ixs + ", c= " + splayer1[ixs]); ixs++;     //ver contenido del array del jugador 1
            }
        }
        public void onFinish() {                                            //cuando termine el timer
            mensajeT.setText("TERMINO");                                    //mostrar mensaje
            //Toast.makeText(getApplicationContext(), "TERMINO " + p, Toast.LENGTH_SHORT).show();
        }
    };


    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonT:                                              //si se presiona el boton del cambio de direccion
                swapArrow = !swapArrow;                                     //cambiar la direccion
                if(swapArrow == true){                                      //direccion en base a las manecillas del reloj
                    for (int i=0;i<4;i++) {                                 //las flechas que se muestran
                        IVclock[i].setVisibility(View.VISIBLE);             //visualizar de estas
                        IVanticlock[i].setVisibility(View.INVISIBLE);       //esconder estas
                    }
                } else {                                                    //si es en contra de las manecillas del reloj
                    for (int i = 0; i < 4; i++) {                           //las flechas que se muestran
                        IVclock[i].setVisibility(View.INVISIBLE);           //esconde estas
                        IVanticlock[i].setVisibility(View.VISIBLE);         //muestra esta
                    }
                }
                break;
            case R.id.btnhand:                                              //boton de mover la mano
                if(swapArrow == true){                                      //clockwise erection
                    do{
                        dirHand++; if(dirHand > 3) dirHand = 0;             //si recorre la mano mas de 4 posiciones regresa al array
                    } while(turnos[dirHand] == 0);                          //mientras sea cero buscar otro jugador activo para ponerle la mano apuntando
                } else {                                                    //anticlockwise direction
                    do{
                        dirHand--; if(dirHand < 0) dirHand = 3;             //si es menos de 0 posiciones poner en posicion 3
                    }while(turnos[dirHand] == 0);                           //mientras sea cero buscar otro jugador activo para ponerle la mano a señalar
                }
                for(int i = 0;i<4;i++)
                    hands[i].setVisibility(View.INVISIBLE);                 //esconder todas las mano
                hands[dirHand].setVisibility(View.VISIBLE);                 //nostar solo la mano del jugador que sigue en turno
                break;
        }
    }


}





