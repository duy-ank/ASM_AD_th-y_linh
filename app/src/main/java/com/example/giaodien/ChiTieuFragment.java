package com.example.giaodien;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.giaodien.adapter.ExpenseAdapter;
import com.example.giaodien.database.DatabaseHelper;

import java.util.List;

public class ChiTieuFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private DatabaseHelper dbHelper;

    public ChiTieuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout fragment_chi_tieu.xml
        return inflater.inflate(R.layout.fragment_chi_tieu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewExpenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new DatabaseHelper(getContext());

        // Lấy danh sách chi tiêu từ database
        List<Expense> expenseList = dbHelper.getAllExpenses();

        // Gắn adapter
        adapter = new ExpenseAdapter(expenseList);
        recyclerView.setAdapter(adapter);
    }
}
