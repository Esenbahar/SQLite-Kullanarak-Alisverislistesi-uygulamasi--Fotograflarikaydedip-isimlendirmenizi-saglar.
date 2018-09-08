package com.esenbaharturkay.alisverislistem;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<Bitmap> shopImage;


    //**Menüyü Bağlıyorum
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Menü Inflater menüyü kullanmamız için gerekli olan bir obje.
        MenuInflater  menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_shop,menu);//menüyü çıkar (inflate) ediyorum.


        return super.onCreateOptionsMenu(menu);
    }

//*****

//** Menüyü seçtiğimizde olacak işlemleri gerçekleştiriyorum.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()== R.id.add_shop){

            Intent intent = new Intent(getApplicationContext(),Main2Activity.class);
            intent.putExtra("info", "new");
            startActivity(intent);


        }


        return super.onOptionsItemSelected(item);
    }
//*****


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.listView);
        final ArrayList<String> imageName = new ArrayList<String>();
        shopImage = new ArrayList<Bitmap>();

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,imageName);
        listView.setAdapter(arrayAdapter);

        try {

            Main2Activity.database =  this.openOrCreateDatabase("Shops",MODE_PRIVATE,null);
          Main2Activity.database.execSQL("CREATE TABLE IF NOT EXISTS shops (name VARCHAR,image BLOB)");

            Cursor cursor = Main2Activity.database.rawQuery("SELECT * FROM shops",null);

            int nameIx = cursor.getColumnIndex("name");
            int imageIx = cursor.getColumnIndex("image");

            cursor.moveToFirst();

            while (cursor != null){
            imageName.add(cursor.getString(nameIx));

            byte[] byteArray = cursor.getBlob(imageIx);
            Bitmap images = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
            shopImage.add(images);

            cursor.moveToNext();

            arrayAdapter.notifyDataSetChanged();


            }

        }catch (Exception e){
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                intent.putExtra("info", "old");
                intent.putExtra("name", imageName.get(position));
                intent.putExtra("position", position);

                startActivity(intent);

            }
        });
    }

}
