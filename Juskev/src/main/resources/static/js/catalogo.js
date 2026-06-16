/* ═══════════════════════════════════════
   JUSKEV — catalogo.js
   Lógica interactiva del catálogo Thymeleaf
═══════════════════════════════════════ */

/* ─── Carrito (persiste en localStorage para sobrevivir login/redirect) ─── */
let cartCatalogo = JSON.parse(localStorage.getItem('juskev_cart') || '[]');

function saveCart() {
  localStorage.setItem('juskev_cart', JSON.stringify(cartCatalogo));
  updateCartBadge();
}

function updateCartBadge() {
  const total = cartCatalogo.reduce(function(s, i) { return s + i.qty; }, 0);
  const badge = document.getElementById('cartCount');
  if (badge) badge.textContent = total;

  const totalPrice = cartCatalogo.reduce(function(s, i) { return s + i.precio * i.qty; }, 0);
  const totalEl = document.getElementById('cartTotal');
  if (totalEl) totalEl.textContent = '$' + totalPrice.toLocaleString('es-CO');

  renderCartItemsCatalogo();
}

document.querySelectorAll('.rating-selector').forEach(selector => {
    const stars = selector.querySelectorAll('.star');
    stars.forEach(star => {
        star.addEventListener('click', function() {
            const rating = Number(this.dataset.rating);
            stars.forEach(s => {
                const value = Number(s.dataset.rating);
                if(value <= rating){ s.classList.add('active'); }
                else { s.classList.remove('active'); }
            });
        });
    });
});








function toggleFav(id, btn) {
    btn.classList.toggle('active');
    btn.textContent = btn.classList.contains('active') ? '♥' : '♡';
}







/**
 * Agrega al carrito un producto del catálogo Thymeleaf.
 *
 * CLAVE: dos ítems del mismo producto pero distinta talla o color
 * se tratan como renglones SEPARADOS en el carrito.
 * La clave compuesta es: id + talla + color
 */
function addToCartFromPage(id, nombre, precio, imagenUrl, talla, color) {
  talla = talla || null;
  color = color || null;

  const existing = cartCatalogo.find(function(c) {
    return c.id === id && c.talla === talla && c.color === color;
  });

  if (existing) {
    existing.qty++;
  } else {
    cartCatalogo.push({
      id: id,
      nombre: nombre,
      precio: precio,
      imagenUrl: imagenUrl,
      talla: talla,
      color: color,
      qty: 1
    });
  }
  saveCart();
  showToastCatalogo('Producto agregado al carrito 🛒');
  openCart();
}

function removeFromCartCatalogo(id, talla, color) {
  talla = talla || null;
  color = color || null;
  cartCatalogo = cartCatalogo.filter(function(c) {
    return !(c.id === id && c.talla === talla && c.color === color);
  });
  saveCart();
}

function changeQtyCatalogo(id, talla, color, delta) {
  talla = talla || null;
  color = color || null;
  const item = cartCatalogo.find(function(c) {
    return c.id === id && c.talla === talla && c.color === color;
  });
  if (!item) return;
  item.qty += delta;
  if (item.qty <= 0) {
    removeFromCartCatalogo(id, talla, color);
    return;
  }
  saveCart();
}

function renderCartItemsCatalogo() {
  const container = document.getElementById('cartItems');
  if (!container) return;

  if (cartCatalogo.length === 0) {
    container.innerHTML = '<div class="cart-empty"><div class="cart-empty-icon">🛒</div><p>Tu carrito está vacío</p></div>';
    return;
  }

  container.innerHTML = cartCatalogo.map(function(item) {
    const imgHtml = item.imagenUrl
      ? '<img src="' + item.imagenUrl + '" alt="' + item.nombre + '" style="width:60px;height:60px;object-fit:cover;border-radius:4px;" onerror="this.style.display=\'none\'">'
      : '<div style="width:60px;height:60px;background:#f0ede8;border-radius:4px;display:flex;align-items:center;justify-content:center;font-size:1.5rem;">👕</div>';

    // Variantes visibles: talla y color
    var varianteParts = [];
    if (item.talla) varianteParts.push('Talla: ' + item.talla);
    if (item.color) {
      varianteParts.push('<span style="display:inline-block;width:12px;height:12px;border-radius:50%;background:' + item.color + ';border:1px solid #ccc;vertical-align:middle;margin-right:3px;"></span>Color');
    }
    var varianteHtml = varianteParts.length
      ? '<div class="cart-item-variant">' + varianteParts.join(' · ') + '</div>'
      : '';

    // IDs seguros para JS (talla y color pueden ser null)
    var tallaJs  = item.talla  ? "'" + item.talla  + "'" : 'null';
    var colorJs  = item.color  ? "'" + item.color  + "'" : 'null';

    return (
      '<div class="cart-item">' +
        '<div class="cart-item-img">' + imgHtml + '</div>' +
        '<div class="cart-item-info">' +
          '<div class="cart-item-name">' + item.nombre + '</div>' +
          varianteHtml +
          '<div class="cart-item-price">$' + (item.precio * item.qty).toLocaleString('es-CO') + '</div>' +
          '<div class="cart-qty">' +
            '<button class="qty-btn" onclick="changeQtyCatalogo(' + item.id + ',' + tallaJs + ',' + colorJs + ',-1); return false;">−</button>' +
            '<span class="qty-num">' + item.qty + '</span>' +
            '<button class="qty-btn" onclick="changeQtyCatalogo(' + item.id + ',' + tallaJs + ',' + colorJs + ',1); return false;">+</button>' +
          '</div>' +
          '<button class="remove-item" onclick="removeFromCartCatalogo(' + item.id + ',' + tallaJs + ',' + colorJs + '); return false;">Eliminar</button>' +
        '</div>' +
      '</div>'
    );
  }).join('');
}































