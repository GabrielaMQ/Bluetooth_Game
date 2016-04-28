package uno.app.uag.com.unouag;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.nfc.Tag;
import android.util.Log;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {

    CommunicationModuleB cModuleB = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    String[] turnsArray;
    String turnsarray;
    String[] connectedPlayers;
    String connectedplayers;
    String[] playersState;
    String playersstate;
    String address;
    String[] allHands;
    String allhands;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private static final String TAG = "BluetoothChatFragment";
    boolean currentTurn=false;
    int myTurn;
    String paquete;
    String delimiter = new String("-");
    boolean server=false;
    //-------------------------------------------------------------------------
    ImageView centercard;                       //imagen de la carta que esta en el centro

    ImageView[] IVclock = new ImageView[4];     //array de las flechas en direccion de las manesillas del reloj
    ImageView[] IVanticlock = new ImageView[4]; //array de las flechas que van al contrario de las manecillas del reloj
    ImageView[] hands = new ImageView[4];       //array de las manitas que indican turno del jugador
    boolean swapArrow = false;                  //bandera de direccion, si es verdadero va en direccion de las manecillas del reloj
    ImageView btnpasar;                         //boton de pasar turno
    ImageView btntomar;                         //boton de tomar carta
    ImageView btnjugar;                         //boton para iniciar el juego

    int dirHand;                                //direccion de la mano = turno del  jugador
    int[] turnos = new int[4];                  //array de turnos
    //VERIFICAR CUANDO SE CONECTEN LOS DEMAS, EL SERVIDOR DEBER SER EL 1
    int turno = 1;                              //variable que indica que jugador sigue

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
    int xs = 0;                                 //incrementador auxiliar
    TextView mensajeT;                          //para presentar mensajes en el centro del tablero que indicaran el color
    int inTurn;

    ImageView btnmenu;                         //boton para regresar al menu

    ImageView ganador1;                         //si gano el jugador1
    ImageView ganador2;                         //si gano el jugador2
    ImageView ganador3;                         //si gano el jugador3
    ImageView ganador4;                         //si gano el jugador4

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //full SCREEN
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setUpCommunicationModule();
        Intent intent = getIntent();                            //obtener dato guardado de main activity
        Bundle extra = intent.getExtras();                      //obtenemos las variables transferidas de otra actividad
        if (extra != null) {
            players = extra.getInt("PLAYERS");                  //guardarlo en variable players
            connectedplayers = extra.getString("PlayersList");
            turnsarray = extra.getString("turnsArray");
            playersstate = extra.getString("playersState");
            inTurn = extra.getInt("inTurno");
            server = extra.getBoolean("server");
            allhands = cntcartasp1 + "," + cntcartasp2 + "," + cntcartasp3 + "," + cntcartasp4;
            paquete = "1-" + connectedplayers + "-" + players + "-" + turnsarray + "-" + inTurn + "-" + "none" + "-" + allhands;//playersstate;
        }
        defineRole(paquete);
        startCommunicationEngine();

        mensajeT = (TextView) findViewById(R.id.messageT);      //relacionamos el texto del centro

        for (int i = 0; i <= 23; i++) {                         //relacionamos 23 cartas con su equivalente en el array del jugador 1
            int resId = getResources().getIdentifier("p1" + (i + 1), "id", getPackageName());   //buscar la imagen nombrada p1+(n)
            player1[i] = (ImageView) findViewById(resId);       //asignamos la imagen al array de imagenes
            player1[i].setVisibility(View.INVISIBLE);           //escodemos todas la cartas del jugador 1
        }

        if (players == 2) {                                     //si solo son dos jugadores
            for (int i = 0; i <= 23; i++) {                     //relacionamos 23 cartas con su equivalente en el array del jugador 1
                int resId = getResources().getIdentifier("p3" + (i + 1), "id", getPackageName());
                player2[i] = (ImageView) findViewById(resId);   //asignamos la imagen al array de imagenes
                player2[i].setVisibility(View.INVISIBLE);       //escodemos todas la cartas del jugador 2
            }
            turnos[0] = 1; turnos[1] = 0;                       //array para colocar la mano que señala al jugador en turno
            turnos[2] = 2; turnos[3] = 0;                       //array para colocar la mano que señala al jugador en turno
        } else if (players == 3) {                              //SI SON 3 JUGADORES
            for (int i = 0; i <= 23; i++) {                     //asignamos imagenes de cartas a los jugadores 2 y 3
                int resId = getResources().getIdentifier("p2" + (i + 1), "id", getPackageName());
                player2[i] = (ImageView) findViewById(resId);   //asignamos la imagen al array de imagenes
                player2[i].setVisibility(View.INVISIBLE);       //escodemos todas la cartas del jugador 2
                int resId2 = getResources().getIdentifier("p4" + (i + 1), "id", getPackageName());
                player3[i] = (ImageView) findViewById(resId2);  //asignamos la imagen al array de imagenes
                player3[i].setVisibility(View.INVISIBLE);       //escodemos todas la cartas del jugador 3
            }
            turnos[0] = 1; turnos[1] = 2;                       //array para colocar la mano que señala al jugador en turno
            turnos[2] = 0; turnos[3] = 3;                       //array para colocar la mano que señala al jugador en turno
        } else if (players == 4) {                              //SI SON 4 JUGADORES
            for (int i = 0; i <= 23; i++) {                     //Relacionamos las cartas con
                int resId = getResources().getIdentifier("p2" + (i + 1), "id", getPackageName());
                player2[i] = (ImageView) findViewById(resId);   //asignamos la imagen al array de imagenes
                player2[i].setVisibility(View.INVISIBLE);       //escodemos todas la cartas del jugador 2
                int resId2 = getResources().getIdentifier("p3" + (i + 1), "id", getPackageName());
                player3[i] = (ImageView) findViewById(resId2);   //asignamos la imagen al array de imagenes
                player3[i].setVisibility(View.INVISIBLE);       //escodemos todas la cartas del jugador 3
                int resId3 = getResources().getIdentifier("p4" + (i + 1), "id", getPackageName());
                player4[i] = (ImageView) findViewById(resId3);   //asignamos la imagen al array de imagenes
                player4[i].setVisibility(View.INVISIBLE);       //escodemos todas la cartas del jugador 4
            }
            turnos[0] = 1; turnos[1] = 2;                       //array para colocar la mano que señala al jugador en turno
            turnos[2] = 3; turnos[3] = 4;                       //array para colocar la mano que señala al jugador en turno
        }

        dirHand = 0;                                            //la mano comienza con el jugador 1
        letras[0] = "b";
        letras[1] = "g";                       //color b=azul, color g=verde
        letras[2] = "r";
        letras[3] = "y";                       //color r=rojo, color y=amarillo
        especiales[0] = "d";                                    //carta draw - tomar 2
        especiales[1] = "r";                                    //carta reverse - cambio de direccion
        especiales[2] = "s";                                    //carta skipped - saltar al jugador en turno
        xs = 0;                                                 //indice de array de nombre de cartas
        for (int i = 0; i < 4; i++)                             //recorre todas las letras b,g,r,y
            for (int j = 0; j < 2; j++)                         //recorre dos veces cada letra
                for (int k = 1; k < 10; k++)                    //del 1 al 9
                    cartas[xs++] = letras[i] + k;               //asigna el nombre al array de nombres

        for (int i = 0; i < 4; i++) {                           //recorre todas las letras  b,g,r,y
            cartas[xs++] = letras[i] + "0";                     //asignamos el cero para las cartas b0,g0,r0,y0
            cartas[xs++] = "wc";                                //asignamos la wild card normal
            cartas[xs++] = "w4";                                //asignamos la wild card 4
        }

        for (int i = 0; i < 4; i++)                             //recorre todas las letras
            for (int j = 0; j < 2; j++)                         //recorre dos veces cada letra
                for (int k = 0; k < 3; k++)                     //recorre todas las cartas especiales
                    cartas[xs++] = letras[i] + especiales[k];   //asignamos cartas especiales draw, reverse, skipped


        centercard = (ImageView) findViewById(R.id.tiradas);    //asignamos carta cualquiera al centro
        IVclock[0] = (ImageView) findViewById(R.id.clock1);     //asignamos imagenes de flecha
        IVclock[1] = (ImageView) findViewById(R.id.clock2);     //asignamos imagenes de flecha
        IVclock[2] = (ImageView) findViewById(R.id.clock3);     //asignamos imagenes de flecha
        IVclock[3] = (ImageView) findViewById(R.id.clock4);     //asignamos imagenes de flecha
        for(int i=0;i<4;i++)                                    //escondemos las flechas
            IVclock[i].setVisibility(View.INVISIBLE);
        IVanticlock[0] = (ImageView) findViewById(R.id.anticlock1);     //asignamos imagenes de flecha al reves
        IVanticlock[1] = (ImageView) findViewById(R.id.anticlock2);     //asignamos imagenes de flecha al reves
        IVanticlock[2] = (ImageView) findViewById(R.id.anticlock3);     //asignamos imagenes de flecha al reves
        IVanticlock[3] = (ImageView) findViewById(R.id.anticlock4);     //asignamos imagenes de flecha al reves
        for(int i=0;i<4;i++)                                    //escondemos las flechas al reves
            IVanticlock[i].setVisibility(View.INVISIBLE);
        hands[0] = (ImageView) findViewById(R.id.hand1);        //asignamos imagenes de las manos
        hands[1] = (ImageView) findViewById(R.id.hand2);
        hands[2] = (ImageView) findViewById(R.id.hand3);
        hands[3] = (ImageView) findViewById(R.id.hand4);
        for(int i=0;i<4;i++)                                    //escondemos las manos
            hands[i].setVisibility(View.INVISIBLE);

        swapArrow = true;                                       //asignamos la direccion manecillas del reloj
        btnpasar = (ImageView) findViewById(R.id.btnturno);     //relacionamos boton pasar turno
        btntomar = (ImageView) findViewById(R.id.btncarta);     //relacionamos boton tomar carta
        btnjugar = (ImageView) findViewById(R.id.btnjugar);     //relacionamos boton jugar partid

        btnmenu = (ImageView) findViewById(R.id.btnmenu);     //relacionamos boton ir a menu

        ganador1 = (ImageView) findViewById(R.id.ganador1);     //relacionamos felicitacion jugador 1
        ganador2 = (ImageView) findViewById(R.id.ganador2);     //relacionamos felicitacion jugador 2
        ganador3 = (ImageView) findViewById(R.id.ganador3);     //relacionamos felicitacion jugador 3
        ganador4 = (ImageView) findViewById(R.id.ganador4);     //relacionamos felicitacion jugador 4

        btnpasar.setOnTouchListener(new View.OnTouchListener() {//si se toco la primera imagen
            public boolean onTouch(View v, MotionEvent event) {             //boton pasar turno
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v; view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate(); break; }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        view.getDrawable().clearColorFilter(); view.invalidate();
                        if(inTurn==players-1){
                            inTurn = 0;
                        }else{
                            inTurn++;
                        }
                        paquete = "1-" + connectedplayers + "-" + players + "-" + turnsarray + "-" + inTurn + "-" + cartaenelcentro.toString() + "-" + allhands;//playersstate;
                        sendMessage(paquete);
                        defineRole(paquete);
                        disponibilidaddecartas();
                        btnpasar.setVisibility(View.INVISIBLE); //escondemos este boton
                        break; } } return true; }
        });

        btntomar.setOnTouchListener(new View.OnTouchListener() {//si se toco la primera imagen
            public boolean onTouch(View v, MotionEvent event) {             //boton tomar carta
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v; view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate(); break; }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        view.getDrawable().clearColorFilter(); view.invalidate();
                        cartaAleatoria();                       //asignamos una carta aleatoria al jugador 1
                        AcomodarCartasJugador1();               //reacomodamos las cartas del jugadore 1
                        btnpasar.setVisibility(View.VISIBLE);   //mostramos boton pasar
                        btntomar.setVisibility(View.INVISIBLE); //escondemos este boton
                        break; } } return true; }
        });

        btnjugar.setOnTouchListener(new View.OnTouchListener() {//si se toco la primera imagen
            public boolean onTouch(View v, MotionEvent event) {             //booton iniciar juego
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v; view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate(); break; }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        view.getDrawable().clearColorFilter(); view.invalidate();
                        inicio();                               //funcion que inicia el jugo y hace los primeros movimientos
                        btnjugar.setVisibility(View.INVISIBLE); //escondemos este boton
                        validarturno();
                        break; } } return true; }
        });

        btnmenu.setOnTouchListener(new View.OnTouchListener() {//si se toco este boton
            public boolean onTouch(View v, MotionEvent event) {             //booton volver al menu
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v; view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate(); break; }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        view.getDrawable().clearColorFilter(); view.invalidate();
                        Intent intent=new Intent(SecondActivity.this, MainActivity.class);
                        startActivity(intent);
                        break; } } return true; }
        });


        ///********************BOTONES/IMAGENES DE CADA CARTA DEL JUGADOR 1
        player1[0].setOnTouchListener(new View.OnTouchListener() {//si se toco la primera imagen
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (validarturno() == true) {           //si es el turno del jugador 1
                            if (validarcarta(splayer1[0]) == true) {    //si la carta es permitida en la jugada actual
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP); //obscurecer
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {           //si es el turno del jugador 1
                            if (validarcarta(splayer1[0]) == true) {    //si la carta es permitida en la jugada actual
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[0], 0);            //tirar carta "nombre", posicion del indice
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });


        player1[1].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[1]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[1]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[1], 1);
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });

        player1[2].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[2]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[2]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[2], 2);
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });

        player1[3].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[3]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[3]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[3], 3);
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });

        player1[4].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[4]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[4]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[4], 4);
                            }
                        }
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
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[5]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[5]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[5], 5);
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });

        player1[6].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[6]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[6]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[6], 6);
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });

        player1[7].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[7]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[7]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[7], 7);
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });

        player1[8].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[8]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[8]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[8], 8);
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });

        player1[9].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[9]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[9]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[9], 9);
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });

        player1[10].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[10]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[10]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[10], 10);
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });

        player1[11].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[11]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[11]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[11], 11);
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });

        player1[12].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[12]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[12]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[12], 12);
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });

        player1[13].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[13]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[13]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[13], 13);
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });

        player1[14].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[14]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[14]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[14], 14);
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });

        player1[15].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[15]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[15]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[15], 15);
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });

        player1[16].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[16]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[16]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[16], 16);
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });

        player1[17].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[17]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[17]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[17], 17);
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });

        player1[18].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[18]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[18]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[18], 18);
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });

        player1[19].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[19]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[19]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[19], 19);
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });

        player1[20].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[20]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[20]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[20], 20);
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });

        player1[21].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[21]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[21]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[21], 21);
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });

        player1[22].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[22]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[22]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[22], 22);
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });

        player1[23].setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[23]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                                view.invalidate();
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (validarturno() == true) {
                            if (validarcarta(splayer1[23]) == true) {
                                ImageView view = (ImageView) v;
                                view.getDrawable().clearColorFilter();
                                view.invalidate();
                                tirarcarta(splayer1[23], 23);
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    void inicio(){
        for(int i=0;i<4;i++)                                            //mostramos las felchas
            IVclock[i].setVisibility(View.VISIBLE);
        hands[dirHand].setVisibility(View.VISIBLE);                     //mostar solo la mano del jugador que sigue en turno
        AsignarCartasInicio(players);                                   //repartimos las cartas a todos los jugadores que estan jugando

        AcomodarCartasJugador1();                                       //poner las cartas (imagenes) del jugador 1
        //AcomodarCartasJugador2();                                       //poner las cartas (imagenes) del jugador 2
        if (players > 2) AcomodarCartasJugador3();                      //poner las cartas (imagenes) del jugador 3 si esta invitado
        if (players > 3) AcomodarCartasJugador4();                      //poner las cartas (imagenes) del jugador 4 si esta invitado

        if(cartaenelcentro.substring(1,2).compareTo("r")==0) {          //si es la carta reverse
            cambiardireccion();                                         //cambiamos de direccion sentido del juego
            cambiarturno();                                             //cambiamos de turno
        } else if(cartaenelcentro.substring(1,2).compareTo("s")==0) {   //si es la carta skipped
            cambiarturno();                                             //cambiamos de turno
        } else if(cartaenelcentro.substring(1,2).compareTo("d")==0) {   //si es la carta draw two
            for(int i=0;i<2;i++)                                        //obligamos al jugador a que tome dos cartas
                cartaAleatoria();                                       //le damos una carta aleatoria
            cambiarturno();                                             //cambiar de turno
            for (int i = 0; i <= cntcartasp1; i++) {                    //debemos limpiar el contenido (visual) de las cartas
                player1[i].setImageDrawable(null);                      //eliminamos las cartas actuales visualmente
                player1[i].setVisibility(View.INVISIBLE);               //escondemos las cartas
            }
            AcomodarCartasJugador1();                                   //Mostramos las cartas
        }
        disponibilidaddecartas();                                       //Habilitamos/Desabilitamos las cartas del jugador 1 en base al turno
    }

    boolean validarturno() {                                            //funcion de validar turno
        /*boolean b = false;                                              //bandera local
        if (currentTurn)                                                 //si es el turno del jugador 1
            b = true;                                                   //es verdad
        else {                                                          //si no es su turno que obscuresca todas sus cartas
            for(int i=0;i<cntcartasp1;i++)                              //todas las cartas del jugador 1
                player1[i].setColorFilter(Color.argb(119, 119, 119, 119)); //la oscurecemos
        }
        return b;  */                                                   //regremaso la bandera
        if(!currentTurn){                                                          //si no es su turno que obscuresca todas sus cartas
            for(int i=0;i<cntcartasp1;i++)                              //todas las cartas del jugador 1
                player1[i].setColorFilter(Color.argb(119, 119, 119, 119)); //la oscurecemos
        }else{
            int i = 0;                 //sacamos la posicion mas a la izquierda para saber desde donde poner las cartas
            while (i < cntcartasp1) {                                           //mientras hay cartas en el jugador 1
                if (validarcarta(splayer1[i])){                         //si es una carta valida comparada con la que esta en el centro la habilitamos
                    player1[i].setColorFilter(Color.argb(0, 255, 255, 255));
                } else {                                                        //si no es valida con la carta del centro (tirada) la obscurecemos
                    player1[i].setColorFilter(Color.argb(119, 119, 119, 119));
                }                                              //decrementamos distacia (puntos) en posicion entre carta
                i++;                                                            //incrementamos el numero de cartas puestas
            }
        }
        return currentTurn;
    }
    void disponibilidaddecartas(){                                      //funcion de disponibilidad de cartas
        if (currentTurn){                                                //si es el turno del jugador 1
            AcomodarCartasJugador1();                                   //reacomodamos las cartas VISUALMENTE
            btntomar.setVisibility(View.VISIBLE);                       //mostramos boton de tomar carta
            btnpasar.setVisibility(View.INVISIBLE);                     //escondemos boton de pasar turno
        } else {                                                        //si no es su turno que obscuresca todas sus cartas
            for(int i=0;i<cntcartasp1;i++)                              //todas las cartas
                player1[i].setColorFilter(Color.argb(119, 119, 119, 119));  //las obscurecemos
            btntomar.setVisibility(View.INVISIBLE);                     //escondemos boton tomar carta
            btnpasar.setVisibility(View.INVISIBLE);                     //escondemos boton pasar turno
        }
    }

    boolean validarcarta(String s) {                                    //funcion de validar carta
        boolean b = false;                                              //bandera local
        String a1 = cartaenelcentro.substring(0, 1);                    //primer caracter de la carta del centro
        String a2 = s.substring(0, 1);                                  //primer caracter de la carta del jugador
        String b1 = cartaenelcentro.substring(1, 2);                    //segundo caracter de la carta del centro
        String b2 = s.substring(1, 2);                                  //segundo caracter de la carta del jugador
        //mismo color, mismo nummero o carta WC/W4
        if (((a1.compareTo(a2) == 0) || (b1.compareTo(b2) == 0)) || (a2.compareTo("w") == 0)) {
            b = true;
        }
        return b;
    }

    void createCommunication() {
        for(int i=0; i<connectedPlayers.length; i++){
            if(!mBluetoothAdapter.getAddress().equals(connectedPlayers[i].toString())){
                boolean found = false;
                for(int j=0; j<cModuleB.getPlayersOnline(); j++){
                    if(cModuleB.getMACs()[j].equals(connectedPlayers[i].toString())){
                        found=true;
                    }
                }
                if(!found){
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_DEVICE_ADDRESS, connectedPlayers[i].toString());
                    connectDevice(intent, true);
                }
            }
        }
    }

    void tirarcarta(String s, int n) {                                  //funcion para poner carta en el centro y quitar la que se tiro
        if(inTurn==players-1){
            inTurn = 0;
        }else{
            inTurn++;
        }
        cntcartasp1--;                                                  //restamos 1 al contador de cartas del jugador1
        updateAllHands();
        paquete = "1-" + connectedplayers + "-" + players + "-" + turnsarray + "-" + inTurn + "-" + s + "-" + allhands; //+ "-" + playersstate;
        sendMessage(paquete);
        //cambiarimagendelcentro(paquete);                                      //cambiamos la imagen del centro
        if(s.compareTo("w4")==0){                                       //si es la w4 WILD FOUR
            cambiarturno();                                             //cambiamos turno
            for(int i=0;i<4;i++)                                        //cuatro veces
                cartaAleatoria();                                       //asignamos cartas aleatorias al siguiente jugador
            cambiarturno();                                             //cambiamos de turno / jugador
            menudecolores();                                            //mostramos el menu de colores
        } else if(s.compareTo("wc")==0) {                               //si es la carta wc WILD CARD
            cambiarturno();                                             //cambiamos de turno
            menudecolores();                                            //mostramos el menu de colores
        } else if(s.substring(1,2).compareTo("r")==0) {                 //si es la carta reverse
            cambiardireccion();                                         //cambiamos de direccion sentido del juego
            cambiarturno();                                             //cambiamos de turno
        } else if(s.substring(1,2).compareTo("s")==0) {                 //si es la carta skipped
            cambiarturno();                                             //cambiamos de turno
            cambiarturno();                                             //cambiamos de turno
        } else if(s.substring(1,2).compareTo("d")==0) {                 //si es la carta drawtwo TOMA 2 CARTAS
            cambiarturno();                                             //cambiamos turno
            for(int i=0;i<2;i++)                                        //obligamos al siguiente jugador a que tome dos cartas
                cartaAleatoria();                                       //le damos una carta aleatoria
            cambiarturno();                                             //volvemos a cambiar de turno
        } else {                                                        //SI ES CUALQUIER OTRA CARTA
            cambiarturno();                                             //cambiamos de turno de jugador
        }
        splayer1[n] = "0";                                              //limpiamos el campo string del jugador 1
        reordenarcartasjugador1(n);                                     //reorganizamos el array de string de los nombres de las cartas del jugador1
        AcomodarCartasJugador1();                                       //mostramos las cartas que restan
        disponibilidaddecartas();                                       //habilitamos/deshabilitamos las cartas segun la carta tirada
    }

    void AsignarCartasInicio(int p) {                                   //funcion para ASIGNAR las PRIMERAS CARTAS
        Resources res = getResources();                                 //obtener la clase actual de recursos
        int n;                                                          //para guardar el numero aleatorio
        for (int i = 0; i < cntcartasp1; i++) {                         //Asignar cartas aleatorias para el jugador 1 al inicio del juego
            do {
                Random rand = new Random();                             //comenzar random
                n = rand.nextInt(76);                                  //entre 0 y 107
            } while (cartas[n] == "0");                                 //mientras sea un 0 buscar otro valor, por que 0 significa que ya no existe
            String cname = cartas[n];                                   //guardamos el nombre en variable temporal
            cartas[n] = "0";                                            //la casilla le ponemos 0 para ya no usarla
            int resID = res.getIdentifier(cname, "drawable", getPackageName()); //buscamos la carta en la carpeta drawable con el nombre
            player1[i].setImageResource(resID);                         //asignamos la imagen en la carta que se visualiza
            splayer1[i] = cname;                                        //guardamos el nombre en el array de los nombres de las cartas del jugador 1
        }
        //asignamos cartas a los demas jugadores
        for (int h = 0; h < (p - 2); h++) {                             //repartir cartas para los otros jugadores, p = numero de jugadores conectadoes
            for (int i = 0; i < 7; i++) {                               //Asignar 7 cartas a cada jugadore
                do {
                    Random rand = new Random();                         //comenzar random
                    n = rand.nextInt(76);                              //entre 0 y 107
                } while (cartas[n] == "0");                             //mientras sea un 0 buscar otro valor
                String cname = cartas[n];                               //guardamos el nombre
                cartas[n] = "0";                                        //la casilla le ponemos 0
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
            n = rand.nextInt(76);                                          //entre 0 y 107
        }
        while ((cartas[n] == "0") || (cartas[n] == "w4") || (cartas[n] == "wc"));   //mientras sea un 0, WC, W4 entonces buscar otro valor
        String cname = cartas[n];                                           //guardamos nombre
        cartas[n] = "0";                                                    //la casilla le ponemos 0 para ya no usarla
        int resID = res.getIdentifier(cname, "drawable", getPackageName()); //buscamos la carta en el drawable
        centercard.setImageResource(resID);                                 //asignamos una carta aleatoria al centro
        cartaenelcentro = cname;                                            //guardamos el nombre en una variable globar para compara cada carta
    }

    void AcomodarCartasJugador1() {
        Resources res = getResources();                                     //obtener la clase actual de recursos
        int startpos = 0, i = 0;                                            //posicion inicial de la primera carta
        int dis = obtenerdistanciaentrecartas(cntcartasp1);                 //distancia entre cartas varia segun el numero de cartas

        startpos = (((cntcartasp1 * dis) / 2) - (dis / 2));                 //sacamos la posicion mas a la izquierda para saber desde donde poner las cartas
        while (i < cntcartasp1) {                                           //mientras hay cartas en el jugador 1
            int resID = res.getIdentifier(splayer1[i], "drawable", getPackageName());   //buscamos la carta la carpeta drawable
            player1[i].setImageResource(resID);                             //asignamos la imagen en la carta que se visualiza
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) player1[i].getLayoutParams();      //marco donde se acomodan las cartas
            params.rightMargin = startpos;                                  //asignamos posicion
            player1[i].setLayoutParams(params);                             //ubicamos posicion en base al marco donde se encuentra la imagen
            player1[i].setVisibility(View.VISIBLE);                         //MOSTRAMOS la carta
            if (validarcarta(splayer1[i]) == true){                         //si es una carta valida comparada con la que esta en el centro la habilitamos
                player1[i].setColorFilter(Color.argb(0, 255, 255, 255));
            } else {                                                        //si no es valida con la carta del centro (tirada) la obscurecemos
                player1[i].setColorFilter(Color.argb(119, 119, 119, 119));
            }
            startpos -= dis;                                                //decrementamos distacia (puntos) en posicion entre carta
            i++;                                                            //incrementamos el numero de cartas puestas
        }
    }

    void AcomodarCartasJugador2() {
        Resources res = getResources();                                     //obtener la clase actual de recursos
        int startpos = 0, i = 0, dis = 14;                                  //posicion inicial de la primera carta
        String namec = "x";
        startpos = (((cntcartasp2 * dis) / 2) - (dis / 2));                 //sacamos la posicion mas a la izquierda para saber desde donde poner las cartas
        while (i < cntcartasp2) {                                           //mientras hay cartas en el jugador 2
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) player2[i].getLayoutParams();      //marco donde se acomodan las cartas
            if (players == 2) {                                             //depende de la cantidad de jugadores
                namec = "backc";                                            //se pondra enfrente del jugador 1
                params.leftMargin = startpos;                               //asignamos posicion
            } else {
                namec = "backb";                                            //o se pondra a la izquierda del jugador 1
                params.bottomMargin = startpos;                             //asignamos posicion
            }
            int resID = res.getIdentifier(namec, "drawable", getPackageName());   //asignamos la imagen en la carta que se visualiza
            //buscamos la carta la carpeta drawable
            player2[i].setImageResource(resID);                             //asignamos la imagen en la carta que se visualiza
            player2[i].setLayoutParams(params);                             //ubicamos posicion en base al marco donde se encuentra la imagen
            player2[i].setVisibility(View.VISIBLE);                         //mostamos la carta
            startpos -= dis;                                                //decrementamos 14 puntos de distancia en posicion
            i++;                                                            //incrementamos el numero de cartas puestas
        }
    }

    void menudecolores() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create(); //creamos mensaje de alerta
        alertDialog.show();                                                 //mostramos mensaje de alerta
        Window win = alertDialog.getWindow();                               //convertimos el mendsaje de alerta en un formulario
        win.setContentView(R.layout.menucolores);                           //le asignamos la actividad de menu de colores
        ImageButton brojo = (ImageButton) win.findViewById(R.id.crojo);     //cada imagen de color es un boton interactuable
        brojo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {                                   //si se toco el boton/color ROJO
                // TODO Auto-generated method stub
                mensajeT.setVisibility(View.VISIBLE);                       //VISUZLIZMOS TEXTO
                mensajeT.setBackgroundColor(0xFFF40000);                    //le ponemos fondo rojo
                mensajeT.setText("ROJO");                                   //TEXTO "ROJO"
                cartaenelcentro = "rX";                                     //la carta del centro se va a llamar rX
                disponibilidaddecartas();                                   //habilitamos/deshabilitamos cartas segun la carta del centro
                alertDialog.cancel();                                       //cerramos mensaje de alerta
            }
        });
        ImageButton bazul = (ImageButton) win.findViewById(R.id.cazul);     //lo mismo que lo anterior pero en color AZUL
        bazul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mensajeT.setVisibility(View.VISIBLE);
                mensajeT.setBackgroundColor(0xFF00E6FF);
                mensajeT.setText("AZUL");
                cartaenelcentro = "bX";
                disponibilidaddecartas();
                alertDialog.cancel();
            }
        });
        ImageButton bverde = (ImageButton) win.findViewById(R.id.cverde);   //lo mismo que lo anterior pero en VERDE
        bverde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mensajeT.setVisibility(View.VISIBLE);
                mensajeT.setBackgroundColor(0xFF2FFF00);
                mensajeT.setText("VERDE");
                cartaenelcentro = "gX";
                disponibilidaddecartas();
                alertDialog.cancel();
            }
        });
        ImageButton bamarillo = (ImageButton) win.findViewById(R.id.camarillo);//lo mismo que lo anterior pero AMARILLO
        bamarillo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mensajeT.setVisibility(View.VISIBLE);
                mensajeT.setBackgroundColor(0xFFFFEE00);
                mensajeT.setText("AMARILLO");
                cartaenelcentro = "yX";
                disponibilidaddecartas();
                alertDialog.cancel();
            }
        });
    }

    void AcomodarCartasJugador3() {                                         //LO MISMO QUE ACOMODAR AL JUGADOR 2 PERO PARA EL 3
        Resources res = getResources();                                     //obtener la clase actual de recursos
        int startpos = 0, i = 0, dis = 14;                                  //posicion inicial de la primera carta
        String namec = "x";
        startpos = (((cntcartasp3 * dis) / 2) - (dis / 2));                 //sacamos la posicion mas a la izquierda para saber desde donde poner las cartas
        while (i < cntcartasp3) {                                           //mientras hay cartas en el jugador 3
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) player3[i].getLayoutParams();      //marco donde se acomodan las cartas
            if (players == 3) {                                             //si hay 3 jugadores lo ponemos a la izqauierda del juhgador 1
                namec = "backd";
                params.topMargin = startpos;                                //asignamos posicion
            } else {                                                        //si hay 4 jugadores lo ponemos enfrende del jugador 1
                namec = "backc";
                params.leftMargin = startpos;                               //asignamos posicion
            }
            int resID = res.getIdentifier(namec, "drawable", getPackageName());//asignamos la imagen en la carta que se visualiza
            //buscamos la carta la carpeta drawable
            player3[i].setImageResource(resID);                             //asignamos la imagen en la carta que se visualiza
            player3[i].setLayoutParams(params);                             //ubicamos posicion en base al marco donde se encuentra la imagen
            player3[i].setVisibility(View.VISIBLE);                         //mostramos la carta (lo de atras) del jgador 3
            startpos -= dis;                                                //decrementamos 14 puntos en posicion
            i++;                                                            //incrementamos el numero de cartas puestas
        }
    }

    void AcomodarCartasJugador4() {                                         //lo mismo que ACOMODARCARTASJUGADOR2 pero para el 4
        Resources res = getResources();                                     //obtener la clase actual de recursos
        int startpos = 0, i = 0, dis = 14;                                  //posicion inicial de la primera carta
        String namec = "x";
        startpos = (((cntcartasp4 * dis) / 2) - (dis / 2));                 //sacamos la posicion mas a la izquierda para saber desde donde poner las cartas
        while (i < cntcartasp4) {                                           //mientras hay cartas en el jugador 3
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) player4[i].getLayoutParams();      //marco donde se acomodan las cartas
            namec = "backd";                                                //se ponen a la izquierda del jugador 1
            params.topMargin = startpos;                                    //asignamos posicion
            int resID = res.getIdentifier(namec, "drawable", getPackageName());  //asignamos la imagen en la carta que se visualiza
            //buscamos la carta la carpeta drawable
            player4[i].setImageResource(resID);                             //asignamos la imagen en la carta que se visualiza
            player4[i].setLayoutParams(params);                             //ubicamos posicion en base al marco donde se encuentra la imagen
            player4[i].setVisibility(View.VISIBLE);                         //mostramos la carta (lo de atras) del jgador 4
            startpos -= dis;                                                //decrementamos 14 puntos en posicion
            i++;                                                            //incrementamos el numero de cartas puestas
        }
    }

    void reordenarcartasjugador1(int id) {                                  //reorganizar las cartas restantes
        for (int i = 0; i <= cntcartasp1; i++) {                            //debemos limpiar el contenido (visual) de las cartas
            player1[i].setImageDrawable(null);                              //eliminamos las cartas actuales visualmente
            player1[i].setVisibility(View.INVISIBLE);                       //las hacemos invisibles
        }
        int j = id;                                                         //indice de apartir de cual se van a acomodar
        while (j < cntcartasp1) {                                           //recorriendo desde la carta que se toco hasta la ultima
            splayer1[j] = splayer1[j + 1];                                  //recorrer las cadenas una posicion para ocupar el espacio ocupado
            j++;
        }
        splayer1[cntcartasp1] = "0";                                        //asignamos 0 al final para no tener un null
    }

    void cambiarimagendelcentro(String vs) {                                //escondemos imagen del centro
        String[] Operation = vs.split(delimiter);
        mensajeT.setText("");                                               //eliminamos cualquier texto
        mensajeT.setVisibility(View.INVISIBLE);                             //escondemos TEXTO del centro
        Resources res = getResources();                                     //obtener la clase actual de recursos
        int resID = res.getIdentifier(Operation[5].toString(), "drawable", getPackageName());    //buscamos la carta en el drawable
        centercard.setImageResource(resID);                                 //asignamos la imagen en la carta del centro
        //ESTE ES EL TEXTO qEU CONTINUAMENTE CAMBIA PARA SABER CONTRA QUE VALIDAMOS CADA MOVIMIENTO
        cartaenelcentro = Operation[5].toString();
        // also validate player cards
        validarcarta(Operation[5].toString());
    }

    int obtenerdistanciaentrecartas(int c) {                                //distancia entre cartas del jugador 1
        int dis = 0, t = 12;                                                //variables locales auxialoares
        if ((c > 0) && (c <= 11))                                           //si son menos de 12 cartas,
            dis = 70;                                                       //se deben acomodar cada 70 puntos de distancia una de otra
        else if ((c > 11) && (c < 24)) {                                    //entre 12 y 23 cartas
            dis = 60;
            while (t++ < c) dis -= 3;                                       //reducir distancia
        } else                                                              //si son mas de 23 cartas se deja fija la distancia NI MODO
            dis = 24;
        return dis;                                                         //regresamos la distancia
    }

    void cambiardireccion(){
        swapArrow = !swapArrow;                                             //cambiar la direccion
        if (swapArrow == true) {                                            //direccion en base a las manecillas del reloj
            for (int i = 0; i < 4; i++) {                                   //las flechas que se muestran
                IVclock[i].setVisibility(View.VISIBLE);                     //visualizar de estas flechas
                IVanticlock[i].setVisibility(View.INVISIBLE);               //esconder estas flechas
            }
        } else {                                                            //si es en contra de las manecillas del reloj
            for (int i = 0; i < 4; i++) {                                   //las flechas que se muestran
                IVclock[i].setVisibility(View.INVISIBLE);                   //esconde estas
                IVanticlock[i].setVisibility(View.VISIBLE);                 //muestra esta
            }
        }
    }

    void cambiarturno() {
        /*if (swapArrow == true) {                                            //clockwise direccion
            do {
                dirHand++;                                                  //imcremntamos indice
                if (dirHand > 3)                                            //si recorre la mano mas de 4 posiciones
                    dirHand = 0;                                            //regresa al inicio del array de turnos
            }
            while (turnos[dirHand] == 0);                                   //mientras sea cero buscar otro jugador activo para ponerle la mano apuntando
        } else {                                                            //anticlockwise direction
            do {
                dirHand--;                                                  //reducimos indice
                if (dirHand < 0)                                            //si es menos de 0
                    dirHand = 3;                                            //posiciones poner en posicion 3
            }
            while (turnos[dirHand] == 0);                                   //mientras sea cero buscar otro jugador activo para ponerle la mano a señalar
        }
        for (int i = 0; i < 4; i++)
            hands[i].setVisibility(View.INVISIBLE);                         //esconder todas las mano
        hands[dirHand].setVisibility(View.VISIBLE);                         //Mostar solo la mano del jugador que sigue en turno
        //turno = turnos[dirHand];   */                                         //guardamos el TURNO DEL JUGADOR QUE SIGUE
    }

    //****NO SE ESTA UTILIZANDO EL CLICKLISTENER
    public void onClick(View v) {
        switch (v.getId()) {  }
    }

    void cartaAleatoria(){
        Resources res = getResources();                                     //obtener la clase actual de recursos
        int n = 0;
        do {
            Random rand = new Random();                                     //comenzar random
            n = rand.nextInt(76);                                          //entre 0 y 107
        } while (cartas[n] == "0");                                         //mientras sea un 0 buscar otro valor, por que 0 significa que ya no existe
        String cname = cartas[n];                                           //nombre de la carta seleccionada
        cartas[n] = "0";                                                    //la casilla le ponemos 0 para ya no usarla
        if (turno == 1) {                                                   //si es el jugador 1
            ++cntcartasp1;                                                  //incrementamos su cantidad de cartas
            int resID = res.getIdentifier(cname, "drawable", getPackageName()); //buscamos la carta en la carpeta drawable
            player1[cntcartasp1-1].setImageResource(resID);                 //asignamos la imagen en la carta que se visualiza
            splayer1[cntcartasp1-1] = cname;                                //guardamos el nombre en el array de los nombres de las cartas del jugador 1
            for (int i = 0; i <= cntcartasp1; i++) {                        //debemos limpiar el contenido (visual) de las cartas
                player1[i].setImageDrawable(null);                          //eliminamos las cartas actuales visualmente
                player1[i].setVisibility(View.INVISIBLE);                   //las escondemos
            }
        } else if(turno == 2) {                                             //si es el turno del jugador 2
            ++cntcartasp2;                                                  //incrementamos su cantida de cartas
            splayer2[cntcartasp2-1] = cname;                                //guardamos el nombre de la carta en el array de cartas que tiene actualmente
            for (int i = 0; i <= cntcartasp2; i++) {                        //debemos limpiar el contenido (visual) de las cartas
                player2[i].setImageDrawable(null);                          //eliminamos las cartas actuales visualmente
                player2[i].setVisibility(View.INVISIBLE);
            }
            //AcomodarCartasJugador2();                                       //ACOMODAMOS LAS CARTAS DEL JUGADOR 2
        } else if(turno == 3) {                                             //LO MISMO QUE EL JGADOR 2 pero apra el 3
            ++cntcartasp3;
            splayer3[cntcartasp3-1] = cname;
            for (int i = 0; i <= cntcartasp3; i++) {                        //debemos limpiar el contenido (visual) de las cartas
                player3[i].setImageDrawable(null);                          //eliminamos las cartas actuales visualmente
                player3[i].setVisibility(View.INVISIBLE);
            }
            AcomodarCartasJugador3();
        }  else if(turno == 4) {                                            //lo mismo que el jugador 2 pero para el 4
            ++cntcartasp4;
            splayer4[cntcartasp4-1] = cname;
            for (int i = 0; i <= cntcartasp4; i++) {                        //debemos limpiar el contenido (visual) de las cartas
                player4[i].setImageDrawable(null);                          //eliminamos las cartas actuales visualmente
                player4[i].setVisibility(View.INVISIBLE);
            }
            AcomodarCartasJugador4();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cModuleB != null) {
            cModuleB.stop();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Second Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://uno.app.uag.com.unouag/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Second Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://uno.app.uag.com.unouag/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    void setUpCommunicationModule(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Initialize the BluetoothChatService to perform bluetooth connections
        cModuleB = new CommunicationModuleB(this, mHandler);

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = SecondActivity.this;
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }

    void defineRole(String paquete){
        currentTurn=false;
        Log.d(TAG, "defineRole(), Received: " + paquete);
        //cModuleB.stop();
        String[] Operation = paquete.split(delimiter);
        connectedPlayers = Operation[1].toString().split(",");
        connectedplayers = Operation[1].toString();
        Log.d(TAG,Operation[1].toString());
        players = Integer.parseInt(Operation[2].toString());                  //guardarlo en variable players
        Log.d(TAG,Operation[2].toString());
        turnsArray =  Operation[3].toString().split(",");
        turnsarray = Operation[3].toString();
        Log.d(TAG,"" + Operation[4].toString());
        inTurn = Integer.parseInt(Operation[4].toString());
        turno = inTurn + 1;
        String card = Operation[5].toString();
        allHands = Operation[6].toString().split(",");
        Log.d(TAG,"cartas de cada jugador: " + Operation[6].toString());
        //playersState = Operation[7].toString().split(",");
        //playersstate = Operation[7].toString();
        for(int i=0; i<connectedPlayers.length; i++){
            if(mBluetoothAdapter.getAddress().equals(connectedPlayers[i].toString()) && (turnsArray[i].toString().equals("" +inTurn))){
                //Current server
                currentTurn=true;
                myTurn=inTurn;
                cntcartasp1 = Integer.parseInt(allHands[i]);
                Log.d(TAG, "Its my turn: " + connectedPlayers[i].toString() + ", turn: " + turnsArray[i].toString());
            }
            emptyHand(Integer.parseInt(allHands[i]),Integer.parseInt(turnsArray[i]));
        }
    }

    void giveItSomeTime(){
        long begin = System.currentTimeMillis();
        long now=0;
        while(now-begin<6000){
            now = System.currentTimeMillis();
        }
    }

    void startCommunicationEngine(){
        int tries=0;
        if(!server){
            Log.d(TAG,"Current Client");
            cModuleB.start();
        }else{
            Log.d(TAG, "Current Server");
            //giveItSomeTime();
            createCommunication();
            while(players != cModuleB.getPlayersOnline() + 1 &&  tries < 5){
                giveItSomeTime();
                createCommunication();
                tries++;
            }
            /*for(int i=0; i<connectedPlayers.length; i++){
                if(!mBluetoothAdapter.getAddress().equals(connectedPlayers[i].toString())){
                    boolean found = false;
                    for(int j=0; j<cModuleB.getPlayersOnline(); j++){
                        if(cModuleB.getMACs()[j].equals(connectedPlayers[i].toString())){
                            found = true;
                        }
                    }
                    if(!found){
                        //playersState[i] = "online";
                    }else{
                        //playersState[i] = "offline";
                    }
                }else{
                    //playersState[i] = "online";
                }
            }
            /*playersstate = "";
            for(int j=0; j<connectedPlayers.length - 1; j++){
                playersstate = playersstate + playersState[j] + ",";
            }
            playersstate = playersstate + playersState[connectedPlayers.length];*/
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        cModuleB.connect(device, secure);
    }

    private void sendMessage(String message) {
        Log.d(TAG,"Sending paquete...");

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            cModuleB.write(send);

            // Reset out string buffer to zero and clear the edit text field
            //mOutStringBuffer.setLength(0);
        }
        Log.d(TAG, "paquete sent");
    }

    void notifyOthers(String message){
        sendMessage(message);
    }

    void connectionLost(){
        // si se pierde la conexion con el servidor
        if(!server){
            Log.d(TAG,"inTurn: " + inTurn + " myTurn: " + myTurn);
            int tmp = myTurn;
            if (myTurn==0){myTurn=players+1;}

            if(inTurn == myTurn - 1){
                Log.d(TAG,"become server");
                currentTurn=true;
                inTurn=myTurn;
                server=true;
            }else{
                Log.d(TAG,"start listening mode");
                currentTurn=false;
                server=false;
            }
            myTurn = tmp;
            //setUpCommunicationModule();
            startCommunicationEngine();
            disponibilidaddecartas();
        }else{
            //setUpCommunicationModule();
            cModuleB.start();
            currentTurn=false;
            server=false;
        }
    }

    void updateAllHands(){
        for(int i=0; i<connectedPlayers.length; i++){
            if(mBluetoothAdapter.getAddress().equals(connectedPlayers[i].toString())){
                allHands[i] = "" + cntcartasp1;
                Log.d(TAG, "cards in turn: " + cntcartasp1);
            }
        }
        allhands = "";
        for(int i=0; i<connectedPlayers.length - 1; i++){
            allhands = allhands + allHands[i] + ",";
        }
        allhands = allhands + allHands[connectedPlayers.length - 1];
        Log.d(TAG, "one card was sent: " + allhands);
    }

    boolean emptyHand(int cartas, int turno){
        boolean winner = false;
        //SI ALGUNO DE LOS JUGADORES SE LES TERMINO LAS CARTAS
        if(cartas == 0){
            winner = true;
            if(turno == 0)
                ganador1.setVisibility(View.VISIBLE);
            else if (turno == 1)
                ganador2.setVisibility(View.VISIBLE);
            else if (turno == 2)
                ganador3.setVisibility(View.VISIBLE);
            else if (turno == 3)
                ganador4.setVisibility(View.VISIBLE);

            //DEBIDO A QUE ALGUIEN GANO ESCONDEMOS TODAS LAS IMAGENES
            /*for(int i=0;i<4;i++)   IVclock[i].setVisibility(View.INVISIBLE);        //escondemos las flecha
            for(int i=0;i<4;i++)   IVanticlock[i].setVisibility(View.INVISIBLE);    //escondemos las flechas al reve
            for(int i=0;i<4;i++)   hands[i].setVisibility(View.INVISIBLE);          //escondemos las manos
            //ESCONDEMOS TODAS LAS CARTAS DE CADA JUGADOR
            for(int i=0;i<cntcartasp1;i++)
                player1[i].setVisibility(View.INVISIBLE);
            for(int i=0;i<cntcartasp2;i++)
                player2[i].setVisibility(View.INVISIBLE);
            for(int i=0;i<cntcartasp3;i++)
                player3[i].setVisibility(View.INVISIBLE);
            for(int i=0;i<cntcartasp4;i++)
                player4[i].setVisibility(View.INVISIBLE);*/
            //MOSTRAMOS EL MENU
            btnmenu.setVisibility(View.VISIBLE);
        } /*else{
            reordenarcartasjugador1(n);                                     //reorganizamos el array de string de los nombres de las cartas del jugador1
            AcomodarCartasJugador1();                                       //mostramos las cartas que restan
            disponibilidaddecartas();                                       //habilitamos/deshabilitamos las cartas segun la carta tirada
        }*/
        return winner;
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = SecondActivity.this;
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case CommunicationModuleB.STATE_CONNECTED:
                            //sendAddress(mBluetoothAdapter.getAddress());
                            //setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            //mConversationArrayAdapter.clear();
                            break;
                        case CommunicationModuleB.STATE_CONNECTING:
                            //setStatus(R.string.title_connecting);
                            break;
                        case CommunicationModuleB.STATE_LISTEN:
                        case CommunicationModuleB.STATE_NONE:
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    cambiarimagendelcentro(writeMessage);
                    defineRole(writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String message = new String(readBuf, 0, msg.arg1);
                    //processPackage(message);
                    //showMessage(message);
                    if(server){
                        notifyOthers(message);
                    }
                    cambiarimagendelcentro(message);
                    defineRole(message);
                    disponibilidaddecartas();
                    //mConversationArrayAdapter.add(":  " + readMessage);
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.CONNECTION_LOST:
                    connectionLost();
                    break;
            }
        }
    };
}





