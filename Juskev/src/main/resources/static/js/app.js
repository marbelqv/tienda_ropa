/* ═══════════════════════════════════════
    JUSKEV— Tienda de Moda Masculina
   app.js
═══════════════════════════════════════ */

/* ─────────────────────────────────────
   BASE DE DATOS DE PRODUCTOS
───────────────────────────────────── */

/* ─────────────────────────────────────
   ILUSTRACIONES SVG DE PRODUCTOS
───────────────────────────────────── */
function getProductSVG(id, size = 'card') {
  const w = size === 'modal' ? 300 : 220;
  const h = size === 'modal' ? 360 : 280;
  const svgs = {
    1: `<svg viewBox="0 0 220 280" fill="none" xmlns="http://www.w3.org/2000/svg" width="${w}" height="${h}">
      <rect x="40" y="10" width="140" height="30" rx="2" fill="#c9a84c" opacity="0.1"/>
      <path d="M40 40 L10 100 L20 270 L200 270 L210 100 L180 40" fill="#2a2a2a"/>
      <path d="M90 40 L70 80 L50 140 L110 160 L170 140 L150 80 L130 40" fill="#353535"/>
      <path d="M110 40 L100 75 L107 160" stroke="#c9a84c" stroke-width="1.5" opacity="0.7"/>
      <path d="M110 40 L120 75 L113 160" stroke="#c9a84c" stroke-width="1.5" opacity="0.7"/>
      <circle cx="110" cy="28" r="22" fill="#2a2a2a"/>
      <rect x="95" y="160" width="30" height="110" rx="1" fill="#2a2a2a"/>
      <circle cx="110" cy="195" r="3" fill="#c9a84c" opacity="0.6"/>
      <circle cx="110" cy="215" r="3" fill="#c9a84c" opacity="0.6"/>
      <circle cx="110" cy="235" r="3" fill="#c9a84c" opacity="0.6"/>
    </svg>`,
    2: `<svg viewBox="0 0 220 280" fill="none" xmlns="http://www.w3.org/2000/svg" width="${w}" height="${h}">
      <rect x="80" y="60" width="60" height="160" rx="10" fill="#1a1a1a"/>
      <rect x="75" y="75" width="70" height="130" rx="8" fill="#252525"/>
      <circle cx="110" cy="140" r="48" fill="#1e1e1e" stroke="#c9a84c" stroke-width="2"/>
      <circle cx="110" cy="140" r="38" fill="#252525"/>
      <circle cx="110" cy="140" r="3" fill="#c9a84c"/>
      <line x1="110" y1="140" x2="110" y2="112" stroke="#c9a84c" stroke-width="2" stroke-linecap="round"/>
      <line x1="110" y1="140" x2="128" y2="147" stroke="#c9a84c" stroke-width="1.5" stroke-linecap="round"/>
      <circle cx="110" cy="105" r="2" fill="#c9a84c" opacity="0.6"/>
      <circle cx="110" cy="175" r="2" fill="#c9a84c" opacity="0.6"/>
      <circle cx="145" cy="140" r="2" fill="#c9a84c" opacity="0.6"/>
      <circle cx="75" cy="140" r="2" fill="#c9a84c" opacity="0.6"/>
    </svg>`,
    3: `<svg viewBox="0 0 220 280" fill="none" xmlns="http://www.w3.org/2000/svg" width="${w}" height="${h}">
      <path d="M60 50 L30 90 L20 270 L200 270 L190 90 L160 50" fill="#2a2a2a"/>
      <path d="M60 50 Q110 30 160 50 L170 80 L140 100 L110 95 L80 100 L50 80 Z" fill="#353535"/>
      <path d="M80 50 L85 90 L110 95 L135 90 L140 50" fill="#2d2d2d"/>
      <line x1="110" y1="95" x2="110" y2="270" stroke="#3a3a3a" stroke-width="1" opacity="0.5"/>
      <path d="M90 130 Q110 140 130 130" stroke="#c9a84c" stroke-width="1" opacity="0.5" fill="none"/>
      <rect x="88" y="60" width="44" height="3" rx="1.5" fill="#c9a84c" opacity="0.4"/>
    </svg>`,
    4: `<svg viewBox="0 0 220 280" fill="none" xmlns="http://www.w3.org/2000/svg" width="${w}" height="${h}">
      <ellipse cx="110" cy="200" rx="85" ry="20" fill="#1a1a1a"/>
      <path d="M30 160 Q30 100 110 90 Q190 100 190 160 L190 200 Q190 220 110 220 Q30 220 30 200 Z" fill="#2a1f15"/>
      <path d="M30 160 Q30 100 110 90 Q190 100 190 160" fill="none" stroke="#c9a84c" stroke-width="1.5" opacity="0.4"/>
      <path d="M60 110 L80 90 L110 85 L140 90 L160 110" fill="none" stroke="#3d2b1f" stroke-width="8" stroke-linecap="round"/>
      <ellipse cx="110" cy="91" rx="35" ry="8" fill="#1a1a1a"/>
      <path d="M75 91 Q110 80 145 91" fill="none" stroke="#2a2a2a" stroke-width="4"/>
    </svg>`,
    5: `<svg viewBox="0 0 220 280" fill="none" xmlns="http://www.w3.org/2000/svg" width="${w}" height="${h}">
      <path d="M50 70 L20 110 L15 270 L205 270 L200 110 L170 70" fill="#1a1a1a"/>
      <path d="M50 70 Q70 45 110 40 Q150 45 170 70 L165 100 L135 110 L110 107 L85 110 L55 100 Z" fill="#252525"/>
      <path d="M85 70 L90 105 L110 107 L130 105 L135 70" fill="#1e1e1e"/>
      <path d="M50 70 L30 100" stroke="#2a2a2a" stroke-width="8" stroke-linecap="round"/>
      <path d="M170 70 L190 100" stroke="#2a2a2a" stroke-width="8" stroke-linecap="round"/>
      <rect x="70" y="190" width="80" height="25" rx="4" fill="#252525"/>
      <line x1="110" y1="107" x2="110" y2="185" stroke="#252525" stroke-width="2"/>
    </svg>`,
    6: `<svg viewBox="0 0 220 280" fill="none" xmlns="http://www.w3.org/2000/svg" width="${w}" height="${h}">
      <rect x="20" y="115" width="180" height="50" rx="4" fill="#2a1f15"/>
      <rect x="20" y="120" width="180" height="40" rx="3" fill="#3d2b1f"/>
      <rect x="82" y="110" width="56" height="60" rx="3" fill="#252525"/>
      <rect x="90" y="118" width="40" height="44" rx="2" fill="#1a1a1a"/>
      <rect x="94" y="126" width="32" height="28" rx="1" fill="#252525"/>
      <line x1="110" y1="130" x2="110" y2="148" stroke="#c9a84c" stroke-width="2"/>
      <line x1="102" y1="140" x2="118" y2="140" stroke="#c9a84c" stroke-width="2"/>
      <rect x="20" y="125" width="8" height="30" rx="1" fill="#c9a84c" opacity="0.3"/>
      <rect x="192" y="125" width="8" height="30" rx="1" fill="#c9a84c" opacity="0.3"/>
    </svg>`,
    7: `<svg viewBox="0 0 220 280" fill="none" xmlns="http://www.w3.org/2000/svg" width="${w}" height="${h}">
      <rect x="15" y="70" width="190" height="140" rx="4" fill="#1a1a2d"/>
      <rect x="15" y="75" width="190" height="130" rx="3" fill="#1e2235"/>
      <line x1="15" y1="100" x2="205" y2="100" stroke="#2a2a3d" stroke-width="1"/>
      <line x1="15" y1="155" x2="205" y2="155" stroke="#2a2a3d" stroke-width="1"/>
      <rect x="15" y="210" width="190" height="60" rx="4" fill="#1a1a2d"/>
      <rect x="30" y="80" width="60" height="10" rx="2" fill="#2d2d45"/>
      <rect x="30" y="108" width="80" height="40" rx="2" fill="#252540"/>
      <rect x="120" y="108" width="75" height="40" rx="2" fill="#252540"/>
      <rect x="30" y="163" width="160" height="35" rx="2" fill="#252540"/>
    </svg>`,
    8: `<svg viewBox="0 0 220 280" fill="none" xmlns="http://www.w3.org/2000/svg" width="${w}" height="${h}">
      <rect x="55" y="100" width="110" height="80" rx="6" fill="#2a1f15"/>
      <rect x="58" y="103" width="104" height="74" rx="5" fill="#3d2b1f"/>
      <rect x="65" y="110" width="46" height="60" rx="3" fill="#2a2a2a"/>
      <rect x="115" y="110" width="40" height="25" rx="2" fill="#2a2a2a"/>
      <rect x="115" y="140" width="40" height="25" rx="2" fill="#2a2a2a"/>
      <rect x="158" y="118" width="18" height="44" rx="9" fill="#888" stroke="#999" stroke-width="1"/>
      <line x1="158" y1="140" x2="176" y2="140" stroke="#aaa" stroke-width="2"/>
      <rect x="65" y="113" width="46" height="12" rx="2" fill="#3d3d3d"/>
    </svg>`
  };
  return svgs[id] || svgs[1];
}

