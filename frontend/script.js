console.log("SCRIPT BASE CARGADO");

// ================= CONFIG =================
const API_URL = "https://cuitibackmystere.onrender.com";

// ================= DOM =================
const container = document.getElementById("productsContainer");
const cartModal = document.getElementById("cartModal");
const openCartBtn = document.getElementById("openCart");
const closeCartBtn = document.getElementById("closeCart");
const cartItems = document.getElementById("cartItems");
const cartCountSpan = document.getElementById("cartCount");

// ================= STATE =================
let products = [];
let cart = [];

// ================= LOAD PRODUCTS =================
async function cargarProductos() {
  container.innerHTML = "<p>Cargando catálogo...</p>";

  try {
    const res = await fetch(`${API_URL}/api/productos`);
    if (!res.ok) throw new Error("HTTP error");
    products = await res.json();
    renderProducts();
  } catch (e) {
    container.innerHTML = "<p>Error cargando catálogo</p>";
    console.error(e);
  }
}

// ================= RENDER PRODUCTS =================
function renderProducts() {
  container.innerHTML = "";

  products.forEach((p, i) => {
    container.innerHTML += `
      <div class="product-card">
        <img src="https://mysterefragancias.com/${p.imagen}">
        <div class="product-info">
          <h3>${p.nombre}</h3>
          <p class="price">$${p.precio}</p>
          <button onclick="addToCart(${i})">Agregar</button>
        </div>
      </div>
    `;
  });
}

// ================= CART =================
function addToCart(i) {
  cart.push(products[i]);
  cartCountSpan.textContent = cart.length;
  renderCart();
}

function renderCart() {
  cartItems.innerHTML = "";
  cart.forEach((p, i) => {
    cartItems.innerHTML += `
      <div>
        ${p.nombre} - $${p.precio}
        <button onclick="removeItem(${i})">✖</button>
      </div>
    `;
  });
}

function removeItem(i) {
  cart.splice(i, 1);
  cartCountSpan.textContent = cart.length;
  renderCart();
}

// ================= MODAL =================
openCartBtn.onclick = () => cartModal.style.display = "block";
closeCartBtn.onclick = () => cartModal.style.display = "none";

// ================= START =================
document.addEventListener("DOMContentLoaded", cargarProductos);
