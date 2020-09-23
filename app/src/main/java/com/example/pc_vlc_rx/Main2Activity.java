package com.example.pc_vlc_rx;

/*Importando bibliotecas */
import androidx.appcompat.app.AppCompatActivity;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class Main2Activity extends AppCompatActivity implements SensorEventListener
{
    /*Declaracoes: objetos, variaveis e vetores*/
    SensorManager sensorManager;
    Sensor sensor;
    boolean avisado = false, avisado2 = false;
    int[] BitsArray, CheckHeader, DataArray, Cabeca, Peh, CheckFooter;
    int ultima_colocada = 0, contador = 0, SecondSize, FirstSize =  50,n = 1, inicio = 0;
    float[] anArray, threshv;
    float sum;
    String bitsString, DataString, s2 = "";
    char nextChar;
    TextView textView, textView2, textView3, textView4, myTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        textView   = findViewById(R.id.WebContent);
        textView2  = findViewById(R.id.textView2);
        textView3  = findViewById(R.id.textView3);
        textView4  = findViewById(R.id.textView4);
        myTextView = findViewById(R.id.myTextView);
        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);

        if (sensorManager != null)
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);  // ADIDIONEI RECENTEMENTO O: if(sensorManager != null)

        anArray = new float[FirstSize];
        threshv = new float[8];
        BitsArray = new int[FirstSize];
        Cabeca = new int[8];
        Peh = new int[8];
        CheckHeader =  new int[8];
        CheckFooter =  new int[8];
        SecondSize = 0;
        bitsString = "";
        DataString = "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.action_start)
        {
            Toast.makeText(getApplicationContext(),"Reinicializando...",Toast.LENGTH_LONG).show();
            Intent Ac2 = new Intent(Main2Activity.this, Main2Activity.class);
            startActivity(Ac2);
            finish();
            return true;
        }

        if(id == R.id.action_sair)
        {
            Toast.makeText(getApplicationContext(),"Sair Clicado",Toast.LENGTH_LONG).show();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*Encerrar o sensor quando o aplicativo nao estiver sendo usado */
    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    /* registro do sensor para quando o aplicativo estiver em uso */
    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public  void onSensorChanged(SensorEvent event)
    {
        /*Verifica se o evento detectado foi um evento do sensor de luz*/
        if (event.sensor.getType() == Sensor.TYPE_LIGHT)
        {
            if (contador < FirstSize) //soh entra quando nao estivermos no fim do anArray
            {
                //isso simula o event.values
               //  anArray = new float[]{10,10,20,10,10,10,20,20, 30,50,30,30,30,30,30,50, 2,5,2,2,5,5,2,2, 30,50,30,30,30,30,30,50, 99,110,99,99,99,110,110,99, 1,1,3,1,1,3,1,1, 100, 100}; //#Alaf$ == 00100011 01000001 01101100 01100001 01000110 00100100
               // textView.setText("Lux: " + anArray[ultima_colocada]);
                //anArray[ultima_colocada] = event.values[0]; //adiciona o valor na ultima posicao do anArray
                textView.setText("Lux: " + event.values[0]); //Imprime na tela o valor atual de luminosidade
                sum += anArray[ultima_colocada];            //soma os valores em lux para depois fazermos a media
                ultima_colocada++;                          //vai para proxima posicao
                contador++;                                 //incrementa contador

                if (contador == n * 8)
                {
                    threshv[n] = sum / 8;
                    textView3.setText("Xm: " + threshv[n]);   //imprime na tela o valor medio (decisao entre 1 e 0)
                    n++;
                    sum = 0;
                }
            }
            //testando o valor medio dinamico (vector threshv)
            else if (contador == FirstSize)
            {
                //Vetor de luminosidade forçado com Inicio e fim, alem de lixo final, o inicial pode ser um problema a se resolver, pois altera o valor medio dinamico
               // anArray = new float[]{10,10,20,10,10,10,20,20, 30,50,30,30,30,30,30,50, 2,5,5,2,5,5,2,2, 30,50,30,30,30,30,30,50, 99,110,110,99,99,110,110,99, 1,1,3,1,1,3,1,1, 100, 100}; //#Alaf$ == 00100011 01000001 01101100 01100001 01100110 00100100
                GenerateBitsArray(anArray);                     //chama proximo metodo
            }
        }
    }

    public void GenerateBitsArray(float[] anArray)
    {
        n = 0;
        /*BitsArray do for, eh o baseado nos valores de lux (unidade de luz), os dois BitsArray que seguem (forcados) sao para testes de conversao bits2string */
        for (int i = 0; i < FirstSize; i++) //soh entra quando nao estivermos no fim do BitsArray
        {
            if(i == n*8)
                n++;
            if (anArray[i] <= threshv[n])
                BitsArray[i] = 0;
            else
                BitsArray[i] = 1;

        }
            /*
            OS TESTES MOSTRARAM QUE DAQUI PARA FRENTE ESTÁ TUDO OK, OU SEJA, TENDO DEFINIDO O VETOR DE BITS, O RESTO FUNCIONA
          */

        /*SEM INICIO E SEM FIM*/
        //BitsArray = new int[]{0,1,0,1,1,1,0,0,1,1,0,1,0,0,1,1,0,1,0,1,1,0,0,0,0,1,0,1,1,1,0,0,1,0,0,1,1,0,1,0,0,1,0,0,1,1,0,1,0,1};
        /*COM INICIO, MAS SEM FIM*/
        //BitsArray = new int[]{0,0,0,0, 0,0,0,0, 0,0,1,0,0,0,1,1, 0,1,0,1,0,1,1,0, 0,1,0,0,1,1,0,0, 0,1,0,0,0,0,1,1, 1,1,1,0,0,1,1,1, 1,1};
        /*COM INICIO E COM FIM*/
        //BitsArray = new int[]{0,0,0,0, 0,0,0,0, 0,0,1,0,0,0,1,1, 0,1,0,1,0,1,1,0, 0,1,0,0,1,1,0,0, 0,1,0,0,0,0,1,1, 0,0,1,0,0,1,0,0, 1,1}; // ~lixo[8]~ #VLC$ ~lixo[2]~
        Cabeca = new int[]{0,0,1,0,0,0,1,1}; //Nossa cabeca eh um #
        Peh = new int[]{0,0,1,0,0,1,0,0}; //Nossa peh eh um $
        PrintData(BitsArray, anArray); //para testes
        ReadData(BitsArray);     //chama o proximo metodo
    }

    public void PrintData(int[] BitsArray, float[] anArray)
    {
        textView2.setText("Binary Array: " + BitsArray[0] + BitsArray[10] + BitsArray[13] + BitsArray[15] + BitsArray[25] + BitsArray[30] + BitsArray[36] + BitsArray[45] + BitsArray[49]);
        textView4.setText("Lux Array:\n" + anArray[0] + " " + anArray[10] + " " + anArray[13] + " " + anArray[15] + " " + anArray[25] + " " + anArray[30] + " " + anArray[36] + " " + anArray[45] + " " + anArray[49]);
    }

    public int DefineStart(int[] BitsArray)
    {
        for (int i = 0; i < FirstSize - 8; i++)
        {
            for (int j = 0; j < CheckHeader.length; j++)
                CheckHeader[j] = BitsArray[i + j];

            if (CompareArrays(CheckHeader, Cabeca))
                return i;
        }
        return (-1);
    }

    public int DefineStop(int[] BitsArray)
    {
        for (int i = 0; i < FirstSize - 8; i++)
        {
            for (int j = 0; j < CheckFooter.length; j++)
                CheckFooter[j] = BitsArray[i + j];

            if (CompareArrays(CheckFooter, Peh))
                return i;
        }
        return (0);
    }

    public boolean CompareArrays (int[] Array1, int[] Array2)
    {
        for (int k = 0; k < Array1.length; k++)
            if (Array1[k] != Array2[k])
                return false;
        return true;
    }

    public void ReadData(int[] BitsArray)
    {
        inicio = DefineStart(BitsArray);
        SecondSize = DefineStop(BitsArray);
        DataArray = new int[SecondSize]; // fase de teste, talvez volte para o top com [FirstSize], na declaracao de vetores
        if (inicio != -1)                 //se a for -1 o cabecalho nao eh # ainda
        {
            if(SecondSize != 0)
            {
                for (int i = 0; i < SecondSize; i++) //adicionar - b aqui funciona?
                    DataArray[i] = BitsArray[i + inicio]; //o novo vetor de dados recebe o vetor antigo menos as posicoes de lixo
                DataToString(DataArray);
            }
            else if(avisado2 != true)
            {
                Toast.makeText(getApplicationContext(),"Não foi possível Identificar o fim dos dados",Toast.LENGTH_LONG).show();
                avisado2 = true;
            }
        }
        else if(avisado != true)
        {
            avisado = true;
            Toast.makeText(getApplicationContext(),"Não foi possível Identificar o início dos dados",Toast.LENGTH_LONG).show();
        }
    }


    public void DataToString(int[] DataArray) //a ser chamado
    {
        StringBuilder sb = new StringBuilder(SecondSize);
        for (int i = 0; i < DataArray.length; i++)
            if (DataArray[i] == 1)
                sb.append("1");
            else
                sb.append("0");
        DataString = sb.toString();
        BitsToString(DataString);
    }

    public void BitsToString (String DataString)
    {
        StringBuilder sb2 = new StringBuilder(SecondSize/8);
        myTextView = findViewById(R.id.myTextView);

        for(int i = 0; i <= DataString.length()-8; i += 8) //this is a little tricky.  we want [0, 7], [9, 16], etc (increment index by 9 if bytes are space-delimited)
        {
            nextChar = (char)Integer.parseInt(DataString.substring(i, i+8), 2);
            sb2.append(nextChar);
        }
        s2 = sb2.toString();
        int limite = (SecondSize - inicio)/8;
        myTextView.setText("" + s2.substring(1,limite)); //resolve o problema de imprimir o cabecalho e o rodape)
    }
    @Override public void onAccuracyChanged(Sensor sensor, int i) { }
}
