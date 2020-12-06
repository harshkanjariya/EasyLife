package com.harsh.hkutils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class HisabActivity extends AppCompatActivity {
	RecyclerView recyclerView;
	HisabAdapter adapter;
	JSONArray array=new JSONArray();

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

		recyclerView=findViewById(R.id.recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		adapter=new HisabAdapter();
		recyclerView.setAdapter(adapter);

		DividerItemDecoration itemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
		recyclerView.addItemDecoration(itemDecoration);

		loadHisab();
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

						array.put(index,object);
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

						array.put(object);
						storeHisab();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				})
				.setNegativeButton("Cancel",null);
		builder.show();
	}

	private void storeHisab() {
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/HKUtils/");
		file=new File(file,"hisab.json");
		try {
			FileOutputStream outputStream=new FileOutputStream(file);
			outputStream.write(array.toString(4).getBytes());
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		loadHisab();
	}

	private void loadHisab(){
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/HKUtils/");
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
				this.array=new JSONArray(sb.toString());
				double credit=0,debit=0;
				for (int i=0;i<array.length();i++){
					JSONObject object=array.getJSONObject(i);
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
		adapter.notifyDataSetChanged();
	}
	class HisabAdapter extends RecyclerView.Adapter<HisabAdapter.HisabViewHolder> {
		LayoutInflater inflater;
		public HisabAdapter() {
			inflater=getLayoutInflater();
		}
		@NonNull
		@Override
		public HisabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			View view=inflater.inflate(R.layout.item_hisab_list,parent,false);
			return new HisabViewHolder(view);
		}
		@Override
		public void onBindViewHolder(@NonNull HisabViewHolder holder, int position) {
			try {
				JSONObject object=array.getJSONObject(position);
				holder.name.setText(object.getString("name"));
				holder.amount.setText(object.getString("amount"));
				holder.description.setText(object.getString("description"));
				if (object.getBoolean("type")){
					holder.type.setText("Debit");
					holder.type.setTextColor(Color.parseColor("#f00000"));
				}else{
					holder.type.setText("Credit");
					holder.type.setTextColor(Color.parseColor("#00a000"));
				}
				holder.layout.setOnLongClickListener(view -> {
					try {
						showEditDialog(position,
								object.getString("name"),
								object.getString("amount"),
								object.getString("description"),
								object.getBoolean("type"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					return true;
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		@Override
		public int getItemCount() { return array.length(); }

		class HisabViewHolder extends RecyclerView.ViewHolder {
			TextView name;
			TextView amount;
			TextView description;
			TextView type;
			ConstraintLayout layout;
			public HisabViewHolder(@NonNull View itemView) {
				super(itemView);
				layout=itemView.findViewById(R.id.item_layout);

				name=itemView.findViewById(R.id.name);
				amount=itemView.findViewById(R.id.amount);
				type=itemView.findViewById(R.id.type);
				description=itemView.findViewById(R.id.description);
			}
		}
	}
}