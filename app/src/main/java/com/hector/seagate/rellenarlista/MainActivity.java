package com.hector.seagate.rellenarlista;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //el array debe guardar objetos de tipo parcelable
    //sino no se podia recuperar el array al rotar la pantalla

    ArrayList<StringParcelable> datos;
    ListView lv;
    Button boton;
    EditText et;
    ArrayAdapter<StringParcelable > adaptador;
    String contenidoet;
    boolean modoNormal =true;
    boolean modoEdiccion = false;
    int posicionEnEdiccion;
    Button botonCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    datos = new ArrayList<StringParcelable>();
    lv= (ListView) findViewById(R.id.lista);
    boton = (Button) findViewById(R.id.boton);
    et = (EditText) findViewById(R.id.et);
    adaptador = new ArrayAdapter<StringParcelable>(getApplicationContext(),android.R.layout.simple_list_item_1,datos);
    lv.setAdapter(adaptador);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               modoEdiccion=true;
                modoNormal=false;
                posicionEnEdiccion = position;

                boton.setText("Editar entrada "+position);
                et.setText(datos.get(position).toString());


                 botonCancelar = new Button(getApplicationContext());
                //hay que darle un id al layout en el xml para poder obtenerlo desde aqui
                //el layout es conveniente que sea de tipo Relative, para luego poder usar los
                //metodos RelativeLayout.Right_of ....

                RelativeLayout  miLayout = (RelativeLayout) findViewById(R.id.milayout);

                miLayout.addView(botonCancelar);


                botonCancelar.setText("cancelar");

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) botonCancelar.getLayoutParams();

                layoutParams.addRule(RelativeLayout.RIGHT_OF, boton.getId());
                layoutParams.addRule(RelativeLayout.ALIGN_BASELINE, boton.getId());
                botonCancelar.setLayoutParams(layoutParams);


                botonCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //al cancelar el modo ediccion, vuelvo a modo normal
                        modoEdiccion=false;
                        modoNormal=true;

                        et.setText("");
                        botonCancelar.setVisibility(View.INVISIBLE);
                        boton.setEnabled(false);
                        boton.setText("Añadir");
                    }
                });




            }
        });

        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //boton.setEnabled(true);
            }
        });

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
             contenidoet = et.getText().toString();

                if  (!(s.length()>0)){
                    boton.setEnabled(false);
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contenidoet = et.getText().toString();
                if (contenidoet!=null){
                    boton.setEnabled(true);
                    //datos.add(contenidoet);
                }else {
                    boton.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //solo se borra el edit text en este metodo afterTextChanged
                if  (!(s.length()>0)){
                    boton.setEnabled(false);
                }
            }
        });



    }

    public void alista (View view){


        if (modoEdiccion){
        boton.setText("Añadir");

            boton.setEnabled(false);
            datos.set(posicionEnEdiccion, new StringParcelable(et.getText().toString()));
            adaptador.notifyDataSetChanged();
            et.setText("");

            modoNormal=true;
            modoEdiccion=false;
        } else {


    contenidoet = et.getText().toString();
    datos.add(new StringParcelable(contenidoet));
    adaptador.notifyDataSetChanged();
    et.setText("");
    boton.setEnabled(false);
}

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        //outState.putStringArrayList("MISDATOS", datos);
        //el metodo de arriba no sirve a la hora de recuperar el array no funcionaba, usar este
        outState.putParcelableArrayList("ARRAY", datos);
        outState.putBoolean("modoediccion", modoEdiccion);
        outState.putBoolean("modonormal", modoNormal);
        outState.putString("textoet",et.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        ArrayList<StringParcelable> misdatos=savedInstanceState.getParcelableArrayList("ARRAY");

        for (int i=0;i<misdatos.size();i++){
            datos.add(misdatos.get(i));
        }

        modoNormal= savedInstanceState.getBoolean("modonormal");
        modoEdiccion= savedInstanceState.getBoolean("modoediccion");

        adaptador.notifyDataSetChanged();
        et.setText(savedInstanceState.getString("textoet"));

        if (modoNormal) {
            boton.setText("Añadir");
            if  (!(botonCancelar==null)){
            botonCancelar.setVisibility(View.INVISIBLE);}

        }



    }
}

 class StringParcelable implements Parcelable{

String mistringserializable;

     public StringParcelable(String mistringserializable) {
         this.mistringserializable = mistringserializable;
     }

     protected StringParcelable(Parcel in) {
         mistringserializable = in.readString();
     }

     public static final Creator<StringParcelable> CREATOR = new Creator<StringParcelable>() {
         @Override
         public StringParcelable createFromParcel(Parcel in) {
             return new StringParcelable(in);
         }

         @Override
         public StringParcelable[] newArray(int size) {
             return new StringParcelable[size];
         }
     };

     @Override
     public int describeContents() {
         return 0;
     }

     @Override
     public void writeToParcel(Parcel dest, int flags) {
         dest.writeString(mistringserializable);
     }
 }
