// Utility functions for searching without diacritics

/**
 * Loại bỏ dấu tiếng Việt
 * @param {string} text - Text cần xử lý
 * @returns {string} Text không dấu
 */
function removeDiacritics(text) {
    if (!text) return '';

    const diacriticsMap = {
        'á': 'a', 'à': 'a', 'ả': 'a', 'ã': 'a', 'ạ': 'a',
        'ă': 'a', 'ắ': 'a', 'ằ': 'a', 'ẳ': 'a', 'ẵ': 'a', 'ặ': 'a',
        'â': 'a', 'ấ': 'a', 'ầ': 'a', 'ẩ': 'a', 'ẫ': 'a', 'ậ': 'a',

        'é': 'e', 'è': 'e', 'ẻ': 'e', 'ẽ': 'e', 'ẹ': 'e',
        'ê': 'e', 'ế': 'e', 'ề': 'e', 'ể': 'e', 'ễ': 'e', 'ệ': 'e',

        'í': 'i', 'ì': 'i', 'ỉ': 'i', 'ĩ': 'i', 'ị': 'i',

        'ó': 'o', 'ò': 'o', 'ỏ': 'o', 'õ': 'o', 'ọ': 'o',
        'ô': 'o', 'ố': 'o', 'ồ': 'o', 'ổ': 'o', 'ỗ': 'o', 'ộ': 'o',
        'ơ': 'o', 'ớ': 'o', 'ờ': 'o', 'ở': 'o', 'ỡ': 'o', 'ợ': 'o',

        'ú': 'u', 'ù': 'u', 'ủ': 'u', 'ũ': 'u', 'ụ': 'u',
        'ư': 'u', 'ứ': 'u', 'ừ': 'u', 'ử': 'u', 'ữ': 'u', 'ự': 'u',

        'ý': 'y', 'ỳ': 'y', 'ỷ': 'y', 'ỹ': 'y', 'ỵ': 'y',

        'đ': 'd'
    };

    return text
        .toLowerCase()
        .split('')
        .map(char => diacriticsMap[char] || char)
        .join('');
}

/**
 * So sánh hai string không dấu
 * @param {string} searchText - Text tìm kiếm
 * @param {string} targetText - Text cần kiểm tra
 * @returns {boolean} True nếu tìm thấy
 */
function searchWithoutDiacritics(searchText, targetText) {
    // Xử lý null/undefined
    if (!searchText) return false;
    if (!targetText) return false;

    // Chuyển sang string nếu là số
    const searchStr = String(searchText).trim();
    const targetStr = String(targetText).trim();

    if (!searchStr || !targetStr) return false;

    const cleanSearch = removeDiacritics(searchStr);
    const cleanTarget = removeDiacritics(targetStr);

    return cleanTarget.includes(cleanSearch);
}

/**
 * Lọc array theo từ khóa tìm kiếm (không dấu)
 * @param {array} items - Array cần lọc
 * @param {string} searchText - Từ khóa tìm kiếm
 * @param {array} fields - Các field cần tìm kiếm
 * @returns {array} Kết quả lọc
 */
function filterBySearch(items, searchText, fields = []) {
    if (!searchText) return items;

    return items.filter(item => {
        return fields.some(field => {
            const value = item[field];
            return searchWithoutDiacritics(searchText, String(value || ''));
        });
    });
}

