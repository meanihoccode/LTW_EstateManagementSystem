// Utility functions cho Pagination

class PaginationManager {
    constructor(options = {}) {
        this.currentPage = 1;
        this.pageSize = options.pageSize || 10;
        this.data = [];
        this.filteredData = [];
        this.isFiltering = false;
    }

    setData(data) {
        this.data = data;
        this.filteredData = data;
        this.currentPage = 1;
    }

    getData() {
        return this.data;
    }

    getPagedData() {
        const startIndex = (this.currentPage - 1) * this.pageSize;
        const endIndex = startIndex + this.pageSize;
        return this.filteredData.slice(startIndex, endIndex);
    }

    getTotalPages() {
        return Math.ceil(this.filteredData.length / this.pageSize);
    }

    goToPage(page) {
        const totalPages = this.getTotalPages();
        if (page >= 1 && page <= totalPages) {
            this.currentPage = page;
            return true;
        }
        return false;
    }

    nextPage() {
        return this.goToPage(this.currentPage + 1);
    }

    prevPage() {
        return this.goToPage(this.currentPage - 1);
    }

    setPageSize(size) {
        this.pageSize = parseInt(size);
        this.currentPage = 1;
    }

    filterData(filterFunc) {
        this.filteredData = this.data.filter(filterFunc);
        this.currentPage = 1;
        this.isFiltering = true;
    }

    clearFilter() {
        this.filteredData = this.data;
        this.currentPage = 1;
        this.isFiltering = false;
    }

    getPaginationInfo() {
        const startIndex = (this.currentPage - 1) * this.pageSize;
        const endIndex = Math.min(startIndex + this.pageSize, this.filteredData.length);
        return {
            startRecord: startIndex + 1,
            endRecord: endIndex,
            totalRecords: this.filteredData.length,
            currentPage: this.currentPage,
            totalPages: this.getTotalPages()
        };
    }

    renderPaginationButtons(containerId, onPageChange) {
        const container = document.getElementById(containerId);
        if (!container) return;

        const totalPages = this.getTotalPages();
        const currentPage = this.currentPage;

        if (totalPages <= 1) {
            container.innerHTML = '';
            return;
        }

        let html = '<button class="page-btn" onclick="' + onPageChange + '(1)" ' + (currentPage === 1 ? 'disabled' : '') + '>«</button>';
        html += '<button class="page-btn" onclick="' + onPageChange + '(' + (currentPage - 1) + ')" ' + (currentPage === 1 ? 'disabled' : '') + '>‹</button>';

        let startPage = Math.max(1, currentPage - 2);
        let endPage = Math.min(totalPages, currentPage + 2);

        if (startPage > 1) {
            html += '<button class="page-btn" onclick="' + onPageChange + '(1)">1</button>';
            if (startPage > 2) {
                html += '<span class="page-ellipsis">...</span>';
            }
        }

        for (let i = startPage; i <= endPage; i++) {
            html += '<button class="page-btn ' + (i === currentPage ? 'active' : '') + '" onclick="' + onPageChange + '(' + i + ')">' + i + '</button>';
        }

        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                html += '<span class="page-ellipsis">...</span>';
            }
            html += '<button class="page-btn" onclick="' + onPageChange + '(' + totalPages + ')">' + totalPages + '</button>';
        }

        html += '<button class="page-btn" onclick="' + onPageChange + '(' + (currentPage + 1) + ')" ' + (currentPage === totalPages ? 'disabled' : '') + '>›</button>';
        html += '<button class="page-btn" onclick="' + onPageChange + '(' + totalPages + ')" ' + (currentPage === totalPages ? 'disabled' : '') + '>»</button>';

        container.innerHTML = html;
    }
}

