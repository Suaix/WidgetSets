package com.summerxia.widgetsets;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.summerxia.widgetlibray.widget.VerticalSlideLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by SummerXia on 2016/7/15.
 */
public class VerticalSlideActivity extends AppCompatActivity implements VerticalSlideLayout.OnInflateContentListener, VerticalSlideLayout.OnItemClickListener, VerticalSlideLayout.OnLoadMoreListener, VerticalSlideLayout.OnScrollDirectionChangeListener {

    private VerticalSlideLayout vslLayout;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vertical_slide_layout);
        random = new Random();
        vslLayout = (VerticalSlideLayout) findViewById(R.id.vsl_container);
        vslLayout.setOnInflateContentListener(this);
        vslLayout.setOnItemClickListener(this);
        vslLayout.setOnloadMoreListener(this);
        vslLayout.setOnScrollDirectionChangedListener(this);
        setTestData();
    }

    private void setTestData() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("this is the content "+i);
        }
        vslLayout.setDataList(list, R.layout.vertical_slide_item);
    }

    @Override
    public void onInflateContent(View view, String data) {
        view.setBackgroundColor(Color.rgb(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
        TextView tvContent = (TextView) view.findViewById(R.id.tv_item_content);
        tvContent.setText(data);
    }

    @Override
    public void onItemClickListener(String data) {
        Toast.makeText(this,data,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadMore() {
        Toast.makeText(this,"onLoadMore",Toast.LENGTH_SHORT).show();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add("this is new content "+i);
        }
        vslLayout.addDataList(list, R.layout.vertical_slide_item);
    }

    @Override
    public void onScrollDirectionChanged(int flag, float dY) {
        Log.i(this.getClass().getSimpleName(), "falg:"+flag+".....dY:"+dY);
    }
}
