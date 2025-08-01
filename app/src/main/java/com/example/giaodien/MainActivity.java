package com.example.giaodien;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Xử lý WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Thiết lập TabLayout và ViewPager2
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        if (tabLayout != null && viewPager != null) {
            ViewPagerAdapter adapter = new ViewPagerAdapter(this);
            viewPager.setAdapter(adapter);

            new TabLayoutMediator(tabLayout, viewPager,
                    (tab, position) -> {
                        switch (position) {
                            case 0:
                                tab.setText("Chi tiêu");
                                break;
                            case 1:
                                tab.setText("Thu nhập");
                                break;
                            case 2:
                                tab.setText("Mượn nợ");
                                break;
                            case 3:
                                tab.setText("Tài sản");
                                break;
                        }
                    }).attach();
        }

        // Thiết lập PieChart
        PieChart pieChart = findViewById(R.id.pieChart);
        if (pieChart != null) {
            setupPieChart(pieChart);
        }
    }

    private void setupPieChart(PieChart pieChart) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(30f, "Chi tiêu"));
        entries.add(new PieEntry(40f, "Thu nhập"));
        entries.add(new PieEntry(30f, "Mượn nợ"));

        PieDataSet dataSet = new PieDataSet(entries, "Phân bổ tài chính");
        dataSet.setColors(new int[]{0xFF00B89C, 0xFFFFC107, 0xFF666666}); // Màu phù hợp với giao diện
        dataSet.setValueTextColor(0xFF333333);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Biểu đồ");
        pieChart.setCenterTextSize(16f);
        pieChart.setHoleRadius(40f);
        pieChart.invalidate();
    }

    // Adapter cho ViewPager (giả định các Fragment đã được tạo)
    public static class ViewPagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
        private static final int NUM_TABS = 4;

        public ViewPagerAdapter(AppCompatActivity activity) {
            super(activity);
        }

        @Override
        public androidx.fragment.app.Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new ChiTieuFragment();
                case 1:
                    return new ThuNhapFragment();
                case 2:
                    return new MuonNoFragment();
                case 3:
                    return new TaiSanFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getItemCount() {
            return NUM_TABS;
        }
    }
}