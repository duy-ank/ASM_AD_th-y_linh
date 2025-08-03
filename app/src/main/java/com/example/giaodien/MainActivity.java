package com.example.giaodien;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.giaodien.database.DatabaseHelper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * MainActivity là màn hình chính của ứng dụng.
 * Nó hiển thị các tab, biểu đồ tổng quan và biểu đồ chi tiêu hàng ngày.
 */
public class MainActivity extends AppCompatActivity {
    // Khai báo các thành phần UI
    private Button btnAddTransaction;
    private Button btnHistory;
    private ViewPager2 viewPager;
    private PieChart pieChart;
    private LineChart dailySpendingChart;

    // Khai báo các đối tượng cần thiết
    private DatabaseHelper dbHelper;
    private SharedPreferences preferences;
    private long currentUserId = -1; // ID của người dùng hiện tại, mặc định là -1

    /**
     * ActivityResultLauncher để xử lý kết quả trả về từ AddExpenseActivity.
     * Khi một giao dịch mới được thêm thành công, nó sẽ cập nhật các biểu đồ.
     */
    private final ActivityResultLauncher<Intent> expenseLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Cập nhật biểu đồ khi có chi tiêu mới được thêm
                    updatePieChart();
                    setupDailySpendingChart();
                    Snackbar.make(findViewById(android.R.id.content),
                            "Đã thêm chi tiêu mới", Snackbar.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Khởi tạo DatabaseHelper và SharedPreferences
        dbHelper = new DatabaseHelper(this);
        preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Thiết lập các thành phần giao diện và logic ban đầu
        setupWindowInsets();
        initializeViews();
        setupViewPager();
        getCurrentUser(); // Lấy ID người dùng hiện tại
        setupPieChart();
        setupDailySpendingChart();
        setupButtonListeners();
    }

    /**
     * Lấy ID người dùng hiện tại từ SharedPreferences và Database.
     * ID này là cần thiết để lọc dữ liệu theo người dùng.
     */
    private void getCurrentUser() {
        String userEmail = preferences.getString("email", "");
        if (!userEmail.isEmpty()) {
            Cursor cursor = dbHelper.getUserInfoByEmail(userEmail);
            if (cursor != null && cursor.moveToFirst()) {
                currentUserId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID));
                cursor.close();
            }
        }
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Ánh xạ các thành phần UI từ layout XML.
     */
    private void initializeViews() {
        btnAddTransaction = findViewById(R.id.btnAddTransaction);
        btnHistory = findViewById(R.id.btnHistory);
        viewPager = findViewById(R.id.viewPager);
        pieChart = findViewById(R.id.pieChart);
        dailySpendingChart = findViewById(R.id.dailySpendingChart);

        TextView tvDate = findViewById(R.id.tvDate);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
        String currentDate = sdf.format(new Date());
        tvDate.setText(currentDate);

        TextView tvUserAvatar = findViewById(R.id.tvUserAvatar);
        tvUserAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Thiết lập các sự kiện click cho các nút.
     */
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

    /**
     * Thiết lập ViewPager2 và TabLayout cho các tab.
     */
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

    /**
     * Cập nhật biểu đồ tròn (Pie Chart).
     * TODO: Lấy dữ liệu thực từ database thay vì dùng dữ liệu giả định.
     */
    private void updatePieChart() {
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

    /**
     * Thiết lập biểu đồ đường để hiển thị chi tiêu hàng ngày trong tháng.
     */
    private void setupDailySpendingChart() {
        if (dailySpendingChart == null) {
            return;
        }

        // Lấy tất cả chi tiêu của tất cả người dùng
        // TODO: Cần dùng dbHelper.getExpensesByUser(currentUserId) nếu muốn lọc theo người dùng
        List<Expense> allExpenses = dbHelper.getAllExpenses();
        Map<Integer, Double> dailyExpenses = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Tổng hợp chi tiêu theo ngày từ danh sách lấy được
        for (Expense expense : allExpenses) {
            calendar.setTimeInMillis(expense.getTimestamp() * 1000L);
            int expenseMonth = calendar.get(Calendar.MONTH);
            int expenseYear = calendar.get(Calendar.YEAR);

            if (expenseMonth == currentMonth && expenseYear == currentYear) {
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                double currentTotal = dailyExpenses.getOrDefault(dayOfMonth, 0.0);
                dailyExpenses.put(dayOfMonth, currentTotal + expense.getAmount());
            }
        }

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 1; i <= daysInMonth; i++) {
            Double totalAmount = dailyExpenses.getOrDefault(i, 0.0);
            entries.add(new Entry(i, totalAmount.floatValue()));
            labels.add(String.valueOf(i));
        }

        // Kiểm tra nếu không có dữ liệu
        if (entries.isEmpty()) {
            dailySpendingChart.clear();
            dailySpendingChart.setNoDataText("Không có dữ liệu chi tiêu trong tháng này.");
            dailySpendingChart.invalidate();
            return;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Chi tiêu hàng ngày");
        dataSet.setColor(Color.parseColor("#F44336"));
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.parseColor("#F44336"));
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        dailySpendingChart.setData(lineData);
        dailySpendingChart.getDescription().setEnabled(false);
        dailySpendingChart.setDrawGridBackground(false);

        XAxis xAxis = dailySpendingChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(6, true);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setAxisMaximum(daysInMonth);

        YAxis leftAxis = dailySpendingChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularity(1f);
        leftAxis.setAxisMinimum(0f);

        dailySpendingChart.getAxisRight().setEnabled(false);

        dailySpendingChart.invalidate();
    }

    /**
     * Adapter cho ViewPager2, quản lý các Fragment của các tab.
     */
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