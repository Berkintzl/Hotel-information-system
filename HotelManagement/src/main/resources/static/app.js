const state={user:null,hotelId:null,clients:[],availableRooms:[]};
const el=(id)=>document.getElementById(id);
const show=(id)=>el(id).classList.remove('hidden');
const hide=(id)=>el(id).classList.add('hidden');

function setNavAuthLoggedIn(loggedIn){
  if(el('openLoginBtn')){ loggedIn? hide('openLoginBtn') : show('openLoginBtn'); }
  if(el('openOwnerSignupBtn')){ loggedIn? hide('openOwnerSignupBtn') : show('openOwnerSignupBtn'); }
  if(el('logoutNavBtn')){ loggedIn? show('logoutNavBtn') : hide('logoutNavBtn'); }
}

function switchView(view){
  if(view==='register'){
    show('register-section');
    hide('login-section');
    hide('session-section');
    hide('rooms-section');
    hide('reservations-section');
    hide('managers-section'); hide('receptionists-section'); if(el('sidebar')) hide('sidebar');
    setNavAuthLoggedIn(false);
  } else if(view==='login'){
    show('login-section');
    hide('register-section');
    hide('session-section');
    hide('rooms-section');
    hide('reservations-section');
    hide('managers-section'); hide('receptionists-section'); if(el('sidebar')) hide('sidebar');
    setNavAuthLoggedIn(false);
  }
}

function switchMain(view){
  const map={rooms:'rooms-section',reservations:'reservations-section',managers:'managers-section',receptionists:'receptionists-section'};
  Object.values(map).forEach(id=>{ if(el(id)) hide(id); });
  const target=map[view];
  if(el(target)) show(target);
  if(view==='rooms'){ loadRooms(); }
  if(view==='reservations'){ loadReservations(); loadClients(); loadAvailableRooms(); }
}

function saveSessionToStorage(){
  try{
    const user=state.user; const hotelId=state.hotelId;
    if(user) localStorage.setItem('hotelAppSession', JSON.stringify({user,hotelId}));
  }catch(e){}
}

function restoreSessionFromStorage(){
  try{
    const raw=localStorage.getItem('hotelAppSession');
    if(!raw) return;
    const obj=JSON.parse(raw)||{};
    if(!obj.user) return;
    state.user=obj.user; state.hotelId=obj.hotelId;
    setupHotelAccessUI(state.user);
    el('sessionInfo').textContent=`User: ${state.user.username} | Role: ${state.user.roleName||''}`;
    hide('login-section'); hide('register-section'); show('session-section'); if(el('sidebar')) show('sidebar'); switchMain('rooms');
    setNavAuthLoggedIn(true);
  }catch(e){}
}

async function login(){
  el('loginStatus').textContent='';
  const username=el('username').value.trim();
  const password=el('password').value.trim();
  if(!username||!password){el('loginStatus').textContent='Username and password are required';return}
  try{
    const res=await fetch('/api/auth/login',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({username,password})});
    if(!res.ok){el('loginStatus').textContent='Login failed';return}
  const user=await res.json();
    state.user=user; state.hotelId=user.hotelId; saveSessionToStorage(); setupHotelAccessUI(user); if(el('hotelSelect')) el('hotelSelect').value=String(state.hotelId);
    el('sessionInfo').textContent=`User: ${user.username} | Role: ${user.roleName||''}`;
    hide('login-section'); show('session-section'); if(el('sidebar')) show('sidebar'); switchMain('rooms');
    await loadRooms(); await loadClients(); await loadAvailableRooms();
    setNavAuthLoggedIn(true);
  }catch(e){el('loginStatus').textContent='Error: '+e.message}
}