/* ─────────────────────────────────────
   UTILIDADES
───────────────────────────────────── */
function starsHTML(rating) {
  let s = '';
  for (let i = 1; i <= 5; i++) {
    s += i <= Math.round(rating) ? '★' : '☆';
  }
  return s;
}

function formatPrice(p) {
  return '$' + p.toLocaleString('es-CO');
}

/* ─────────────────────────────────────
   RENDER PRODUCTOS
───────────────────────────────────── */


function filterProducts(btn, cat) {
  document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');
  renderProducts(cat);
}







let favoritos = JSON.parse(localStorage.getItem('favoritos')) || [];

function toggleFav(id, e) {
  if (e) e.stopPropagation();

  if (favoritos.includes(id)) {
    favoritos = favoritos.filter(f => f !== id);
    showToast('Eliminado de favoritos');
  } else {
    favoritos.push(id);
    showToast('Agregado a favoritos');
  }

  localStorage.setItem('favoritos', JSON.stringify(favoritos));
  renderProducts(); // refresca para actualizar el icono
}





/* ─────────────────────────────────────
   MODAL DETALLE DEL PRODUCTO
───────────────────────────────────── */
function openModal(id, e) {
  if (e) e.stopPropagation();
  const p = products.find(x => x.id === id);
  if (!p) return;

  document.getElementById('modalImg').innerHTML = getProductSVG(id, 'modal');
  document.getElementById('modalInfo').innerHTML = `
    <div class="modal-brand">${p.brand}</div>
    <div class="modal-title">${p.name}</div>
    <div class="modal-stars">${starsHTML(p.rating)}
      <span style="color:var(--muted);font-size:0.8rem">${p.rating} / 5</span>
    </div>
    <div class="modal-review-count">${p.reviews} reseñas verificadas</div>
    <div class="modal-price">
      ${formatPrice(p.price)}
      ${p.oldPrice ? `<span style="font-size:1rem;color:var(--muted);text-decoration:line-through">${formatPrice(p.oldPrice)}</span>` : ''}
    </div>
    <div class="modal-desc">${p.desc}</div>
    ${p.sizes ? `
      <div class="size-label">Talla</div>
      <div class="size-options">
        ${p.sizes.map((s, i) => `<button class="size-btn${i === 0 ? ' active' : ''}" onclick="selectSize(this)">${s}</button>`).join('')}
      </div>` : ''}
    <div class="color-label">Color</div>
    <div class="color-options">
      ${p.colors.map((c, i) => `<div class="color-dot${i === 0 ? ' active' : ''}" style="background:${c}" onclick="selectColor(this)"></div>`).join('')}
    </div>
    <button class="btn-cart-modal" onclick="addToCart(${p.id})">Agregar al Carrito</button>
    <div class="reviews-section">
      <div class="reviews-title">Reseñas de clientes</div>
      ${p.reviewList.map(r => `
        <div class="review-item">
          <div class="review-header">
            <span class="reviewer-name">${r.name}</span>
            <span class="review-date">${r.date}</span>
          </div>
          <div class="review-stars">${starsHTML(r.stars)}</div>
          <div class="review-text">${r.text}</div>
        </div>
      `).join('')}
    </div>
  `;

  document.getElementById('productModal').classList.add('open');
  document.body.style.overflow = 'hidden';
}

