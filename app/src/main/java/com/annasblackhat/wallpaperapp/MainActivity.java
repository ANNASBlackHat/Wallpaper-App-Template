package com.annasblackhat.wallpaperapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.annasblackhat.wallpaperapp.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ProgressDialog dialog;
    private List<String> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        images = new WallpaperData().getImages();
        binding.recView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recView.setAdapter(new MainAdapter(images));

        dialog = new ProgressDialog(this);
        dialog.setMessage("loading...");
        dialog.setCancelable(false);

        binding.recView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 28);
                    return;
                }
                dialog.show();
                Observable.fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return downloadImage(images.get(position));
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(@NonNull Boolean aBoolean) throws Exception {
                                String msg = aBoolean ? "Wallpaper successfully updated!" : "Sorry we have some problem!";
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
            }
        }));
    }

    private boolean downloadImage(String path) {
        InputStream is = null;
        Bitmap bmImg = null;
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            bmImg = BitmapFactory.decodeStream(is, null, options);

            String imgPath = url.getPath();
            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Wallpapers");
            dir.mkdirs();

            String fileName = new File(imgPath).getName();
            if(!(fileName.endsWith(".jpg") || fileName.endsWith(".png")))
                fileName = fileName+".jpg";
            File file = new File(dir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bmImg.compress(Bitmap.CompressFormat.JPEG, 75, fos);
            fos.flush();
            fos.close();

            File imageFile = file;
                /*MediaScannerConnection.scanFile(this,
                        new String[] { imageFile.getPath() },
                        new String[] { "image/jpeg" }, null);*/
            WallpaperManager wm = WallpaperManager.getInstance(this);
            wm.setBitmap(bmImg);
            createFile("", dir, ".nomedia");

            return true;
        } catch (MalformedURLException e) {
            System.out.println("xxx downloadImage: "+e.getMessage());
        } catch (IOException e) {
            System.out.println("xxx downloadImage IOException: "+e.getMessage());
        }finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {

                }
            }
        }
        return false;
    }

    private void createFile(String data, File parentDir, String fileName){
        File file = new File(parentDir, fileName);
        FileOutputStream os;
        try {
            os = new FileOutputStream(file);
            os.write(data.getBytes());
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("xx FileNotFoundException "+e);
        } catch (IOException e) {
            System.out.println("xx IOException "+e);
        } catch (Exception e) {
            System.out.println("xx Exception "+e);
        }
    }

}
