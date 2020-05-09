package com.example.myapplication;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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
import java.util.Timer;
import java.util.TimerTask;

public class MainClient extends AppCompatActivity {

    private static final int PORT = 5000;
    private static Button connectBtn , comandaBtn ;
    private static EditText server , comanda;
    private ImageView imgStatus;
    private TextView txtViewStatus,txtViewResposta;
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
        txtViewResposta = findViewById(R.id.txtViewResposta);
        comandaBtn = findViewById(R.id.comandaBtn);
        comanda = findViewById(R.id.comandaTextView);

        imgStatus.setBackgroundColor(Color.rgb(255,0,0));

        tipusConexio.setChecked(false);
        //tipusConexio.setTextOff("Conexió manual");
        //tipusConexio.setTextOn("Conexió automàtica");


        tipusConexio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                Timer timer = null;

                if (b) {

                    Toast.makeText(getApplicationContext(),"Conexió automàtica engegada",Toast.LENGTH_SHORT).show();

                    // Desactivem els botons de conexió manual
                    comandaBtn.setEnabled(false);
                    connectBtn.setEnabled(false);

                    int minuts = 5;
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                           EnviaServidor envia = new EnviaServidor();
                           envia.execute(server.getText().toString(),comanda.getText().toString());
                        }
                    }, 0 , 1000*60*minuts );



                } else {

                    Toast.makeText(getApplicationContext(),"Conexió manual engegada",Toast.LENGTH_SHORT).show();

                    comandaBtn.setEnabled(true);
                    connectBtn.setEnabled(true);

                }

            }
        });

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(server.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"La direcció IP no pot estar buida!",Toast.LENGTH_SHORT).show();
                } else {
                    ComprobaEstat estat = new ComprobaEstat(server.getText().toString());
                    estat.execute();
                }
            }
        });

        comandaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (comanda.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"La comanda no pot estar buida!",Toast.LENGTH_SHORT).show();
                } else {
                    EnviaServidor envia = new EnviaServidor();
                    envia.execute(server.getText().toString(),comanda.getText().toString().trim());
                }
            }
        });
    }

    public class EnviaServidor extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {

            String ip = strings[0];
            String resposta = "";
            try{
                Socket socket = new Socket(ip,PORT);

                // Enviem peticio al servidor
                PrintStream output = new PrintStream(socket.getOutputStream());
                String peticio = strings[1];
                output.println(peticio);

                //Rebem la resposta
                BufferedReader input = new BufferedReader( new InputStreamReader(socket.getInputStream()));
                resposta = input.readLine();

                socket.close();

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return resposta;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            txtViewResposta.setText(s);
        }
    }

    public class ComprobaEstat extends AsyncTask<Void,Void,Boolean> {

        private String ip;
        private String resposta;

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

                PrintStream output = new PrintStream(socket.getOutputStream());
                output.println("wonder");

                BufferedReader input = new BufferedReader( new InputStreamReader(socket.getInputStream()));
                resposta = input.readLine();

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
                imgStatus.setBackgroundColor(Color.rgb(0,255,0));
                txtViewStatus.setText(R.string.conectat);
                Toast.makeText(getApplicationContext(),resposta,Toast.LENGTH_SHORT).show();

            } else {

                imgStatus.setBackgroundColor(Color.rgb(255,0,0));
                txtViewStatus.setText("No conectat");

            }
        }
    }

}