function closeModal() {
  document.getElementById('productModal').classList.remove('open');
  document.body.style.overflow = '';
}

function selectSize(btn) {
  btn.closest('.size-options').querySelectorAll('.size-btn').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');
}

function selectColor(dot) {
  dot.closest('.color-options').querySelectorAll('.color-dot').forEach(d => d.classList.remove('active'));
  dot.classList.add('active');
}

/* ─────────────────────────────────────
   CARRITO DE COMPRAS
───────────────────────────────────── */
/* ─────────────────────────────────────
   CARRITO DE COMPRAS
   Delega a catalogo.js (sessionStorage)
───────────────────────────────────── */

// addToCart es usada por el modal de productos en la página de inicio.
// Delega a addToCartFromPage de catalogo.js para mantener un único carrito.
function addToCart(id, e) {
  if (e) e.stopPropagation();
  if (typeof addToCartFromPage === 'function') {
    const p = (typeof products !== 'undefined') ? products.find(x => x.id === id) : null;
    const nombre = p ? p.name : 'Producto';
    const precio = p ? p.price : 0;
    const imagen = p ? (p.imagenUrl || '') : '';
    addToCartFromPage(id, nombre, precio, imagen);
  }
}

function openCart() {
  const sidebar = document.getElementById('cartSidebar');
  const overlay = document.getElementById('cartOverlay');
  if (sidebar) sidebar.classList.add('open');
  if (overlay) overlay.classList.add('open');
  document.body.style.overflow = 'hidden';
}

