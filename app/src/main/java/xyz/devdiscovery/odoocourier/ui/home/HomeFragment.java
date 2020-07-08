package xyz.devdiscovery.odoocourier.ui.home;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import au.com.bytecode.opencsv.CSVReader;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import xyz.devdiscovery.odoocourier.MainDirectory;
import xyz.devdiscovery.odoocourier.R;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements
        AdapterView.OnItemSelectedListener {

    private HomeViewModel homeViewModel;
    private String bestUrl = "https://www.andr-discovery.xyz/category.xml";
    private Spinner spMainSelectCategory;
    private ArrayList<String> categoryList = new ArrayList<String>();
    private Button buttonSend;
    private EditText priceMessege;
    private EditText textMessage;
    private Button TakePicture;
    private Button upload;

    static final int REQUEST_TAKE_PHOTO = 1;
    private String mCurrentPhotoPath;
    private ImageView imageView;
    private Uri photoURI;
    private TextView txtShow;
    private String arrList;
    private String S2_Build = "[EXP_BUILDING]",
            S3_Consum = "[EXP_CAR_CONSUMABLES]",
            S4_Car_Day = "[EXP_CAR_DAY_PARKING]",
            S5_Car_Night = "[EXP_CAR_NIGHT_PARKING]",
            S6_Car_Servis = "[EXP_CAR_SERVICE]",
            S7_Car_Wash = "[EXP_CAR_WASH]",
            S8_Comunic = "[EXP_COMMUNICATION_FEE]",
            S9_Intertaim = "[EXP_ENTERTAINMENT]",
            S10_Equipment = "[EXP_EQUIPMENT_OPERATION]",
            S11_Gas_Car = "[EXP_GAS_CAR_FUEL]",
            S12_HouseHold = "[EXP_HOUSEHOLD_EXPENSES]",
            S13_Load_Unload = "[EXP_LOADING_UNLOADING]",
            S14_Other_Transport = "[EXP_OTHER_TRANSPORT]",
            S15_Permits = "[EXP_PERMITS]",
            S16_Petrol_car = "[EXP_PETROL_CAR_FUEL]",
            S17_Stationery = "[EXP_STATIONERY]",
            S18_Training = "[EXP_TRAINING]";




    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

           imageView = root.findViewById(R.id.imageView);
        // upload = (Button) root.findViewById(R.id.uploadXML);
           spMainSelectCategory = (Spinner) root.findViewById(R.id.selectIdProduct);
           buttonSend = (Button) root.findViewById(R.id.sendMailButton);
           priceMessege = (EditText) root.findViewById(R.id.priceTextMessege);
           textMessage = (EditText) root.findViewById(R.id.editTextMessege);
           TakePicture = (Button) root.findViewById(R.id.button);
           txtShow = (TextView) root.findViewById(R.id.showNote);





        List<String[]> list = new ArrayList<String[]>();
        String next[] = {};
        try {
            InputStreamReader csvStreamReader = new InputStreamReader(
                    getActivity().getAssets().open(
                            "null"));

            CSVReader reader = new CSVReader(csvStreamReader);
            for (;;) {
                next = reader.readNext();
                if (next != null) {
                    list.add(next);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0;  i < list.size(); i++) {
            categoryList.add(list.get(i)[1]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1,
                categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);

        spMainSelectCategory.setAdapter(adapter);
        spMainSelectCategory.setOnItemSelectedListener(this);




        TakePicture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
                Toast toast = Toast.makeText(getActivity(), "Запуск камеры для снимка",Toast.LENGTH_LONG);
                toast.show();
            }
        });



     //  Метод для загрузки XML массива, через клик кнопки.
     /*   upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    new DownloadPageTask().execute(bestUrl);
                } else {
                    Toast.makeText(getActivity(), "Нет интернета", Toast.LENGTH_SHORT).show();
                }
            }
        });

    */
        // Для добавления загрузки XML автоматической загрузкой, удалить этот код
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadPageTask().execute(bestUrl);
        } else {
            Toast.makeText(getActivity(), "Не могу загрузить список, отсуствует интернет", Toast.LENGTH_LONG).show();
        }
        // --- END

        return root;
    }

    // Создаю меню для раздела "Расходы"
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_settings:

                ConnectivityManager connectivityManager =
                        (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    new DownloadPageTask().execute(bestUrl);
                } else {
                    Toast.makeText(getActivity(), "Не могу загрузить список, отсуствует интернет", Toast.LENGTH_LONG).show();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // END Menu


    public void onItemSelected(final AdapterView<?> arg0, final View arg1, final int arg2,
                               final long arg3) {

        if (categoryList.get(arg2).equals("Строительство")) {
            txtShow.setText("Строительство");
        } else
            if (categoryList.get(arg2).equals("Авторасходники")) {
            txtShow.setText("Омыватьель для стекла, освежитель воздуха, щетки и пр.");
        } else
            if (categoryList.get(arg2).equals("Автопарковка")) {
            txtShow.setText("Автопаркова автомобиля дневное время");
        } else
            if (categoryList.get(arg2).equals("Автостоянка")) {
            txtShow.setText("Стоянка автомобила в ночное время");
        } else
            if (categoryList.get(arg2).equals("Услуги автосервиса")) {
            txtShow.setText("Услуги автосервиса");
        } else
            if (categoryList.get(arg2).equals("Автомойка")) {
            txtShow.setText("Автомойка, уборка салона авто");
        } else
            if (categoryList.get(arg2).equals("Абонплата за связь")) {
            txtShow.setText("Абонплата за связь, покупка стартовых пакетов и пр.");
        } else
            if (categoryList.get(arg2).equals("Представительские расходы")) {
            txtShow.setText("Представительские расходы");
        } else
            if (categoryList.get(arg2).equals("Эксплуатация оборудования")) {
            txtShow.setText("Ремонт оргтехники, заправка картриджа МФУ, телефонов, смарфонов и пр.");
        } else
            if (categoryList.get(arg2).equals("Заправка автомобиля газом")) {
            txtShow.setText("Заправка автомобиля газом");
        } else
            if (categoryList.get(arg2).equals("Хозяйственные расходы")) {
            txtShow.setText("Моющие и чистящие средства, мусорные пакеты и пр.");
        } else
            if (categoryList.get(arg2).equals("Погрузка-разгрузка")) {
            txtShow.setText("Оплата временным стороним рабочим приглашенным для выполнения погрузочно-разгрузочных работ");
        } else
            if (categoryList.get(arg2).equals("Прочий транспорт")) {
            txtShow.setText("Общественный транспорт, такси и пр.");
        } else
            if (categoryList.get(arg2).equals("Разрешительные документы")) {
            txtShow.setText("Все установленные разрешительные документы: страховки, налог на дорогу, и т.п. Т.е., все, что необходимо платить государству за то, чтобы оно разрешило какую-то деятельность");
        } else
            if (categoryList.get(arg2).equals("Заправка автомобиля бензином")) {
            txtShow.setText("Заправка автомобиля бензином");
        } else
            if (categoryList.get(arg2).equals("Канцелярские принадлежности")) {
            txtShow.setText("Бумага, ручки, карандаши и пр.");
        } else
            if (categoryList.get(arg2).equals("Обучение персонала")) {
            txtShow.setText("Обучение персонала");
        }

        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {



                String message = textMessage.getText().toString();
                String price = priceMessege.getText().toString();
                if (price.isEmpty())
                {
                    emptyField();
                    return;
                }


            // Условия на добавления АТРИБУТОВ в EXTRTA_SUBJECTS
                if (categoryList.get(arg2).equals("Строительство")) {
                    arrList=S2_Build;
                } else
                    if (categoryList.get(arg2).equals("Авторасходники")) {
                    arrList=S3_Consum;
                } else
                    if (categoryList.get(arg2).equals("Автопарковка")) {
                        arrList=S4_Car_Day;
                } else
                    if (categoryList.get(arg2).equals("Автостоянка")) {
                        arrList=S5_Car_Night;
                } else
                    if (categoryList.get(arg2).equals("Услуги автосервиса")) {
                        arrList=S6_Car_Servis;
                } else
                    if (categoryList.get(arg2).equals("Автомойка")) {
                        arrList=S7_Car_Wash;
                } else
                    if (categoryList.get(arg2).equals("Абонплата за связь")) {
                        arrList=S8_Comunic;
                } else
                    if (categoryList.get(arg2).equals("Представительские расходы")) {
                        arrList=S9_Intertaim;
                } else
                    if (categoryList.get(arg2).equals("Эксплуатация оборудования")) {
                        arrList=S10_Equipment;
                } else
                    if (categoryList.get(arg2).equals("Заправка автомобиля газом")) {
                        arrList=S11_Gas_Car;
                } else
                    if (categoryList.get(arg2).equals("Хозяйственные расходы")) {
                        arrList=S12_HouseHold;
                } else
                    if (categoryList.get(arg2).equals("Погрузка-разгрузка")) {
                        arrList=S13_Load_Unload;
                } else
                    if (categoryList.get(arg2).equals("Прочий транспорт")) {
                        arrList=S14_Other_Transport;
                } else
                    if (categoryList.get(arg2).equals("Разрешительные документы")) {
                        arrList=S15_Permits;
                } else
                    if (categoryList.get(arg2).equals("Заправка автомобиля бензином")) {
                        arrList=S16_Petrol_car;
                } else
                    if (categoryList.get(arg2).equals("Канцелярские принадлежности")) {
                        arrList=S17_Stationery;
                } else
                    if (categoryList.get(arg2).equals("Обучение персонала")) {
                        arrList=S18_Training;
                }
            //







                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"expense@pro100systems.com.ua"});
                email.putExtra(Intent.EXTRA_SUBJECT, arrList + " " + categoryList.get(arg2) + " " + price);
                email.putExtra(Intent.EXTRA_TEXT, message);
                email.putExtra(Intent.EXTRA_STREAM, photoURI);

                email.setType("message/rfc822");

                email.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmailExternal");
                Toast toast = Toast.makeText(getActivity(), "Запускаю клиент Gmail", Toast.LENGTH_LONG);
                toast.show();
                try {
                    startActivity(email);

                } catch (ActivityNotFoundException ex) {
                    // GMail not found
                }





                // Сброс данных с полей
                textMessage.setText("");
                priceMessege.setText("");
                // imageView.setImageDrawable(R.drawable.icon);
                imageView.setImageResource(R.drawable.icon_odoo);

            }
        });

    }

    public void emptyField (){
        Toast.makeText(getActivity(), "Укажите в поле \"Стоимость\" сумму по чеку", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

// Размещение изображения в объект ImageView
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            imageView.setImageURI(photoURI);
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                //  Error occurred while creating the File
                Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(getActivity(), "xyz.devdiscovery.odoocourier.FileProvider", photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    // Remount read XML file.
    private class DownloadPageTask extends
            AsyncTask<String, Void, List<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... urls) {
            try {
                return downloadOneUrl(urls[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return categoryList;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            spMainSelectCategory.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.row, result));

            super.onPostExecute(result);
        }
    }

    /*
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getApplicationContext(), android.R.layout.simple_list_item_1,
                categoryList);
            adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);

            spMainSelectCategory.setAdapter(adapter);
            spMainSelectCategory.setOnItemSelectedListener(this);
    */

    private ArrayList<String> downloadOneUrl(String myurl) throws
            Exception {
        InputStream inputStream = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setReadTimeout(100000);
            connection.setConnectTimeout(100000);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK
                inputStream = connection.getInputStream();

                InputSource inputSource = new InputSource(inputStream);
                // Создаем экземпляр XPath
                XPath xpath = XPathFactory.newInstance().newXPath();
                // задаем выражение для разбора
                String expression = "//title";
                // список полученных узлов
                NodeList nodes = (NodeList) xpath.evaluate(expression,
                        inputSource, XPathConstants.NODESET);


                // если узел найден
                if (nodes != null && nodes.getLength() > 0) {
                    categoryList.clear();
                    int nodesLength = nodes.getLength();
                    for (int i = 0; i < nodesLength; ++i) {
                        // формируем списочный массив
                        Node node = nodes.item(i);
                        categoryList.add(node.getTextContent());
                    }
                }
            } else {
                String data = connection.getResponseMessage()
                        + " . Error Code : " + responseCode;
            }
            connection.disconnect();
            // return data;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return categoryList;
    }

/*
    public void onClickSpinner(View view) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadPageTask().execute(bestUrl);
        } else {
            Toast.makeText(getActivity(), "Нет интернета", Toast.LENGTH_SHORT).show();
        }
    }
*/

}