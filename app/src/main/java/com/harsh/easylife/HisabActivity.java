package com.harsh.easylife;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.harsh.hkutils.list.HKList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class HisabActivity extends AppCompatActivity {
	ArrayList<JSONObject> array=new ArrayList<>();
	ArrayList<String>names=new ArrayList<>();
	HKList list;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hisab);

		setupHisabList();
		loadHisab();
	}
	private void setupHisabList() {
		list = findViewById(R.id.hisab_list);
		list.init(R.layout.item_hisab_list, array, (holder, object,i) -> {
			try {
				holder.setText(R.id.name,object.getString("name"));
				holder.setText(R.id.amount,object.getString("amount"));
				holder.setText(R.id.description,object.getString("description"));

				TextView type=holder.textView(R.id.type);
				if (object.getBoolean("type")){
					type.setText("Debit");
					type.setTextColor(Color.parseColor("#f00000"));
				}else{
					type.setText("Credit");
					type.setTextColor(Color.parseColor("#00a000"));
				}
				holder.view(R.id.item_layout).setOnLongClickListener(view -> {
					try {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
							showEditDialog(array.indexOf(object),
									object.getString("name"),
									object.getString("amount"),
									object.getString("description"),
									object.getBoolean("type"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					return true;
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}
		});
	}
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	private void showEditDialog(int index, String name, String amount, String description, boolean type) {
		LayoutInflater inflater=getLayoutInflater();
		LinearLayout layout= (LinearLayout) inflater.inflate(R.layout.dialog_add_hisab,null,false);

		EditText nameEditText=layout.findViewById(R.id.name);
		EditText amountEditText=layout.findViewById(R.id.amount);
		EditText descriptionEditText=layout.findViewById(R.id.description);
		SwitchCompat switchCompat=layout.findViewById(R.id.type);

		nameEditText.setText(name);
		amountEditText.setText(amount);
		descriptionEditText.setText(description);
		switchCompat.setChecked(type);

		AlertDialog.Builder builder=new AlertDialog.Builder(this)
				.setView(layout)
				.setCancelable(false)
				.setPositiveButton("Save", (dialogInterface, i) -> {
					if (nameEditText.getText().toString().isEmpty()){
						Snackbar.make(layout,"Please enter name", BaseTransientBottomBar.LENGTH_SHORT).show();
						nameEditText.requestFocus();
						return;
					}
					if (amountEditText.getText().toString().isEmpty()){
						Snackbar.make(layout,"Please enter amount", BaseTransientBottomBar.LENGTH_SHORT).show();
						amountEditText.requestFocus();
						return;
					}
					if (descriptionEditText.getText().toString().isEmpty()){
						Snackbar.make(layout,"Please enter description", BaseTransientBottomBar.LENGTH_SHORT).show();
						descriptionEditText.requestFocus();
						return;
					}
					String n=nameEditText.getText().toString();
					String a=amountEditText.getText().toString();
					String d=descriptionEditText.getText().toString();
					boolean t=switchCompat.isChecked();

					try {
						JSONObject object=new JSONObject();

						object.put("name",n);
						object.put("amount",a);
						object.put("description",d);
						object.put("type",t);

						array.set(index,object);
						storeHisab();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				})
				.setNeutralButton("Delete",(dialog,i)->{
					array.remove(index);
					storeHisab();
				})
				.setNegativeButton("Cancel",null);
		builder.show();
	}
	private void showAddDialog(){
		LayoutInflater inflater=getLayoutInflater();
		LinearLayout layout= (LinearLayout) inflater.inflate(R.layout.dialog_add_hisab,null,false);

		EditText name=layout.findViewById(R.id.name);
		EditText amount=layout.findViewById(R.id.amount);
		EditText description=layout.findViewById(R.id.description);
		SwitchCompat switchCompat=layout.findViewById(R.id.type);

		AlertDialog.Builder builder=new AlertDialog.Builder(this)
				.setView(layout)
				.setCancelable(false)
				.setPositiveButton("Save", (dialogInterface, i) -> {
					if (name.getText().toString().isEmpty()){
						Snackbar.make(layout,"Please enter name", BaseTransientBottomBar.LENGTH_SHORT).show();
						name.requestFocus();
						return;
					}
					if (amount.getText().toString().isEmpty()){
						Snackbar.make(layout,"Please enter amount", BaseTransientBottomBar.LENGTH_SHORT).show();
						amount.requestFocus();
						return;
					}
					if (description.getText().toString().isEmpty()){
						Snackbar.make(layout,"Please enter description", BaseTransientBottomBar.LENGTH_SHORT).show();
						description.requestFocus();
						return;
					}
					String n=name.getText().toString();
					String a=amount.getText().toString();
					String d=description.getText().toString();
					boolean type=switchCompat.isChecked();

					try {
						JSONObject object=new JSONObject();

						object.put("name",n);
						object.put("amount",a);
						object.put("description",d);
						object.put("type",type);

						array.add(object);
						storeHisab();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				})
				.setNegativeButton("Cancel",null);
		builder.show();
	}

	private void storeHisab() {
		File file=Utils.appFolder();
		file=new File(file,"hisab.json");
		JSONArray jsonArray=new JSONArray();
		for (JSONObject obj:array){
			jsonArray.put(obj);
		}
		try {
			FileOutputStream outputStream=new FileOutputStream(file);
			outputStream.write(jsonArray.toString(4).getBytes());
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		loadHisab();
	}

	private void loadHisab(){
		this.array.clear();
		File file=Utils.appFolder();
		file=new File(file,"hisab.json");
		if (!file.exists()) {
			try {
				boolean created=file.createNewFile();
				if (!created)
					Snackbar.make(findViewById(R.id.root_layout),"File cannot be created",BaseTransientBottomBar.LENGTH_SHORT).show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		StringBuilder sb=new StringBuilder();
		try {
			BufferedReader reader= new BufferedReader(new FileReader(file));
			String s;
			while((s=reader.readLine())!=null){
				sb.append(s);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			try {
				JSONArray jsonArray=new JSONArray(sb.toString());
				double credit=0,debit=0;
				for (int i=0;i<jsonArray.length();i++){
					JSONObject object=jsonArray.getJSONObject(i);
					this.array.add(object);
					if (object.getBoolean("type"))
						debit+=Double.parseDouble(object.getString("amount"));
					else
						credit+=Double.parseDouble(object.getString("amount"));
				}
				TextView creditView=findViewById(R.id.total_credit);
				TextView debitView=findViewById(R.id.total_debit);

				creditView.setText(""+credit);
				debitView.setText(""+debit);
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e("hisab parse error",e.getMessage());
			}
		}
		list.update();
	}
}