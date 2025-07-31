package com.example.giaodien;

import android.graphics.Color;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment; // Thêm import này
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.github.mikephil.charting.charts.PieChart;
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

        // Xử lý WindowInsets để tránh che khuất giao diện
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo TabLayout và ViewPager2
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        if (tabLayout != null && viewPager != null) {
            // Thiết lập adapter
            ViewPagerAdapter adapter = new ViewPagerAdapter(this);
            viewPager.setAdapter(adapter);

            // Kết nối TabLayout với ViewPager2
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
        } else {
            android.util.Log.e("MainActivity", "tabLayout hoặc viewPager không tồn tại trong layout");
        }

        // Khởi tạo PieChart
        PieChart pieChart = findViewById(R.id.pieChart);
        if (pieChart != null) {
            setupPieChart(pieChart);
        } else {
            android.util.Log.e("MainActivity", "pieChart không tồn tại trong layout");
        }
    }

    // Hàm thiết lập PieChart
    private void setupPieChart(PieChart pieChart) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(30f, "Chi tiêu"));
        entries.add(new PieEntry(40f, "Thu nhập"));
        entries.add(new PieEntry(30f, "Mượn nợ"));

        PieDataSet dataSet = new PieDataSet(entries, "Phân bổ tài chính");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Biểu đồ");
        pieChart.setCenterTextSize(16f);
        pieChart.setHoleRadius(40f);
        pieChart.invalidate();
    }

    // Adapter tùy chỉnh cho ViewPager
    public static class ViewPagerAdapter extends FragmentStateAdapter {
        private static final int SO_LUONG_TAB = 4;

        public ViewPagerAdapter(AppCompatActivity activity) {
            super(activity);
        }

        @Override
        public Fragment createFragment(int position) {
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
            return SO_LUONG_TAB;
        }
    }
}