package com.xy.fy.main;

import com.mc.util.Util;
import com.xy.fy.main.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ShowScoreActivity extends Activity {

  /** Called when the activity is first created. */
  private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
  private final int FP = ViewGroup.LayoutParams.MATCH_PARENT;
  // private Button back;
  private TextView XNandXQ;
  private LinearLayout layout;// Ϊ��ʵ�ֵ����ȡ��

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.card_score);
    Util.setContext(getApplicationContext());
    init();
    String[][] score = null;
    Intent i = getIntent();
    String xn_xq = i.getStringExtra("xn_and_xq");
    XNandXQ.setText(xn_xq);
    String _row = i.getStringExtra("col");// ����
    int row_int = Integer.valueOf(_row);
    score = new String[row_int][];
    for (int j = 0; j < row_int; j++) {
      score[j] = i.getStringArrayExtra("score" + j);
    }
    // �½�TableLayout01��ʵ��
    TableLayout tableLayout = (TableLayout) findViewById(R.id.score);
    // ȫ�����Զ����հ״�
    tableLayout.setStretchAllColumns(true);
    // ����4�У� _col �еı��
    for (int row = 0; row < row_int; row++) {
      TableRow tableRow = new TableRow(this);
      for (int col = 0; col < 4; col++) {

        TextView tv = new TextView(this);
        // tv������ʾ
        tv.setText(score[row][col]);
        tv.setTextSize(20);
        if (col == 0) {// �γ�
          tv.setTextColor(Color.CYAN);
        }
        tableRow.addView(tv);
      }
      // �½���TableRow��ӵ�TableLayout
      tableLayout.addView(tableRow, new TableLayout.LayoutParams(FP, WC));
    }
  }

  private void init() {
    // TODO Auto-generated method stub
    // back = (Button)findViewById(R.id.butBack);
    layout = (LinearLayout) findViewById(R.id.layout);
    layout.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        finish();
      }
    });
    XNandXQ = (TextView) findViewById(R.id.XNandXQ);
    /*
     * back.setOnClickListener(new OnClickListener() {
     * 
     * @Override public void onClick(View v) { // TODO Auto-generated method stub finish(); } });
     */
  }
}
