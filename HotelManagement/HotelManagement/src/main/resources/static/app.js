const state = { user: null, hotelId: null, clients: [], availableRooms: [] };
const el = (id) => document.getElementById(id);
const show = (id) => el(id) && el(id).classList.remove('hidden');
const hide = (id) => el(id) && el(id).classList.add('hidden');

function showWelcomeScreen() {
  show('welcome-screen');
  hide('main-app');
}

function showMainApp() {
  hide('welcome-screen');
  show('main-app');
}

function switchAuthView(view) {
  if (view === 'register') {
    show('register-section');
    hide('login-section');
  } else if (view === 'login') {
    show('login-section');
    hide('register-section');
  }
}

function switchMain(view) {
  const map = { rooms: 'rooms-section', reservations: 'reservations-section', managers: 'managers-section', receptionists: 'receptionists-section' };
  Object.values(map).forEach(id => { if (el(id)) hide(id); });
  const target = map[view];
  if (el(target)) show(target);

  if (view === 'rooms') { loadRooms(); }
  if (view === 'reservations') { loadReservations(); loadClients(); loadAvailableRooms(); }
}



async function loadDashboard() {
  if (el('dashboardStatus')) el('dashboardStatus').textContent = '';
  const selectedHotelId = state.hotelId && state.hotelId !== 0 ? state.hotelId : null;
  let hotels = []; 

  try {
    const hotelsRes = await fetch('/api/hotels');
    if (hotelsRes.ok) {
      hotels = await hotelsRes.json(); 
      const hotelsTable = el('dashboardHotelsTable');
      if (hotelsTable) {
        hotelsTable.innerHTML = '';

        const filteredHotels = selectedHotelId ? hotels.filter(h => h.id === selectedHotelId) : hotels;

        for (const h of filteredHotels) {
          const roomsRes = await fetch(`/api/hotels/${h.id}/rooms`);
          const roomCount = roomsRes.ok ? (await roomsRes.json()).length : 0;

          const tr = document.createElement('tr');
          tr.innerHTML = `<td>${h.id}</td><td>${h.name || ''}</td><td>${h.address || ''}</td><td>${roomCount}</td>`;
          hotelsTable.appendChild(tr);
        }
      }
    }

    const usersRes = await fetch('/api/users');
    if (usersRes.ok) {
      const users = await usersRes.json();
      const usersTable = el('dashboardUsersTable');
      if (usersTable) {
        usersTable.innerHTML = '';

        const filteredUsers = selectedHotelId ? users.filter(u => u.hotelId === selectedHotelId) : users;

        filteredUsers.forEach(u => {
          const tr = document.createElement('tr');
          const hotelName = hotels.find(h => h.id === u.hotelId)?.name || `Hotel ${u.hotelId}`;
          tr.innerHTML = `<td>${u.id}</td><td>${u.username || ''}</td><td>${u.roleName || ''}</td><td>${hotelName}</td>`;
          usersTable.appendChild(tr);
        });
      }
    }

    const roomsRes = await fetch('/api/admin/rooms');
    if (roomsRes.ok) {
      const rooms = await roomsRes.json();
      const roomsTable = el('dashboardRoomsTable');
      if (roomsTable) {
        roomsTable.innerHTML = '';

        const filteredRooms = selectedHotelId ? rooms.filter(r => r.hotelId === selectedHotelId) : rooms;

        filteredRooms.forEach(r => {
          const tr = document.createElement('tr');
          tr.innerHTML = `<td>${r.id}</td><td>${r.roomNumber || ''}</td><td>${r.roomCategory || ''}</td><td>${r.hotelName || 'Hotel ' + r.hotelId}</td><td>${r.isOccupied ? 'Yes' : 'No'}</td>`;
          roomsTable.appendChild(tr);
        });
      }
    }

    const dashboardSection = el('dashboard-section');
    if (dashboardSection) {
      const h2 = dashboardSection.querySelector('h2');
      if (h2) {
        if (selectedHotelId) {
          const hotelName = hotels.find(h => h.id === selectedHotelId)?.name || `Hotel ${selectedHotelId}`;
          h2.textContent = `${hotelName} - Overview`;
        } else {
          h2.textContent = 'System Overview';
        }
      }
    }
  } catch (e) {
    if (el('dashboardStatus')) el('dashboardStatus').textContent = 'Error loading dashboard: ' + e.message;
  }
}



