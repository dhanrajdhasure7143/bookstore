// Configuration
const API_BASE_URL = 'http://localhost:8080/api';

// Global state
let currentUser = null;
let currentToken = null;
let currentPage = 0;
let totalPages = 0;
let editingBookId = null;

// Initialize the application
document.addEventListener('DOMContentLoaded', function() {
    // Check if user is already logged in
    const savedToken = localStorage.getItem('bookstore_token');
    const savedUser = localStorage.getItem('bookstore_user');
    
    if (savedToken && savedUser) {
        currentToken = savedToken;
        currentUser = JSON.parse(savedUser);
        showMainApp();
        loadBooks();
    }
});

// Authentication Functions
function showLogin() {
    document.getElementById('loginForm').style.display = 'block';
    document.getElementById('registerForm').style.display = 'none';
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-btn')[0].classList.add('active');
}

function showRegister() {
    document.getElementById('loginForm').style.display = 'none';
    document.getElementById('registerForm').style.display = 'block';
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-btn')[1].classList.add('active');
}

async function login(event) {
    event.preventDefault();
    
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    
    showLoading(true);
    
    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });
        
        if (response.ok) {
            const data = await response.json();
            currentToken = data.token;
            currentUser = data.user;
            
            // Save to localStorage
            localStorage.setItem('bookstore_token', currentToken);
            localStorage.setItem('bookstore_user', JSON.stringify(currentUser));
            
            showToast('Login successful!', 'success');
            showMainApp();
            loadBooks();
        } else {
            const error = await response.json();
            showToast(error.message || 'Login failed', 'error');
        }
    } catch (error) {
        console.error('Login error:', error);
        showToast('Network error. Please try again.', 'error');
    } finally {
        showLoading(false);
    }
}

async function register(event) {
    event.preventDefault();
    
    const username = document.getElementById('regUsername').value;
    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;
    
    showLoading(true);
    
    try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, email, password })
        });
        
        if (response.ok) {
            const data = await response.json();
            currentToken = data.token;
            currentUser = data.user;
            
            // Save to localStorage
            localStorage.setItem('bookstore_token', currentToken);
            localStorage.setItem('bookstore_user', JSON.stringify(currentUser));
            
            showToast('Registration successful!', 'success');
            showMainApp();
            loadBooks();
        } else {
            const error = await response.json();
            showToast(error.message || 'Registration failed', 'error');
        }
    } catch (error) {
        console.error('Registration error:', error);
        showToast('Network error. Please try again.', 'error');
    } finally {
        showLoading(false);
    }
}

function logout() {
    currentToken = null;
    currentUser = null;
    localStorage.removeItem('bookstore_token');
    localStorage.removeItem('bookstore_user');
    
    document.getElementById('authSection').style.display = 'block';
    document.getElementById('mainApp').style.display = 'none';
    document.getElementById('mainHeader').style.display = 'none';
    
    // Reset forms
    document.getElementById('loginForm').reset();
    document.getElementById('registerForm').reset();
    
    showToast('Logged out successfully', 'info');
}

function showMainApp() {
    document.getElementById('authSection').style.display = 'none';
    document.getElementById('mainApp').style.display = 'block';
    document.getElementById('mainHeader').style.display = 'block';
    
    // Update welcome message
    document.getElementById('welcomeMessage').textContent = 
        `Welcome, ${currentUser.username} (${currentUser.role})`;
    
    // Show admin-only features
    if (currentUser.role === 'ADMIN') {
        document.getElementById('addBookBtn').style.display = 'inline-flex';
        document.getElementById('actionsHeader').style.display = 'table-cell';
    } else {
        document.getElementById('addBookBtn').style.display = 'none';
        document.getElementById('actionsHeader').style.display = 'none';
    }
}

// Book Management Functions
async function loadBooks() {
    if (!currentToken) return;
    
    const pageSize = document.getElementById('pageSize').value;
    const sortBy = document.getElementById('sortBy').value;
    const sortDir = document.getElementById('sortDir').value;
    
    showLoading(true);
    
    try {
        const response = await fetch(
            `${API_BASE_URL}/books?page=${currentPage}&size=${pageSize}&sortBy=${sortBy}&sortDir=${sortDir}`,
            {
                headers: {
                    'Authorization': `Bearer ${currentToken}`
                }
            }
        );
        
        if (response.ok) {
            const data = await response.json();
            displayBooks(data.content);
            updatePagination(data);
        } else {
            showToast('Failed to load books', 'error');
        }
    } catch (error) {
        console.error('Load books error:', error);
        showToast('Network error while loading books', 'error');
    } finally {
        showLoading(false);
    }
}

