# Scan Pay - QR Payment System (Android)

A native Android mobile application that revolutionizes payment processing by converting M-Pesa Till Numbers and Paybill numbers into scannable EMVCo-standard QR codes.

## Problem Solved

- **Manual Payment Friction**: Eliminate tedious manual entry of Till/Paybill numbers
- **Payment Verification Gaps**: Provide secure, instant payment confirmation and receipt management
- **Merchant Analytics**: Enable real-time transaction tracking and business insights
- **Integration Barriers**: Bridge modern QR technology with existing M-Pesa infrastructure

## Key Features

### For Customers
- **Quick QR Scanning**: Instant payment initiation via QR code scan
- **Secure Transactions**: JWT-authenticated payments with M-Pesa STK push
- **Transaction History**: Complete payment history with receipts
- **Multi-role Support**: Register as both customer and merchant within single app

### For Merchants
- **EMVCo Compliant QR Generation**: Create static and dynamic QR codes
- **Payment Dashboard**: Real-time transaction tracking and analytics
- **Business Management**: Store management, transaction categorization
- **Scan Analytics**: Complete audit trail of QR scans and payment attempts

## Tech Stack

### Mobile (This Repository)
- **Language**: Java
- **Framework**: Android (API Level 28+, Target 35)
- **Architecture**: MVVM with Repository pattern
- **Networking**: Retrofit 2 + OkHttp3
- **Authentication**: JWT tokens with SessionManager
- **Database**: Room (local caching)
- **Testing**: Espresso, JUnit4

### Backend Integration (Separate Repository)
- **Framework**: Flask (Python)
- **Payment Gateway**: Daraja API (M-Pesa)
- **Database**: PostgreSQL
- **Auth**: JWT-based role access control (Customer/Vendor)

## Getting Started

### Prerequisites
- Android Studio (latest)
- JDK 11+
- Android SDK 35+
- Emulator or physical device (API 28+)

### Build & Run

```bash
# Clone the repository
git clone https://github.com/yourusername/QRPaymentApplication.git
cd QRPaymentApplication

# Build the debug APK
./gradlew assembleDebug

# Run on emulator/device
./gradlew installDebug
```

### Configuration

Update backend API endpoints in `build.gradle.kts` or through runtime configuration:
- API Base URL (must point to running backend instance)
- JWT token expiration settings
- Payment timeout values

## Project Structure

```
app/
├── src/main/java/com/example/qr_payment_application/
│   ├── api/                    # Retrofit service definitions
│   ├── data/
│   │   ├── model/             # Database entities & DTOs
│   │   ├── repository/        # Data access layer
│   │   └── response/          # API response mappers
│   ├── ui/
│   │   ├── auth/              # Login, register, forgot password
│   │   ├── dashboard/         # Role-based dashboards (Customer/Vendor)
│   │   ├── qr/                # QR scanning & generation
│   │   └── payment/           # Payment flow screens
│   ├── utils/
│   │   ├── SessionManager.java  # Auth state & role handling
│   │   └── AuthInterceptor.java # JWT header injection
│   └── viewmodel/             # MVVM ViewModels
├── src/test/java/            # Unit tests
└── src/androidTest/java/      # Instrumentation tests
```

## API Endpoints Used

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/auth/register` | User/vendor registration |
| POST | `/api/auth/login` | Authentication |
| POST | `/api/auth/forgot-password` | Request password reset token |
| POST | `/api/auth/reset-password` | Reset password with token |
| POST | `/api/qr/generate` | Create QR code |
| POST | `/api/qr/scan` | Validate QR and initiate payment |
| POST | `/api/payment/initiate` | Start M-Pesa STK push |
| POST | `/api/payment/stk_callback` | Receive payment confirmation (backend→Daraja→backend) |
| GET | `/api/payment/{id}/status` | Check transaction status |

## Payment Flow

1. **Customer scans QR** → App sends scan to backend
2. **Backend validates QR** → Retrieves merchant details
3. **Customer enters amount** (for static QR) → Confirms payment
4. **Backend initiates STK push** → M-Pesa prompt on customer phone
5. **Customer enters PIN** → M-Pesa processes payment
6. **Daraja sends callback** → Backend marks transaction complete
7. **App polls status** → Shows payment confirmation to user

## Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumentation Tests
```bash
./gradlew connectedAndroidTest
```

### Manual Testing
- Use physical device for M-Pesa STK push testing (sandbox environment)
- Test both customer and merchant roles within same app instance
- Session switching: logout and re-login to test role switching

## Important Notes

- **Backend Required**: This app requires a running backend instance. See backend repository for setup.
- **M-Pesa Sandbox**: Default configuration uses Daraja sandbox. Update credentials in backend for production.
- **Session Management**: Role (customer/vendor) is cached locally. Clear app data to reset.
- **Payment Polling**: After initiating payment, app polls `/api/payment/{id}/status` to check completion.
- **Security**: Never commit `.env` files or credentials. Use environment-based configuration.

## Development Notes

### Role-Based Dashboard Routing
- Login response includes `user_type` (customer/user/merchant/vendor)
- App normalizes aliases: `user` → `customer`, `vendor` → `merchant`
- Dashboard navigation determined by SessionManager role classification

### Callback Route
- Canonical M-Pesa callback endpoint: `POST /api/payment/stk_callback`
- Backend validates callback and updates transaction status
- App polls transaction status endpoint to refresh UI

## Contributing

1. Create a feature branch
2. Commit changes with clear messages
3. Push to GitHub
4. Open a Pull Request with description

## License

[Your License Here]

## Contact

For questions or issues:
- Open an issue on GitHub
- See backend repository for server-side implementation: [Link to backend repo]