function saveSessionToStorage() {
  try {
    const user = state.user; const hotelId = state.hotelId;
    if (user) localStorage.setItem('hotelAppSession', JSON.stringify({ user, hotelId }));
  } catch (e) { }
}

function restoreSessionFromStorage() {
  try {
    const raw = localStorage.getItem('hotelAppSession');
    if (!raw) return;
    const obj = JSON.parse(raw) || {};
    if (!obj.user) return;
    state.user = obj.user; state.hotelId = obj.hotelId;
    setupHotelAccessUI(state.user);
    el('sessionInfo').textContent = `${state.user.username} (${state.user.roleName || ''})`;
    showMainApp();
    switchMain('rooms');
  } catch (e) { }
}

async function login() {
  el('loginStatus').textContent = '';
  const username = el('username').value.trim();
  const password = el('password').value.trim();
  if (!username || !password) { el('loginStatus').textContent = 'Username and password are required'; return }
  try {
    const res = await fetch('/api/auth/login', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ username, password }) });
    if (!res.ok) { el('loginStatus').textContent = 'Login failed'; return }
    const user = await res.json();
    state.user = user; state.hotelId = user.hotelId; saveSessionToStorage(); setupHotelAccessUI(user); if (el('hotelSelect')) el('hotelSelect').value = String(state.hotelId);
    el('sessionInfo').textContent = `${user.username} (${user.roleName || ''})`;
    showMainApp();

    // Everyone sees rooms first
    switchMain('rooms');
    await loadRooms(); await loadClients(); await loadAvailableRooms();
  } catch (e) { el('loginStatus').textContent = 'Error: ' + e.message }
}

function logout() {
  state.user = null; state.hotelId = null;
  try { localStorage.removeItem('hotelAppSession'); } catch (e) { }
  el('username').value = ''; el('password').value = ''; el('sessionInfo').textContent = '';
  if (el('hotelSelect')) { el('hotelSelect').value = ''; hide('hotelSelect'); }
  if (el('hotelId')) { el('hotelId').value = ''; el('hotelId').disabled = false; show('hotelId'); }
  showWelcomeScreen();
  switchAuthView('login');
}

function syncHotelFromSelect() {
  const v = parseInt(el('hotelSelect').value, 10); if (!isNaN(v)) state.hotelId = v;
}


async function loadHotels() {
  const sel = el('hotelSelect'); if (!sel) return;
  sel.innerHTML = '';
  try {
    const res = await fetch('/api/hotels');
    if (!res.ok) return;
    const hotels = await res.json();

    const allOpt = document.createElement('option');
    allOpt.value = '0';
    allOpt.textContent = 'All Hotels';
    sel.appendChild(allOpt);

    hotels.forEach(h => {
      const opt = document.createElement('option');
      opt.value = String(h.id);
      opt.textContent = h.name || ('Hotel ' + h.id);
      sel.appendChild(opt);
    });
    if (state.hotelId) { sel.value = String(state.hotelId); }
    else { state.hotelId = 0; sel.value = '0'; }
  } catch (e) { }
}