function displayBooks(books) {
    const tbody = document.getElementById('booksTableBody');
    tbody.innerHTML = '';
    
    if (books.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="8" class="text-center text-muted">
                    <i class="fas fa-book-open"></i> No books found
                </td>
            </tr>
        `;
        return;
    }
    
    books.forEach(book => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${book.id}</td>
            <td><strong>${escapeHtml(book.title)}</strong></td>
            <td>${escapeHtml(book.author)}</td>
            <td>${formatDate(book.publishedDate)}</td>
            <td>${escapeHtml(book.genre || '-')}</td>
            <td>$${book.price.toFixed(2)}</td>
            <td><code>${book.isbn}</code></td>
            ${currentUser.role === 'ADMIN' ? `
                <td class="actions-column">
                    <div class="action-buttons">
                        <button class="btn btn-warning" onclick="editBook(${book.id})" title="Edit">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-danger" onclick="deleteBook(${book.id})" title="Delete">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            ` : ''}
        `;
        tbody.appendChild(row);
    });
}

function updatePagination(data) {
    totalPages = data.totalPages;
    const pagination = document.getElementById('pagination');
    pagination.innerHTML = '';
    
    if (totalPages <= 1) return;
    
    // Previous button
    const prevBtn = document.createElement('button');
    prevBtn.className = `btn btn-secondary ${currentPage === 0 ? 'disabled' : ''}`;
    prevBtn.innerHTML = '<i class="fas fa-chevron-left"></i>';
    prevBtn.onclick = () => changePage(currentPage - 1);
    prevBtn.disabled = currentPage === 0;
    pagination.appendChild(prevBtn);
    
    // Page numbers
    const startPage = Math.max(0, currentPage - 2);
    const endPage = Math.min(totalPages - 1, currentPage + 2);
    
    if (startPage > 0) {
        const firstBtn = document.createElement('button');
        firstBtn.className = 'btn btn-secondary';
        firstBtn.textContent = '1';
        firstBtn.onclick = () => changePage(0);
        pagination.appendChild(firstBtn);
        
        if (startPage > 1) {
            const ellipsis = document.createElement('span');
            ellipsis.textContent = '...';
            ellipsis.className = 'pagination-ellipsis';
            pagination.appendChild(ellipsis);
        }
    }
    
    for (let i = startPage; i <= endPage; i++) {
        const pageBtn = document.createElement('button');
        pageBtn.className = `btn ${i === currentPage ? 'btn-primary active' : 'btn-secondary'}`;
        pageBtn.textContent = i + 1;
        pageBtn.onclick = () => changePage(i);
        pagination.appendChild(pageBtn);
    }
    
    if (endPage < totalPages - 1) {
        if (endPage < totalPages - 2) {
            const ellipsis = document.createElement('span');
            ellipsis.textContent = '...';
            ellipsis.className = 'pagination-ellipsis';
            pagination.appendChild(ellipsis);
        }
        
        const lastBtn = document.createElement('button');
        lastBtn.className = 'btn btn-secondary';
        lastBtn.textContent = totalPages;
        lastBtn.onclick = () => changePage(totalPages - 1);
        pagination.appendChild(lastBtn);
    }
    
    // Next button
    const nextBtn = document.createElement('button');
    nextBtn.className = `btn btn-secondary ${currentPage === totalPages - 1 ? 'disabled' : ''}`;
    nextBtn.innerHTML = '<i class="fas fa-chevron-right"></i>';
    nextBtn.onclick = () => changePage(currentPage + 1);
    nextBtn.disabled = currentPage === totalPages - 1;
    pagination.appendChild(nextBtn);
    
    // Page info
    const pageInfo = document.createElement('div');
    pageInfo.className = 'pagination-info';
    pageInfo.textContent = `Page ${currentPage + 1} of ${totalPages} (${data.totalElements} books)`;
    pagination.appendChild(pageInfo);
}

function changePage(page) {
    if (page >= 0 && page < totalPages && page !== currentPage) {
        currentPage = page;
        loadBooks();
    }
}



// Modal Functions
function showAddBookModal() {
    editingBookId = null;
    document.getElementById('modalTitle').textContent = 'Add New Book';
    document.getElementById('bookForm').reset();
    document.getElementById('bookModal').classList.add('show');
}

function editBook(bookId) {
    editingBookId = bookId;
    document.getElementById('modalTitle').textContent = 'Edit Book';
    
    // Find the book data from the current table
    const rows = document.querySelectorAll('#booksTableBody tr');
    for (let row of rows) {
        const cells = row.cells;
        if (cells[0].textContent == bookId) {
            document.getElementById('bookTitle').value = cells[1].textContent;
            document.getElementById('bookAuthor').value = cells[2].textContent;
            document.getElementById('bookPublishedDate').value = cells[3].textContent;
            document.getElementById('bookGenre').value = cells[4].textContent === '-' ? '' : cells[4].textContent;
            document.getElementById('bookPrice').value = parseFloat(cells[5].textContent.replace('$', ''));
            document.getElementById('bookIsbn').value = cells[6].textContent;
            break;
        }
    }
    
    document.getElementById('bookModal').classList.add('show');
}

function closeBookModal() {
    document.getElementById('bookModal').classList.remove('show');
    editingBookId = null;
}

async function saveBook(event) {
    event.preventDefault();
    
    const bookData = {
        title: document.getElementById('bookTitle').value.trim(),
        author: document.getElementById('bookAuthor').value.trim(),
        publishedDate: document.getElementById('bookPublishedDate').value,
        genre: document.getElementById('bookGenre').value.trim() || null,
        price: parseFloat(document.getElementById('bookPrice').value),
        isbn: document.getElementById('bookIsbn').value.trim()
    };
    
    // Validate ISBN format (10 or 13 digits only)
    if (!/^\d{10}$|^\d{13}$/.test(bookData.isbn)) {
        showToast('ISBN must be exactly 10 or 13 digits (no hyphens or spaces)', 'error');
        return;
    }
    
    showLoading(true);
    
    try {
        const url = editingBookId 
            ? `${API_BASE_URL}/books/${editingBookId}`
            : `${API_BASE_URL}/books`;
        
        const method = editingBookId ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${currentToken}`
            },
            body: JSON.stringify(bookData)
        });
        
        if (response.ok) {
            const action = editingBookId ? 'updated' : 'created';
            showToast(`Book ${action} successfully!`, 'success');
            closeBookModal();
            loadBooks();
        } else {
            const error = await response.json();
            if (error.fieldErrors) {
                // Handle validation errors
                const errorMessages = Object.values(error.fieldErrors).join(', ');
                showToast(`Validation error: ${errorMessages}`, 'error');
            } else {
                showToast(error.message || `Failed to ${editingBookId ? 'update' : 'create'} book`, 'error');
            }
        }
    } catch (error) {
        console.error('Save book error:', error);
        showToast('Network error while saving book', 'error');
    } finally {
        showLoading(false);
    }
}

