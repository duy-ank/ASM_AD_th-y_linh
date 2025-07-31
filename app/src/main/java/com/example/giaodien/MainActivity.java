package com.example.giaodien;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo PieChart và dữ liệu
        PieChart pieChart = findViewById(R.id.pieChart);

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(40f, "Ăn uống"));
        entries.add(new PieEntry(30f, "Di chuyển"));
        entries.add(new PieEntry(20f, "Giải trí"));
        entries.add(new PieEntry(10f, "Khác"));

        PieDataSet dataSet = new PieDataSet(entries, "Chi tiêu");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        pieChart.setUsePercentValues(true);
        pieChart.setCenterText("Biểu đồ");
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setHoleRadius(40f);

        Description desc = new Description();
        desc.setText("");
        pieChart.setDescription(desc);

        pieChart.invalidate(); // refresh
    }
}
