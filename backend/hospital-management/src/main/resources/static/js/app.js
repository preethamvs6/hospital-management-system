/**
 * Hospital Management System — Core JavaScript
 * Shared utilities: API calls, auth, navigation, toasts, modals, pagination
 */

const API_BASE = '/api';

// ---- State ----
let currentUser = null;

// ---- API Helper ----
async function fetchAPI(url, options = {}) {
    const defaults = {
        headers: { 'Content-Type': 'application/json' },
        credentials: 'same-origin'
    };
    const config = { ...defaults, ...options, headers: { ...defaults.headers, ...options.headers } };
    try {
        const response = await fetch(API_BASE + url, config);
        if (response.status === 401) {
            window.location.href = '/login.html';
            return null;
        }
        const data = await response.json();
        if (!response.ok) throw { status: response.status, ...data };
        return data;
    } catch (error) {
        if (error.status) throw error;
        console.error('API Error:', error);
        throw { status: 500, message: 'Network error. Please try again.' };
    }
}

// ---- Auth ----
async function checkAuth() {
    try {
        const user = await fetchAPI('/auth/me');
        if (user && user.userId) {
            currentUser = user;
            updateUserUI();
            return true;
        }
    } catch (e) { /* not authenticated */ }
    window.location.href = '/login.html';
    return false;
}

function updateUserUI() {
    if (!currentUser) return;
    const nameEl = document.getElementById('user-display-name');
    const roleEl = document.getElementById('user-display-role');
    const avatarEl = document.getElementById('user-avatar');
    if (nameEl) nameEl.textContent = currentUser.fullName || currentUser.username;
    if (roleEl) roleEl.textContent = currentUser.role;
    if (avatarEl) avatarEl.textContent = (currentUser.fullName || currentUser.username).charAt(0).toUpperCase();
}

async function logout() {
    try {
        await fetchAPI('/auth/logout', { method: 'POST' });
    } catch (e) { /* ignore */ }
    currentUser = null;
    window.location.href = '/login.html';
}

// ---- Toast Notifications ----
function showToast(message, type = 'info') {
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    container.appendChild(toast);
    setTimeout(() => toast.remove(), 4000);
}

// ---- Modal ----
function openModal(id) {
    const modal = document.getElementById(id);
    if (modal) modal.classList.add('active');
}

function closeModal(id) {
    const modal = document.getElementById(id);
    if (modal) modal.classList.remove('active');
}

// Close modal on overlay click
document.addEventListener('click', (e) => {
    if (e.target.classList.contains('modal-overlay')) {
        e.target.classList.remove('active');
    }
});

// ---- Sidebar Navigation ----
function initSidebar() {
    const toggle = document.querySelector('.mobile-toggle');
    const sidebar = document.querySelector('.sidebar');
    if (toggle && sidebar) {
        toggle.addEventListener('click', () => sidebar.classList.toggle('open'));
        document.addEventListener('click', (e) => {
            if (window.innerWidth <= 768 && !sidebar.contains(e.target) && !toggle.contains(e.target)) {
                sidebar.classList.remove('open');
            }
        });
    }
    // Highlight active nav item
    const currentPage = window.location.pathname.split('/').pop() || 'dashboard.html';
    document.querySelectorAll('.nav-item').forEach(item => {
        if (item.getAttribute('data-page') === currentPage) {
            item.classList.add('active');
        }
    });
}

// ---- Pagination ----
function renderPagination(containerId, totalItems, currentPage, pageSize, onPageChange) {
    const totalPages = Math.ceil(totalItems / pageSize);
    const container = document.getElementById(containerId);
    if (!container || totalPages <= 1) { if (container) container.innerHTML = ''; return; }
    let html = '';
    html += `<button class="page-btn" onclick="${onPageChange}(${currentPage - 1})" ${currentPage <= 1 ? 'disabled' : ''}>&laquo;</button>`;
    for (let i = 1; i <= totalPages; i++) {
        html += `<button class="page-btn ${i === currentPage ? 'active' : ''}" onclick="${onPageChange}(${i})">${i}</button>`;
    }
    html += `<button class="page-btn" onclick="${onPageChange}(${currentPage + 1})" ${currentPage >= totalPages ? 'disabled' : ''}>&raquo;</button>`;
    container.innerHTML = html;
}

// ---- Counter Animation ----
function animateCounter(element, target) {
    const duration = 1500;
    const start = 0;
    const startTime = performance.now();
    function update(currentTime) {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);
        const eased = 1 - Math.pow(1 - progress, 3);
        element.textContent = Math.floor(start + (target - start) * eased);
        if (progress < 1) requestAnimationFrame(update);
    }
    requestAnimationFrame(update);
}

// ---- Format Helpers ----
function formatDate(dateStr) {
    if (!dateStr) return '—';
    const d = new Date(dateStr);
    return d.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
}

function formatCurrency(amount) {
    if (amount == null) return '$0.00';
    return '$' + Number(amount).toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
}

function getBadgeClass(status) {
    const map = {
        'SCHEDULED': 'badge-scheduled', 'COMPLETED': 'badge-completed', 'CANCELLED': 'badge-cancelled',
        'PENDING': 'badge-pending', 'PAID': 'badge-paid', 'PARTIAL': 'badge-partial'
    };
    return map[status] || 'badge-scheduled';
}

// ---- Sidebar HTML Generator ----
function getSidebarHTML() {
    return `
    <button class="mobile-toggle" aria-label="Toggle Menu">☰</button>
    <aside class="sidebar" id="sidebar">
        <div class="sidebar-header">
            <div class="sidebar-logo">MedCore<span>Hospital Management</span></div>
        </div>
        <nav class="sidebar-nav">
            <a href="/dashboard.html" class="nav-item" data-page="dashboard.html">
                <span class="icon">📊</span> Dashboard
            </a>
            <a href="/doctors.html" class="nav-item" data-page="doctors.html">
                <span class="icon">🩺</span> Doctors
            </a>
            <a href="/patients.html" class="nav-item" data-page="patients.html">
                <span class="icon">🧑‍🤝‍🧑</span> Patients
            </a>
            <a href="/appointments.html" class="nav-item" data-page="appointments.html">
                <span class="icon">📅</span> Appointments
            </a>
            <a href="/billing.html" class="nav-item" data-page="billing.html">
                <span class="icon">💳</span> Billing
            </a>
            <a href="/contact.html" class="nav-item" data-page="contact.html">
                <span class="icon">📞</span> Contact
            </a>
        </nav>
        <div class="sidebar-footer">
            <div class="user-info">
                <div class="user-avatar" id="user-avatar">A</div>
                <div>
                    <div class="user-name" id="user-display-name">User</div>
                    <div class="user-role" id="user-display-role">ADMIN</div>
                </div>
            </div>
            <button class="btn btn-secondary btn-sm" onclick="logout()" style="width:100%;margin-top:12px;">
                🚪 Logout
            </button>
        </div>
    </aside>`;
}

// ---- Init ----
document.addEventListener('DOMContentLoaded', () => {
    initSidebar();
});
