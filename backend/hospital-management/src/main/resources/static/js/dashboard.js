/** Dashboard page logic */
document.addEventListener('DOMContentLoaded', async () => {
    if (!await checkAuth()) return;
    loadStats();
    loadRecentAppointments();
    loadRecentBills();
});

async function loadStats() {
    try {
        const stats = await fetchAPI('/dashboard/stats');
        animateCounter(document.getElementById('stat-doctors'), stats.totalDoctors || 0);
        animateCounter(document.getElementById('stat-patients'), stats.totalPatients || 0);
        animateCounter(document.getElementById('stat-appointments'), stats.totalAppointments || 0);
        animateCounter(document.getElementById('stat-completed'), stats.completedAppointments || 0);
        animateCounter(document.getElementById('stat-scheduled'), stats.scheduledAppointments || 0);
        const revEl = document.getElementById('stat-revenue');
        const rev = stats.totalRevenue || 0;
        revEl.textContent = formatCurrency(rev);
    } catch (e) { console.error('Stats error:', e); }
}

async function loadRecentAppointments() {
    try {
        const data = await fetchAPI('/appointments');
        const tbody = document.getElementById('recent-appointments');
        const items = (data || []).slice(0, 5);
        if (items.length === 0) { tbody.innerHTML = '<tr><td colspan="4" class="empty-state">No appointments yet</td></tr>'; return; }
        tbody.innerHTML = items.map(a => `<tr>
            <td>${a.patientName}</td><td>${a.doctorName}</td><td>${formatDate(a.appointmentDate)}</td>
            <td><span class="badge ${getBadgeClass(a.status)}">${a.status}</span></td>
        </tr>`).join('');
    } catch (e) { document.getElementById('recent-appointments').innerHTML = '<tr><td colspan="4">Error loading</td></tr>'; }
}

async function loadRecentBills() {
    try {
        const data = await fetchAPI('/bills');
        const tbody = document.getElementById('recent-bills');
        const items = (data || []).slice(0, 5);
        if (items.length === 0) { tbody.innerHTML = '<tr><td colspan="4" class="empty-state">No bills yet</td></tr>'; return; }
        tbody.innerHTML = items.map(b => `<tr>
            <td>${b.patientName}</td><td>${formatCurrency(b.amount)}</td><td>${formatCurrency(b.paidAmount)}</td>
            <td><span class="badge ${getBadgeClass(b.paymentStatus)}">${b.paymentStatus}</span></td>
        </tr>`).join('');
    } catch (e) { document.getElementById('recent-bills').innerHTML = '<tr><td colspan="4">Error loading</td></tr>'; }
}