function setupHotelAccessUI(user) {
  const role = (user && user.roleName || '').toLowerCase();

  if (role === 'admin') {
    hide('hotelId');
    show('hotelSelect');
    show('session-section');
  } else {
    show('hotelId');
    hide('hotelSelect');
    hide('session-section');
    if (el('hotelId')) {
      el('hotelId').disabled = true;
      el('hotelId').value = String(state.hotelId || '');
    }
  }

  // Show/hide sidebar buttons based on role
  if (el('navManagersBtn')) el('navManagersBtn').style.display = ((role === 'admin' || role === 'owner') ? 'block' : 'none');
  if (el('navReceptionistsBtn')) el('navReceptionistsBtn').style.display = ((role === 'admin' || role === 'owner' || role === 'manager') ? 'block' : 'none');
}

async function loadRooms() {
  el('roomsStatus').textContent = '';
  const hotelId = state.hotelId; if (!hotelId) { el('roomsStatus').textContent = 'Hotel ID is required'; return }
  try {
    const res = await fetch(`/api/hotels/${hotelId}/rooms`);
    if (!res.ok) { el('roomsStatus').textContent = 'Failed to load rooms'; return }
    const rooms = await res.json();
    const tbody = el('roomsTable');
    tbody.innerHTML = '';
    rooms.forEach(r => {
      const tr = document.createElement('tr');
      const cancelBtn = (r.isOccupied && r.reservationNumber) ? `<button class="btn btn-warning btn-sm" data-cancel="${r.reservationNumber}" data-room="${r.roomNumber}">Cancel Reservation</button>` : '';
      tr.innerHTML = `<td>${r.id || ''}</td><td>${r.roomNumber || ''}</td><td>${r.roomCategory || ''}</td><td>${r.isOccupied ? 'Yes' : 'No'}</td><td>${r.reservationNumber || ''}</td><td><div class="btn-group"><button class="btn btn-danger btn-sm" data-room="${r.roomNumber}">Delete</button>${cancelBtn}</div></td>`;
      tbody.appendChild(tr);
    });
    tbody.querySelectorAll('button[data-room]').forEach(b => b.addEventListener('click', async (e) => {
      const rn = e.target.getAttribute('data-room');
      await deleteRoom(rn);
    }));
    tbody.querySelectorAll('button[data-cancel]').forEach(b => b.addEventListener('click', async (e) => {
      const resNum = e.target.getAttribute('data-cancel');
      await cancelReservationDirect(resNum);
    }));
  } catch (e) { el('roomsStatus').textContent = 'Error: ' + e.message }
}

async function addRoom() {
  el('roomsStatus').textContent = '';
  const hotelId = state.hotelId; const roomNumber = el('roomNumber').value.trim(); const roomCategory = el('roomCategory').value.trim();
  if (!hotelId || !roomNumber || !roomCategory) { el('roomsStatus').textContent = 'Hotel ID, room number and category are required'; return }
  try {
    const res = await fetch(`/api/hotels/${hotelId}/rooms`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ roomNumber, roomCategory }) });
    if (res.status === 409) { el('roomsStatus').textContent = 'Room number already exists'; return }
    if (!res.ok) { el('roomsStatus').textContent = 'Failed to add room'; return }
    el('roomNumber').value = ''; el('roomCategory').value = '';
    await loadRooms(); await loadAvailableRooms();
  } catch (e) { el('roomsStatus').textContent = 'Error: ' + e.message }
}

async function deleteRoom(roomNumber) {
  el('roomsStatus').textContent = '';
  const hotelId = state.hotelId; if (!hotelId || !roomNumber) { el('roomsStatus').textContent = 'Hotel ID and room number are required'; return }
  try {
    const res = await fetch(`/api/hotels/${hotelId}/rooms/${encodeURIComponent(roomNumber)}`, { method: 'DELETE' });
    if (!res.ok) {
      let msg = 'Failed to delete room';
      try { msg = await res.text(); } catch (e) { }
      el('roomsStatus').textContent = msg || 'Failed to delete room';
      return;
    }
    await loadRooms();
  } catch (e) { el('roomsStatus').textContent = 'Error: ' + e.message }
}

