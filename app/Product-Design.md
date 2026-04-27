# Scan Pay - QR Payment System
## Product Design Document

---

## Project Overview

### What is Scan Pay?
Scan Pay is a comprehensive fintech mobile application that revolutionizes payment processing by converting traditional Till Numbers and Paybill numbers into scannable QR codes. The system bridges the gap between traditional mobile money services (like M-Pesa) and modern QR-based payment systems.

### Problem Being Solved
**Current Pain Points:**
- Manual entry of Till numbers and Paybill numbers is time-consuming and error-prone
- Customers need to remember or manually input merchant payment details
- Limited payment verification and receipt management
- Poor transaction tracking and analytics for both customers and merchants
- Lack of seamless integration between QR technology and existing mobile money infrastructure

**Target Users:**
- **Customers**: Individuals who make frequent payments to merchants and want a faster, more convenient payment method
- **Merchants**: Businesses (retail shops, restaurants, service providers) who want to streamline payment collection and reduce transaction errors

### Desired End Product
A native Android mobile application that provides:
- **For Customers**: Instant QR code scanning, secure payment processing, transaction history, and receipt management
- **For Merchants**: QR code generation, payment collection, transaction analytics, and business management tools
- **Unified Experience**: Seamless integration with existing M-Pesa infrastructure while providing modern QR-based convenience that is based on the EMVCo standard for QR codes.

---

## Backend Implementation Status

### ✅ Already Implemented Features

#### 1. **User Management System**
- **Customer Registration**: Complete user registration with name, phone, email, password
- **Merchant Registration**: Comprehensive vendor registration including:
    - Business details (name, business_name, business_shortcode)
    - Financial identifiers (merchant_id, MCC codes, PSP details)
    - Location data (store_label, country_code, currency_code)
- **Authentication**: JWT-based authentication system with role-based access control
- **Password Security**: Secure password hashing and validation

#### 2. **QR Code Management System**
- **QR Code Generation**: EMVCo compliant QR code creation
- **QR Types Support**:
    - **Static QR Codes**: Merchant-specific codes where customers enter the amount
    - **Dynamic QR Codes**: Transaction-specific codes with pre-embedded amounts
- **QR Status Management**: Active, inactive, and expired status tracking
- **Payload Management**: Both encoded string and JSON payload storage

#### 3. **Payment Processing Engine**
- **M-Pesa Integration**: Complete Daraja API integration for STK push payments
- **Transaction Management**: Comprehensive transaction tracking with multiple statuses
- **Payment Sessions**: Session-based payment handling with expiration management
- **Transaction Types**: Support for incoming/outgoing transactions with detailed categorization
- **Callback Handling**: Automated processing of payment callbacks from M-Pesa

-**Later looking forward to add**: Support for additional payment methods (e.g., card payments, other mobile money providers)
#### 4. **Analytics and Reporting**
- **Transaction Analytics**: Detailed transaction history and analytics for merchants
- **Scan Logging**: Complete audit trail of QR code scans and payment attempts
- **Admin Dashboard**: Administrative oversight and system analytics
- **User Analytics**: Customer transaction patterns and history

#### 5. **API Infrastructure**
- **RESTful APIs**: Complete API suite for all mobile app operations
- **Authentication Endpoints**: `/api/auth/register` and `/api/auth/login` for both user types
- **QR Management**: `/api/qr/generate`, `/api/qr/scan` endpoints
- **Payment Processing**: `/api/payment/initiate`, `/api/payment/callback` endpoints
- **Analytics Endpoints**: User and vendor analytics APIs

---

## Mobile Application Requirements

### Platform Specifications
- **Platform**: Native Android Application
- **Language**: Java
- **Target SDK**: 36
- **Minimum SDK**: 28
- **UI Design**: Following the attached design mockup image

### 📱 Core Mobile App Features

a#### 1. **Authentication & Onboarding**
- **Registration Flow**:
    - User type selection (Customer vs Merchant)
    - Form validation matching backend schema
    - Email verification and phone number validation
- **Login System**:
    - JWT token management
    - Secure credential storage
    - Biometric authentication support

#### 2. **QR Code Scanner Module**
- **Camera Integration**: Real-time QR code scanning using device camera
- **QR Validation**: Client-side QR code format validation
- **Payload Parsing**: Decode EMVCo compliant QR payloads
- **Scan History**: Local storage of scanned QR codes
- **Offline Validation**: Basic QR format checks before API calls

#### 3. **Payment Processing Interface**
- **Amount Entry**: Dynamic amount input for static QR codes
- **Payment Confirmation**: Clear payment details verification screen
- **STK Push Trigger**: Integration with backend payment initiation
- **Status Tracking**: Real-time payment status monitoring
- **Receipt Generation**: Digital receipt creation and storage

