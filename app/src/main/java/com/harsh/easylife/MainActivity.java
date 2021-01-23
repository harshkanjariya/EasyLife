package com.harsh.easylife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.harsh.hkutils.Tuple;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	MainAdapter adapter;
	RecyclerView recyclerView;
	ArrayList<Tuple<Integer,String,View.OnClickListener>> list=new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED ||
//				ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||
//				ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED )
//			ActivityCompat.requestPermissions(this,new String[]{
//					Manifest.permission.READ_CONTACTS,
//					Manifest.permission.READ_EXTERNAL_STORAGE,
//					Manifest.permission.WRITE_EXTERNAL_STORAGE
//			},4);
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/HKUtils/");
		if (!file.exists())
			file.mkdir();

		list.add(new Tuple<>(R.drawable.ic_baseline_local_phone_24,"Contacts",
				view -> startActivity(new Intent(MainActivity.this,ContactsActivity.class))));
		list.add(new Tuple<>(R.drawable.ic_baseline_menu_book_24,"Hisab",
				view -> startActivity(new Intent(MainActivity.this,HisabActivity.class))));
		list.add(new Tuple<>(R.drawable.ic_flask,"Experiment",
				view->startActivity(new Intent(MainActivity.this,ExperimentActivity.class))));

		adapter=new MainAdapter(list);

		recyclerView = findViewById(R.id.recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(adapter);

		DividerItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
		recyclerView.addItemDecoration(itemDecoration);
	}
	class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
		ArrayList<Tuple<Integer, String, View.OnClickListener>> list;
		LayoutInflater inflater;
		public MainAdapter(ArrayList<Tuple<Integer, String, View.OnClickListener>> list){
			this.list=list;
			inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		}
		@NonNull
		@Override
		public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			View view = inflater.inflate(R.layout.list_item_main,parent,false);
			return new MainViewHolder(view);
		}
		@Override
		public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
			holder.text.setText(list.get(position).b);
			Drawable drawable = ContextCompat.getDrawable(MainActivity.this,list.get(position).a);
			holder.text.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
			holder.text.setOnClickListener(list.get(position).c);
		}
		@Override
		public int getItemCount() {
			return list.size();
		}
		class MainViewHolder extends RecyclerView.ViewHolder {
			TextView text;
			public MainViewHolder(@NonNull View itemView) {
				super(itemView);
				text=itemView.findViewById(R.id.text);
			}
		}
	}
}