function logout(){
  state.user=null; state.hotelId=null;
  try{ localStorage.removeItem('hotelAppSession'); }catch(e){}
  el('username').value=''; el('password').value=''; el('sessionInfo').textContent=''; if(el('hotelSelect')){ el('hotelSelect').value=''; hide('hotelSelect'); } if(el('hotelId')){ el('hotelId').value=''; el('hotelId').disabled=false; show('hotelId'); }
  hide('session-section'); hide('rooms-section'); hide('reservations-section'); hide('managers-section'); hide('receptionists-section'); if(el('sidebar')) hide('sidebar'); show('login-section');
  setNavAuthLoggedIn(false);
}

function syncHotelFromSelect(){
  const v=parseInt(el('hotelSelect').value,10); if(!isNaN(v)) state.hotelId=v;
}

async function loadHotels(){
  const sel=el('hotelSelect'); if(!sel) return;
  sel.innerHTML='';
  try{
    const res=await fetch('/api/hotels');
    if(!res.ok) return;
    const hotels=await res.json();
    hotels.forEach(h=>{
      const opt=document.createElement('option');
      opt.value=String(h.id);
      opt.textContent=h.name||('Hotel '+h.id);
      sel.appendChild(opt);
    });
    if(state.hotelId){ sel.value=String(state.hotelId); }
    else if(hotels.length>0){ state.hotelId=hotels[0].id; sel.value=String(state.hotelId); }
  }catch(e){}
}

function setupHotelAccessUI(user){
  const role=(user&&user.roleName||'').toLowerCase();
  if(role==='admin'){ hide('hotelId'); show('hotelSelect'); }
  else{ show('hotelId'); hide('hotelSelect'); if(el('hotelId')){ el('hotelId').disabled=true; el('hotelId').value=String(state.hotelId||''); } }

  if(el('sidebar')) show('sidebar');
  if(el('navManagersBtn')) el('navManagersBtn').style.display=((role==='admin'||role==='owner')?'':'none');
  if(el('navReceptionistsBtn')) el('navReceptionistsBtn').style.display=((role==='admin'||role==='owner'||role==='manager')?'':'none');
}

async function loadRooms(){
  el('roomsStatus').textContent='';
  const hotelId=state.hotelId; if(!hotelId){el('roomsStatus').textContent='Hotel ID is required';return}
  try{
    const res=await fetch(`/api/hotels/${hotelId}/rooms`);
    if(!res.ok){el('roomsStatus').textContent='Failed to load rooms';return}
    const rooms=await res.json();
    const tbody=el('roomsTable');
    tbody.innerHTML='';
    rooms.forEach(r=>{
      const tr=document.createElement('tr');
      const cancelBtn = (r.isOccupied && r.reservationNumber)? `<button class="btn btn-warning btn-sm" data-cancel="${r.reservationNumber}" data-room="${r.roomNumber}">Cancel Reservation</button>` : '';
      tr.innerHTML=`<td>${r.id||''}</td><td>${r.roomNumber||''}</td><td>${r.roomCategory||''}</td><td>${r.isOccupied?'Yes':'No'}</td><td>${r.reservationNumber||''}</td><td><div class="btn-group"><button class="btn btn-danger btn-sm" data-room="${r.roomNumber}">Delete</button>${cancelBtn}</div></td>`;
      tbody.appendChild(tr);
    });
    tbody.querySelectorAll('button[data-room]').forEach(b=>b.addEventListener('click',async(e)=>{
      const rn=e.target.getAttribute('data-room');
      await deleteRoom(rn);
    }));
    tbody.querySelectorAll('button[data-cancel]').forEach(b=>b.addEventListener('click',async(e)=>{
      const resNum=e.target.getAttribute('data-cancel');
      await cancelReservationDirect(resNum);
    }));
  }catch(e){el('roomsStatus').textContent='Error: '+e.message}
}

