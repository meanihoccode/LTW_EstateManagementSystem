// ===== DARK MODE - SHARED ACROSS ALL PAGES =====
function initGlobalDarkMode() {
    const isDarkMode = localStorage.getItem('darkMode') === 'true';

    // Áp dụng dark mode nếu đã bật trước đó
    if (isDarkMode) {
        document.body.classList.add('dark-mode');
        updateDarkModeButtonsText('☀️');
    }
}

function updateDarkModeButtonsText(text) {
    const buttons = document.querySelectorAll('.btn-dark-mode');
    buttons.forEach(btn => {
        btn.textContent = text;
    });
}

function setupDarkModeToggle() {
    const darkModeButtons = document.querySelectorAll('.btn-dark-mode');

    darkModeButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            document.body.classList.toggle('dark-mode');
            const isNowDark = document.body.classList.contains('dark-mode');
            localStorage.setItem('darkMode', isNowDark);
            updateDarkModeButtonsText(isNowDark ? '☀️' : '🌙');
        });
    });
}

// Khởi tạo dark mode khi trang tải
document.addEventListener('DOMContentLoaded', function() {
    initGlobalDarkMode();
    setupDarkModeToggle();
});

