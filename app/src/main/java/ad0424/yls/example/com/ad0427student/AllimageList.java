package ad0424.yls.example.com.ad0427student;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class AllimageList extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ImgMyAdapter mImgMyAdapter;
    private Gson mGson;
    private final String IMG_URL = "http://192.168.134.83:8080/Student/Imgs";
    private AllImgListBean imgs;
    private final String UPDATE_IMG_URL = "http://192.168.134.83:8080/Student/updateImg";
    private int StuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);


        initViews();

    }

    private void getImgs() {

    }

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleView);
        //final LinearLayoutManager manager = new LinearLayoutManager(this);
        final GridLayoutManager manager = new GridLayoutManager(this,3);
        OkHttpUtils.get().url(IMG_URL).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(AllimageList.this, "" + e.toString() ,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                Toast.makeText(AllimageList.this, "" + response.toString(), Toast.LENGTH_SHORT).show();
                mGson = new Gson();
                imgs = mGson.fromJson(response.toString(),AllImgListBean.class);
                mImgMyAdapter = new ImgMyAdapter(imgs);
                mRecyclerView.setLayoutManager(manager);
                mRecyclerView.setAdapter(mImgMyAdapter);
                mImgMyAdapter.notifyDataSetChanged();
            }
        });
        Intent intent = getIntent();
         StuId = intent.getIntExtra("id", 1);
        Toast.makeText(this, ""+StuId, Toast.LENGTH_SHORT).show();





    }


class ImgMyAdapter extends RecyclerView.Adapter<ImgMyAdapter.ViewHolder>{
    private AllImgListBean mAllImgListBean;
    public ImgMyAdapter(AllImgListBean mAllImgListBean){
        this.mAllImgListBean = mAllImgListBean;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.img_list_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        AllImgListBean.ImgListBean imgListBean = mAllImgListBean.getImgList().get(position);
        Glide.with(AllimageList.this).load(imgListBean.getImg()).into(holder.img);

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = holder.getAdapterPosition();
                AllImgListBean.ImgListBean image = mAllImgListBean.getImgList().get(index);
                OkHttpUtils.get().url(UPDATE_IMG_URL)//
                        .addParams("id",String.valueOf(StuId))//
                        .addParams("img",image.getImg())//
                        .build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if(response.toString().equals("yes")){
                            Toast.makeText(AllimageList.this, "请点击刷新按钮刷新列表！！！", Toast.LENGTH_SHORT).show();
                          Intent i = new Intent(AllimageList.this,MainActivity.class);
                            startActivity(i);
                            finish();
                        }else {
                            Toast.makeText(AllimageList.this, "更新失败！！！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return mAllImgListBean.getImgList().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
        }
    }
}


}
