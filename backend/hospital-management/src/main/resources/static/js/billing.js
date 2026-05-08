/** Billing page logic */
let allBills = [];
document.addEventListener('DOMContentLoaded', async () => { if (await checkAuth()) { loadBills(); loadBillDropdowns(); loadRevenue(); } });

async function loadBills() {
    try {
        allBills = await fetchAPI('/bills');
        renderBills(allBills);
        document.getElementById('bill-pending').textContent = allBills.filter(b => b.paymentStatus === 'PENDING').length;
    } catch (e) { document.getElementById('bills-table').innerHTML = '<tr><td colspan="7">Error loading</td></tr>'; }
}

function renderBills(list) {
    const tbody = document.getElementById('bills-table');
    if (!list.length) { tbody.innerHTML = '<tr><td colspan="7" class="empty-state"><div class="empty-icon">💳</div><div>No bills yet</div></td></tr>'; return; }
    tbody.innerHTML = list.map(b => {
        const balance = (b.amount - b.paidAmount).toFixed(2);
        return `<tr>
        <td><strong style="color:var(--text-primary)">${b.patientName}</strong></td>
        <td>${formatCurrency(b.amount)}</td><td>${formatCurrency(b.paidAmount)}</td>
        <td>${formatCurrency(balance)}</td>
        <td><span class="badge ${getBadgeClass(b.paymentStatus)}">${b.paymentStatus}</span></td>
        <td>${formatDate(b.billDate)}</td>
        <td>${b.paymentStatus !== 'PAID' ? `<button class="btn btn-primary btn-sm" onclick="openPayModal(${b.id}, ${balance})">💵 Pay</button>` : '✅'}</td>
    </tr>`;
    }).join('');
}

async function loadRevenue() {
    try {
        const rev = await fetchAPI('/bills/revenue');
        document.getElementById('bill-revenue').textContent = formatCurrency(rev.totalRevenue);
        document.getElementById('bill-paid').textContent = formatCurrency(rev.totalPaid);
    } catch (e) { /* ignore */ }
}

async function loadBillDropdowns() {
    try {
        const patients = await fetchAPI('/patients');
        document.getElementById('bill-patient').innerHTML = '<option value="">Select Patient</option>' + patients.map(p => `<option value="${p.id}">${p.fullName}</option>`).join('');
    } catch (e) { /* ignore */ }
}

document.getElementById('create-bill-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
        await fetchAPI('/bills', { method: 'POST', body: JSON.stringify({
            patientId: parseInt(document.getElementById('bill-patient').value),
            amount: parseFloat(document.getElementById('bill-amount').value),
            dueDate: document.getElementById('bill-due').value || null,
            paymentMethod: document.getElementById('bill-method').value || null
        })});
        closeModal('create-bill-modal'); showToast('Bill created', 'success'); e.target.reset(); loadBills(); loadRevenue();
    } catch (err) { showToast(err.message || 'Failed', 'error'); }
});

function openPayModal(id, balance) {
    document.getElementById('pay-bill-id').value = id;
    document.getElementById('pay-amount').value = balance;
    openModal('pay-modal');
}

document.getElementById('pay-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('pay-bill-id').value;
    try {
        await fetchAPI(`/bills/${id}/pay`, { method: 'PUT', body: JSON.stringify({
            amount: parseFloat(document.getElementById('pay-amount').value),
            paymentMethod: document.getElementById('pay-method').value
        })});
        closeModal('pay-modal'); showToast('Payment recorded', 'success'); loadBills(); loadRevenue();
    } catch (err) { showToast(err.message || 'Payment failed', 'error'); }
});
