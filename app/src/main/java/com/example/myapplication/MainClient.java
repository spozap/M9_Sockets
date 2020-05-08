package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        setContentView(R.layout.mainclient);
        super.onCreate(savedInstanceState);

        connectBtn = findViewById(R.id.connectBtn);
        server = findViewById(R.id.ipTextView);
        imgStatus = findViewById(R.id.imgViewStatus);
        txtViewStatus = findViewById(R.id.txtViewStatus);

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(server.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"La direcció IP no pot estar buida!",Toast.LENGTH_SHORT).show();
                } else {
                  if(conectar(server.getText().toString())){

                  } else {

                  }
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

}