async function loadReservations() {
  el('reservationsStatus').textContent = '';
  const hotelId = state.hotelId; if (!hotelId) { el('reservationsStatus').textContent = 'Hotel ID is required'; return }
  try {
    const res = await fetch(`/api/hotels/${hotelId}/reservations`);
    if (!res.ok) { el('reservationsStatus').textContent = 'Failed to load reservations'; return }
    const arr = await res.json();
    const tbody = el('reservationsTable');
    tbody.innerHTML = '';
    arr.forEach(r => {
      const tr = document.createElement('tr');
      const canBtn = r.isCancelled ? '' : `<div class="btn-group"><button class="btn btn-warning btn-sm" data-res="${r.reservationNumber}">Cancel</button></div>`;
      tr.innerHTML = `<td>${r.reservationNumber || ''}</td><td>${r.roomNumber || ''}</td><td>${r.startDate || ''}</td><td>${r.endDate || ''}</td><td>${r.isCancelled ? 'Yes' : 'No'}</td><td>${canBtn}</td>`;
      tbody.appendChild(tr);
    });
    tbody.querySelectorAll('button[data-res]').forEach(b => b.addEventListener('click', async (e) => {
      const rn = e.target.getAttribute('data-res');
      await cancelReservationDirect(rn);
    }));
  } catch (e) { el('reservationsStatus').textContent = 'Error: ' + e.message }
}

function bind() {
  if (el('loginBtn')) el('loginBtn').addEventListener('click', login);
  if (el('hotelSelect')) el('hotelSelect').addEventListener('change', () => { syncHotelFromSelect(); loadRooms(); loadClients(); loadAvailableRooms(); loadReservations(); loadDashboard(); });
  if (el('addRoomBtn')) el('addRoomBtn').addEventListener('click', addRoom);
  if (el('loadReservationsBtn')) el('loadReservationsBtn').addEventListener('click', loadReservations);
  if (el('signupOwnerBtn')) el('signupOwnerBtn').addEventListener('click', signupOwner);
  if (el('showRegisterLink')) el('showRegisterLink').addEventListener('click', (e) => { e.preventDefault(); switchAuthView('register'); });
  if (el('showLoginLink')) el('showLoginLink').addEventListener('click', (e) => { e.preventDefault(); switchAuthView('login'); });
  if (el('logoutNavBtn')) el('logoutNavBtn').addEventListener('click', logout);
  if (el('navDashboardBtn')) el('navDashboardBtn').addEventListener('click', () => { switchMain('dashboard'); });
  if (el('navRoomsBtn')) el('navRoomsBtn').addEventListener('click', () => { switchMain('rooms'); });
  if (el('navReservationsBtn')) el('navReservationsBtn').addEventListener('click', () => { switchMain('reservations'); });
  if (el('navManagersBtn')) el('navManagersBtn').addEventListener('click', () => { switchMain('managers'); });
  if (el('navReceptionistsBtn')) el('navReceptionistsBtn').addEventListener('click', () => { switchMain('receptionists'); });
  if (el('addManagerBtn')) el('addManagerBtn').addEventListener('click', addManager);
  if (el('addReceptionistBtn')) el('addReceptionistBtn').addEventListener('click', addReceptionist);
  if (el('addClientBtn')) el('addClientBtn').addEventListener('click', addClient);
  if (el('createReservationBtn')) el('createReservationBtn').addEventListener('click', createReservation);
}

document.addEventListener('DOMContentLoaded', async () => { bind(); await loadHotels(); restoreSessionFromStorage(); if (state.user) { await loadRooms(); await loadClients(); await loadAvailableRooms(); } });

