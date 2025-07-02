// 📁 src/main/resources/static/js/auth.js (یا هر مسیر دیگری که دوست دارید)

// تابع برای دریافت هدرهای احراز هویت (شامل JWT Token)
function getAuthHeaders() {
    const token = localStorage.getItem('jwtToken');
    if (token) {
        return {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        };
    } else {
        // اگر توکن وجود ندارد، کاربر باید به صفحه لاگین هدایت شود
        console.error("JWT token not found. Redirecting to login.");
        window.location.href = '/login.html?error=session_expired'; // هدایت به صفحه لاگین با پیام خطا
        return {}; // برگرداندن شیء خالی برای جلوگیری از خطا در فراخوانی fetch
    }
}

// تابع برای مدیریت پاسخ‌های API (بررسی 401/403 و هدایت به لاگین)
function handleApiResponse(response) {
    if (response.status === 401 || response.status === 403) {
        console.error("Unauthorized or Forbidden access. Redirecting to login.");
        localStorage.removeItem('jwtToken'); // حذف توکن نامعتبر
        window.location.href = '/login.html?error=unauthorized'; // هدایت به صفحه لاگین با پیام خطا
        throw new Error("Unauthorized or Forbidden"); // پرتاب خطا برای جلوگیری از ادامه پردازش
    }
    return response; // پاسخ را برای پردازش بعدی برگردانید
}

// تابع برای انجام لاگ اوت
function logout() {
    localStorage.removeItem('jwtToken'); // حذف توکن
    // اگر اندپوینت لاگ اوت در بک‌اند دارید (مثل /perform_logout)
    fetch('/perform_logout', { method: 'POST' }) // Spring Security این را مدیریت می‌کند
        .finally(() => {
            window.location.href = '/login.html?logout=true'; // هدایت به صفحه لاگین
        });
}