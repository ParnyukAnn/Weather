package com.aparnyuk.weather;

        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.Locale;

        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.squareup.picasso.Picasso;

public class WeatherAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<WeatherInformation> objects;

   public WeatherAdapter(Context context, ArrayList<WeatherInformation> arrayWeather) {
        this.ctx = context;
        this.objects = arrayWeather;
        this.lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return objects.size();
    }


    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

        @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item, parent, false);
        }

        WeatherInformation w = getWeatherInformation(position);


        if (Math.round(w.main.temp) >0) {((TextView) view.findViewById(R.id.tvTemperature)).setText(" +" + Math.round(w.main.temp)+"C");}
         else {((TextView) view.findViewById(R.id.tvTemperature)).setText(" " + Math.round(w.main.temp)+"C");}
        ((TextView) view.findViewById(R.id.tvTime)).setText((w.dt_txt).substring(11,16));
        ((TextView) view.findViewById(R.id.tvData)).setText((w.dt_txt).substring(0,10));
        ((TextView) view.findViewById(R.id.tvWeather)).setText(w.weather[0].description);

        Picasso.with(ctx)
                .load("http://openweathermap.org/img/w/"+w.weather[0].icon+".png")
                .placeholder(R.drawable.dunno)
                .error(R.drawable.dunno)
                .into((ImageView) view.findViewById(R.id.ivImage));


        try {
            String dt = w.dt_txt;
            Locale locale = new Locale("ru");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(dt);
            Calendar c = Calendar.getInstance();
            c.set(date.getYear(), date.getMonth(), date.getDay());
            dt = c.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.SHORT, locale);
            dt = dt.substring(0,1).toUpperCase()+dt.substring(1)+".";
            ((TextView) view.findViewById(R.id.tvDay)).setText(dt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private WeatherInformation getWeatherInformation (int position) {
        return ((WeatherInformation) getItem(position));
    }



}