#### 4. **Merchant-Specific Features**
- **QR Code Generation**:
    - Static QR creation for general use
    - Dynamic QR generation for specific amounts
    - QR code sharing and printing options
- **Transaction Dashboard**:
    - Real-time transaction monitoring
    - Daily/weekly/monthly analytics
    - Revenue tracking and insights
- **Business Profile Management**:
    - Merchant information updates
    - Business category management
    - Store location settings

#### 5. **Customer-Specific Features**
- **Transaction History**: Complete payment history with search and filters
- **Favorite Merchants**: Save frequently used merchant QR codes
- **Payment Methods**: Manage linked M-Pesa accounts
- **Spending Analytics**: Personal spending insights and categories

---

## Technical Architecture

### 📐 Application Architecture Pattern
**Recommended**: Model-View-Presenter (MVP) Pattern
- **Model**: Data layer handling API calls and local storage
- **View**: UI components and user interactions
- **Presenter**: Business logic and data binding

### 🔧 Required Dependencies
```gradle
dependencies {
    // Networking
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.10.0'
    
    // QR Code Scanning
    implementation 'com.journeyapps:zxing-android-embedded:4.3.0'
    implementation 'com.google.zxing:core:3.5.1'
    
    // Local Database
    implementation 'androidx.room:room-runtime:2.4.3'
    annotationProcessor 'androidx.room:room-compiler:2.4.3'
    
    // UI Components
    implementation 'com.google.android.material:material:1.8.0'
    implementation 'androidx.recyclerview:recyclerview:1.3.0'
    implementation 'androidx.cardview:cardview:1.0.0'
    
    // Camera
    implementation 'androidx.camera:camera-camera2:1.2.2'
    implementation 'androidx.camera:camera-lifecycle:1.2.2'
    implementation 'androidx.camera:camera-view:1.2.2'
    
    // Authentication & Security
    implementation 'androidx.biometric:biometric:1.1.0'
    implementation 'androidx.security:security-crypto:1.1.0-alpha06'
}
```

### 🗂️ Project Structure
```
app/
├── src/main/java/com/scanpay/mobile/
│   ├── activities/
│   │   ├── MainActivity.java
│   │   ├── LoginActivity.java
│   │   ├── RegisterActivity.java
│   │   └── PaymentActivity.java
│   ├── fragments/
│   │   ├── ScannerFragment.java
│   │   ├── HistoryFragment.java
│   │   └── ProfileFragment.java
│   ├── models/
│   │   ├── User.java
│   │   ├── Merchant.java
│   │   ├── Transaction.java
│   │   └── QRCode.java
│   ├── network/
│   │   ├── ApiService.java
│   │   ├── ApiClient.java
│   │   └── AuthInterceptor.java
│   ├── database/
│   │   ├── AppDatabase.java
│   │   ├── TransactionDao.java
│   │   └── QRCodeDao.java
│   ├── utils/
│   │   ├── QRCodeUtils.java
│   │   ├── PreferenceManager.java
│   │   └── ValidationUtils.java
│   └── presenters/
│       ├── LoginPresenter.java
│       ├── ScanPresenter.java
│       └── PaymentPresenter.java
```

---

## User Flow & Navigation

### 👤 Customer Journey

#### **Onboarding Flow**
1. **App Launch** → Splash screen with brand identity
2. **First Time Setup** → User type selection (Customer/Merchant)
3. **Registration** → Form with fields: name, phone, email, password
4. **Verification** → Email/SMS verification
5. **Login Success** → Navigate to main dashboard

#### **Payment Flow**
1. **Main Dashboard** → Access scanner, history, profile
2. **QR Scanner** → Camera view with scanning overlay
3. **QR Detection** → Automatic QR code recognition and validation
4. **Payment Details** →
    - For Static QR: Amount entry screen
    - For Dynamic QR: Confirmation screen with pre-filled amount
5. **Payment Confirmation** → Review merchant details, amount, fees
6. **STK Push** → M-Pesa payment prompt on device
7. **Status Monitoring** → Real-time payment status updates
8. **Receipt** → Digital receipt with transaction details
9. **History Update** → Automatic addition to transaction history

#### **Secondary Features**
- **Transaction History**: Searchable list with filters by date, merchant, amount
- **Favorites**: Quick access to frequently used merchants
- **Profile Management**: Update personal information, payment methods
- **Support**: Help center, contact information, FAQ

### 🏪 Merchant Journey

#### **Onboarding Flow**
1. **Registration** → Extended form with business details:
    - Personal: name, email, phone, password
    - Business: business_name, business_shortcode, store_label
    - Financial: merchant_id, MCC code, PSP details
2. **Verification** → Business verification process
3. **Setup Complete** → Access to merchant dashboard

