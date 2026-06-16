/* ═══════════════════════════════════════
   JUSKEV — checkout.js
   Maneja: resumen del carrito, validación,
   y envío del pedido con datos de envío.
═══════════════════════════════════════ */

/* ── Leer carrito desde localStorage (mismo formato que catalogo.js) ── */
var cartCatalogo = JSON.parse(localStorage.getItem('juskev_cart') || '[]');

/* ── Render resumen al cargar ── */
document.addEventListener('DOMContentLoaded', function () {
  // Re-leer el carrito aquí por si acaso el parse inicial fue antes del DOM
  cartCatalogo = JSON.parse(localStorage.getItem('juskev_cart') || '[]');

  renderSummary();
  setupPaymentToggle();

  // Si el carrito está vacío, redirigir al catálogo con un pequeño delay
  // para dar tiempo a que localStorage esté disponible tras un redirect de login
  if (cartCatalogo.length === 0) {
    showToast('Tu carrito está vacío');
    setTimeout(function () {
      window.location.href = '/catalogo';
    }, 2000);
  }
});

/* ── Renderiza los items del resumen ── */
function renderSummary() {
  var container = document.getElementById('summaryItems');
  if (!container) return;

  if (cartCatalogo.length === 0) {
    container.innerHTML = '<div class="summary-loading">Tu carrito está vacío</div>';
    return;
  }

  var total = 0;
  var html = '';

  cartCatalogo.forEach(function (item) {
    var subtotal = item.precio * item.qty;
    total += subtotal;

    // Imagen
    var imgHtml = item.imagenUrl
      ? '<img class="summary-item-img" src="' + item.imagenUrl + '" alt="' + item.nombre + '" onerror="this.style.display=\'none\'">'
      : '<div class="summary-item-placeholder">👕</div>';

    // Variantes
    var varianteParts = [];
    if (item.talla) varianteParts.push('Talla: ' + item.talla);
    if (item.color) {
      varianteParts.push(
        '<span class="color-dot" style="background:' + item.color + ';"></span> Color'
      );
    }
    var varianteHtml = varianteParts.length
      ? '<div class="summary-item-variant">' + varianteParts.join(' · ') + '</div>'
      : '';

    html +=
      '<div class="summary-item">' +
        imgHtml +
        '<div class="summary-item-info">' +
          '<div class="summary-item-name">' + item.nombre + '</div>' +
          varianteHtml +
          '<div class="summary-item-qty">Cantidad: ' + item.qty + '</div>' +
        '</div>' +
        '<div class="summary-item-price">$' + subtotal.toLocaleString('es-CO') + '</div>' +
      '</div>';
  });

  container.innerHTML = html;

  // Totales
  document.getElementById('summarySubtotal').textContent = '$' + total.toLocaleString('es-CO');
  document.getElementById('summaryTotal').textContent    = '$' + total.toLocaleString('es-CO');
}

/* ── Toggle info bancaria ── */
function setupPaymentToggle() {
  var radios   = document.querySelectorAll('input[name="metodoPago"]');
  var bankInfo = document.getElementById('bankInfo');

  radios.forEach(function (radio) {
    radio.addEventListener('change', function () {
      if (radio.value === 'TRANSFERENCIA') {
        bankInfo.classList.add('visible');
      } else {
        bankInfo.classList.remove('visible');
      }
    });
  });
}

/* ── Validar campos ── */
function validarFormulario() {
  var direccion = document.getElementById('direccion').value.trim();
  var telefono  = document.getElementById('telefono').value.trim();

  var ok = true;

  if (!direccion) {
    document.getElementById('direccion').classList.add('error');
    showToast('⚠️ Por favor ingresa tu dirección de envío');
    ok = false;
  } else {
    document.getElementById('direccion').classList.remove('error');
  }

  if (!telefono || telefono.length < 7) {
    document.getElementById('telefono').classList.add('error');
    if (ok) showToast('⚠️ Por favor ingresa un teléfono válido');
    ok = false;
  } else {
    document.getElementById('telefono').classList.remove('error');
  }

  return ok;
}

/* ── Confirmar pedido ── */
function confirmarPedido() {
  if (!validarFormulario()) return;

  var direccion    = document.getElementById('direccion').value.trim();
  var telefono     = document.getElementById('telefono').value.trim();
  var notas        = document.getElementById('notas').value.trim();
  var metodoPago   = document.querySelector('input[name="metodoPago"]:checked').value;

  var csrfMeta   = document.querySelector('meta[name="_csrf"]');
  var headerMeta = document.querySelector('meta[name="_csrf_header"]');
  var csrfToken  = csrfMeta   ? csrfMeta.getAttribute('content')   : null;
  var csrfHeader = headerMeta ? headerMeta.getAttribute('content') : 'X-CSRF-TOKEN';

  // Construir items
  var items = cartCatalogo.map(function (item) {
    return {
      productoId:     item.id,
      cantidad:       item.qty,
      precioUnitario: item.precio,
      talla:          item.talla  || null,
      color:          item.color  || null
    };
  });

  var payload = {
    direccionEnvio: direccion,
    telefono:       telefono,
    notas:          notas,
    metodoPago:     metodoPago,
    items:          items
  };

  // UI de carga
  var btn     = document.getElementById('btnConfirmar');
  var btnText = document.getElementById('btnText');
  var spinner = document.getElementById('btnSpinner');
  btn.disabled     = true;
  btnText.textContent = 'Procesando...';
  spinner.style.display = 'inline-block';

  var headers = { 'Content-Type': 'application/json' };
  headers[csrfHeader] = csrfToken;

  fetch('/cliente/pedido/checkout', {
    method: 'POST',
    headers: headers,
    body: JSON.stringify(payload),
    credentials: 'same-origin'
  })
  .then(function (res) {
    if (res.status === 401 || res.status === 403) {
      window.location.href = '/auth/login';
      return null;
    }
    return res.json();
  })
  .then(function (data) {
    if (!data) return;
    if (data.ok) {
      // Limpiar carrito
      localStorage.removeItem('juskev_cart');
      showToast('✅ ¡Pedido confirmado!');
      setTimeout(function () {
        window.location.href = '/cliente/mis-pedidos';
      }, 1500);
    } else {
      showToast('❌ ' + (data.error || 'Error al procesar el pedido'));
      btn.disabled           = false;
      btnText.textContent    = 'Confirmar Pedido';
      spinner.style.display  = 'none';
    }
  })
  .catch(function (err) {
    console.error('Error checkout:', err);
    showToast('❌ Error de conexión. Intenta de nuevo.');
    btn.disabled           = false;
    btnText.textContent    = 'Confirmar Pedido';
    spinner.style.display  = 'none';
  });
}

/* ── Toast ── */
function showToast(msg) {
  var toast = document.getElementById('checkoutToast');
  if (!toast) return;
  toast.textContent = msg;
  toast.classList.add('show');
  setTimeout(function () { toast.classList.remove('show'); }, 3000);
}