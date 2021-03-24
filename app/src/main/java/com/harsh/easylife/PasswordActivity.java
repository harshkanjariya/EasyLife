package com.harsh.easylife;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;

public class PasswordActivity extends AppCompatActivity {

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

	GridView gridView;
	GridAdapter gridAdapter;

	ArrayList<Pair<String,String>> list = new ArrayList<>();
	JSONArray jsonArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password);

		gridAdapter = new GridAdapter(this,list);
		gridView = findViewById(R.id.grid_view);
		gridView.setAdapter(gridAdapter);
		loadInfo();
	}
	private void loadInfo(){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(getFilesDir()+"/password_groups.json"));
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
				list.add(new Pair<>(obj.getString("title"),obj.getString("id")));
			}
			gridAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	ImageView img;
	Uri selectedUri;
	private void showAddDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Add Group");
		ConstraintLayout layout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.layout_add_password_group,null,false);
		img = layout.findViewById(R.id.img);
		img.setOnClickListener(v -> {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent, "Select Picture"), 4);
		});

		builder.setView(layout);
		builder.setPositiveButton("Save", (dialog, which) -> {
			TextView txt = layout.findViewById(R.id.title);

			BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
			long id = System.currentTimeMillis();
			File file = new File(getFilesDir()+"/password_groups/");
			if (!file.exists())
				file.mkdir();
			file = new File(file,id+".jpg");
			try {
				drawable.getBitmap().compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(file));

				JSONObject obj=new JSONObject();
				obj.put("title",txt.getText().toString());
				obj.put("id",id+"");
				if (jsonArray==null)
					jsonArray=new JSONArray();
				jsonArray.put(obj);
				saveGroups();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		builder.show();
	}

	private void saveGroups() {
		File file = new File(getFilesDir()+"/password_groups.json");
		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(file);
			outputStream.write(jsonArray.toString(4).getBytes());
			outputStream.close();
			loadInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==4 && resultCode==RESULT_OK && data!=null){
			selectedUri = data.getData();
			if (img!=null)
				Glide.with(this)
						.load(selectedUri)
						.into(img);
		}
	}

	static class GridAdapter extends BaseAdapter {

		ArrayList<Pair<String, String>> list;
		LayoutInflater inflater;
		Context context;

		public GridAdapter(Context context,ArrayList<Pair<String, String>> list) {
			this.context = context;
			inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
			this.list = list;
		}
		@Override
		public int getCount() { return list.size(); }
		@Override
		public Object getItem(int position) { return list.get(position); }
		@Override
		public long getItemId(int position) { return position; }
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = inflater.inflate(R.layout.layout_password_grid,parent,false);

			Pair<String,String> pair = list.get(position);

			ImageView img = convertView.findViewById(R.id.img);
			assert pair.second != null;
			Glide.with(parent)
					.load(new File(context.getFilesDir()+"/password_groups/"+pair.second+".jpg"))
					.into(img);

			TextView txt = convertView.findViewById(R.id.txt);
			txt.setText(pair.first);

			convertView.setOnClickListener(v -> {
				Intent intent = new Intent(context.getApplicationContext(),PasswordListActivity.class);
				intent.putExtra("id",pair.second);
				intent.putExtra("title",pair.first);
				context.startActivity(intent);
			});

			return convertView;
		}
	}
}