async function signupOwner() {
  el('registerStatus').textContent = '';
  const name = el('regHotelName').value.trim();
  const address = el('regHotelAddress').value.trim();
  const username = el('regUsername').value.trim();
  const password = el('regPassword').value.trim();
  if (!name || !address || !username || !password) { el('registerStatus').textContent = 'All fields are required'; return; }
  try {
    const res = await fetch('/api/auth/register/owner', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ name, address, username, password }) });
    if (!res.ok) { el('registerStatus').textContent = 'Sign up failed'; return; }
    el('registerStatus').textContent = 'Owner registered';
    el('username').value = username; el('password').value = password; await login();
  } catch (e) { el('registerStatus').textContent = 'Error: ' + e.message }
}

async function addManager() {
  if (el('managerStatus')) el('managerStatus').textContent = '';
  const hotelId = state.hotelId; const username = el('managerUsername').value.trim(); const password = el('managerPassword').value.trim();
  if (!hotelId || !username || !password) { if (el('managerStatus')) el('managerStatus').textContent = 'Hotel ID, username and password are required'; return; }
  try {
    const res = await fetch(`/api/hotels/${hotelId}/managers`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ username, password }) });
    if (res.status === 409) {
      const msg = await res.text();
      if (el('managerStatus')) el('managerStatus').textContent = msg || 'Username already exists';
      return;
    }
    if (!res.ok) {
      const msg = await res.text();
      if (el('managerStatus')) el('managerStatus').textContent = msg || 'Failed to add manager';
      return;
    }
    el('managerUsername').value = ''; el('managerPassword').value = ''; if (el('managerStatus')) el('managerStatus').textContent = 'Manager added';
  } catch (e) { if (el('managerStatus')) el('managerStatus').textContent = 'Error: ' + e.message }
}

async function addReceptionist() {
  if (el('recStatus')) el('recStatus').textContent = '';
  const hotelId = state.hotelId; const username = el('recUsername').value.trim(); const password = el('recPassword').value.trim();
  if (!hotelId || !username || !password) { if (el('recStatus')) el('recStatus').textContent = 'Hotel ID, username and password are required'; return; }
  try {
    const res = await fetch(`/api/hotels/${hotelId}/receptionists`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ username, password }) });
    if (res.status === 409) {
      const msg = await res.text();
      if (el('recStatus')) el('recStatus').textContent = msg || 'Username already exists';
      return;
    }
    if (!res.ok) {
      const msg = await res.text();
      if (el('recStatus')) el('recStatus').textContent = msg || 'Failed to add receptionist';
      return;
    }
    el('recUsername').value = ''; el('recPassword').value = ''; if (el('recStatus')) el('recStatus').textContent = 'Receptionist added';
  } catch (e) { if (el('recStatus')) el('recStatus').textContent = 'Error: ' + e.message }
}

async function addClient() {
  el('reservationsStatus').textContent = '';
  const hotelId = state.hotelId; const name = el('clientNameAdd') ? el('clientNameAdd').value.trim() : ''; const email = el('clientEmail').value.trim();
  if (!hotelId || !name) { el('reservationsStatus').textContent = 'Hotel ID and client name are required'; return; }
  try {
    const res = await fetch(`/api/hotels/${hotelId}/clients`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ name, email }) });
    if (!res.ok) { el('reservationsStatus').textContent = 'Failed to add client'; return; }
    el('clientEmail').value = ''; el('reservationsStatus').textContent = `Client created`; if (el('clientNameAdd')) el('clientNameAdd').value = ''; await loadClients();
  } catch (e) { el('reservationsStatus').textContent = 'Error: ' + e.message }
}