async function deleteBook(bookId) {
    if (!confirm('Are you sure you want to delete this book? This action cannot be undone.')) {
        return;
    }
    
    showLoading(true);
    
    try {
        const response = await fetch(`${API_BASE_URL}/books/${bookId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${currentToken}`
            }
        });
        
        if (response.ok) {
            showToast('Book deleted successfully!', 'success');
            loadBooks();
        } else {
            const error = await response.json();
            showToast(error.message || 'Failed to delete book', 'error');
        }
    } catch (error) {
        console.error('Delete book error:', error);
        showToast('Network error while deleting book', 'error');
    } finally {
        showLoading(false);
    }
}

// Utility Functions
function showLoading(show) {
    const overlay = document.getElementById('loadingOverlay');
    if (show) {
        overlay.classList.add('show');
    } else {
        overlay.classList.remove('show');
    }
}

function showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    
    const icons = {
        success: 'fas fa-check-circle',
        error: 'fas fa-exclamation-circle',
        warning: 'fas fa-exclamation-triangle',
        info: 'fas fa-info-circle'
    };
    
    toast.innerHTML = `
        <i class="${icons[type]}"></i>
        <div class="toast-message">${escapeHtml(message)}</div>
    `;
    
    container.appendChild(toast);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        if (toast.parentNode) {
            toast.parentNode.removeChild(toast);
        }
    }, 5000);
    
    // Remove on click
    toast.addEventListener('click', () => {
        if (toast.parentNode) {
            toast.parentNode.removeChild(toast);
        }
    });
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

// Event Listeners
document.addEventListener('keydown', function(event) {
    // Close modal on Escape key
    if (event.key === 'Escape') {
        closeBookModal();
    }
});

// Close modal when clicking outside
document.getElementById('bookModal').addEventListener('click', function(event) {
    if (event.target === this) {
        closeBookModal();
    }
});

// Reset page when changing sort or page size
document.getElementById('sortBy').addEventListener('change', () => {
    currentPage = 0;
});

document.getElementById('sortDir').addEventListener('change', () => {
    currentPage = 0;
});

document.getElementById('pageSize').addEventListener('change', () => {
    currentPage = 0;
});