/* ─── Modal de detalle de producto (carga datos via fetch) ─── */
let productoActualModal = null;

function openProductModal(id) {
  var modalImg  = document.getElementById('modalImg');
  var modalInfo = document.getElementById('modalInfo');
  if (!modalImg || !modalInfo) return;

  modalImg.innerHTML  = '<div style="display:flex;align-items:center;justify-content:center;height:100%;font-size:2rem;color:#ccc;">⏳</div>';
  




  modalInfo.innerHTML = '';
/*modalInfo.innerHTML =
        badgeHtml +
        '<div class="modal-brand">' + (p.marca || '') + '</div>' +
        '<div class="modal-title">' + p.nombre + '</div>' +
        '<div class="modal-price" style="margin:12px 0;">' + precioFmt + oldHtml + '</div>' +
        tallasHtml +
        coloresHtml +
        descHtml +
        '<button class="btn-cart-modal" id="btnAddModal" onclick="addFromModal(' + p.id + ', \'' +
          p.nombre.replace(/'/g, "\\'") + '\', ' + p.precio + ', \'' + (p.imagenUrl || '') + '\')">' +
          'AGREGAR AL CARRITO' +
        '</button>';

      cargarResenas(id);
    }
*/











  var modal = document.getElementById('productModal');
  if (modal) { modal.classList.add('open'); document.body.style.overflow = 'hidden'; }

  fetch('/api/producto/' + id)
    .then(function(r) { return r.json(); })
    .then(function(p) {
      productoActualModal = { id: p.id, nombre: p.nombre, precio: p.precio, imagenUrl: p.imagenUrl };

      // ── Galería de imágenes ──
      var imgs = p.imagenes && p.imagenes.length ? p.imagenes : (p.imagenUrl ? [p.imagenUrl] : []);
      if (imgs.length > 1) {
        var thumbs = imgs.map(function(url, i) {
          return '<img src="' + url + '" data-idx="' + i + '" class="modal-thumb' + (i === 0 ? ' active' : '') + '"'
            + ' onerror="this.src=\'/img/logo.png\'"'
            + ' onclick="switchModalImg(this, \'' + url + '\')" alt="foto ' + (i+1) + '">';
        }).join('');
        modalImg.innerHTML =
          '<img id="modalMainImg" src="' + imgs[0] + '" alt="' + p.nombre + '"'
          + ' style="width:100%;height:420px;object-fit:cover;border-radius:8px;margin-bottom:8px;"'
          + ' onerror="this.src=\'/img/logo.png\'">'+
          '<div class="modal-thumbs">' + thumbs + '</div>';
      } else {
        var src = imgs[0] || '/img/logo.png';
        modalImg.innerHTML =
          '<img src="' + src + '" alt="' + p.nombre + '"'
          + ' style="width:100%;height:100%;object-fit:cover;border-radius:8px;"'
          + ' onerror="this.src=\'/img/logo.png\'">';
      }

      // ── Badge ──
      var badgeHtml = p.badge
        ? '<span class="product-badge badge-sale" "">' + p.badge + '</span><br>'
        : '';

      // ── Precio ──
      var precioFmt = '$' + parseInt(p.precio).toLocaleString('es-CO');
      var oldHtml   = p.descuento > 0
        ? '<span style="font-size:1rem;color:#888;text-decoration:line-through;margin-left:8px;">'
          + '$' + parseInt(p.precioBase).toLocaleString('es-CO') + '</span>'
        : '';

      // ── Tallas ──
      var tallasHtml = '';
      if (p.tallas && p.tallas.length) {
        var chips = p.tallas.map(function(t) {
          return '<span class="modal-talla-chip" data-talla="' + t + '" onclick="seleccionarTalla(this)">' + t + '</span>';
        }).join('');
        tallasHtml = '<div class="modal-section"><div class="modal-section-label">TALLA</div>'
          + '<div class="modal-tallas">' + chips + '</div>'
          + '<small id="tallaError" style="color:#c0392b;display:none;">Selecciona una talla</small></div>';
      }

      // ── Colores ──
      var coloresHtml = '';
      if (p.colores && p.colores.length) {
        var swatches = p.colores.map(function(hex) {
          return '<span class="modal-color-swatch" data-color="' + hex + '" '
            + 'style="background:' + hex + '" onclick="seleccionarColor(this)" title="' + hex + '"></span>';
        }).join('');
        coloresHtml = '<div class="modal-section"><div class="modal-section-label">COLOR</div>'
          + '<div class="modal-colores">' + swatches + '</div>'
          + '<small id="colorError" style="color:#c0392b;display:none;">Selecciona un color</small></div>';
      }

      // ── Descripción ──
      var descHtml = p.descripcion
        ? '<div class="modal-section"><p style="font-size:0.9rem;color:#444;line-height:1.6;">' + p.descripcion + '</p></div>'
        : '';



     modalInfo.innerHTML =
        badgeHtml +
        '<div class="modal-brand">' + (p.marca || '') + '</div>' +
        '<div class="modal-title">' + p.nombre + '</div>' +
        '<div class="modal-price" style="margin:12px 0;">' + precioFmt + oldHtml + '</div>' +
        tallasHtml +
        coloresHtml +
        descHtml +
        '<button class="btn-cart-modal" id="btnAddModal" onclick="addFromModal(' + p.id + ', \'' +
          p.nombre.replace(/'/g, "\\'") + '\', ' + p.precio + ', \'' + (p.imagenUrl || '') + '\')">' +
          'AGREGAR AL CARRITO' +
        '</button>';

      cargarResenas(id);
    })




    .catch(function() {
      modalImg.innerHTML = '<div style="text-align:center;padding:2rem;font-size:3rem;">👕</div>';
      modalInfo.innerHTML = '<p style="color:#c00">Error cargando el producto.</p>';
    });
}

function switchModalImg(thumb, url) {
  document.querySelectorAll('.modal-thumb').forEach(function(t) { t.classList.remove('active'); });
  thumb.classList.add('active');
  var main = document.getElementById('modalMainImg');
  if (main) main.src = url;
}

function seleccionarTalla(el) {
  document.querySelectorAll('.modal-talla-chip').forEach(function(c) { c.classList.remove('active'); });
  el.classList.add('active');
  var err = document.getElementById('tallaError');
  if (err) err.style.display = 'none';
}

function seleccionarColor(el) {
  document.querySelectorAll('.modal-color-swatch').forEach(function(c) { c.classList.remove('active'); });
  el.classList.add('active');
  var err = document.getElementById('colorError');
  if (err) err.style.display = 'none';
}

function addFromModal(id, nombre, precio, imagenUrl) {
  var tallaChips    = document.querySelectorAll('.modal-talla-chip');
  var colorSwatches = document.querySelectorAll('.modal-color-swatch');
  var tallaActiva   = document.querySelector('.modal-talla-chip.active');
  var colorActivo   = document.querySelector('.modal-color-swatch.active');

  if (tallaChips.length > 0 && !tallaActiva) {
    document.getElementById('tallaError').style.display = 'inline';
    return;
  }
  if (colorSwatches.length > 0 && !colorActivo) {
    document.getElementById('colorError').style.display = 'inline';
    return;
  }

  var talla = tallaActiva ? tallaActiva.dataset.talla : null;
  var color = colorActivo ? colorActivo.dataset.color : null;

  addToCartFromPage(id, nombre, precio, imagenUrl, talla, color);
  closeModal();
}

function closeModal() {
  const modal = document.getElementById('productModal');
  if (modal) modal.classList.remove('open');
  document.body.style.overflow = '';
}

/* ─── Cart sidebar open/close ─── */
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

/* ─── Toast ─── */
function showToastCatalogo(msg) {
  let t = document.getElementById('toast');
  if (!t) {
    t = document.createElement('div');
    t.id = 'toast';
    t.style.cssText = 'position:fixed;bottom:24px;left:50%;transform:translateX(-50%);' +
      'background:#1a1a1a;color:#fff;padding:12px 24px;border-radius:24px;' +
      'font-size:0.9rem;z-index:9999;opacity:0;transition:opacity 0.3s;pointer-events:none;';
    document.body.appendChild(t);
  }
  t.textContent = msg;
  t.style.opacity = '1';
  setTimeout(function() { t.style.opacity = '0'; }, 2500);
}

/* ─── Cerrar modal al click en overlay ─── */
document.addEventListener('DOMContentLoaded', function() {
  const modal = document.getElementById('productModal');
  if (modal) {
    modal.addEventListener('click', function(e) {
      if (e.target === modal) closeModal();
    });
  }

  const cartOverlay = document.getElementById('cartOverlay');
  if (cartOverlay) {
    cartOverlay.addEventListener('click', closeCart);
  }

  updateCartBadge();
});

/* ─── MENÚ DE USUARIO (navbar dropdown) ─── */
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

/* ─── FINALIZAR COMPRA ─── */
function finalizarCompra() {
  if (cartCatalogo.length === 0) {
    showToastCatalogo('Tu carrito está vacío');
    return;
  }

  var authMeta = document.querySelector('meta[name="user-authenticated"]');
  var isAuthenticated = authMeta && authMeta.getAttribute('content') === 'true';
  if (!isAuthenticated) {
    closeCart();
    window.location.href = '/auth/login';
    return;
  }

  // Redirigir al checkout — el carrito ya está en sessionStorage
  closeCart();
  window.location.href = '/checkout';
}












//reseñas






function cargarResenas(productoId) {
    console.log('cargarResenas llamada con id:', productoId);
    fetch('/api/producto/' + productoId + '/resenas')
        .then(r => r.json())
        .then(resenas => {
            const contenedor = document.getElementById('modalResenas');
            if (!contenedor) return;

            const prom = resenas.length
                ? (resenas.reduce((s, r) => s + r.estrellas, 0) / resenas.length).toFixed(1)
                : null;

            let html = '<div class="resenas-header">'
                + '<span class="resenas-titulo">Reseñas</span>'
                + (prom ? '<span class="resenas-prom">★ ' + prom + ' (' + resenas.length + ')</span>' : '')
                + '</div>';

            if (resenas.length === 0) {
                html += '<p class="sin-resenas">Sé el primero en dejar una reseña.</p>';
            } else {
                resenas.forEach(r => {
                    const stars = '★'.repeat(r.estrellas) + '☆'.repeat(5 - r.estrellas);
                    const fecha = new Date(r.fechaResena).toLocaleDateString('es-CO');
                    html += '<div class="resena-item">'
                        + '<div class="resena-top">'
                        + '<span class="resena-autor">' + (r.nombreAutor || 'Anónimo') + '</span>'
                        + '<span class="resena-estrellas">' + stars + '</span>'
                        + '<span class="resena-fecha">' + fecha + '</span>'
                        + '</div>'
                        + '<p class="resena-comentario">' + (r.comentario || '') + '</p>'
                        + '</div>';
                });
            }

            const estaLogueado = document.querySelector('meta[name="user-authenticated"]')
                ?.getAttribute('content') === 'true';

            html += estaLogueado
                ? '<div class="resena-form">'
                    + '<div class="resena-form-titulo">Dejar una reseña</div>'
                    + '<div class="resena-stars" id="resenaStars">'
                    + [1,2,3,4,5].map(n => '<span class="rstar" data-v="' + n + '">★</span>').join('')
                    + '</div>'
                    + '<textarea id="resenaComentario" placeholder="Tu comentario..." class="form-input" rows="3" style="margin-bottom:8px"></textarea>'
                    + '<button onclick="enviarResena(' + productoId + ')" class="btn-cart-modal" style="font-size:13px;height:40px">Enviar reseña</button>'
                    + '</div>'


: '<div class="resena-login">'
    + '<p style="font-size:13px;color:#aaa;margin-bottom:10px">¿Quieres dejar una reseña?</p>'
    + '<a href="/auth/login" class="btn-login-resena">Inicia sesión</a>'
    + '</div>';

          
            contenedor.innerHTML = html;

            let seleccionada = 0;
            document.querySelectorAll('.rstar').forEach(s => {
                s.addEventListener('mouseover', () => {
                    document.querySelectorAll('.rstar').forEach(x =>
                        x.classList.toggle('lit', +x.dataset.v <= +s.dataset.v));
                });
                s.addEventListener('click', () => {
                    seleccionada = +s.dataset.v;
                    document.querySelectorAll('.rstar').forEach(x =>
                        x.classList.toggle('sel', +x.dataset.v <= seleccionada));
                });
            });
            document.getElementById('resenaStars')?.addEventListener('mouseleave', () => {
                document.querySelectorAll('.rstar').forEach(x => x.classList.remove('lit'));
            });
        });
}

function enviarResena(productoId) {
    const estrellas = document.querySelectorAll('.rstar.sel').length;
    const comentario = document.getElementById('resenaComentario').value.trim();
    if (!estrellas) { alert('Selecciona una calificación'); return; }
    if (!comentario) { alert('Escribe un comentario'); return; }

    fetch('/api/producto/' + productoId + '/resenas', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ estrellas, comentario })
    })
    .then(r => r.json())
    .then(() => cargarResenas(productoId))
    .catch(() => alert('Error al enviar la reseña'));
}