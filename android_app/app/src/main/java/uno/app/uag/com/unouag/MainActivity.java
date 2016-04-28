package uno.app.uag.com.unouag;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.util.Log;
import android.widget.AdapterView;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    CommunicationModuleA cModule = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private StringBuffer mOutStringBuffer;
    private String mConnectedDeviceName = null;
    private static final String TAG = "BluetoothChatFragment";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    ArrayAdapter<String> mNewDevicesArrayAdapter;
    String[] connectedPlayers = new String[4]; //lista de usuarios conectados
    private ArrayAdapter<String> mConversationArrayAdapter;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    String address;
    String delimiter = new String("-");
    boolean server=false;
    String inTurn = "0";
    String connectedPlayersArray = "";
    String turnsArray = "";
    String playersState = "";
    String deviceName;
    //-------------------------------------------------------------------------
    ListView listview;                                      //lista de los jugadores
    String[] valores1 = new String[]{"Lupe Esparza", "Ramiro", "Javier", "Choche"};     //NOMBRES DE EJEMPLOS
    ArrayList<String> Conectados = new ArrayList<String>(); //lista de usuarios conectados

    TextView buscando;                                      //mensaje de buscando
    TextView titulo;                                        //texto que muestra la leyenda "Jugadores"
    TextView njugadores;                                    //texto con la cantidad de jugadores
    Button botonIniciar;                                    //boton de jugar para sincronizar jugadores
    Button botonUnirse;                                     //boton para unirse a partida
    Button botonBuscar;                                     //boton buscar
    Button botonComenzar;                                   //boton de comenzar juego
    Button botonSincronizar;                                //boton de comenzar juego
    ImageView ArrowLeft;                                    //flecha izquierda
    ImageView ArrowRight;                                   //flecha derecha

    Integer Decrementador = 20;                             //indice de segundo
    Integer indiceA = 0;                                    //contador de jugadores conectados
    Integer players = 2;                                    //numero de jugadores conectados
    //Integer playersR = 2;                                   //numero (RANDOM) de jugadores conectados TEMPORAL EN UNIRSE A PARTIDA
    Integer tipoconexion = 0;                               //tipo de inicio de juego, 1 = iniciar partida, 2 = unirse a partida

    boolean primerbuscada=false;                            //bandera TEMPORAL PARA SIMULAR que faltan jugadores
    boolean candado = false;                                //bandera TEMPORAL PARA SIMULAR que faltan jugadores

    ArrayAdapter<String> adapter;                           //adaptador de array

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Random rand = new Random();                                     //comienza randomizado
        //playersR = rand.nextInt(3) + 2;                                 //de 2  a 4 jugadores
        setUpCommunicationModule();
        //Toast.makeText(getApplicationContext(), "players= " + playersR, Toast.LENGTH_SHORT).show();   //muestra los jugadores aceptados
        Conectados.add("Lupe Esparza");                                 //meter al primer jugadores
        //listview = (ListView) findViewById(R.id.lista);                 //relacionar la lista(visual) con el campo lista
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Conectados); //realcionamos el array de conetados con el adapter

        //listview.setAdapter(adapter);                                   //conecatmos adapatador con el control lista
        buscando = (TextView) findViewById(R.id.mensaje2);              //relacionamos el texto del mensaje con el texto
        botonIniciar = (Button) findViewById(R.id.btnIniciar);          //relacionamos boton iniciar Partida
        botonComenzar = (Button) findViewById(R.id.btncomenzar);        //relacionamios boton Comenzar partida
        botonSincronizar = (Button) findViewById(R.id.btnSincronizar);  //relacionamios boton Sincronizar partida
        botonBuscar = (Button) findViewById(R.id.btnBuscar);            //relacionamos boton buscar jugadores
        botonUnirse = (Button) findViewById(R.id.btnUnirse);            //relacionamos boton de unirse a partida
        ArrowLeft = (ImageView) findViewById(R.id.arrowleft);           //relacionamos la flecha izquierda
        ArrowRight = (ImageView) findViewById(R.id.arrowright);         //realcionamos la flecha derecha
        titulo = (TextView) findViewById(R.id.letrerojug);              //relacionamos el texto del letrero "JUGADORES"
        njugadores = (TextView) findViewById(R.id.numerojugadores);     //relacionamos el texto del numero de jugadores que queremos

        botonIniciar.setOnClickListener(this);                          //hacemos que este pendiente al clic del usuario
        botonComenzar.setOnClickListener(this);                         //hacemos que este pendiente al clic del usuario
        botonUnirse.setOnClickListener(this);                           //hacemos que este pendiente al clic del usuario
        botonBuscar.setOnClickListener(this);                           //hacemos que este pendiente al clic del usuario
        botonSincronizar.setOnClickListener(this);                      //hacemos que este pendiente al clic del usuario

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


    CountDownTimer myCountDown = new CountDownTimer(20000, 1000) {  //timer de 10 segundos cada segundo
        public void onTick(long millisUntilFinished) {              //funcion activa cada segundo
            Decrementador--;                                        //restale uno al contador
            if(tipoconexion == 1){                                  //si es INICIAR PARTIDA
                buscando.setText("Buscando..." + Integer.toString(Decrementador));          //muestra el segundo
                    //Toast.makeText(getApplicationContext(), "players online= " + indiceA, Toast.LENGTH_SHORT).show();   //muestra los jugadores aceptados
                //if(indiceA < players){                               //mientras no se completen jugadores conectados
                    //**************ATENCION: MODIFICAR ESTO CUANDO SE DESARROLLE EL MODULO DE BLUETOOTH ******
                //    if(indiceA == (players-1) && primerbuscada == false)    //si falta un jugador de conectarse
                 //           candado = true;                                 //bandera para volver a buscar
                  //  if(candado == false){
                   //     Conectados.add(indiceA,valores1[indiceA]);          //agregarlos a la lista
                   //     indiceA++;                                          //incrementar jugadores conectados
                   //     adapter.notifyDataSetChanged();                     //actualizar lista
                   //     listview.smoothScrollToPosition(adapter.getCount());//visualizar el ultimo registro en la lista
                   // }
                    //******************************************************************************************
                //}
            } else {                                                 //SI ES UNIRSE A PARTIDA
                //********SIMULACION DE SINCRONIZACION *********************
                //****AQUI SE MANDARIA EL DATO AL SERVIDOR PARA CONECTARLO
                buscando.setText("Sincronizando..." + Integer.toString(Decrementador) ); //muestra segundos
                //**********************************************************
            }
        }
        public void onFinish() {                                            //funcion que se activa al terminar el timer
            printStatus();
            if(tipoconexion == 1){                                          //SI ES INICIAR PARTIDA
                if(players == indiceA){                                     //SI COMPLETO LOS JUGADORES
                    buscando.setVisibility(View.INVISIBLE);                 //escondemos texto buscando
                    botonComenzar.setVisibility(View.VISIBLE);              //mostrar boton de comenzar
                    synchronizePlayers();
                } else{                                                     //si no se completo los jugadores al terminar el TIMER
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);   //creamos mensaje de alerta
                    builder.setTitle("Atención:");                          //le ponemos titulo
                    builder.setMessage("Faltan Jugadores...");              //pregunta
                    builder.setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {//opcion de buscar de nuevo
                            synchronizePlayers();
                            candado = false;                                //bandera TEMPORAL de volver a buscar
                            primerbuscada = true;                           //bandera TEMPORAL, para solo buscar una sola vez
                            Decrementador = 10;                             //reiciciar el contado de segundos
                            //createPlayersList();
                            //myCountDown.start();                            //reiniciar el TIMER
                            dialog.dismiss();                               //cerramos este mensaje de alerta
                        }
                    });
                    builder.setNegativeButton("Comenzar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//si comenzamos con los que encontro
                            synchronizePlayers();
                            botonComenzar.setVisibility(View.VISIBLE);      //mostrar boton de comenzar
                            dialog.dismiss();                               //cerramos este mensaje
                        }
                    });

                    AlertDialog alert = builder.create();                   //creamos este mensajoe
                    alert.show();                                           //lo mostramos
                }
            } else {                                                        //si la opcion es UNIRSE A PARTIDA
                //buscando.setText("Sincronizado");                           //mostramos esta palabra
                //players = playersR;                                         //pasamos el valor de jugadores RANDOM a la variable de jugadores
                //botonComenzar.setVisibility(View.VISIBLE);                  //mostrar boton de comenzar
                Intent intent = new Intent(MainActivity.this,SecondActivity.class);     //pre[paramos la siguiente actividad (formulario)
                intent.putExtra("PLAYERS",players);                         //guardamos el numero de jugadores en una varianle globla entre las actividaes
                startActivity(intent);                              //iniciamos segunda actividad
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnIniciar:                                       //si le dimos clic en el boton INICIAR PARTIDA
                botonIniciar.setVisibility(View.INVISIBLE);             //escondemos boton INICIAR PARTIDA
                botonUnirse.setVisibility(View.INVISIBLE);              //escondemos boton UNIRSE A PARTIDA
                titulo.setVisibility(View.VISIBLE);                     //mostramos leyenda "JUGADORES"
                njugadores.setVisibility(View.VISIBLE);                 //mostramos cnatidad de jugadores
                ArrowLeft.setVisibility(View.VISIBLE);                  //mostramos flecha izquierda
                ArrowRight.setVisibility(View.VISIBLE);                 //mostranmos flecha derecha
                botonBuscar.setVisibility(View.VISIBLE);                //mostramos boton buscar
                tipoconexion = 1;                                       //fue la opcion INICIAR PARTIDA
                break;
            case R.id.btnUnirse:                                        //Si le dimos clic a la opcion UNIRSE A PARTIDA
                botonIniciar.setVisibility(View.INVISIBLE);             //escondemos boton INICIAR PARTIDA
                botonUnirse.setVisibility(View.INVISIBLE);              //escondemos boton UNBIRSE A PARTIDA
                botonSincronizar.setVisibility(View.VISIBLE);           //mostramos boton sincronizar
                tipoconexion = 2;                                       //fue la opcion UNISRSE A PARTIDA
                listview.setVisibility(View.VISIBLE);
                search4Players();
                break;
            case R.id.btnBuscar:                                        //si le dimos clic al boton buscar
                ArrowRight.setVisibility(View.INVISIBLE);               //escondemos flicha
                ArrowLeft.setVisibility(View.INVISIBLE);                //escondemos fliecha
                botonBuscar.setVisibility(View.INVISIBLE);              //escondemos botonbuscar
                buscando.setVisibility(View.VISIBLE);                   //mostramos texto buscando
                //listview.setVisibility(View.VISIBLE);                   //mostramos la lista
                wait4Players();
                myCountDown.start();                                    //iniciamos timer
                break;
            case R.id.btnSincronizar:                                   //si le dimos clic al boton de sincronizar
                botonSincronizar.setVisibility(View.INVISIBLE);         //escondemos el boton
                buscando.setText("Sincronizando...");                   //mosrtramos texto
                buscando.setVisibility(View.VISIBLE);                   //mostramos texto sinconimndpo
                myCountDown.start();                                    //iniciamos timer
                break;
            case R.id.btncomenzar:                                      //si le dimos clic al boton comenzar
                synchronizePlayers();
                //Intent intent = new Intent(MainActivity.this,SecondActivity.class);     //pre[paramos la siguiente actividad (formulario)
                //intent.putExtra("PLAYERS",players);                         //guardamos el numero de jugadores en una varianle globla entre las actividaes
                //startActivity(intent);                              //iniciamos segunda actividad
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client.connect();
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

    @Override
    public void onDestroy() {
        mBluetoothAdapter.setName(deviceName);
        if (cModule != null) {
            cModule.stop();
        }
        super.onDestroy();
    }

    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (cModule != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (cModule.getState() == CommunicationModuleA.STATE_NONE) {
                // Start the Bluetooth chat services
                //cModule.start();
            }
        }
    }

    void printStatus(){
        Log.d(TAG,"Final Results ------------------------------");
        Log.d(TAG,"Players Conected: " + indiceA);
        for(int i=0; i<indiceA; i++){
            Log.d(TAG,"MAC User" + i + ": " + connectedPlayers[i].toString() );
        }
        Log.d(TAG,"Final Results ------------------------------");
    }

    void setUpCommunicationModule(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceName = mBluetoothAdapter.getName();
        // Initialize the BluetoothChatService to perform bluetooth connections
        cModule = new CommunicationModuleA(this, mHandler);

        // Initialize the buffer for outgoing messages
        //mOutStringBuffer = new StringBuffer("");
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = MainActivity.this;
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        }
        //if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        //}
        listview = (ListView) findViewById(R.id.lista);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        listview.setAdapter(mNewDevicesArrayAdapter);
        listview.setOnItemClickListener(mDeviceClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Find and set up the ListView for newly discovered devices
        //ListView newDevicesListView = (ListView) findViewById(R.id.lista);
        //newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        //newDevicesListView.setOnItemClickListener(mDeviceClickListener);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getName().startsWith("UnoUAG-")){
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            }
        }
    };

    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            address = info.substring(info.length() - 17);
            String name = info.substring(1, 17);
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            Log.d(TAG, "connect to: " + device);
            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            connectDevice(intent, true);
        }
    };

    void wait4Players(){
        mBluetoothAdapter.setName("UnoUAG-" + deviceName);
        server=true;
        address = "0-" + mBluetoothAdapter.getAddress().toString();
        processPackage(address);
        cModule.start();
    }

    void search4Players(){
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        mBluetoothAdapter.startDiscovery();
    }

    void addConnection(String address){
        connectedPlayers[indiceA]=address;
        indiceA++;
    }

    void sendAddress(String address){
        sendMessage("0-" + address);
    }

    void processPackage(String paquete){
        Log.d(TAG, "received: " + paquete);
        String[] Operation = paquete.split(delimiter, 0);
        String inTurn = "0";
        if(Operation[0].equals("0")){
            addConnection(Operation[1]);
        }
        if(Operation[0].equals("1")){
            mBluetoothAdapter.setName(deviceName);
            cModule.stop();
            giveItSomeTime();
            Intent intent=new Intent(MainActivity.this, SecondActivity.class);
            intent.putExtra("PlayersList", Operation[1]);
            intent.putExtra("turnsArray", Operation[3]);
            intent.putExtra("inTurn", inTurn);
            intent.putExtra("PLAYERS", indiceA);
            intent.putExtra("playersState", Operation[5]);
            startActivity(intent);
        }
    }

    void synchronizePlayers(){
        for(int i=0; i<indiceA-1; i++){
            connectedPlayersArray = connectedPlayersArray + connectedPlayers[i].toString() + ",";
            turnsArray = turnsArray + i + ",";
            playersState = playersState + "online,";
        }
        connectedPlayersArray = connectedPlayersArray + connectedPlayers[indiceA-1].toString();
        turnsArray = turnsArray + (indiceA-1);
        playersState = playersState + "online";

        String paquete = "1-" + connectedPlayersArray + "-" + indiceA + "-" + turnsArray + "-" + inTurn + "-" + playersState;
        sendMessage(paquete);
        mBluetoothAdapter.setName(deviceName);
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        //if (cModule.getState() != CommunicationModuleA.STATE_CONNECTED) {
         //   Toast.makeText(this, "Not Connected", Toast.LENGTH_SHORT).show();
          //  return;
       // }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            cModule.write(send);

            // Reset out string buffer to zero and clear the edit text field
            //mOutStringBuffer.setLength(0);
        }
    }

    void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Log.d(TAG, message);
    }

    void stopModule(){
        cModule.stop();
        Intent intent=new Intent(this, SecondActivity.class);
        intent.putExtra("PlayersList", connectedPlayersArray);
        intent.putExtra("turnsArray", turnsArray);
        intent.putExtra("inTurn", inTurn);
        intent.putExtra("PLAYERS", indiceA);
        intent.putExtra("playersState", playersState);
        intent.putExtra("server",server);
        startActivity(intent);
    }

    void giveItSomeTime(){
        long begin = System.currentTimeMillis();
        long now=0;
        while(now-begin<3000){
            now = System.currentTimeMillis();
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = MainActivity.this;
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case CommunicationModuleA.STATE_CONNECTED:
                            //sendAddress(mBluetoothAdapter.getAddress());
                            //setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            //mConversationArrayAdapter.clear();
                            break;
                        case CommunicationModuleA.STATE_CONNECTING:
                            //setStatus(R.string.title_connecting);
                            break;
                        case CommunicationModuleA.STATE_LISTEN:
                        case CommunicationModuleA.STATE_NONE:
                            //setStatus(R.string.title_not_connected);
                            break;
                        case CommunicationModuleA.STATE_SENT:
                            if(server){
                                stopModule();
                            }
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    //showMessage("sending: " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String message = new String(readBuf, 0, msg.arg1);
                    processPackage(message);
                    //showMessage("receiving: " + message);
                    //mConversationArrayAdapter.add(":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    if(!server){
                        sendAddress(mBluetoothAdapter.getAddress().toString());
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setUpCommunicationModule();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(MainActivity.this, "Unknown error occurred",
                            Toast.LENGTH_SHORT).show();
                    MainActivity.this.finish();
                }
        }
    }

    private void connectDevice(Intent data, boolean secure) {
            // Get the device MAC address
            String address = data.getExtras()
                    .getString(EXTRA_DEVICE_ADDRESS);
            // Get the BluetoothDevice object
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            // Attempt to connect to the device
            cModule.connect(device, secure);
    }
}
