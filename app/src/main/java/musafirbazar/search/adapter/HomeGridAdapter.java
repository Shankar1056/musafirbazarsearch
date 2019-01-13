package musafirbazar.search.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import musafirbazar.search.R;
import musafirbazar.search.model.HomeCategory;
import musafirbazar.search.network.Utilz;

import static musafirbazar.search.R.id.cat_name;

/**
 * Created by Shankar on 1/5/2017.
 */

public class HomeGridAdapter extends BaseAdapter {
    HomeCategory model;
    ArrayList<HomeCategory> modellist;
    private ArrayList<HomeCategory> filterlist = null;
    private Activity ativity;
    private int newcount;

    private static LayoutInflater inflater = null;

    public HomeGridAdapter(Activity c, ArrayList<HomeCategory> haml) {
        try {
            this.ativity = c;
            this.modellist = haml;
            this.filterlist = new ArrayList<HomeCategory>();
            this.filterlist.addAll(modellist);
            inflater = (LayoutInflater) ativity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {

        }

    }

    @Override
    public int getCount() {
        return modellist.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        public TextView cat_name;
        ImageView cat_image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        final ViewHolder holder;

        if (convertView == null) {
            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.homegrid_item, null);


            /****** View Holder Object to contain tabitem.xml file elements ******/
            holder = new ViewHolder();
            holder.cat_image = (ImageView) vi.findViewById(R.id.cat_image);
            holder.cat_name = (TextView) vi.findViewById(cat_name);

            holder.cat_name.setTypeface(Utilz.font(ativity, "medium"));


            vi.setTag(holder);
            vi.setTag(cat_name, holder.cat_name);
            vi.setTag(R.id.cat_image, holder.cat_image);

        } else {
            holder = (ViewHolder) vi.getTag();

        }
        if (modellist.size() <= 0) {
        } else {

            model = modellist.get(position);
            holder.cat_image.setTag(position);
            holder.cat_name.setTag(position);


            holder.cat_name.setText(model.getCat_name());


            Picasso.with(ativity).load(model.getCat_image()).into(holder.cat_image);


        }
        return vi;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        modellist.clear();
        if (charText.length() == 0) {
            modellist.addAll(filterlist);
        } else {
            for (HomeCategory wp : filterlist) {
                if (wp.getCat_name().toLowerCase(Locale.getDefault()).contains(charText)) {
                    modellist.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }


}
