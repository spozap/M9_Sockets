package com.example.myapplication;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainClient extends AppCompatActivity {

    private static final int PORT = 5000;
    private static Button connectBtn;
    private static EditText server;
    private ImageView imgStatus;
    private TextView txtViewStatus;
    private Switch tipusConexio;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        setContentView(R.layout.mainclient);
        super.onCreate(savedInstanceState);

        connectBtn = findViewById(R.id.connectBtn);
        server = findViewById(R.id.ipTextView);
        imgStatus = findViewById(R.id.imgViewStatus);
        txtViewStatus = findViewById(R.id.txtViewStatus);
        tipusConexio = findViewById(R.id.tipusconexio);

        imgStatus.setBackgroundColor(Color.rgb(255,0,0));

        tipusConexio.setChecked(false);
        tipusConexio.setTextOff("Conexió manual");
        tipusConexio.setTextOn("Conexió automàtica");


        if(tipusConexio.isChecked()){

            // Si la opció de conexió automàtica està habilitada , es deshabilita el botó de conexió manual
            connectBtn.setVisibility(View.GONE);

        }

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(server.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"La direcció IP no pot estar buida!",Toast.LENGTH_SHORT).show();
                } else {
                    ComprobaEstat estat = new ComprobaEstat(server.getText().toString().trim());
                    estat.execute();
                }
            }
        });
    }

    public boolean conectar(String SERVER){
        //flag per controlar cicle del programa
        boolean exit=false;
        //flag per s'ha fet la connexió al servidor
        boolean connected = false;

        //Socket per la comunicació client-servidor
        Socket socket;
        try {
            System.out.println("*** Inici Client ***");
            while( !exit ){

                //Inicialitzem la connexió amb el servidor
                socket = new Socket(SERVER, PORT);

                // Si es connecta , cambiem el valor del boolea i modifiquem el color del semàfor i el txtView del estat
                connected = true;

                imgStatus.setBackgroundColor(Color.rgb(98,236,0));
                txtViewStatus.setText(R.string.conectat);


                //Dipòsit per llegir el que ens passi el servidor
                BufferedReader input = new BufferedReader( new InputStreamReader(socket.getInputStream()));

                //Dipòsit per imprimir les dades del servidor
                PrintStream output = new PrintStream(socket.getOutputStream());

                //Dipòsit per recollir el que escriu l'usuari
                BufferedReader brRequest = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("\nClient> Escriu una comanda: ");

                //Variable amb el que l'usuari ha escrit
                String request = brRequest.readLine();

                //Enviament al servidor de la petició del client
                output.println(request);

                //Recollida des del servidor de la resposta a la petició
                String st = input.readLine();

                //Imprimim la resposta per pantalla
                if( st != null )
                    System.out.println("Servidor> " + st );

                //Sortida del programa
                if(request.equals("exit")){
                    exit=true;
                    System.out.println("\n**** Fi Client ****");
                }
                //Sortida del programa
                if(request.equals("stop")){
                    exit=true;
                    System.out.println("\n**** Sayonara server ****");
                }
                socket.close();
            }//end while
        } catch (UnknownHostException e) {
            connected = false;
        } catch (IOException e) {
            connected = false;
        }

        return  connected;

    }

    public class ComprobaEstat extends AsyncTask<Void,Void,Boolean> {

        private String ip;

        public ComprobaEstat(String ip){
            this.ip = ip;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            Socket socket = null;
            boolean status = false;

            try {
                socket = new Socket(ip,PORT);

                status = socket.isConnected();

                socket.close();

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return status;
        }


        @Override
        protected void onPostExecute(Boolean status) {
            super.onPostExecute(status);

            if (status){

                imgStatus.setBackgroundColor(Color.rgb(98,236,0));
                txtViewStatus.setText(R.string.conectat);

            } else {

                imgStatus.setBackgroundColor(Color.rgb(255,0,0));
                txtViewStatus.setText("No conectat");

            }
        }
    }

}