#### **QR Management Flow**
1. **Merchant Dashboard** → Overview of transactions, analytics
2. **QR Generation** →
    - **Static QR**: Generate permanent merchant QR code
    - **Dynamic QR**: Create amount-specific QR codes
3. **QR Display** → Show QR code for customer scanning
4. **QR Sharing** → Export QR code as image, print options

#### **Transaction Management**
1. **Live Monitoring** → Real-time incoming payment notifications
2. **Transaction History** → Detailed transaction records
3. **Analytics Dashboard** →
    - Daily/weekly/monthly revenue charts
    - Customer demographics
    - Peak transaction times
    - Payment method breakdowns
4. **Reporting** → Export transaction reports

#### **Business Management**
- **Profile Settings**: Update business information, operating hours
- **QR Code Management**: Activate/deactivate QR codes
- **Payment Settings**: Configure transaction limits, fees
- **Customer Insights**: Repeat customer analysis

---

## Integration Points

### 🔌 Backend API Integration
- **Base URL**: `http://localhost:5000/api` (Development - localhost)
- **Production URL**: TBD (Considering deployment options)
- **Authentication**: JWT Bearer tokens
- **Content Type**: `application/json`
- **Error Handling**: Standardized error response format
- **M-Pesa Integration**: Already tested and functional on backend

### 📊 Data Synchronization
- **Transaction Sync**: Real-time transaction status updates
- **QR Code Sync**: Merchant QR code status synchronization
- **Analytics Sync**: Periodic analytics data refresh
- **Offline Support**: Local storage for critical transaction data

---

## Security Considerations

### 🔒 Security Implementation
- **Token Management**: Secure JWT storage using Android Keystore
- **API Security**: HTTPS only communication
- **Data Encryption**: Local database encryption for sensitive data
- **Biometric Authentication**: Fingerprint/face recognition for app access (a to be feature added in future)
- **Payment Security**: No storage of payment credentials on device
- **Session Management**: Automatic session expiry and renewal

---

## Performance Requirements

### ⚡ Performance Targets
- **QR Scan Speed**: < 2 seconds from camera open to scan completion
- **Payment Processing**: < 10 seconds from confirmation to STK push
- **API Response Time**: < 3 seconds for all API calls
- **App Launch Time**: < 3 seconds cold start
- **Offline Functionality**: Basic QR scanning and validation without internet
- **Memory Usage**: Optimized for devices with 2GB RAM or less
- **Battery Optimization**: Efficient camera usage and background processing
- **Low-End Device Support**: Smooth performance on Android 9+ devices

---

## Future Enhancements

### 🚀 Potential Features (Phase 2)
- **Multi-language Support**: Swahili, English  and other local languages expansion
- **Enhanced Analytics**: Advanced spending insights and business intelligence
- **Loyalty Programs**: Merchant-specific loyalty point systems
- **Bulk Payments**: Multiple merchant payment processing
- **NFC Support**: Near-field communication payment alternative
- **Invoice Generation**: Digital invoice creation for merchants
- **Offline Mode**: Enhanced offline functionality for areas with poor connectivity
- **Voice Commands**: Voice-activated QR scanning for accessibility
- **Dark/Light Theme Toggle**: User preference-based theme switching
- **Advanced Charts**: Interactive analytics with drill-down capabilities

---

## Clarifications Provided

### ✅ Answered Questions

1. **UI Design Reference**: ✅ **RESOLVED** - Design mockups provided showing:
    - Merchant Dashboard with revenue analytics and transaction charts
    - Customer Scan & Pay interface with personalized spending overview
    - Analytics screen with spending breakdown and merchant insights

2. **Branding Guidelines**: ✅ **RESOLVED** - Green-blue gradient color scheme with dark theme as shown in mockups

3. **M-Pesa Integration**: ✅ **RESOLVED** - Backend M-Pesa integration already tested and functional

4. **Deployment Strategy**: ✅ **RESOLVED** - Google Play Store distribution confirmed as primary target

5. **Testing Environment**: ✅ **RESOLVED** - Localhost development environment with future deployment options being considered

6. **Localization**: ✅ **RESOLVED** - Start with English, support language switching via settings with app restart

7. **Device Support**: ✅ **RESOLVED** - Optimized for diverse Kenya market including low-grade Android devices

### 🔄 Additional Considerations for Development

- **Responsive Design**: Ensure UI scales properly across different screen sizes (5" to 7" displays)
- **Accessibility**: Implement accessibility features for users with disabilities
- **Network Resilience**: Handle poor network conditions common in Kenya
- **Data Usage Optimization**: Minimize data consumption for users on limited data plans
- **Currency Formatting**: Proper KES (Kenyan Shilling) formatting throughout the app
- **Cultural Adaptation**: Consider local payment habits and user behavior patterns

---

*This document will be updated as development progresses and additional requirements are identified.*
