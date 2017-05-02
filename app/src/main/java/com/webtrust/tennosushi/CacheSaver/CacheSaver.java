package com.webtrust.tennosushi.CacheSaver;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.IsolatedContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by Pantich on 24.04.2017.
 */
//Класс @CacheSaver нужен для сохранения данных в кэш приложения. Является синглтоном

public class CacheSaver {
    private static CacheSaver cacheSaver;
    private String cacheFoldel;//папка кэша приложения пока не придумал как её иницализировать
    private CacheSaver(){}

    public static synchronized CacheSaver getCache(){
        if(cacheSaver==null){
            cacheSaver=new CacheSaver();
        }
        return cacheSaver;

    }


    public void PictureSave(Bitmap bitmap){

        OutputStream fOut = null;



        try {
            File file = new File(cacheFolder, "suka"+".jpg");
            fOut = new FileOutputStream(file);


            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut); // сохранять картинку в png-формате с 85% сжатия.
            fOut.flush();
            fOut.close();

        }
        catch (Exception e) // здесь необходим блок отслеживания реальных ошибок и исключений, общий Exception приведен в качестве примера
        {

        }

    }

    public void PictureSave(InputStream inputStream){ //Сохряет картинку из потока

        Bitmap bimapToSave= BitmapFactory.decodeStream(inputStream);

        PictureSave(bimapToSave);
    }
    }
