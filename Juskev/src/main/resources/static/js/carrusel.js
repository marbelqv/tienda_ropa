/* ═══════════════════════════════════════
   JUSKEV — carrusel.js
   Gestión del carrusel hero en inicio.html
═══════════════════════════════════════ */

let currentSlide = 0;
let totalSlidesCount = 0;
let autoplayInterval = null;

/**
 * Inicializa el carrusel con el número real de slides del DOM.
 * Llamado desde inicio.html: initCarrusel(totalSlides)
 */
function initCarrusel(total) {
  totalSlidesCount = total || 1;
  currentSlide = 0;

  buildDots();
  goSlide(0);

  // Autoplay cada 5 segundos
  startAutoplay();

  // Pausar al pasar el mouse
  const hero = document.querySelector('.hero');
  if (hero) {
    hero.addEventListener('mouseenter', stopAutoplay);
    hero.addEventListener('mouseleave', startAutoplay);
  }

  // Swipe en móvil
  initSwipe();
}






function buildDots() {
  const dotsContainer = document.getElementById('dots');
  if (!dotsContainer) return;
  dotsContainer.innerHTML = '';
  for (let i = 0; i < totalSlidesCount; i++) {
    const dot = document.createElement('button');
    dot.className = 'dot' + (i === 0 ? ' active' : '');
    dot.setAttribute('aria-label', 'Slide ' + (i + 1));
    dot.addEventListener('click', () => goSlide(i));
    dotsContainer.appendChild(dot);
  }
}

function goSlide(n) {
  if (totalSlidesCount === 0) return;
  currentSlide = ((n % totalSlidesCount) + totalSlidesCount) % totalSlidesCount;

  const slidesEl = document.getElementById('slides');
  if (slidesEl) {
    slidesEl.style.transform = 'translateX(-' + (currentSlide * 100) + '%)';
  }

  document.querySelectorAll('.dot').forEach(function(d, i) {
    d.classList.toggle('active', i === currentSlide);
  });
}

function nextSlide() { goSlide(currentSlide + 1); }
function prevSlide()  { goSlide(currentSlide - 1); }

function startAutoplay() {
  stopAutoplay();
  if (totalSlidesCount > 1) {
    autoplayInterval = setInterval(nextSlide, 9000);
  }
}

function stopAutoplay() {
  if (autoplayInterval) {
    clearInterval(autoplayInterval);
    autoplayInterval = null;
  }
}

function initSwipe() {
  const slidesEl = document.getElementById('slides');
  if (!slidesEl) return;
  let touchStartX = 0;
  slidesEl.addEventListener('touchstart', function(e) {
    touchStartX = e.changedTouches[0].screenX;
  }, { passive: true });
  slidesEl.addEventListener('touchend', function(e) {
    const diff = touchStartX - e.changedTouches[0].screenX;
    if (Math.abs(diff) > 50) { diff > 0 ? nextSlide() : prevSlide(); }
  }, { passive: true });
}



