package com.example.web.util;

import com.example.web.event.PaymentSuccessEvent;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PaymentEmailTemplate {

    public static String buildPaymentSuccessHtml(PaymentSuccessEvent event) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedTotal = currencyFormat.format(event.totalAmount());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String paymentTime = event.paymentTime().format(formatter);

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f6; color: #333; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
                    .header { text-align: center; border-bottom: 2px solid #4CAF50; padding-bottom: 20px; margin-bottom: 20px; }
                    .header h1 { color: #4CAF50; margin: 0; }
                    .content { line-height: 1.6; }
                    .order-details { background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin: 20px 0; }
                    .order-details table { width: 100%%; }
                    .order-details td { padding: 8px 0; }
                    .order-details td:first-child { font-weight: bold; width: 40%%; color: #555; }
                    .footer { text-align: center; margin-top: 30px; font-size: 0.9em; color: #777; border-top: 1px solid #eee; padding-top: 20px; }
                    .btn { display: inline-block; padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Thanh Toán Thành Công</h1>
                    </div>
                    <div class="content">
                        <p>Xin chào <strong>%s</strong>,</p>
                        <p>Cảm ơn bạn đã mua sắm tại Tech Devices Shop. Chúng tôi đã nhận được thanh toán cho đơn hàng của bạn.</p>
                        
                        <div class="order-details">
                            <table>
                                <tr><td>Mã Đơn Hàng:</td><td>#%s</td></tr>
                                <tr><td>Thời Gian Thanh Toán:</td><td>%s</td></tr>
                                <tr><td>Phương Thức Thanh Toán:</td><td>%s</td></tr>
                                <tr><td>Tổng Tiền:</td><td style="color: #E53935; font-weight: bold; font-size: 1.1em;">%s</td></tr>
                            </table>
                        </div>
                        
                        <p>Đơn hàng của bạn đang được xử lý và sẽ sớm được giao đến bạn. Bạn có thể kiểm tra trạng thái đơn hàng trên website của chúng tôi.</p>
                        
                        <div style="text-align: center;">
                            <a href="#" class="btn">Xem Đơn Hàng</a>
                        </div>
                    </div>
                    <div class="footer">
                        <p>&copy; 2026 Tech Devices Shop. All rights reserved.</p>
                        <p>Đây là email tự động, vui lòng không trả lời.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                event.customerName() != null ? event.customerName() : event.email(),
                event.orderCode() != null ? event.orderCode() : String.valueOf(event.orderId()),
                paymentTime,
                event.paymentMethod(),
                formattedTotal
            );
    }
}
