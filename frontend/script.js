const API_URL = "https://cuitibackmystere.onrender.com";
const PHONE = "2615161952";

const container = document.getElementById("productsContainer");
const cartItems = document.getElementById("cartItems");
const cartCountSpan = document.getElementById("cartCount");
const payMpBtn = document.getElementById("payMP");

let products = [];
let cart = [];

// =========================
// CARGAR PRODUCTOS
// =========================
async function cargarProductos() {
  container.innerHTML = "Cargando catálogo...";
  try {
    const res = await fetch(`${API_URL}/api/productos`);
    const data = await res.json();
    products = data;
    renderProducts();
  } catch (e) {
    container.innerHTML = "Error cargando catálogo";
    console.error(e);
  }
}

// =========================
// MOSTRAR PRODUCTOS
// =========================
function renderProducts() {
  container.innerHTML = "";
  products.forEach((p, i) => {
    container.innerHTML += `
      <div class="product-card">
        <img src="https://mysterefragancias.com/${p.imagen}">
        <h3>${p.nombre}</h3>
        <p>$${p.precio}</p>
        <button onclick="addToCart(${i})">Agregar</button>
      </div>
    `;
  });
}

function addToCart(i) {
  cart.push(products[i]);
  cartCountSpan.textContent = cart.length;
  renderCart();
}

function renderCart() {
  cartItems.innerHTML = "";
  cart.forEach(p => {
    cartItems.innerHTML += `<p>${p.nombre} - $${p.precio}</p>`;
  });
}

// =========================
// MERCADO PAGO (SIMPLE)
// =========================
payMpBtn.onclick = pagar;

async function pagar() {
  if (cart.length === 0) {
    alert("Carrito vacío");
    return;
  }

  const body = {
    items: cart.map(p => ({
      title: p.nombre,
      quantity: 1,
      price: p.precio
    }))
  };

  const res = await fetch(`${API_URL}/api/pay/create`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  });

  const data = await res.json();
  window.location.href = data.initPoint;
}

document.addEventListener("DOMContentLoaded", cargarProductos);