async function addRoom(){
  el('roomsStatus').textContent='';
  const hotelId=state.hotelId; const roomNumber=el('roomNumber').value.trim(); const roomCategory=el('roomCategory').value.trim();
  if(!hotelId||!roomNumber||!roomCategory){el('roomsStatus').textContent='Hotel ID, room number and category are required';return}
  try{
    const res=await fetch(`/api/hotels/${hotelId}/rooms`,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({roomNumber,roomCategory})});
    if(res.status===409){el('roomsStatus').textContent='Room number already exists';return}
    if(!res.ok){el('roomsStatus').textContent='Failed to add room';return}
    el('roomNumber').value=''; el('roomCategory').value='';
    await loadRooms(); await loadAvailableRooms();
  }catch(e){el('roomsStatus').textContent='Error: '+e.message}
}

async function deleteRoom(roomNumber){
  el('roomsStatus').textContent='';
  const hotelId=state.hotelId; if(!hotelId||!roomNumber){el('roomsStatus').textContent='Hotel ID and room number are required';return}
  try{
    const res=await fetch(`/api/hotels/${hotelId}/rooms/${encodeURIComponent(roomNumber)}`,{method:'DELETE'});
    if(!res.ok){
      let msg='Failed to delete room';
      try{ msg=await res.text(); }catch(e){}
      el('roomsStatus').textContent=msg||'Failed to delete room';
      return;
    }
    await loadRooms();
  }catch(e){el('roomsStatus').textContent='Error: '+e.message}
}

async function loadReservations(){
  el('reservationsStatus').textContent='';
  const hotelId=state.hotelId; if(!hotelId){el('reservationsStatus').textContent='Hotel ID is required';return}
  try{
    const res=await fetch(`/api/hotels/${hotelId}/reservations`);
    if(!res.ok){el('reservationsStatus').textContent='Failed to load reservations';return}
    const arr=await res.json();
    const tbody=el('reservationsTable');
    tbody.innerHTML='';
    arr.forEach(r=>{
      const tr=document.createElement('tr');
      const canBtn = r.isCancelled? '' : `<div class="btn-group"><button class="btn btn-warning btn-sm" data-res="${r.reservationNumber}">Cancel</button></div>`;
      tr.innerHTML=`<td>${r.reservationNumber||''}</td><td>${r.roomNumber||''}</td><td>${r.startDate||''}</td><td>${r.endDate||''}</td><td>${r.isCancelled?'Yes':'No'}</td><td>${canBtn}</td>`;
      tbody.appendChild(tr);
    });
    tbody.querySelectorAll('button[data-res]').forEach(b=>b.addEventListener('click',async(e)=>{
      const rn=e.target.getAttribute('data-res');
      await cancelReservationDirect(rn);
    }));
  }catch(e){el('reservationsStatus').textContent='Error: '+e.message}
}

function bind(){
  el('loginBtn').addEventListener('click',login);
  el('logoutBtn').addEventListener('click',logout);
  if(el('hotelSelect')) el('hotelSelect').addEventListener('change',()=>{syncHotelFromSelect();loadRooms();loadClients();loadAvailableRooms();loadReservations();});
  el('addRoomBtn').addEventListener('click',addRoom);
  el('loadReservationsBtn').addEventListener('click',loadReservations);
  if(el('signupOwnerBtn')) el('signupOwnerBtn').addEventListener('click',signupOwner);
  if(el('openOwnerSignupBtn')) el('openOwnerSignupBtn').addEventListener('click',()=>{ switchView('register'); });
  if(el('openLoginBtn')) el('openLoginBtn').addEventListener('click',()=>{ switchView('login'); });
  if(el('logoutNavBtn')) el('logoutNavBtn').addEventListener('click',logout);
  if(el('navRoomsBtn')) el('navRoomsBtn').addEventListener('click',()=>{ switchMain('rooms'); });
  if(el('navReservationsBtn')) el('navReservationsBtn').addEventListener('click',()=>{ switchMain('reservations'); });
  if(el('navManagersBtn')) el('navManagersBtn').addEventListener('click',()=>{ switchMain('managers'); });
  if(el('navReceptionistsBtn')) el('navReceptionistsBtn').addEventListener('click',()=>{ switchMain('receptionists'); });
  if(el('addManagerBtn')) el('addManagerBtn').addEventListener('click',addManager);
  if(el('addReceptionistBtn')) el('addReceptionistBtn').addEventListener('click',addReceptionist);
  if(el('addClientBtn')) el('addClientBtn').addEventListener('click',addClient);
  if(el('createReservationBtn')) el('createReservationBtn').addEventListener('click',createReservation);
}

