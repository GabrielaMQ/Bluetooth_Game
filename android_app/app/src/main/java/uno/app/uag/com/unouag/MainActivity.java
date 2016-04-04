package uno.app.uag.com.unouag;

import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    ListView listview;                                      //lista de los jugadores
    String[] valores1 = new String[]{"John Lennon", "Paul McCartney", "George Harrison", "Ringo Starr"};

    ArrayList<String> Conectados = new ArrayList<String>();      //lista de usuarios conectados

    TextView buscando;                                      //mensaje de buscando
    Button botonJugar;                                      //boton de jugar para sincronizar jugadores
    Button botonComenzar;                                   //boton de comenzar juego

    Integer Decrementador = 10;                             //indice de segundo
    Integer indiceA = 1;                                    //contador de jugadores conectados
    Integer players;                                        //numero de jugadores conectados
    Animation anim = new AlphaAnimation(0.0f, 1.0f);        //animacion de parpadear

    ArrayAdapter<String> adapter;                           //adaptador de array


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Random rand = new Random();                             //comienza randomizado
        players = rand.nextInt(3) + 2;                          //de 2  a 4 jugadores

        Toast.makeText(getApplicationContext(), "players= " + players, Toast.LENGTH_SHORT).show();//muestra los jugadores aceptados


        Conectados.add("John Lennon");                          //meter al primer jugadores
        listview = (ListView) findViewById(R.id.lista);         //realcionar la lista(visual) con el campo lista
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Conectados);
        listview.setAdapter(adapter);                           //conecatmos adapatador con el control lista
        buscando = (TextView) findViewById(R.id.mensaje2);      //relacionamos el texto del mensaje con el texto

        anim.setDuration(100);                                  //tiempo de parpadeo
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

        botonJugar = (Button) findViewById(R.id.btnJugar);      //relacionamos botones
        botonComenzar = (Button) findViewById(R.id.btncomenzar);//relacionamios botones

        botonJugar.setOnClickListener(this);                    //acemos que este pendeinte al clic del usuario
        botonComenzar.setOnClickListener(this);                 //lo mismo qque arriba

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


  CountDownTimer myCountDown = new CountDownTimer(10000, 1000) {  //timer de 10 segundos cada segundo
        public void onTick(long millisUntilFinished) {              //funcion activa cada segundo
            Decrementador--;
            buscando.setText("Buscando..." + Integer.toString(Decrementador));
            if(indiceA < players){                                  //mientras no se hay jugadores disponibles
                Conectados.add(indiceA,valores1[indiceA]);          //agregarlos a la lista
                indiceA++;                                          //incrementar jugadores conectados
                listview.smoothScrollToPosition(adapter.getCount());//visuzlizar el ultimo //NO FUNCIONA
            }
        }

        public void onFinish() {                                    //funcion que se activa al terminar el timer
            botonComenzar.setVisibility(View.VISIBLE);              //mostrar texto coemnsar
            buscando.setText(players + " Jugadores");               //muestra jugadores conectados
            buscando.setBackgroundColor(0xFF02BB02);                //color ver de fondo
            buscando.setVisibility(View.VISIBLE);                   //texto visible
            anim.cancel();                                          //terminar animacion de parpadeo
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnJugar:                                     //boton jugar
                botonJugar.setVisibility(View.INVISIBLE);           //escondemos boton jugar
                buscando.setVisibility(View.VISIBLE);               //mostramos texto buscando
                listview.setVisibility(View.VISIBLE);               //mostramos la lista
                buscando.startAnimation(anim);                      //iniciamos animacion del texto buscando
                myCountDown.start();                                //iniciamos timer
                break;
            case R.id.btncomenzar:                                  //boton comenzar
                Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                intent.putExtra("PLAYERS",players);                 //guardamos el numero de jugadores en una varianle globla entre las actividaes
                startActivity(intent);                              //iniciamos segunda actividad
                break;

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
}