async function createReservation() {
  el('reservationsStatus').textContent = '';
  const hotelId = state.hotelId;
  const clientName = el('clientNameInput') ? el('clientNameInput').value.trim() : '';
  const roomNumber = el('resRoomSelect').value;
  const startDate = el('startDate').value;
  const endDate = el('endDate').value;
  if (!hotelId || !clientName || !roomNumber || !startDate || !endDate) { el('reservationsStatus').textContent = 'All reservation fields are required'; return; }
  const client = (state.clients || []).find(c => (c.name || '').toLowerCase() === clientName.toLowerCase());
  if (!client) { el('reservationsStatus').textContent = 'Select client from suggestions'; return; }
  const clientId = client.id;
  try {
    const body = { clientId, roomNumber, cancellationType: '', startDate, endDate };
    const res = await fetch(`/api/hotels/${hotelId}/reservations`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) });
    if (!res.ok) { el('reservationsStatus').textContent = 'Failed to create reservation'; return; }
    if (el('clientNameInput')) el('clientNameInput').value = ''; el('resRoomSelect').value = ''; el('startDate').value = ''; el('endDate').value = '';
    el('reservationsStatus').textContent = 'Reservation created';
    await loadRooms(); await loadReservations(); await loadAvailableRooms();
  } catch (e) { el('reservationsStatus').textContent = 'Error: ' + e.message }
}

async function loadClients() {
  const hotelId = state.hotelId; const dl = el('clientList'); const tbody = el('clientsTable'); if (dl) dl.innerHTML = ''; if (tbody) tbody.innerHTML = ''; if (!hotelId) return;
  try {
    const res = await fetch(`/api/hotels/${hotelId}/clients`);
    if (!res.ok) return;
    state.clients = await res.json();
    if (dl) state.clients.forEach(c => { const opt = document.createElement('option'); opt.value = c.name || ''; dl.appendChild(opt); });
    if (tbody) state.clients.forEach(c => { const tr = document.createElement('tr'); tr.innerHTML = `<td>${c.name || ''}</td><td>${c.email || ''}</td>`; tbody.appendChild(tr); });
  } catch (e) { }
}

async function loadAvailableRooms() {
  const hotelId = state.hotelId; const sel = el('resRoomSelect'); if (!sel || !hotelId) return; sel.innerHTML = '';
  try {
    const res = await fetch(`/api/hotels/${hotelId}/rooms/available`);
    if (!res.ok) return;
    const rooms = await res.json(); state.availableRooms = rooms || [];
    state.availableRooms.forEach(r => { const opt = document.createElement('option'); opt.value = r.roomNumber; opt.textContent = `${r.roomNumber} (${r.roomCategory || ''})`; sel.appendChild(opt); });
  } catch (e) { }
}
async function cancelReservation() {
  el('reservationsStatus').textContent = '';
  const rnEl = el('cancelReservationNumber'); const typeEl = el('cancelType');
  const reservationNumber = rnEl ? rnEl.value.trim() : ''; const cancellationType = typeEl ? typeEl.value.trim() : '';
  if (!reservationNumber || !cancellationType) { el('reservationsStatus').textContent = 'Reservation number and cancellation type are required'; return; }
  try {
    const res = await fetch(`/api/reservations/${encodeURIComponent(reservationNumber)}/cancel`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ cancellationType }) });
    if (!res.ok) { el('reservationsStatus').textContent = 'Failed to cancel reservation'; return; }
    if (rnEl) rnEl.value = ''; if (typeEl) typeEl.value = '';
    el('reservationsStatus').textContent = 'Reservation cancelled';
    await loadReservations(); await loadRooms(); await loadAvailableRooms();
  } catch (e) { el('reservationsStatus').textContent = 'Error: ' + e.message }
}

async function cancelReservationDirect(reservationNumber) {
  el('reservationsStatus').textContent = '';
  const cancellationType = 'customer_request';
  try {
    const res = await fetch(`/api/reservations/${encodeURIComponent(reservationNumber)}/cancel`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ cancellationType }) });
    if (!res.ok) { el('reservationsStatus').textContent = 'Failed to cancel reservation'; return; }
    el('reservationsStatus').textContent = 'Reservation cancelled';
    await loadReservations(); await loadRooms(); await loadAvailableRooms();
  } catch (e) { el('reservationsStatus').textContent = 'Error: ' + e.message }
}
