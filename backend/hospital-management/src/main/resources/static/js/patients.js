/** Patients page logic */
let allPatients = [];
document.addEventListener('DOMContentLoaded', async () => { if (await checkAuth()) loadPatients(); });

async function loadPatients() {
    try {
        allPatients = await fetchAPI('/patients');
        renderPatients(allPatients);
    } catch (e) { document.getElementById('patients-table').innerHTML = '<tr><td colspan="6">Error loading</td></tr>'; }
}

function renderPatients(list) {
    const tbody = document.getElementById('patients-table');
    if (!list.length) { tbody.innerHTML = '<tr><td colspan="6" class="empty-state"><div class="empty-icon">🧑‍🤝‍🧑</div><div>No patients found</div></td></tr>'; return; }
    tbody.innerHTML = list.map(p => `<tr>
        <td><strong style="color:var(--text-primary)">${p.fullName}</strong></td>
        <td>${p.email}</td><td>${p.phone || '—'}</td><td>${p.gender || '—'}</td><td>${p.bloodGroup || '—'}</td>
        <td><button class="btn btn-secondary btn-sm" onclick='editPatient(${JSON.stringify(p)})'>✏️</button>
        <button class="btn btn-danger btn-sm" onclick="deletePatient(${p.id})" style="margin-left:4px">🗑️</button></td>
    </tr>`).join('');
}

function searchPatients() {
    const q = document.getElementById('patient-search').value.toLowerCase();
    renderPatients(allPatients.filter(p => p.fullName.toLowerCase().includes(q) || p.email.toLowerCase().includes(q)));
}

function editPatient(p) {
    document.getElementById('edit-pat-id').value = p.id;
    document.getElementById('edit-pat-fullName').value = p.fullName;
    document.getElementById('edit-pat-email').value = p.email;
    document.getElementById('edit-pat-phone').value = p.phone || '';
    document.getElementById('edit-pat-bloodGroup').value = p.bloodGroup || '';
    document.getElementById('edit-pat-address').value = p.address || '';
    document.getElementById('edit-pat-history').value = p.medicalHistory || '';
    openModal('edit-patient-modal');
}

document.getElementById('edit-patient-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('edit-pat-id').value;
    try {
        await fetchAPI(`/patients/${id}`, { method: 'PUT', body: JSON.stringify({
            fullName: document.getElementById('edit-pat-fullName').value,
            email: document.getElementById('edit-pat-email').value,
            phone: document.getElementById('edit-pat-phone').value,
            bloodGroup: document.getElementById('edit-pat-bloodGroup').value,
            address: document.getElementById('edit-pat-address').value,
            medicalHistory: document.getElementById('edit-pat-history').value
        })});
        closeModal('edit-patient-modal'); showToast('Patient updated', 'success'); loadPatients();
    } catch (err) { showToast(err.message || 'Update failed', 'error'); }
});

async function deletePatient(id) {
    if (!confirm('Remove this patient?')) return;
    try { await fetchAPI(`/patients/${id}`, { method: 'DELETE' }); showToast('Patient removed', 'success'); loadPatients(); }
    catch (err) { showToast('Delete failed', 'error'); }
}
