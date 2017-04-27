package ad0424.yls.example.com.ad0427student;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private final String TAG = "MainActivity";
    private final String ALL_STU_URL = "http://192.168.134.83:8080/Student/findAllStu";
    private final String DEL_URL = "http://192.168.134.83:8080/Student/deleteStu";
    private final String INSERT_URL = "http://192.168.134.83:8080/Student/insertStu";
    private final String UPDATE_URL = "http://192.168.134.83:8080/Student/updateStu";
    private final String UPLOAD_URL = "http://192.168.134.83:8080/Student/UploadServlet";
    private final String FIND_ALL_ID = "http://192.168.134.83:8080/Student/FindAllId";
    private AllStudentListBean mAllStudentListBean;
    private MyAdapter mMyAdapter;
    private Button mbtn_insert;
    private Button mbtn_del;
    private Button mbtn_update;
    private EditText medt_id;
    private EditText medt_name;
    private EditText medt_age;
    private EditText medt_sex;
    private int id;
    private String name;
    private int age;
    private String sex;
    protected static final int GET_HEAD_IMG = 1001;
    private static final int CROP_HEAD = 1002;
    private Bitmap bmp;
    private String path;
    private Uri imgUri;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String ids[] = new String[1000];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        applyPermissions();
        initViews();

    }

    private void applyPermissions() {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            String permissions[] = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1001);
        }
    }


    private void getImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GET_HEAD_IMG);
    }


    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mbtn_insert = (Button) findViewById(R.id.btn_insert);
        mbtn_del = (Button) findViewById(R.id.btn_del);
        mbtn_update = (Button) findViewById(R.id.btn_update);
        medt_id = (EditText) findViewById(R.id.edt_id);
        medt_age = (EditText) findViewById(R.id.edt_age);
        medt_sex = (EditText) findViewById(R.id.edt_sex);
        medt_name = (EditText) findViewById(R.id.edt_name);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllStu();
            }
        });
        getAllStu();
        mbtn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findAllId();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getContent();
                                    insertStu(id, name, age, sex);
                                    clearEdt();
                                }
                            });

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });

        mbtn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findAllId();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        updateStu(id, name, age, sex);
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                getContent();

            }
        });

        //删除
        mbtn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (medt_id.getText().toString().trim().isEmpty()) {
                    Toast.makeText(MainActivity.this, "id不能为空！！！", Toast.LENGTH_SHORT).show();
                    return;
                }
                final int id = Integer.parseInt(medt_id.getText().toString().trim());


            }
        });


    }

    private void clearEdt() {
        medt_id.setText("");
        medt_name.setText("");
        medt_sex.setText("");
        medt_age.setText("");
    }

    private void getContent() {


        if (medt_id.getText().toString().trim().isEmpty() || medt_name.getText().toString().trim().isEmpty() ||
                medt_age.getText().toString().trim().isEmpty() || medt_sex.getText().toString().trim().isEmpty()
                ) {
            Toast.makeText(MainActivity.this, "id,name,age,sex不能为空！！！", Toast.LENGTH_SHORT).show();
            return;
        }
        id = Integer.parseInt(medt_id.getText().toString().trim());
        name = medt_name.getText().toString().trim();
        age = Integer.parseInt(medt_age.getText().toString().trim());
        sex = medt_sex.getText().toString().trim();
    }

    //更新
    private void updateStu(int id, String name, int age, String sex) throws UnsupportedEncodingException {
        int temp = 0;
        for (int i = 0; i < ids.length; i++) {
            if (String.valueOf(id).equals(ids[i])) {
                temp++;

            }
        }
        if (temp == 0) {
            Toast.makeText(this, "id" + id + "不存在，请重新输入！！！", Toast.LENGTH_SHORT).show();
            return;
        }

        Map params = new HashMap();
        params.put("id", id + "");
        params.put("name", name);
        params.put("age", age + "");
        params.put("sex", sex);

        OkHttpUtils.get().url(UPDATE_URL).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                getAllStu();
            }
        });
        getContent();
        clearEdt();
    }

    private void insertStu(int id, String name, int age, String sex) {

        for (int i = 0; i < ids.length; i++) {
            if (String.valueOf(id).equals(ids[i])) {
                Toast.makeText(this, "id" + id + "已经存在，请重新输入！！！", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        Map params = new HashMap();
        params.put("id", id + "");
        params.put("name", name);
        params.put("age", age + "");
        params.put("sex", sex);

        OkHttpUtils.get().url(INSERT_URL).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                getAllStu();
            }
        });


    }

    private void getAllStu() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        OkHttpUtils.get().url(ALL_STU_URL).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.d(TAG, "onError: " + e.getMessage());

            }

            @Override
            public void onResponse(String response, int id) {

                try {
                    response = new String(response.toString().getBytes("ISO8859-1"), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Toast.makeText(MainActivity.this, "" + response.toString() + id, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onResponse: " + response.toString());

                Gson gson = new Gson();
                mAllStudentListBean = gson.fromJson(response.toString(), AllStudentListBean.class);
                mMyAdapter = new MyAdapter(mAllStudentListBean);
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.setAdapter(mMyAdapter);
                mMyAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private AllStudentListBean mAllStudentListBean;

        public MyAdapter(AllStudentListBean mAllStudentListBean) {
            this.mAllStudentListBean = mAllStudentListBean;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.allstulist_item, parent, false);
            ViewHolder holder = new ViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final AllStudentListBean.StuListBean stuListBean = mAllStudentListBean.getStuList().get(position);

            holder.tv_id.setText(String.valueOf(stuListBean.getId()));
            holder.tv_name.setText(stuListBean.getName());
            holder.tv_age.setText(String.valueOf(stuListBean.getAge()));
            holder.tv_sex.setText(stuListBean.getSex());
            holder.head_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent  = new Intent(MainActivity.this,AllimageList.class);
//                    intent.putExtra("id",stuListBean.getId());
//                    startActivity(intent);
                    getImg();
                }
            });

            holder.iv_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    id = mAllStudentListBean.getStuList().get(holder.getAdapterPosition()).getId();
                    Toast.makeText(MainActivity.this, "id=" + id, Toast.LENGTH_SHORT).show();
                    delStu();
                }
            });
            String url = stuListBean.getHeadimg();
            Glide.with(MainActivity.this).load(url).into(holder.head_img);
        }

        @Override
        public int getItemCount() {
            return mAllStudentListBean.getStuList().size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView head_img;
            private TextView tv_id;
            private TextView tv_name;
            private TextView tv_age;
            private TextView tv_sex;
            private ImageView iv_del;

            public ViewHolder(View itemView) {
                super(itemView);
                head_img = (ImageView) itemView.findViewById(R.id.head_img);
                tv_id = (TextView) itemView.findViewById(R.id.tv_id);
                tv_name = (TextView) itemView.findViewById(R.id.tv_name);
                tv_age = (TextView) itemView.findViewById(R.id.tv_age);
                tv_sex = (TextView) itemView.findViewById(R.id.tv_sex);
                iv_del = (ImageView) itemView.findViewById(R.id.iv_del);
            }
        }
    }

    private void delStu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("警告！")
                .setIcon(R.drawable.alert)
                .setMessage("确定要删除吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OkHttpUtils.get().url(DEL_URL).addParams("id", id + "").build().execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {

                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                //更新列表
                                getAllStu();
                            }
                        });
                    }
                })

        ;
        builder.show();

        clearEdt();
    }


    private void upLoadImg(String path) {
        Toast.makeText(this, "id=" + id, Toast.LENGTH_SHORT).show();
        OkHttpUtils
                .post()//
                .url(UPLOAD_URL)//
                .addParams("id", String.valueOf(id))//
                .addFile("ab", "abc.jpg", new File(path))//
                .build()//
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                        getAllStu();
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_HEAD_IMG) {

            if (data != null) {
                imgUri = data.getData();

                //获取图片路径
                String proj[] = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(imgUri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(column_index);
                Log.i("aaaaaaaaaaaaaaaaaaaa", "onActivityResult: " + path);
                Toast.makeText(this, "" + path, Toast.LENGTH_SHORT).show();


            }


            // 裁剪图片
            Intent intent = new Intent();
            intent.setAction("com.android.camera.action.CROP");
            intent.setDataAndType(imgUri, "image/*");
            intent.putExtra("crop", true);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("return-data", true);

            startActivityForResult(intent, CROP_HEAD);

        }

        if (requestCode == CROP_HEAD) {
            if (data == null) {
                return;
            }
            Bundle bundle = data.getExtras();
            bmp = bundle.getParcelable("data");
            upLoadImg(path);
        }
    }

    private void findAllId() {
        OkHttpUtils.post().url(FIND_ALL_ID)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse: " + response.toString());
                        ids = response.toString().split(",");
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
