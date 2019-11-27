package com.example.smartcity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;

import java.util.List;


public class InputTipsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
        Inputtips.InputtipsListener, OnItemClickListener {

    private ListView mInputListView;
    private List<Tip> mCurrentTipList;
    private InputTipsAdapter mIntipAdapter;

    public static String DEFAULT_CITY = "西安";
    public static final int RESULT_CODE_INPUTTIPS = 101;
    public static final int REQUEST_SUC = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_inputtips);
        super.onCreate(savedInstanceState);

        initSearchView();
        mInputListView = findViewById(R.id.inputtip_list);
        mInputListView.setOnItemClickListener(this);
    }

    private void initSearchView() {
        SearchView searchView = findViewById(R.id.keyWord);
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(false);
        searchView.onActionViewExpanded();
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(false);
    }

    /**
     * 输入提示回调
     */
    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        if (rCode == REQUEST_SUC) {
            mCurrentTipList = tipList;
            mIntipAdapter = new InputTipsAdapter(getApplicationContext(), mCurrentTipList);
            mInputListView.setAdapter(mIntipAdapter);
            mIntipAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "错误码 :" + rCode, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     *点击提示内容回调
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (mCurrentTipList != null) {
            Tip tip = (Tip) adapterView.getItemAtPosition(i);
            Intent intent = new Intent();
            intent.putExtra("tip", tip);
            setResult(RESULT_CODE_INPUTTIPS, intent);
            this.finish();
        }
    }

    /**
     * 按下确认键触发，本例为键盘回车或搜索键
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * 输入字符变化时触发
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        if (!TextUtils.isEmpty(newText)) {
            InputtipsQuery inputquery = new InputtipsQuery(newText, DEFAULT_CITY);
            Inputtips inputTips = new Inputtips(InputTipsActivity.this, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        } else {
            if (mIntipAdapter != null && mCurrentTipList != null) {
                mCurrentTipList.clear();
                mIntipAdapter.notifyDataSetChanged();
            }
        }
        return true;
    }

}