console.log("SCRIPT OK");

// ================= CONFIG =================
const API_URL = "https://cuitibackmystere.onrender.com";

// ================= DOM =================
const container = document.getElementById("productsContainer");
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
    console.error(e);
    container.innerHTML = "<p>Error cargando catálogo</p>";
  }
}

// ================= RENDER =================
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

// ================= CART =================
function addToCart(i) {
  cart.push(products[i]);
  cartCountSpan.textContent = cart.length;
}

// ================= START =================
document.addEventListener("DOMContentLoaded", cargarProductos);
