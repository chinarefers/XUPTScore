package com.xy.fy.asynctask;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mc.util.HttpUtilMc;
import com.mc.util.Util;
import com.xy.fy.adapter.LibAdapter;
import com.xy.fy.singleton.BookList;
import com.xy.fy.util.StaticVarUtil;
import com.xy.fy.util.ViewUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.LinearLayout;

public class RenewLibAsynctask extends AsyncTask<String, String, String> {

  ProgressDialog  progressDialog;
  Activity mActivity;
  private OnRenew onRenew;
  public interface OnRenew{
    void onRenew(String result,ProgressDialog progressDialog);
  }
  public RenewLibAsynctask(Activity mActivity,OnRenew onRenew) {
    // TODO Auto-generated constructor stub
    this.mActivity = mActivity;
    this.progressDialog = ViewUtil.getProgressDialog(mActivity, "����������");
    progressDialog.show();
    this.onRenew = onRenew;
  }
  @Override
  protected String doInBackground(String... params) {
    // TODO Auto-generated method stub
    Util.getRenewParmas(mActivity, params[0]);
    String url = HttpUtilMc.LIB_URL + "/servlet/RenewServlet?data="
        + StaticVarUtil.renewData
        + "&viewstate=" + StaticVarUtil.renewViewstate;
    return HttpUtilMc.queryStringForPost(url);
  }

  @Override
  protected void onPostExecute(String result) {
    // TODO Auto-generated method stub
    super.onPostExecute(result);
    onRenew.onRenew(result,progressDialog);
    
  }

  
  private void ShowLibMessage(LibAdapter adapter) {
    adapter.notifyDataSetChanged();
  }
}