document.addEventListener('DOMContentLoaded',async()=>{ bind(); await loadHotels(); restoreSessionFromStorage(); if(state.user){ await loadRooms(); await loadClients(); await loadAvailableRooms(); } });

async function signupOwner(){
  el('registerStatus').textContent='';
  const name=el('regHotelName').value.trim();
  const address=el('regHotelAddress').value.trim();
  const username=el('regUsername').value.trim();
  const password=el('regPassword').value.trim();
  if(!name||!address||!username||!password){ el('registerStatus').textContent='All fields are required'; return; }
  try{
    const res=await fetch('/api/auth/register/owner',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({name,address,username,password})});
    if(!res.ok){ el('registerStatus').textContent='Sign up failed'; return; }
    el('registerStatus').textContent='Owner registered';
    el('username').value=username; el('password').value=password; await login();
  }catch(e){ el('registerStatus').textContent='Error: '+e.message }
}

async function addManager(){
  if(el('managerStatus')) el('managerStatus').textContent='';
  const hotelId=state.hotelId; const username=el('managerUsername').value.trim(); const password=el('managerPassword').value.trim();
  if(!hotelId||!username||!password){ if(el('managerStatus')) el('managerStatus').textContent='Hotel ID, username and password are required'; return; }
  try{
    const res=await fetch(`/api/hotels/${hotelId}/managers`,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({username,password})});
    if(!res.ok){ if(el('managerStatus')) el('managerStatus').textContent='Failed to add manager'; return; }
    el('managerUsername').value=''; el('managerPassword').value=''; if(el('managerStatus')) el('managerStatus').textContent='Manager added';
  }catch(e){ if(el('managerStatus')) el('managerStatus').textContent='Error: '+e.message }
}

async function addReceptionist(){
  if(el('recStatus')) el('recStatus').textContent='';
  const hotelId=state.hotelId; const username=el('recUsername').value.trim(); const password=el('recPassword').value.trim();
  if(!hotelId||!username||!password){ if(el('recStatus')) el('recStatus').textContent='Hotel ID, username and password are required'; return; }
  try{
    const res=await fetch(`/api/hotels/${hotelId}/receptionists`,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({username,password})});
    if(!res.ok){ if(el('recStatus')) el('recStatus').textContent='Failed to add receptionist'; return; }
    el('recUsername').value=''; el('recPassword').value=''; if(el('recStatus')) el('recStatus').textContent='Receptionist added';
  }catch(e){ if(el('recStatus')) el('recStatus').textContent='Error: '+e.message }
}

async function addClient(){
  el('reservationsStatus').textContent='';
  const hotelId=state.hotelId; const name=el('clientNameAdd')?el('clientNameAdd').value.trim():''; const email=el('clientEmail').value.trim();
  if(!hotelId||!name){ el('reservationsStatus').textContent='Hotel ID and client name are required'; return; }
  try{
    const res=await fetch(`/api/hotels/${hotelId}/clients`,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({name,email})});
    if(!res.ok){ el('reservationsStatus').textContent='Failed to add client'; return; }
    el('clientEmail').value=''; el('reservationsStatus').textContent=`Client created`; if(el('clientNameAdd')) el('clientNameAdd').value=''; await loadClients();
  }catch(e){ el('reservationsStatus').textContent='Error: '+e.message }
}

