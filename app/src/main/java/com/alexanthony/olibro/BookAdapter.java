package com.alexanthony.olibro;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BookAdapter extends BaseAdapter {
	
	//book list and layout
	private ArrayList<Book> books;
	private LayoutInflater bookInf;
	
	//constructor
	public BookAdapter(Context c, ArrayList<Book> theBooks){
		books = theBooks;
		bookInf=LayoutInflater.from(c);
	}

	@Override
	public int getCount() {
		return books.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//map to book layout
		LinearLayout bookLay = (LinearLayout)bookInf.inflate
				(R.layout.book, parent, false);
		//get title and author views
		TextView songView = (TextView)bookLay.findViewById(R.id.book_title);
		TextView authorView = (TextView)bookLay.findViewById(R.id.book_author);
		//get book using position
		Book currBook = books.get(position);
		//get title and author strings
		songView.setText(currBook.getTitle());
		authorView.setText(currBook.getAuthor());
		//set position as tag
		bookLay.setTag(position);
		return bookLay;
	}

}
