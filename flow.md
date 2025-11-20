┌─────────────────────────────────────────────────────────┐
│                    USER (Khách hàng)                     │
└───────────────────┬─────────────────────────────────────┘
                    │
        ┌───────────┴───────────┐
        │    API GATEWAY        │ ← Cổng vào duy nhất
        └───────────┬───────────┘
                    │
    ┌───────────────┼───────────────┐
    │               │               │
┌───▼────┐    ┌────▼─────┐   ┌────▼─────┐
│IDENTITY│    │ CATALOG  │   │  ORDER   │
│(Đăng   │    │(Tàu,ga,  │   │ (Đặt vé) │
│ nhập)  │    │ lịch)    │   │          │
└────────┘    └──────────┘   └────┬─────┘
                                   │
                    ┌──────────────┼──────────────┐
                    │              │              │
              ┌─────▼────┐   ┌────▼─────┐  ┌────▼─────┐
              │ PAYMENT  │   │  PROMO   │  │  NOTIFY  │
              │(Thanh    │   │(Giảm giá)│  │ (Email)  │
              │ toán)    │   │          │  │          │
              └──────────┘   └──────────┘  └──────────┘
              
## 🔄 LUỒNG HOẠT ĐỘNG HOÀN CHỈNH

### **Kịch bản: User đặt vé tàu SE1**
```
BƯỚC 1: Đăng nhập
User → Identity Service
├─ POST /api/auth/login
├─ username: "nguyenvana"
└─ password: "123456"
→ Trả về JWT token

BƯỚC 2: Tìm chuyến tàu
User → Catalog Service
├─ GET /api/trips?from=HN&to=SG&date=2025-11-20
└─ Trả về danh sách chuyến tàu

BƯỚC 3: Chọn chỗ
User → Catalog Service
├─ GET /api/trips/1/seats
└─ Trả về sơ đồ ghế (ghế nào còn trống)

BƯỚC 4: Đặt vé
User → Order Service
├─ POST /api/bookings
├─ trip_id: 1
├─ seat_id: 150
├─ passenger_name: "Nguyen Van An"
└─ passenger_id: "001234567890"
→ Tạo booking, status = "pending_payment"
→ GỬI EVENT: "BookingCreated"

BƯỚC 5: Áp mã giảm giá (optional)

├─ POST /api/promotions/apply
├─ booking_id: 1
└─ promo_code: "GIAM50K"
→ Giảm giá 50k

BƯỚC 6: Thanh toán
User → Payment Service
├─ POST /api/payments
├─ booking_id: 1
├─ amount: 450000
└─ method: "vnpay"
→ Chuyển hướng sang VNPay
→ User thanh toán xong
→ VNPay callback về Payment Service
→ Cập nhật payment status = "paid"
→ GỬI EVENT: "PaymentSuccess"

BƯỚC 7: Order Service nhận event
Payment Service → Order Service (qua Event Bus)
├─ Event: "PaymentSuccess"
└─ Order Service cập nhật booking status = "paid"
→ GỬI EVENT: "BookingConfirmed"

BƯỚC 8: Ticket Service nhận event
Order Service → Ticket Service
├─ Event: "BookingConfirmed"
└─ Ticket Service tạo vé điện tử
   ├─ Generate QR code
   └─ GỬI EVENT: "TicketIssued"

BƯỚC 9: Notify Service nhận event
Ticket Service → Notify Service
├─ Event: "TicketIssued"
└─ Gửi email kèm vé PDF cho user              