function closeCart() {
  const sidebar = document.getElementById('cartSidebar');
  const overlay = document.getElementById('cartOverlay');
  if (sidebar) sidebar.classList.remove('open');
  if (overlay) overlay.classList.remove('open');
  document.body.style.overflow = '';
}

/* ─────────────────────────────────────
   INICIO DE SESIÓN
───────────────────────────────────── */
function openLogin() {
  document.getElementById('loginOverlay').classList.add('open');
  document.body.style.overflow = 'hidden';
}

function closeLogin() {
  document.getElementById('loginOverlay').classList.remove('open');
  document.body.style.overflow = '';
}

function switchTab(tab) {
  document.querySelectorAll('.login-tab').forEach(t => t.classList.remove('active'));
  document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
  document.getElementById(`tabBtn${tab.charAt(0).toUpperCase() + tab.slice(1)}`).classList.add('active');
  document.getElementById(`tab-${tab}`).classList.add('active');
}

function fakeLogin() {
  closeLogin();
  document.querySelector('.btn-login').textContent = 'Mi Cuenta';
  showToast('¡Bienvenido de vuelta!');
}






/* ─────────────────────────────────────
   CARRUSEL
───────────────────────────────────── */
let currentSlide = 0;
const totalSlides = 3;

function goSlide(n) {
  currentSlide = n;
  document.getElementById('slides').style.transform = `translateX(-${n * 100}%)`;
  document.querySelectorAll('.dot').forEach((d, i) => {
    d.classList.toggle('active', i === n);
  });
}

function nextSlide() { goSlide((currentSlide + 1) % totalSlides); }
function prevSlide() { goSlide((currentSlide - 1 + totalSlides) % totalSlides); }

// Auto-avance del carrusel cada 5 segundos
setInterval(nextSlide, 5000);

/* ─────────────────────────────────────
   TOAST NOTIFICATION
───────────────────────────────────── */
function showToast(msg) {
  const t = document.getElementById('toast');
  t.textContent = msg;
  t.classList.add('show');
  setTimeout(() => t.classList.remove('show'), 2500);
}

/* ─────────────────────────────────────
   EVENTOS DE CIERRE (CLICK EN OVERLAY)
───────────────────────────────────── */
document.addEventListener('DOMContentLoaded', function() {
  const productModal = document.getElementById('productModal');
  if (productModal) {
    productModal.addEventListener('click', function(e) {
      if (e.target === this) closeModal();
    });
  }

  const loginOverlay = document.getElementById('loginOverlay');
  if (loginOverlay) {
    loginOverlay.addEventListener('click', function(e) {
      if (e.target === this) closeLogin();
    });
  }

  // renderProducts solo existe en páginas que usan productos estáticos
  if (typeof renderProducts === 'function') renderProducts();
});

/* ─────────────────────────────────────
   MENÚ DE USUARIO (navbar dropdown)
   Funciona en todas las páginas
───────────────────────────────────── */
function toggleUserMenu(btn) {
  var wrapper = btn.closest('.user-menu-wrapper');
  if (!wrapper) return;
  var dropdown = wrapper.querySelector('.user-dropdown');
  if (!dropdown) return;
  var isOpen = dropdown.classList.contains('open');
  document.querySelectorAll('.user-dropdown.open').forEach(function(d) {
    d.classList.remove('open');
  });
  if (!isOpen) dropdown.classList.add('open');
}

document.addEventListener('click', function(e) {
  if (!e.target.closest('.user-menu-wrapper')) {
    document.querySelectorAll('.user-dropdown.open').forEach(function(d) {
      d.classList.remove('open');
    });
  }
});