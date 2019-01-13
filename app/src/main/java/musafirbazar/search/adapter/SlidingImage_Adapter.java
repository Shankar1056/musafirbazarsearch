package musafirbazar.search.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import musafirbazar.search.R;
import musafirbazar.search.activity.OfferShopDescription;
import musafirbazar.search.model.OfferBannerDesc;
import musafirbazar.search.network.Utilz;


public class SlidingImage_Adapter extends PagerAdapter {


    private ArrayList<OfferBannerDesc> imageModelArrayList;
    private LayoutInflater inflater;
    private Context context;
    private OfferShopDescription description;


    public SlidingImage_Adapter(Context context, ArrayList<OfferBannerDesc> imageModelArrayList, OfferShopDescription description) {
        this.context = context;
        this.imageModelArrayList = imageModelArrayList;
        this.description = description;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return imageModelArrayList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.image);
        final TextView validtill = (TextView) imageLayout
                .findViewById(R.id.validtill);
        validtill.setTypeface(Utilz.font(context,"medium"));


//        imageView.setImageResource(imageModelArrayList.get(position).getImage_drawable());
//        textimage.setText(imageModelArrayList.get(position).getTitle());
        
            Picasso.with(context).load(imageModelArrayList.get(position).getBanner_image()).into(imageView);
            validtill.setText("Valid until " + imageModelArrayList.get(position).getValid_till());
    
            view.addView(imageLayout, 0);
    
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        description.sendpos();
            }
        });

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}