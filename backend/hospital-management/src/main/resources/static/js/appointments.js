/** Appointments page logic */
let allAppointments = [];
document.addEventListener('DOMContentLoaded', async () => { if (await checkAuth()) { loadAppointments(); loadDropdowns(); } });

async function loadAppointments() {
    try {
        allAppointments = await fetchAPI('/appointments');
        renderAppointments(allAppointments);
        document.getElementById('appt-total').textContent = allAppointments.length;
        document.getElementById('appt-scheduled').textContent = allAppointments.filter(a => a.status === 'SCHEDULED').length;
        document.getElementById('appt-completed').textContent = allAppointments.filter(a => a.status === 'COMPLETED').length;
    } catch (e) { document.getElementById('appointments-table').innerHTML = '<tr><td colspan="6">Error loading</td></tr>'; }
}

function renderAppointments(list) {
    const tbody = document.getElementById('appointments-table');
    if (!list.length) { tbody.innerHTML = '<tr><td colspan="6" class="empty-state"><div class="empty-icon">📅</div><div>No appointments</div></td></tr>'; return; }
    tbody.innerHTML = list.map(a => `<tr>
        <td><strong style="color:var(--text-primary)">${a.patientName}</strong></td>
        <td>${a.doctorName}<br><small style="color:var(--text-muted)">${a.doctorSpecialization||''}</small></td>
        <td>${formatDate(a.appointmentDate)}</td><td>${a.appointmentTime}</td>
        <td><span class="badge ${getBadgeClass(a.status)}">${a.status}</span></td>
        <td>${a.status === 'SCHEDULED' ? `
            <button class="btn btn-secondary btn-sm" onclick="completeAppt(${a.id})">✅</button>
            <button class="btn btn-danger btn-sm" onclick="cancelAppt(${a.id})" style="margin-left:4px">❌</button>
        ` : '—'}</td>
    </tr>`).join('');
}

function filterAppointments() {
    const s = document.getElementById('status-filter').value;
    renderAppointments(s ? allAppointments.filter(a => a.status === s) : allAppointments);
}

async function loadDropdowns() {
    try {
        const [patients, doctors] = await Promise.all([fetchAPI('/patients'), fetchAPI('/doctors')]);
        document.getElementById('book-patient').innerHTML = '<option value="">Select Patient</option>' + patients.map(p => `<option value="${p.id}">${p.fullName}</option>`).join('');
        document.getElementById('book-doctor').innerHTML = '<option value="">Select Doctor</option>' + doctors.map(d => `<option value="${d.id}">${d.fullName} — ${d.specialization}</option>`).join('');
    } catch (e) { console.error('Dropdown error:', e); }
}

document.getElementById('book-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
        await fetchAPI('/appointments', { method: 'POST', body: JSON.stringify({
            patientId: parseInt(document.getElementById('book-patient').value),
            doctorId: parseInt(document.getElementById('book-doctor').value),
            appointmentDate: document.getElementById('book-date').value,
            appointmentTime: document.getElementById('book-time').value,
            reason: document.getElementById('book-reason').value
        })});
        closeModal('book-modal'); showToast('Appointment booked!', 'success'); e.target.reset(); loadAppointments();
    } catch (err) { showToast(err.message || 'Booking failed', 'error'); }
});

async function completeAppt(id) {
    try { await fetchAPI(`/appointments/${id}/complete`, { method: 'PUT' }); showToast('Appointment completed', 'success'); loadAppointments(); }
    catch (e) { showToast('Failed', 'error'); }
}

async function cancelAppt(id) {
    if (!confirm('Cancel this appointment?')) return;
    try { await fetchAPI(`/appointments/${id}/cancel`, { method: 'PUT' }); showToast('Appointment cancelled', 'success'); loadAppointments(); }
    catch (e) { showToast('Failed', 'error'); }
}
