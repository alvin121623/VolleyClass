package application.aku.volleyclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import application.aku.volleyclass.METHOD.Custom_item;
import application.aku.volleyclass.METHOD.IResult;
import application.aku.volleyclass.METHOD.Object_item;
import application.aku.volleyclass.METHOD.VolleyClass;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.ivback)
    ImageView ivback;
    @BindView(R.id.etname)
    EditText etname;
    @BindView(R.id.ivadd)
    ImageView ivadd;
    @BindView(R.id.SRLrefresh)
    SwipeRefreshLayout SRLrefresh;
    @BindView(R.id.listitem)
    RecyclerView listitem;
    @BindView(R.id.LLmore)
    LinearLayout LLmore;
    @BindView(R.id.tvempty)
    TextView tvempty;

    JSONArray arrayItems = null;
    ArrayList<Object_item> listItems = new ArrayList<>();
    Custom_item adapterItems;

    VolleyClass volleyService;

    int page = 1;
    String name, id;

    SweetAlertDialog spinx;

    String token = "token";
    String api = "https://domain.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initVolleyResult();
        set_click();
        set_data();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
    }

    private void set_data() {
        adapterItems = new Custom_item(this, listItems);
        listitem.setAdapter(adapterItems);
        listitem.setHasFixedSize(true);
        listitem.setLayoutManager(new LinearLayoutManager(this));
        listitem.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    page++;
                    volleyService.restAPI(Request.Method.GET,api+"?page="+page+"&limit=50", null, token, "GET");
                }
            }
        });

        volleyService.restAPI(Request.Method.GET,api+"?page="+page+"&limit=50", null, token, "GET");
    }

    private void set_click() {
        ivback.setOnClickListener(view -> {
            finish();
            overridePendingTransition(0, 0);
        });

        ivadd.setOnClickListener(view -> {
            name = etname.getText().toString();

            if (TextUtils.isEmpty(name)){
                etname.setError("Can't be empty");
                etname.requestFocus();
            }else {
                if (TextUtils.isEmpty(id)){
                    JSONObject data = new JSONObject();
                    try {
                        data.put("name", name);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    volleyService.restAPI(Request.Method.POST, api, data, token, "POST");
                }else {
                    JSONObject data = new JSONObject();
                    try {
                        data.put("name", name);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    volleyService.restAPI(Request.Method.PUT, api+"/"+id, data, token, "PUT");
                }
            }
        });

        SRLrefresh.setOnRefreshListener(() -> {
            page = 1;
            volleyService.restAPI(Request.Method.GET,api+"?page="+page+"&limit=50", null, token, "GET");
        });
    }

    void initVolleyResult() {
        IResult result = new IResult() {

            @Override
            public void notifyLoad(String type, boolean load) {
                if (type.equals("GET")){
                    if (load){
                        LLmore.setVisibility(View.VISIBLE);
                    }else {
                        LLmore.setVisibility(View.GONE);
                        SRLrefresh.setRefreshing(false);
                    }
                }else if (type.equals("POST")){
                    if (load){
                        spinx = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                        spinx.setTitleText("Please wait");
                        spinx.setCancelable(false);
                        spinx.show();
                    }else {
                        spinx.cancel();
                    }
                }else if (type.equals("DELETE")){
                    if (load){
                        spinx = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                        spinx.setTitleText("Please wait");
                        spinx.setCancelable(false);
                        spinx.show();
                    }else {
                        spinx.cancel();
                    }
                }else if (type.equals("PUT")){
                    if (load){
                        spinx = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                        spinx.setTitleText("Please wait");
                        spinx.setCancelable(false);
                        spinx.show();
                    }else {
                        spinx.cancel();
                    }
                }
            }

            @Override
            public void notifySuccess(String type, JSONObject response) {
                if (type.equals("GET")){
                    if (page == 1){
                        listItems.clear();
                    }

                    try {
                        arrayItems = response.getJSONArray("data");

                        for (int i = 0; i < arrayItems.length(); i++) {
                            JSONObject c = arrayItems.getJSONObject(i);

                            listItems.add(new Object_item(
                                    c.getString("id"),
                                    c.getString("name")
                            ));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapterItems.notifyDataSetChanged();

                    if (listItems.size() == 0){
                        tvempty.setVisibility(View.VISIBLE);
                    }else {
                        tvempty.setVisibility(View.GONE);
                    }
                }else if (type.equals("POST")){
                    etname.setText("");

                    new AlertDialog.Builder(MainActivity.this, R.style.Alert)
                            .setTitle("Success")
                            .setMessage("Data Added")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialogInterface, x) -> {
                                dialogInterface.cancel();
                                page = 1;
                                volleyService.restAPI(Request.Method.GET,api+"?page="+page+"&limit=50", null, token, "GET");
                            })
                            .show();
                }else if (type.equals("DELETE")){
                    page = 1;
                    volleyService.restAPI(Request.Method.GET,api+"?page="+page+"&limit=50", null, token, "GET");
                }else if (type.equals("PUT")){
                    id = "";
                    etname.setText("");

                    new AlertDialog.Builder(MainActivity.this, R.style.Alert)
                            .setTitle("Success")
                            .setMessage("Data Updated")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialogInterface, x) -> {
                                dialogInterface.cancel();
                                page = 1;
                                volleyService.restAPI(Request.Method.GET,api+"?page="+page+"&limit=50", null, token, "GET");
                            })
                            .show();
                }
            }

            @Override
            public void notifyError(String type, String message) {
                new AlertDialog.Builder(MainActivity.this, R.style.Alert)
                        .setTitle("Error")
                        .setMessage(message)
                        .setPositiveButton("OK", (dialogInterface, x) -> dialogInterface.cancel())
                        .show();
            }

            @Override
            public void notifyUnauth(String type, String message) {
                new AlertDialog.Builder(MainActivity.this, R.style.Alert)
                        .setTitle("Unauthorized")
                        .setMessage(message)
                        .setPositiveButton("OK", (dialogInterface, x) -> dialogInterface.cancel())
                        .show();
            }

            @Override
            public void notifyFailed(String type, String message) {
                new AlertDialog.Builder(MainActivity.this, R.style.Alert)
                        .setTitle("Failed")
                        .setMessage(message)
                        .setPositiveButton("OK", (dialogInterface, x) -> dialogInterface.cancel())
                        .show();
            }
        };
        volleyService = new VolleyClass(result, this);
    }

    public void klik(int position, String aksi) {
        if (aksi.equals("detail")){
            Toast.makeText(MainActivity.this, listItems.get(position).getId()+"\n"+listItems.get(position).getName(), Toast.LENGTH_SHORT).show();
        }else if (aksi.equals("edit")){
            id = listItems.get(position).getId();
            etname.setText(listItems.get(position).getName());
        }else if (aksi.equals("delete")){
            new AlertDialog.Builder(MainActivity.this, R.style.AppTheme2)
                    .setTitle("Delete")
                    .setMessage("Are you sure?")
                    .setPositiveButton("YES", (dialogInterface, i) -> {
                        dialogInterface.cancel();

                        volleyService.restAPI(Request.Method.DELETE, api+"/"+listItems.get(position).getId(), null, token, "DELETE");
                    })
                    .setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.cancel())
                    .show();
        }
    }
}
