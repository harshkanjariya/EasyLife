package com.harsh.easylife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ContactsActivity extends AppCompatActivity {

	ContactAdapter adapter;
	RecyclerView recyclerView;
	ArrayList<Pair<String,String>> list=new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);

		list = getContacts();

		JSONArray array = getContactJson();
		list=combineListArray(list,array);
		storeContacts(list);

		adapter=new ContactAdapter(list);

		recyclerView = findViewById(R.id.recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(adapter);

		DividerItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
		recyclerView.addItemDecoration(itemDecoration);

		EditText search = findViewById(R.id.search);
		search.setHint("Search in "+list.size()+" contacts");
		search.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
			@Override
			public void afterTextChanged(Editable editable) {
				adapter.filter(search.getText().toString());
			}
		});
	}
	private void storeContacts(ArrayList<Pair<String, String>> list) {
		File file = Utils.appFolder();
		file=new File(file,"contacts.json");
		JSONArray array=new JSONArray();
		for (Pair<String, String> s:list){
			JSONObject object=new JSONObject();
			try {
				object.put("number",s.first);
				object.put("name",s.second);
				array.put(object);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream outputStream=new FileOutputStream(file);
			outputStream.write(array.toString(4).getBytes());
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private ArrayList<Pair<String,String>> combineListArray(ArrayList<Pair<String, String>> list, JSONArray array){
		Map<String,String>map=new HashMap<>();
		for (Pair<String, String> s:list){
			map.put(s.first,s.second);
		}
		for(int i=0;i<array.length();i++){
			JSONObject object;
			try {
				object = array.getJSONObject(i);
				map.put(object.getString("number"),object.getString("name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		list.clear();
		for (Map.Entry<String,String> s:map.entrySet()){
			list.add(new Pair<>(s.getKey(),s.getValue()));
		}
		return list;
	}
	private JSONArray getContactJson(){
		File file=Utils.appFolder();
		file=new File(file,"contacts.json");
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
				return new JSONArray(sb);
			} catch (JSONException e) {
				e.printStackTrace();
				return new JSONArray();
			}
		}else{
			return new JSONArray();
		}
	}
	class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MainViewHolder> {

		ArrayList<Pair<String, String>> originalList;
		ArrayList<Pair<String, String>> list=new ArrayList<>();
		LayoutInflater inflater;

		public ContactAdapter(ArrayList<Pair<String, String>> list){
			this.originalList=list;
			filter(null);
			inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		}
		private void sort(){
			Collections.sort(list, new Comparator<Pair<String, String>>() {
				@Override
				public int compare(Pair<String, String> stringStringPair, Pair<String, String> t1) {
					return stringStringPair.second.compareTo(t1.second);
				}
			});
		}
		public void filter(String s){
			if (s==null || s.isEmpty()){
				list.clear();
				list=new ArrayList<>(originalList);
				sort();
				notifyDataSetChanged();
			}else{
				list.clear();
				for (Pair<String, String> x:originalList){
					if (x.first.toLowerCase().contains(s.toLowerCase()) ||
							x.second.toLowerCase().contains(s.toLowerCase()))
						list.add(x);
				}
				sort();
				notifyDataSetChanged();
			}
		}
		@NonNull
		@Override
		public ContactAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			View view = inflater.inflate(R.layout.list_item_contact,parent,false);
			return new ContactAdapter.MainViewHolder(view);
		}
		@Override
		public void onBindViewHolder(@NonNull ContactAdapter.MainViewHolder holder, int position) {
			holder.text1.setText(list.get(position).second);
			holder.text2.setText(list.get(position).first);
			holder.layout.setOnClickListener(view -> {
				Intent callIntent = new Intent(Intent.ACTION_VIEW);
				callIntent.setData(Uri.parse("tel:"+list.get(position).first));
				startActivity(callIntent);
			});
		}
		@Override
		public int getItemCount() {
			return list.size();
		}
		class MainViewHolder extends RecyclerView.ViewHolder {
			TextView text1,text2;
			LinearLayout layout;
			public MainViewHolder(@NonNull View itemView) {
				super(itemView);
				layout=itemView.findViewById(R.id.layout);
				text1=itemView.findViewById(R.id.text1);
				text2=itemView.findViewById(R.id.text2);
			}
		}
	}
	public ArrayList<Pair<String,String>> getContacts(){
		Cursor cursor=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
		ArrayList<Pair<String,String>>list=new ArrayList<>();
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				number = number.replaceAll("\\s", "");
				if (number.charAt(0) != '+')
					number = "+91" + number;
				Pair<String,String>pair=new Pair<>(number,name);
				boolean found=false;
				for (Pair<String,String> p:list)
					if (p.first.equals(pair.first)){
						found=true;
						break;
					}
				if (!found)
					list.add(pair);
			}
			cursor.close();
		}
		return list;
	}
}