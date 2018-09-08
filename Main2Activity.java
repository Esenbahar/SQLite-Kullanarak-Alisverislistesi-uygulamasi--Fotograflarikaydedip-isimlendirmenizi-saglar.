package com.esenbaharturkay.alisverislistem;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Main2Activity extends AppCompatActivity {
    ImageView imageview;
    EditText editText;
    static SQLiteDatabase database; //static yaptım çünkü Main2Activty den ulaşabildiğim gibi MainActiviytyden de ulaşabilmek için.
    Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        imageview = (ImageView) findViewById(R.id.imageView);
        editText = (EditText)findViewById(R.id.editText);
        Button button = (Button)findViewById(R.id.button);


        Intent intent = getIntent();

        String info = intent.getStringExtra("info");

        if (info.equalsIgnoreCase("new")){

            Bitmap background = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_launcher_background);
            imageview.setImageBitmap(background);

            button.setVisibility(View.VISIBLE);
            editText.setText("");

        }else {
            String name = intent.getStringExtra("name");
            editText.setText(name);
            int position = intent.getIntExtra("position", 0);
            imageview.setImageBitmap(MainActivity.shopImage.get(position));
            button.setVisibility(View.INVISIBLE) ;
        }

    }

    public void select (View view){

        if (checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            //Kullanıcı izni yok ise olacaklar.

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE} , 2);

        }else{

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,1); //buradan bir resim alacağım için startActivity demiyoruz StartActivtyForresult Diyoruz.

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
     //izin var ise
        if (requestCode == 2){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1); //buradan bir resim alacağım için startActivity demiyoruz StartActivtyForresult Diyoruz.

            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && requestCode == RESULT_OK && data != null){
            Uri image = data.getData();

            try {

                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image);
                imageview.setImageBitmap(selectedImage);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    public void save (View view){

        String imageName = editText.getText().toString(); //isimi kaydetmek için.

        //** Görseli Kaydetmek için
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArray = outputStream.toByteArray();

        try {


            database = this.openOrCreateDatabase("Shops",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS shops (name VARCHAR,image BLOB)");

            String sqlString ="INSERT INTO shops (name,image)VALUES (?,?)";
            SQLiteStatement statement = database.compileStatement(sqlString);
            statement.bindString(1,imageName);
            statement.bindBlob(2,byteArray);
            statement.execute();
        }catch (Exception e){
            e.printStackTrace();
        }

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
//***


    }
}
