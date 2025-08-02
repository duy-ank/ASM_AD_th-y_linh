package com.example.giaodien;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button btnAddTransaction;
    private Button btnHistory;
    private ViewPager2 viewPager;
    private PieChart pieChart;

    // ActivityResultLauncher để xử lý kết quả trả về từ AddExpenseActivity
    private final ActivityResultLauncher<Intent> expenseLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Cập nhật biểu đồ khi có chi tiêu mới được thêm
                    updatePieChart();
                    Snackbar.make(findViewById(android.R.id.content),
                            "Đã thêm chi tiêu mới", Snackbar.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setupWindowInsets();
        initializeViews();
        setupViewPager();
        setupPieChart();
        setupButtonListeners();
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeViews() {
        btnAddTransaction = findViewById(R.id.btnAddTransaction);
        btnHistory = findViewById(R.id.btnHistory);
        viewPager = findViewById(R.id.viewPager);
        pieChart = findViewById(R.id.pieChart);
    }

    private void setupButtonListeners() {
        // Xử lý sự kiện thêm giao dịch
        btnAddTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            expenseLauncher.launch(intent);
        });

        // Xử lý sự kiện xem lịch sử
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ExpenseHistoryActivity.class);
            startActivity(intent);
        });
    }

    private void setupViewPager() {
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    String[] tabTitles = {"Chi tiêu", "Thu nhập", "Mượn nợ", "Tài sản"};
                    tab.setText(tabTitles[position]);
                }).attach();
    }

    private void setupPieChart() {
        if (pieChart != null) {
            updatePieChart();
        }
    }

    private void updatePieChart() {
        // TODO: Thay thế bằng dữ liệu thực từ database
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(30f, "Chi tiêu"));
        entries.add(new PieEntry(40f, "Thu nhập"));
        entries.add(new PieEntry(30f, "Mượn nợ"));

        PieDataSet dataSet = new PieDataSet(entries, "Phân bổ tài chính");
        dataSet.setColors(new int[]{0xFF00B89C, 0xFFFFC107, 0xFF666666});
        dataSet.setValueTextColor(0xFF333333);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Tổng quan");
        pieChart.setCenterTextSize(16f);
        pieChart.setHoleRadius(40f);
        pieChart.setEntryLabelColor(0xFF333333);
        pieChart.invalidate();
    }

    // ViewPager Adapter
    public static class ViewPagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
        private static final int NUM_TABS = 4;
        private final AppCompatActivity activity;

        public ViewPagerAdapter(AppCompatActivity activity) {
            super(activity);
            this.activity = activity;
        }

        @Override
        public androidx.fragment.app.Fragment createFragment(int position) {
            switch (position) {
                case 0: return new ChiTieuFragment();
                case 1: return new ThuNhapFragment();
                case 2: return new MuonNoFragment();
                case 3: return new TaiSanFragment();
                default: return null;
            }
        }

        @Override
        public int getItemCount() {
            return NUM_TABS;
        }
    }
}