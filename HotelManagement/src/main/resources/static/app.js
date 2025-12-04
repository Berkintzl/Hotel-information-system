const state={user:null,hotelId:null};
const el=(id)=>document.getElementById(id);
const show=(id)=>el(id).classList.remove('hidden');
const hide=(id)=>el(id).classList.add('hidden');

async function login(){
  el('loginStatus').textContent='';
  const username=el('username').value.trim();
  const password=el('password').value.trim();
  if(!username||!password){el('loginStatus').textContent='Username and password are required';return}
  try{
    const res=await fetch('/api/auth/login',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({username,password})});
    if(!res.ok){el('loginStatus').textContent='Login failed';return}
    const user=await res.json();
    state.user=user; state.hotelId=user.hotelId; el('hotelId').value=state.hotelId;
    el('sessionInfo').textContent=`User: ${user.username} | Role: ${user.roleName||''}`;
    hide('login-section'); show('session-section'); show('rooms-section'); show('reservations-section');
    await loadRooms();
  }catch(e){el('loginStatus').textContent='Error: '+e.message}
}

function logout(){
  state.user=null; state.hotelId=null;
  el('username').value=''; el('password').value=''; el('sessionInfo').textContent=''; el('hotelId').value='';
  hide('session-section'); hide('rooms-section'); hide('reservations-section'); show('login-section');
}

function syncHotelId(){
  const v=parseInt(el('hotelId').value,10); if(!isNaN(v)) state.hotelId=v;
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
      tr.innerHTML=`<td>${r.id||''}</td><td>${r.roomNumber||''}</td><td>${r.roomCategory||''}</td><td>${r.isOccupied?'Yes':'No'}</td><td>${r.reservationNumber||''}</td><td><button data-room="${r.roomNumber}">Delete</button></td>`;
      tbody.appendChild(tr);
    });
    tbody.querySelectorAll('button[data-room]').forEach(b=>b.addEventListener('click',async(e)=>{
      const rn=e.target.getAttribute('data-room');
      await deleteRoom(rn);
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
    await loadRooms();
  }catch(e){el('roomsStatus').textContent='Error: '+e.message}
}

async function deleteRoom(roomNumber){
  el('roomsStatus').textContent='';
  const hotelId=state.hotelId; if(!hotelId||!roomNumber){el('roomsStatus').textContent='Hotel ID and room number are required';return}
  try{
    const res=await fetch(`/api/hotels/${hotelId}/rooms/${encodeURIComponent(roomNumber)}`,{method:'DELETE'});
    if(!res.ok){el('roomsStatus').textContent='Failed to delete room';return}
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
      tr.innerHTML=`<td>${r.reservationNumber||''}</td><td>${r.roomNumber||''}</td><td>${r.startDate||''}</td><td>${r.endDate||''}</td><td>${r.isCancelled?'Yes':'No'}</td>`;
      tbody.appendChild(tr);
    });
  }catch(e){el('reservationsStatus').textContent='Error: '+e.message}
}

function bind(){
  el('loginBtn').addEventListener('click',login);
  el('logoutBtn').addEventListener('click',logout);
  el('hotelId').addEventListener('change',()=>{syncHotelId();loadRooms()});
  el('addRoomBtn').addEventListener('click',addRoom);
  el('loadReservationsBtn').addEventListener('click',loadReservations);
}

document.addEventListener('DOMContentLoaded',bind);
