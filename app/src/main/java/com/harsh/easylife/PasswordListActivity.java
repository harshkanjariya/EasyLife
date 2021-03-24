package com.harsh.easylife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.harsh.hkutils.list.HKList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;

public class PasswordListActivity extends AppCompatActivity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_menu,menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId()==R.id.menu_add){
			showAddDialog();
		}
		return super.onOptionsItemSelected(item);
	}

	private void showAddDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Add Group");
		LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_add_password, null, false);
		builder.setView(layout);
		builder.setPositiveButton("Save", (dialog, which) -> {
			TextView idTx = layout.findViewById(R.id.id);
			TextView passTx = layout.findViewById(R.id.password);
			if (jsonArray==null)
				jsonArray = new JSONArray();
			JSONObject obj=new JSONObject();
			try {
				obj.put("id",idTx.getText().toString());
				obj.put("password",passTx.getText().toString());
				jsonArray.put(obj);
				savePasswords();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		});
		builder.show();
	}

	private void savePasswords() {
		File file = new File(getFilesDir()+"/passwords/"+groupId+".json");
		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(file);
			outputStream.write(jsonArray.toString(4).getBytes());
			outputStream.close();
			loadPasswords();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	String groupId;
	HKList hkList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_list);

		Intent intent = getIntent();
		getSupportActionBar().setTitle(intent.getStringExtra("title"));
		groupId = intent.getStringExtra("id");

		File file = new File(getFilesDir()+"/passwords");
		if (!file.exists())
			file.mkdir();

		hkList = findViewById(R.id.password_list);
		hkList.init(R.layout.layout_password_item, list, (holder, object, position) -> {
			holder.setText(R.id.id,object.first);
			holder.setText(R.id.password,object.second);
		});
		loadPasswords();
	}
	ArrayList<Pair<String,String>>list = new ArrayList<>();
	JSONArray jsonArray;
	private void loadPasswords(){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(getFilesDir()+"/passwords/"+groupId+".json"));
			String str;
			StringBuilder builder = new StringBuilder();
			while ((str=reader.readLine())!=null){
				builder.append(str);
			}
			reader.close();
			jsonArray = new JSONArray(builder.toString());
			list.clear();
			for (int i=0;i<jsonArray.length();i++){
				JSONObject obj = jsonArray.getJSONObject(i);
				list.add(new Pair<>(obj.getString("id"),obj.getString("password")));
			}
			hkList.update();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}