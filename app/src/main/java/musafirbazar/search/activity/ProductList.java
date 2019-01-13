package musafirbazar.search.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Comparator;

import musafirbazar.search.R;
import musafirbazar.search.adapter.ProductListAdapter;
import musafirbazar.search.common.ClsGeneral;
import musafirbazar.search.common.CustomItemAnimator;
import musafirbazar.search.model.CategoryListBean;
import musafirbazar.search.model.ProductListModel;
import musafirbazar.search.network.Utilz;
import musafirbazar.search.webservices.WebServices;
import io.fabric.sdk.android.Fabric;

import static musafirbazar.search.R.id.toolbartext;

/**
 * Created by Shankar on 5/5/2017.
 */

public class ProductList extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView listrecycler;
    private AllCategory allCategory;
    private String menuid, cityid, stateid, name;
    private ProductListAdapter categoryListAdapter;
    private ArrayList<ProductListModel> productListModels = new ArrayList<>();
    private TextView totalcount;
    private SwipeRefreshLayout expandable_list_swipe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        setContentView(R.layout.productlist);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = ProductList.this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(ProductList.this, R.color.colorPrimaryDark));

        }
        try {
            Intent intent = getIntent();
            menuid = intent.getStringExtra("subcat_id");
            name = intent.getStringExtra("name");
            cityid = ClsGeneral.getPreferences(ProductList.this, "cityid");
            stateid = ClsGeneral.getPreferences(ProductList.this, "stateid");
        } catch (Exception e) {
            e.printStackTrace();
        }
        domapping();
    }

    private void domapping() {
        try {
            expandable_list_swipe = (SwipeRefreshLayout) findViewById(R.id.expandable_list_swipe);
            TextView actionbar_title = (TextView) findViewById(toolbartext);
            totalcount = (TextView) findViewById(R.id.totalcount);
            findViewById(R.id.back).setOnClickListener(this);

            actionbar_title.setText(name);
            findViewById(R.id.tryagain).setOnClickListener(this);

            listrecycler = (RecyclerView) findViewById(R.id.listrecycler);
            listrecycler.setLayoutManager(new LinearLayoutManager(this));
            listrecycler.setItemAnimator(new CustomItemAnimator());
            categoryListAdapter = new ProductListAdapter(ProductList.this, new ArrayList<ProductListModel>(), R.layout.productlist_row, ProductList.this);
            listrecycler.setAdapter(categoryListAdapter);

            actionbar_title.setTypeface(Utilz.font(ProductList.this, "bold"));
            totalcount.setTypeface(Utilz.font(ProductList.this, "medium"));
            expandable_list_swipe.setOnRefreshListener(this);
            if (Utilz.isInternetConnected(ProductList.this))
                expandable_list_swipe.setRefreshing(true);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (productListModels.size() == 0) {
            if (Utilz.isInternetConnected(ProductList.this)) {
                allCategory = new AllCategory();
                allCategory.execute(WebServices.SUBCATEGORY);
            } else {
                Toast.makeText(ProductList.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void call(int i) {

        try {

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + productListModels.get(i).getVendor_contact()));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(ProductList.this, "Phone facility not available on your device", Toast.LENGTH_SHORT).show();
        }

    }

    public void animateActivity(int i) {

        Intent intent = new Intent(ProductList.this, ProductDescription.class);
        intent.putExtra("vendorname", productListModels.get(i).getVendor_name());
        intent.putExtra("vendorcontact", productListModels.get(i).getVendor_contact());
        intent.putExtra("vendoremail", productListModels.get(i).getVendor_email());
        intent.putExtra("contactperson", productListModels.get(i).getVendor_contact_person_name());
        intent.putExtra("contactlocation", productListModels.get(i).getVendor_Location());
        intent.putExtra("websitelink", productListModels.get(i).getVender_url());
        intent.putExtra("description", productListModels.get(i).getProduct_LongDesc());
        intent.putExtra("product_id", productListModels.get(i).getProduct_id());
        intent.putExtra("liked", productListModels.get(i).getLiked());
        intent.putExtra("likecount", productListModels.get(i).getLike_count());
        intent.putExtra("wishliststatus", productListModels.get(i).getProduct_wishliststatus());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tryagain:
                if (Utilz.isInternetConnected(ProductList.this)) {
                    allCategory = new AllCategory();
                    allCategory.execute(WebServices.SUBCATEGORY);
                } else {
                    Toast.makeText(ProductList.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

                break;
        }

    }

    @Override
    public void onRefresh() {
        if (Utilz.isInternetConnected(ProductList.this))
            expandable_list_swipe.setRefreshing(true);
        allCategory = new AllCategory();
        allCategory.execute(WebServices.SUBCATEGORY);
    }


    private class AllCategory extends AsyncTask<String, Void, String> {
        String result1;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(ProductList.this);
                mProgressDialog.setMessage(getString(R.string.loading));
                mProgressDialog.setIndeterminate(true);
            }

            mProgressDialog.show();*/
        }

        @Override
        protected String doInBackground(String... params) {

            nameValuePairs.add(new BasicNameValuePair("state_id", "1"));
            nameValuePairs.add(new BasicNameValuePair("city_id", "1"));
            nameValuePairs.add(new BasicNameValuePair("sub_catid", menuid));
            try {
                result1 = Utilz.executeHttpPost(params[0], nameValuePairs);
            } catch (Exception e) {
                e.printStackTrace();
            }


            return result1;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (expandable_list_swipe != null)
                expandable_list_swipe.setRefreshing(false);
            LinearLayout nodatafound = (LinearLayout) findViewById(R.id.nodatafound);

            if (s != null) {
                try {
                    Gson gson = new Gson();
                    final CategoryListBean subscriptionsResponse = gson.fromJson(s, CategoryListBean.class);
                    if (subscriptionsResponse.getSuccess().equalsIgnoreCase("true")) {

                        if (productListModels.size() > 0) {
                            productListModels.clear();
                        }
                        categoryListAdapter.clearApplications();
                        if (subscriptionsResponse.getData().size() > 0) {
                            nodatafound.setVisibility(View.GONE);
                            listrecycler.setVisibility(View.VISIBLE);
                            for (int i = 0; i < subscriptionsResponse.getData().size(); i++) {
                                if (subscriptionsResponse.getData().get(i).getProduct_visibility_status().equalsIgnoreCase("1")) {
                                    productListModels.add(subscriptionsResponse.getData().get(i));
                                }
                            }
                            totalcount.setText("Total result found( " + productListModels.size() + " )");
                            dofilterAndsetadapter(productListModels);
                            mProgressDialog.cancel();
                        } else {
                            mProgressDialog.cancel();

                            nodatafound.setVisibility(View.VISIBLE);
                            listrecycler.setVisibility(View.GONE);
                        }

                    } else {
                       // mProgressDialog.cancel();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                   // mProgressDialog.cancel();
                }

            }
        }
    }

    private void dofilterAndsetadapter(ArrayList<ProductListModel> productListModels) {

        ShopSortComparator icc = new ShopSortComparator();
        java.util.Collections.sort(productListModels, icc);

        categoryListAdapter.addApplications(productListModels);
        categoryListAdapter.notifyDataSetChanged();

    }

    class ShopSortComparator implements Comparator<ProductListModel> {
        public int compare(ProductListModel strA, ProductListModel strB) {
            return strA.getBadge().compareTo(strB.getBadge());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
