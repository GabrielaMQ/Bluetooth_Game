package uno.app.uag.com.unouag;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.SyncStateContract;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    // Get the device MAC address
    String user1Name;
    String user1MAC;
        /*
        String user2Name;
        String user2MAC;
        String user3Name;
        String user3MAC;
        String user4Name;
        String user4MAC;
        */

    /**
     * Member object for the chat services
     */
    private BluetoothService mService = null;

    private static final String TAG = "MainActivity";
    private BluetoothAdapter btAdapter;
    private static final int DISCOVERY_REQUEST = 1;
    public String toastText ="";
    private BluetoothDevice remoteDevice;
    public Boolean secure= false;

    ListView listview;                                      //lista de los jugadores
    String[] valores1 = new String[]{"Lupe Esparza", "Ramiro", "Javier", "Choche"};     //NOMBRES DE EJEMPLOS
    ArrayList<String> Conectados = new ArrayList<String>(); //lista de usuarios conectados

    TextView buscando;                                      //mensaje de buscando
    TextView titulo;                                        //texto que muestra la leyenda "Jugadores"
    TextView njugadores;                                    //texto con la cantidad de jugadores
    Button botonIniciar;                                    //boton de jugar para sincronizar jugadores
    TextView debug;
   // Button botonUnirse;                                     //boton para unirse a partida
    Button botonBuscar;                                     //boton buscar
    Button botonComenzar;                                   //boton de comenzar juego
    Button botonSincronizar;                                //boton de comenzar juego
    Button botonEnviar;
    Button botonLeer;
    ImageView ArrowLeft;                                    //flecha izquierda
    ImageView ArrowRight;                                   //flecha derecha

    Integer Decrementador = 10;                             //indice de segundo
    Integer indiceA = 1;                                    //contador de jugadores conectados
    Integer players = 2;                                    //numero de jugadores conectados
    Integer playersR = 2;                                   //numero (RANDOM) de jugadores conectados TEMPORAL EN UNIRSE A PARTIDA
    Integer tipoconexion = 0;                               //tipo de inicio de juego, 1 = iniciar partida, 2 = unirse a partida

    boolean primerbuscada=false;                            //bandera TEMPORAL PARA SIMULAR que faltan jugadores
    boolean candado = false;                                //bandera TEMPORAL PARA SIMULAR que faltan jugadores

    ArrayAdapter<String> adapter;                           //adaptador de array

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Start Bluetooth ASUSARR
        TurnOnBT();

        Random rand = new Random();                                     //comienza randomizado
        playersR = rand.nextInt(3) + 2;                                 //de 2  a 4 jugadores

        //Toast.makeText(getApplicationContext(), "players= " + playersR, Toast.LENGTH_SHORT).show();   //muestra los jugadores aceptados
        //Conectados.add("Lupe Esparza");                                 //meter al primer jugadores
        listview = (ListView) findViewById(R.id.lista);                 //relacionar la lista(visual) con el campo lista
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Conectados); //realcionamos el array de conetados con el adapter

        listview.setAdapter(adapter);                                   //conecatmos adapatador con el control lista
        buscando = (TextView) findViewById(R.id.mensaje2);              //relacionamos el texto del mensaje con el texto
        botonIniciar = (Button) findViewById(R.id.btnIniciar);          //relacionamos boton iniciar Partida
        botonComenzar = (Button) findViewById(R.id.btncomenzar);        //relacionamios boton Comenzar partida
        botonSincronizar = (Button) findViewById(R.id.btnSincronizar);  //relacionamios boton Sincronizar partida
        botonBuscar = (Button) findViewById(R.id.btnBuscar);            //relacionamos boton buscar jugadores
        botonEnviar = (Button) findViewById(R.id.btn_send);
        botonLeer = (Button) findViewById(R.id.btn_leer);
        //botonUnirse = (Button) findViewById(R.id.btnUnirse);            //relacionamos boton de unirse a partida
        ArrowLeft = (ImageView) findViewById(R.id.arrowleft);           //relacionamos la flecha izquierda
        ArrowRight = (ImageView) findViewById(R.id.arrowright);         //realcionamos la flecha derecha
        titulo = (TextView) findViewById(R.id.letrerojug);              //relacionamos el texto del letrero "JUGADORES"
        njugadores = (TextView) findViewById(R.id.numerojugadores);     //relacionamos el texto del numero de jugadores que queremos
        //debug = (TextView) findViewById(R.id.tv_debug);                 //text view de debug

        //Llama a setup(); este inicia el servicio SERVER/CLIENT
        //setup();

        botonLeer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mService.getState()==0)
                    Toast.makeText(getApplicationContext(),"STATE_NONE",Toast.LENGTH_SHORT).show();
                if (mService.getState()==1)
                    Toast.makeText(getApplicationContext(),"STATE_LISTEN",Toast.LENGTH_SHORT).show();
                if (mService.getState()==2)
                    Toast.makeText(getApplicationContext(),"STATE_CONNECTING",Toast.LENGTH_SHORT).show();
                if (mService.getState()==3)
                    Toast.makeText(getApplicationContext(),"STATE_CONNECTED",Toast.LENGTH_SHORT).show();
            }
        });

        botonIniciar.setOnClickListener(this);                              //hacemos que este pendiente al clic del usuario

        botonEnviar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendMessage("HOLA MUNDO");
                Intent intent = new Intent(MainActivity.this,SecondActivity.class);     //preparamos la siguiente actividad (formulario)
                startActivity(intent);                              //iniciamos segunda actividad
            }
            });

        botonComenzar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                // Get the device MAC address
                 user1Name = Conectados.get(0);
                 user1MAC = Conectados.get(1);
                /*
                user2Name = Conectados.get(2);
                user2MAC = Conectados.get(3);
                user3Name = Conectados.get(4);
                user3MAC = Conectados.get(5);
                user4Name = Conectados.get(6);
                user4MAC = Conectados.get(7);
                */

                // Get the BluetoothDevice object
                BluetoothDevice device = btAdapter.getRemoteDevice(user1MAC);

                //Intenta conectarse al dispositivo
                mService.connect(device, secure);

                // Revisa si esta conectado por lo menos a un dispositivo
                if (mService.getState() != BluetoothService.STATE_CONNECTED) {
                    Log.d(TAG,"NOT_connected");
                    return;
                }

                adapter.notifyDataSetChanged();//Refresca el adapter
            }
        });

        botonBuscar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //findDevicesPaired
                listview.setVisibility(View.VISIBLE);
                //toastText="Checking for know paired devices...";
                //Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
                Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
                for (BluetoothDevice pairedDevice : pairedDevices)
                {
                    Conectados.add(pairedDevice.getName());
                    Conectados.add(pairedDevice.getAddress());
                    remoteDevice=pairedDevice;
                }
                Conectados.add("Fin");
                adapter.notifyDataSetChanged();

                botonComenzar.setVisibility(View.VISIBLE);
            }
        });

        ArrowLeft.setOnTouchListener(new View.OnTouchListener() {       //si tocamos la flecha izquierda
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);    //obscurecemos mientreas presionamos
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        view.getDrawable().clearColorFilter();
                        view.invalidate();
                        if (players > 2) players--;                     //reducimos la cantidad de jugadores hasta 2
                        njugadores.setText(players.toString());         //mostramos la cantidad de jugadores
                        break;
                    }
                }


                return true;
            }
        });

        ArrowRight.setOnTouchListener(new View.OnTouchListener() {      //si se toco la flecha derecha
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);    //obscurecemos la flecha
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        view.getDrawable().clearColorFilter();
                        view.invalidate();
                        if (players < 4) players++;                     //incrementamos el numero de jugadores hasta 4
                        njugadores.setText(players.toString());         //mostramos la cantidad de jugadores
                        break;
                    }
                }
                return true;
            }
        });

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();    //ejecuta esta aplicacion
    }


    CountDownTimer myCountDown = new CountDownTimer(10000, 1000) {  //timer de 10 segundos cada segundo
        public void onTick(long millisUntilFinished) {              //funcion activa cada segundo
            Decrementador--;                                        //restale uno al contador
            if(tipoconexion == 1){                                  //si es INICIAR PARTIDA
                buscando.setText("Buscando..." + Integer.toString(Decrementador));          //muestra el segundo
                if(indiceA < players){                               //mientras no se completen jugadores conectados
                    //**************ATENCION: MODIFICAR ESTO CUANDO SE DESARROLLE EL MODULO DE BLUETOOTH ******
                    if(indiceA == (players-1) && primerbuscada == false)    //si falta un jugador de conectarse
                            candado = true;                                 //bandera para volver a buscar
                    if(candado == false){
                        Conectados.add(indiceA,valores1[indiceA]);          //agregarlos a la lista
                        indiceA++;                                          //incrementar jugadores conectados
                        adapter.notifyDataSetChanged();                     //actualizar lista
                        listview.smoothScrollToPosition(adapter.getCount());//visualizar el ultimo registro en la lista
                    }
                    //******************************************************************************************
                }
            } else {                                                 //SI ES UNIRSE A PARTIDA
                //********SIMULACION DE SINCRONIZACION *********************
                //****AQUI SE MANDARIA EL DATO AL SERVIDOR PARA CONECTARLO
                buscando.setText("Sincronizando..." + Integer.toString(Decrementador) ); //muestra segundos
                //**********************************************************
            }
        }
        public void onFinish() {                                            //funcion que se activa al terminar el timer
            if(tipoconexion == 1){                                          //SI ES INICIAR PARTIDA
                if(players == indiceA){                                     //SI COMPLETO LOS JUGADORES
                    buscando.setVisibility(View.INVISIBLE);                 //escondemos texto buscando
                    botonComenzar.setVisibility(View.VISIBLE);              //mostrar boton de comenzar
                } else{                                                     //si no se completo los jugadores al terminar el TIMER
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);   //creamos mensaje de alerta
                    builder.setTitle("Atención:");                          //le ponemos titulo
                    builder.setMessage("Faltan Jugadores...");              //pregunta
                    builder.setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {//opcion de buscar de nuevo
                            candado = false;                                //bandera TEMPORAL de volver a buscar
                            primerbuscada = true;                           //bandera TEMPORAL, para solo buscar una sola vez
                            Decrementador = 10;                             //reiciciar el contado de segundos
                            myCountDown.start();                            //reiniciar el TIMER
                            dialog.dismiss();                               //cerramos este mensaje de alerta
                        }
                    });
                    builder.setNegativeButton("Comenzar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//si comenzamos con los que encontro
                            botonComenzar.setVisibility(View.VISIBLE);      //mostrar boton de comenzar
                            dialog.dismiss();                               //cerramos este mensaje
                        }
                    });

                    AlertDialog alert = builder.create();                   //creamos este mensajoe
                    alert.show();                                           //lo mostramos
                }
            } else {                                                        //si la opcion es UNIRSE A PARTIDA
                buscando.setText("Sincronizado");                           //mostramos esta palabra
                players = playersR;                                         //pasamos el valor de jugadores RANDOM a la variable de jugadores
                botonComenzar.setVisibility(View.VISIBLE);                  //mostrar boton de comenzar
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnIniciar:                                       //si le dimos clic en el boton INICIAR PARTIDA
                botonIniciar.setVisibility(View.INVISIBLE);             //escondemos boton INICIAR PARTIDA
                //botonUnirse.setVisibility(View.INVISIBLE);              //escondemos boton UNIRSE A PARTIDA
                titulo.setVisibility(View.VISIBLE);                     //mostramos leyenda "JUGADORES"
                njugadores.setVisibility(View.VISIBLE);                 //mostramos cnatidad de jugadores
                ArrowLeft.setVisibility(View.VISIBLE);                  //mostramos flecha izquierda
                ArrowRight.setVisibility(View.VISIBLE);                 //mostranmos flecha derecha
                botonBuscar.setVisibility(View.VISIBLE);                //mostramos boton buscar
                tipoconexion = 1;                                       //fue la opcion INICIAR PARTIDA
                break;
            /*case R.id.btnUnirse:                                        //Si le dimos clic a la opcion UNIRSE A PARTIDA
                botonIniciar.setVisibility(View.INVISIBLE);             //escondemos boton INICIAR PARTIDA
                botonUnirse.setVisibility(View.INVISIBLE);              //escondemos boton UNBIRSE A PARTIDA
                botonSincronizar.setVisibility(View.VISIBLE);           //mostramos boton sincronizar
                tipoconexion = 2;                                       //fue la opcion UNISRSE A PARTIDA
                break;
               */
            case R.id.btnBuscar:                                        //si le dimos clic al boton buscar
                ArrowRight.setVisibility(View.INVISIBLE);               //escondemos flicha
                ArrowLeft.setVisibility(View.INVISIBLE);                //escondemos fliecha
                botonBuscar.setVisibility(View.INVISIBLE);              //escondemos botonbuscar
                buscando.setVisibility(View.VISIBLE);                   //mostramos texto buscando
                listview.setVisibility(View.VISIBLE);                   //mostramos la lista
                myCountDown.start();                                    //iniciamos timer
                break;
            case R.id.btnSincronizar:                                   //si le dimos clic al boton de sincronizar
                botonSincronizar.setVisibility(View.INVISIBLE);         //escondemos el boton
                buscando.setText("Sincronizando...");                   //mosrtramos texto
                buscando.setVisibility(View.VISIBLE);                   //mostramos texto sinconimndpo
                myCountDown.start();                                    //iniciamos timer
                break;
            case R.id.btncomenzar:                                      //si le dimos clic al boton comenzar
                Intent intent = new Intent(MainActivity.this,SecondActivity.class);     //preparamos la siguiente actividad (formulario)
                intent.putExtra("PLAYERS",players);                         //guardamos el numero de jugadores en una varianle globla entre las actividaes
                startActivity(intent);                              //iniciamos segunda actividad
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"OnStart");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://uno.app.uag.com.unouag/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);

        setup();
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
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

    public void TurnOnBT(){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter.isEnabled()){
            Toast.makeText(MainActivity.this, "BT is ON", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(MainActivity.this, "BT is OFF", Toast.LENGTH_SHORT).show();
            //Forcing Turn BT ON
            String scanModeChanged = BluetoothAdapter.ACTION_SCAN_MODE_CHANGED;
            String beDiscoverable = BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE;
            IntentFilter filter = new IntentFilter(scanModeChanged);
            registerReceiver(bluetoothState, filter);
            startActivityForResult(new Intent(beDiscoverable), DISCOVERY_REQUEST);

        }

    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(getApplicationContext(), "not_connected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            //mOutStringBuffer.setLength(0);
            //mOutEditText.setText(mOutStringBuffer);
        }
    }


    //Detects any change on BT device
    BroadcastReceiver bluetoothState = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String prevStateExtra = BluetoothAdapter.EXTRA_PREVIOUS_STATE;
            String stateExtra = BluetoothAdapter.EXTRA_STATE;
            int state = intent.getIntExtra(stateExtra, -1);
            int previousState = intent.getIntExtra(prevStateExtra, -1);
            switch (state){
                case(BluetoothAdapter.STATE_TURNING_ON):
                    toastText="Bluetooth Turning ON";
                    Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
                    break;
                case (BluetoothAdapter.STATE_ON):
                    toastText="Bluetooth is ON";
                    Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
                    break;
                case (BluetoothAdapter.STATE_TURNING_OFF):
                    toastText="Bluetooth is Turning Off";
                    Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
                    break;
                case (BluetoothAdapter.STATE_OFF):
                    toastText="Bluetooth is OFF";
                    Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };



    @Override
    public void onDestroy() {
        super.onDestroy();
        //if (mService != null) {
        //   mService.stop();
        //}
    }

    private void setup() {
        Log.d(TAG, "setup()");

        // Initialize the BluetoothService to perform bluetooth connections
        //Start BluetoothService
        mService = new BluetoothService(MainActivity.this,mHandler);

    }

    /**
     * The Handler that gets information back from the BluetoothService
     */
     private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = MainActivity.this;
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            Log.d(TAG,"DEVICE CONNECTED");

                            break;
                        case BluetoothService.STATE_CONNECTING:
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Log.d(TAG, "MESSAGE_SENT");
                    Toast.makeText(activity, "MESSAGE_SENT", Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d(TAG,"MESSAGE_RECEIVED");
                    Toast.makeText(activity, "MESSAGE_RECEIVED", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to " + user1Name + " " + user1MAC, Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this,"CONNECTED SUCCESSFULLY-->LANZAR SIGUIENTE ACTIVITY", Toast.LENGTH_SHORT).show();
                        //Lanzar siguiente Activity
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

}//END

