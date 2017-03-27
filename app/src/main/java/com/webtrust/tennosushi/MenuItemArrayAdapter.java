package com.webtrust.tennosushi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter; // Родительский класс
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MenuItemArrayAdapter extends ArrayAdapter<MenuItem> {
    // Класс для повторного использования представлений списка при прокрутке
    private static class ViewHolder {
        ImageView menuImageView;
        TextView menuTextView;
    }

    // Кэш для уже загруженных картинок (объектов Bitmap)
    private Map<String, Bitmap> bitmaps = new HashMap<>();

    // Конструктор для инициализации унаследованных членов суперкласса
    public MenuItemArrayAdapter(Context context, List<MenuItem> MenuItemList) {
        /*
        в первом и третьем аргументах передаются объект Context (то есть активность,
        в которой отображается ListView) и List<MenuItem> (список выводимых данных).
        Второй аргумент конструктора суперкласса представляет идентификатор ресурса
        макета, содержащего компонент TextView, в котором отображаются данные ListView.
        Аргумент –1 означает, что в приложении используется пользовательский макет,
        чтобы элемент списка не ограничивался одним компонентом TextView.
         */
        super(context, -1, MenuItemList);
    }

    // Создание пользовательских представлений для элементов ListView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Получение объекта Weather для заданной позиции ListView
        MenuItem menuItem = getItem(position);

        //Объект, содержащий ссылки на представления элемента списка
        ViewHolder viewHolder;

        // Проверить возможность повторного использования ViewHolder для элемента, вышедшего за границы экрана
        if (convertView == null) { // Объекта ViewHolder нет, создать его
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.card_list_item, parent, false); // последнем аргументе передается флаг автоматического присоединения представлений
            viewHolder.menuImageView = (ImageView) convertView.findViewById(R.id.menu_image);
            viewHolder.menuTextView = (TextView) convertView.findViewById(R.id.menu_text);
            convertView.setTag(viewHolder);
        }else { // Cуществующий объект ViewHolder используется заново
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Если картинка уже загружена, использовать ее;
        // в противном случае загрузить в отдельном потоке
        if (bitmaps.containsKey(menuItem.picURL)) {
            viewHolder.menuImageView.setImageBitmap(
                    bitmaps.get(menuItem.picURL));
        }else {
            // Загрузить и вывести значок погодных условий
            new LoadImageTask(viewHolder.menuImageView).execute(menuItem.picURL);
        }

        // Получить данные из объекта MenuItem и заполнить представления
        // Назначается текст компонентов TextView элемента ListView
        viewHolder.menuTextView.setText(menuItem.name); // Первый аргумент - строка; Второй - аргументы для форматирования

        return convertView; // Вернуть готовое представление элемента
    }

    // Кажись, изменение imageView так же изменяет и аргумент, переданный в конструкторе LoadImageTask(). Таким образом, создается нечно вроде "ссылки"
    // AsyncTask для загрузки изображения в отдельном потоке
    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView; // Для вывода миниатюры

        // Сохранение ImageView для загруженного объекта Bitmap
        public LoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        // загрузить изображение; params[0] содержит URL-адрес изображения
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(params[0]); // Создать URL для изображения

                // Открыть объект HttpURLConnection, получить InputStream
                // и загрузить изображение
                connection = (HttpURLConnection) url.openConnection(); // Преобразование типа необходимо, потому что метод возвращает URLConnection

                try (InputStream inputStream = connection.getInputStream()) {
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    bitmaps.put(params[0], bitmap); // Кэширование
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally { // Этот участок кода будет выполняться независимо от того, какие исключения были возбуждены и перехвачены
                connection.disconnect(); // Закрыть HttpURLConnection
            }

            return bitmap;
        }

        // Связать значок погодных условий с элементом списка
        // Выполняется в потоке GUI вроде как для вывода изображения
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
