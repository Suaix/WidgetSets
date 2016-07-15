package com.summerxia.widgetsets;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.summerxia.widgetlibray.widget.NomarlDistributionView;

/**
 * Created by SummerXia on 2016/7/15.
 */
public class NomarlDistributionActivity extends AppCompatActivity implements NomarlDistributionView.OnValueChagedListener, View.OnClickListener {
    private TextView tvInvestRate;
    private NomarlDistributionView ndView;
    private EditText etNomarlDistributionPosition;
    private EditText etValueLinePosition;
    private TextView tvNomarlDistributionButton;
    private TextView tvValueLineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nomarl_distribution_layout);
        tvInvestRate = (TextView) findViewById(R.id.tv_reta_of_invest);
        ndView = (NomarlDistributionView) findViewById(R.id.nd_nomarl_distribution_view);
        ndView.setOnValueChagedListener(this);
        ndView.setNomarlDistributionPosition(45);
        ndView.setValueLinePosition(46);
        etNomarlDistributionPosition = (EditText) findViewById(R.id.et_nomarl_distributon_position);
        etValueLinePosition = (EditText) findViewById(R.id.et_value_line_position);
        tvNomarlDistributionButton = (TextView) findViewById(R.id.tv_nomarl_distributon_position_button);
        tvValueLineButton = (TextView) findViewById(R.id.tv_value_line_position_button);
        tvNomarlDistributionButton.setOnClickListener(this);
        tvValueLineButton.setOnClickListener(this);
    }

    @Override
    public void onValueChaged(int value) {
        tvInvestRate.setText(value+"%");
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_nomarl_distributon_position_button:
                if(TextUtils.isEmpty(etNomarlDistributionPosition.getText().toString())){
                    Toast.makeText(this,"请输入值", Toast.LENGTH_SHORT).show();
                } else {
                    int num = Integer.parseInt(etNomarlDistributionPosition.getText().toString().trim());
                    ndView.setNomarlDistributionPosition(num);
                }
                break;
            case R.id.tv_value_line_position_button:
                if (TextUtils.isEmpty(etValueLinePosition.getText().toString())){
                    Toast.makeText(this,"请输入值", Toast.LENGTH_SHORT).show();
                } else {
                    int num = Integer.parseInt(etValueLinePosition.getText().toString().trim());
                    ndView.setValueLinePosition(num);
                }
                break;
        }
    }
}
