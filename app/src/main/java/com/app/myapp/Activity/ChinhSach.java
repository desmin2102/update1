package com.app.myapp.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.myapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChinhSach extends AppCompatActivity {
private TextView chinhSach;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chinh_sach);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Nút quay lại
        ImageView imBack = findViewById(R.id.imBack);
        imBack.setOnClickListener(v -> finish());

        chinhSach=findViewById(R.id.chinhSach);
        String value="Điều 1: Mục đích\n" +
                "Mục đích của các điều khoản (\"điều khoản\") để quy định các quyền và nghĩa vụ của các thành viên và các\n" +
                "thủ tục để các thành viên được sử dụng chương trình khách hàng thường xuyên do LOTTE Cinema cung cấp.\n" +
                "Điều 2: Định Nghĩa.\n" +
                "Thành viên có thẻ thành viên do HGB Cinema cung cấp (gọi tắt là Thẻ LPoint) nghĩa là các thành viên chấp thuận điều khoản này, đã đề nghị Công ty cấp thẻ thành viên, và đã được cấp thẻ thành viên, và cũng được gọi chung là thành viên.\n" +
                "Chương trình khách hàng thường xuyên HGB\n" +
                "Cinema nghĩa là các chương trình chăm sóc khách hàng thường xuyên do HGB Cinema cấp cho thành viên, như quy định tại điều 3, và cũng được gọi chung là chương trình.\n" +
                "Điểm tích luỹ nghĩa là điểm được cấp cho thành viên theo tỷ lệ tích luỹ do HGB Cinema thông báo khi thành viên mua các sản phẩm hoặc dịch vụ từ HGB Cinema.\n" +
                "Điểm sử dụng là điểm khách hàng đã sử dụng khi mua các sản phẩm dịch vụ hoặc đổi lấy quà tặng miễn phí tại HGB Cinema\n" +
                "Điểm hiện hữu nghĩa là điểm tích luỹ mà đáp ứng các tiêu chuẩn do công ty đặt ra, sau khi đã trừ những điểm mà thành viên đã sử dụng, và có sẵn để cho các thành viên sử dụng khi mua các sản phẩm dịch vụ " +
                "từ HGB Cinema hoặc đổi lấy quà tặng miễn phí, v.v..\n"+ "Điều 3: Chương Trình.\n" +
                "HGB Cinema cung cấp cho các thành viên một hoặc nhiều hình thức chương trình sau:\n" +
                "Tích luỹ và sử dụng điểm: Thành viên có thể sử dụng điểm được tích luỹ thông qua việc mua các sản phẩm và dịch vụ từ HGB Cinema theo quy định mà HGB Cinema đặt ra.\n" +
                "Chương trình áp dụng ưu đãi: Các thành viên có thể sử dụng các chương trình ưu đãi như là giảm giá hay được hưởng miễn phí do HGB Cinema đưa ra.\n" +
                "Các chương trình khác: Công ty có thể phát triển thêm những chương trình để phục vụ thành viên.\n" +
                "Điều 4: Tư cách thành viên và Cấp thẻ thành viên.\n" +
                "Bất cứ khách hàng nào mong muốn trở thành thành viên có thể nộp đơn xin gia nhập thành viên bằng cách điền các thông tin bắt buộc trong mẫu đơn xin gia nhập do HGB Cinema quy định và đã đồng ý các điều khoản này và chấp thuận về thu thập, sử dụng và tiết lộ thông tin cá nhân.\n" +
                "Không thành viên nào được xin phép chuyển nhượng hay cho mượn tư cách thành viên của họ cho người thứ ba và cũng không được sử dụng để làm vật bảo đảm.";

        chinhSach.setText(value);
    }

}