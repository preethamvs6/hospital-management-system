/** Doctors page logic */
let allDoctors = [];
document.addEventListener('DOMContentLoaded', async () => { if (await checkAuth()) loadDoctors(); });

async function loadDoctors() {
    try {
        allDoctors = await fetchAPI('/doctors');
        renderDoctors(allDoctors);
    } catch (e) { document.getElementById('doctors-table').innerHTML = '<tr><td colspan="6">Error loading doctors</td></tr>'; }
}

function renderDoctors(docs) {
    const tbody = document.getElementById('doctors-table');
    if (!docs.length) { tbody.innerHTML = '<tr><td colspan="6" class="empty-state"><div class="empty-icon">🩺</div><div>No doctors found</div></td></tr>'; return; }
    tbody.innerHTML = docs.map(d => `<tr>
        <td><strong style="color:var(--text-primary)">${d.fullName}</strong></td>
        <td>${d.specialization}</td><td>${d.department || '—'}</td>
        <td>${formatCurrency(d.consultationFee)}</td><td>${d.email}</td>
        <td><button class="btn btn-secondary btn-sm" onclick='editDoctor(${JSON.stringify(d)})'>✏️</button>
        <button class="btn btn-danger btn-sm" onclick="deleteDoctor(${d.id})" style="margin-left:4px">🗑️</button></td>
    </tr>`).join('');
}

function searchDoctors() {
    const q = document.getElementById('doctor-search').value.toLowerCase();
    const filtered = allDoctors.filter(d => d.fullName.toLowerCase().includes(q) || d.specialization.toLowerCase().includes(q) || (d.department||'').toLowerCase().includes(q));
    renderDoctors(filtered);
}

document.getElementById('add-doctor-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
        await fetchAPI('/auth/register', { method: 'POST', body: JSON.stringify({
            fullName: document.getElementById('doc-fullName').value, username: document.getElementById('doc-username').value,
            email: document.getElementById('doc-email').value, password: document.getElementById('doc-password').value,
            phone: document.getElementById('doc-phone').value, role: 'DOCTOR',
            specialization: document.getElementById('doc-specialization').value, department: document.getElementById('doc-department').value,
            qualification: document.getElementById('doc-qualification').value, licenseNumber: document.getElementById('doc-license').value,
            consultationFee: parseFloat(document.getElementById('doc-fee').value) || 0
        })});
        closeModal('add-doctor-modal'); showToast('Doctor added successfully', 'success');
        e.target.reset(); loadDoctors();
    } catch (err) { showToast(err.message || 'Failed to add doctor', 'error'); }
});

function editDoctor(d) {
    document.getElementById('edit-doc-id').value = d.id;
    document.getElementById('edit-doc-fullName').value = d.fullName;
    document.getElementById('edit-doc-email').value = d.email;
    document.getElementById('edit-doc-specialization').value = d.specialization;
    document.getElementById('edit-doc-department').value = d.department || '';
    document.getElementById('edit-doc-phone').value = d.phone || '';
    document.getElementById('edit-doc-fee').value = d.consultationFee || 0;
    openModal('edit-doctor-modal');
}

document.getElementById('edit-doctor-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('edit-doc-id').value;
    try {
        await fetchAPI(`/doctors/${id}`, { method: 'PUT', body: JSON.stringify({
            fullName: document.getElementById('edit-doc-fullName').value, email: document.getElementById('edit-doc-email').value,
            specialization: document.getElementById('edit-doc-specialization').value, department: document.getElementById('edit-doc-department').value,
            phone: document.getElementById('edit-doc-phone').value, consultationFee: parseFloat(document.getElementById('edit-doc-fee').value) || 0
        })});
        closeModal('edit-doctor-modal'); showToast('Doctor updated', 'success'); loadDoctors();
    } catch (err) { showToast(err.message || 'Update failed', 'error'); }
});

async function deleteDoctor(id) {
    if (!confirm('Are you sure you want to remove this doctor?')) return;
    try { await fetchAPI(`/doctors/${id}`, { method: 'DELETE' }); showToast('Doctor removed', 'success'); loadDoctors(); }
    catch (err) { showToast(err.message || 'Delete failed', 'error'); }
}