async function createReservation(){
  el('reservationsStatus').textContent='';
  const hotelId=state.hotelId;
  const clientName=el('clientNameInput')?el('clientNameInput').value.trim():'';
  const roomNumber=el('resRoomSelect').value;
  const startDate=el('startDate').value;
  const endDate=el('endDate').value;
  if(!hotelId||!clientName||!roomNumber||!startDate||!endDate){ el('reservationsStatus').textContent='All reservation fields are required'; return; }
  const client=(state.clients||[]).find(c=> (c.name||'').toLowerCase()===clientName.toLowerCase());
  if(!client){ el('reservationsStatus').textContent='Select client from suggestions'; return; }
  const clientId=client.id;
  try{
    const body={clientId,roomNumber,cancellationType:'',startDate,endDate};
    const res=await fetch(`/api/hotels/${hotelId}/reservations`,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(body)});
    if(!res.ok){ el('reservationsStatus').textContent='Failed to create reservation'; return; }
    if(el('clientNameInput')) el('clientNameInput').value=''; el('resRoomSelect').value=''; el('startDate').value=''; el('endDate').value='';
    el('reservationsStatus').textContent='Reservation created';
    await loadRooms(); await loadReservations(); await loadAvailableRooms();
  }catch(e){ el('reservationsStatus').textContent='Error: '+e.message }
}

async function loadClients(){
  const hotelId=state.hotelId; const dl=el('clientList'); const tbody=el('clientsTable'); if(dl) dl.innerHTML=''; if(tbody) tbody.innerHTML=''; if(!hotelId) return;
  try{
    const res=await fetch(`/api/hotels/${hotelId}/clients`);
    if(!res.ok) return;
    state.clients=await res.json();
    if(dl) state.clients.forEach(c=>{ const opt=document.createElement('option'); opt.value=c.name||''; dl.appendChild(opt); });
    if(tbody) state.clients.forEach(c=>{ const tr=document.createElement('tr'); tr.innerHTML=`<td>${c.name||''}</td><td>${c.email||''}</td>`; tbody.appendChild(tr); });
  }catch(e){}
}

async function loadAvailableRooms(){
  const hotelId=state.hotelId; const sel=el('resRoomSelect'); if(!sel||!hotelId) return; sel.innerHTML='';
  try{
    const res=await fetch(`/api/hotels/${hotelId}/rooms/available`);
    if(!res.ok) return;
    const rooms=await res.json(); state.availableRooms=rooms||[];
    state.availableRooms.forEach(r=>{ const opt=document.createElement('option'); opt.value=r.roomNumber; opt.textContent=`${r.roomNumber} (${r.roomCategory||''})`; sel.appendChild(opt); });
  }catch(e){}
}
async function cancelReservation(){
  el('reservationsStatus').textContent='';
  const rnEl=el('cancelReservationNumber'); const typeEl=el('cancelType');
  const reservationNumber=rnEl?rnEl.value.trim():''; const cancellationType=typeEl?typeEl.value.trim():'';
  if(!reservationNumber||!cancellationType){ el('reservationsStatus').textContent='Reservation number and cancellation type are required'; return; }
  try{
    const res=await fetch(`/api/reservations/${encodeURIComponent(reservationNumber)}/cancel`,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({cancellationType})});
    if(!res.ok){ el('reservationsStatus').textContent='Failed to cancel reservation'; return; }
    if(rnEl) rnEl.value=''; if(typeEl) typeEl.value='';
    el('reservationsStatus').textContent='Reservation cancelled';
    await loadReservations(); await loadRooms(); await loadAvailableRooms();
  }catch(e){ el('reservationsStatus').textContent='Error: '+e.message }
}

async function cancelReservationDirect(reservationNumber){
  el('reservationsStatus').textContent='';
  const cancellationType='customer_request';
  try{
    const res=await fetch(`/api/reservations/${encodeURIComponent(reservationNumber)}/cancel`,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({cancellationType})});
    if(!res.ok){ el('reservationsStatus').textContent='Failed to cancel reservation'; return; }
    el('reservationsStatus').textContent='Reservation cancelled';
    await loadReservations(); await loadRooms(); await loadAvailableRooms();
  }catch(e){ el('reservationsStatus').textContent='Error: '+e.message }
}
