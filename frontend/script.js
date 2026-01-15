// =========================
// CONFIGURACIÓN
// =========================
const PHONE = "2615161952";
console.log("SCRIPT CARGADO OK");

// =========================
// REFERENCIAS AL DOM
// =========================
const API_URL = "https://cuitibackmystere.onrender.com".replace(/\/$/, "");


const container      = document.getElementById("productsContainer");
const cartModal      = document.getElementById("cartModal");
const openCartBtn    = document.getElementById("openCart");
const closeCartBtn   = document.getElementById("closeCart");
const cartItems      = document.getElementById("cartItems");
const cartTotal      = document.getElementById("cartTotal");
const payCashBtn = document.getElementById("payCash");
// const payMpBtn   = document.getElementById("payMP");

const discountInput  = document.getElementById("discountCode");
const discountBtn    = document.getElementById("applyDiscount");
const cartCountSpan  = document.getElementById("cartCount");
const alertBox       = document.getElementById("addAlert");

// =========================
// ESTADO DEL CATÁLOGO
// =========================
let products = [];
let quantities = {};
let cart = [];
let discount = 0;
let appliedCode = null;
let searchText = "";
let stockFilter = "all";



// =========================
// CARGAR PRODUCTOS
// =========================
async function cargarProductos() {
    if (container) {
        container.innerHTML = "<p style='padding:10px'>Cargando catálogo...</p>";
    }

    // 1️⃣ Intentar desde cache local
    const cache = localStorage.getItem("catalogo_cache");
    if (cache) {
        try {
            products = JSON.parse(cache);
            renderProducts();
        } catch {}
    }

    // 2️⃣ Pedir al backend en segundo plano
    try {
        const res = await fetch(`${API_URL}/api/productos`, {
            method: "GET",
            headers: {
                "Accept": "application/json"
            }
        });

        if (!res.ok) throw new Error("HTTP " + res.status);

        const data = await res.json();

        // 3️⃣ Guardar cache
        localStorage.setItem("catalogo_cache", JSON.stringify(data));

        products = data;
        renderProducts();

    } catch (e) {
        console.error("ERROR backend:", e);

        // 4️⃣ Si no hay cache y falla
        if (!cache && container) {
            container.innerHTML = `
              <p style="padding:10px;color:#b44545">
                No se pudo cargar el catálogo.<br>
                Probá nuevamente en unos segundos.
              </p>`;
        }
    }
}



// =========================
// FILTRO POR CATEGORÍAS
// =========================
function filtrar(categoria) {
    document.querySelectorAll(".top-buttons button").forEach(btn => {
        btn.classList.toggle("active", btn.textContent.toLowerCase() === categoria);
    });

    renderProducts(categoria);

    document.getElementById("catalogo").scrollIntoView({ behavior: "smooth" });
}

// =========================
// MOSTRAR PRODUCTOS
// =========================
function renderProducts(filtro = "todos") {
    container.innerHTML = "";

    products.forEach((p, index) => {

      // FILTRO POR TEXTO
if (searchText && !p.nombre.toLowerCase().includes(searchText)) return;

// FILTRO POR STOCK
if (stockFilter === "available" && p.stock <= 0) return;
if (stockFilter === "out" && p.stock > 0) return;


        const tieneStock = p.stock > 0;

        container.innerHTML += `
        <div class="product-card">
           <img src="https://mysterefragancias.com/${p.imagen}">

            <div class="product-info">

                ${p.sale ? `<span class="badge-sale">SALE</span>` : ""}

                <h3>${p.nombre}</h3>

                ${
                    p.sale && p.precioAntes
                    ? `
                        <p class="price">
                            <span class="old-price">$${p.precioAntes.toLocaleString("es-AR")}</span>
                            <span class="new-price">$${p.precio.toLocaleString("es-AR")}</span>
                        </p>
                      `
                    : `
                        <p class="price">$${p.precio.toLocaleString("es-AR")}</p>
                      `
                }

                ${
                    tieneStock
                    ? `
                        <div class="qty-selector">
                            <button onclick="changeQty(${index}, -1)">−</button>
                            <span id="qty-${index}">${quantities[index] || 1}</span>
                            <button onclick="changeQty(${index}, 1)">+</button>
                        </div>
                        <button class="add-btn" onclick="addToCart(${index})">
                            Agregar al carrito
                        </button>
                      `
                    : `
                        <p class="out-of-stock">Consultar por encargue</p>
                        <button class="add-btn" onclick="consultarEncargo(${index})">
                            Consultar por WhatsApp
                        </button>
                      `
                }

            </div>
        </div>
        `;

       }); // fin forEach

    // 👇 SI NO HAY RESULTADOS
    if (container.innerHTML.trim() === "") {
        container.innerHTML = `
          <div style="text-align:center; margin-top:30px;">
            <p>No encontramos lo que buscás 😕</p>
            <button class="help-btn" onclick="ayudaWhatsApp()">
              Escribinos por WhatsApp
            </button>
          </div>
        `;
    }
}

}


// =========================
// CONSULTAR ENCARGO
// =========================
function consultarEncargo(i) {
    const p = products[i];
    const texto = encodeURIComponent(
        `Hola! Me interesa encargar el perfume: ${p.nombre}. ¿Cuánto demora en llegar?`
    );
    window.open(`https://wa.me/54${PHONE}?text=${texto}`, "_blank");
}

// =========================
// DESCUENTOS
// =========================


discountBtn.addEventListener("click", async () => {
    const code = discountInput.value.trim().toUpperCase();
    if (!code) return alert("Ingresá un código");

    const res = await fetch(`${API_URL}/api/descuentos/validar`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ codigo: code })
    });

    const data = await res.json();

    if (!data.valido) {
        discount = 0;
        appliedCode = null;
        alert("Código inválido o agotado");
    } else {
        discount = data.porcentaje / 100;
        appliedCode = code;
        alert(`Código aplicado: ${data.porcentaje}% OFF`);
    }

    renderCart();
});
;

// =========================
// CAMBIAR CANTIDAD
// =========================
function changeQty(i, num) {
    quantities[i] = (quantities[i] || 1) + num;
    if (quantities[i] < 1) quantities[i] = 1;

    document.getElementById(`qty-${i}`).textContent = quantities[i];
}

// =========================
// AÑADIR AL CARRITO
// =========================
function addToCart(i) {
    const p = products[i];
    const qty = quantities[i] || 1;

    if (p.stock < qty) return alert("No hay suficiente stock.");

   

    for (let n = 0; n < qty; n++) cart.push(p);

    cartCountSpan.textContent = cart.length;

    alertBox.textContent = `Agregado: ${p.nombre} x${qty}`;
    alertBox.classList.add("show");

    setTimeout(() => alertBox.classList.remove("show"), 1500);

    quantities[i] = 1;
    renderProducts(); 
    renderCart();
}

// =========================
// CARRITO
// =========================
if (openCartBtn && closeCartBtn && cartModal) {
    openCartBtn.onclick = () => cartModal.style.display = "block";
    closeCartBtn.onclick = () => cartModal.style.display = "none";
}

function renderCart() {
    cartItems.innerHTML = "";
    let subtotal = 0;

    cart.forEach((p, index) => {
        subtotal += p.precio;

        cartItems.innerHTML += `
        <div class="product-card">
            <div class="product-info">
                <h3>${p.nombre}</h3>
                <p class="price">$${p.precio.toLocaleString("es-AR")}</p>
            </div>
            <span class="delete-item" onclick="removeItem(${index})">✖</span>
        </div>`;
    });

    const total = subtotal * (1 - discount);
    const descuentoPesos = subtotal - total;

    document.getElementById("cartSubtotal").textContent = subtotal.toLocaleString("es-AR");
    document.getElementById("cartTotal").textContent = total.toLocaleString("es-AR");

    if (discount > 0) {
        document.getElementById("cartDiscountLine").style.display = "block";
        document.getElementById("cartDiscountPercent").textContent = (discount * 100);
        document.getElementById("cartDiscountValue").textContent = descuentoPesos.toLocaleString("es-AR");
    } else {
        document.getElementById("cartDiscountLine").style.display = "none";
    }
    // 🔥 FIX MOBILE: resetear botón de pago SIEMPRE




    
}
// =========================
// SELECCIONAR MÉTODO DE ENVÍO
// =========================
document.querySelectorAll(".shipping-option").forEach(op => {
    op.addEventListener("click", () => {

        // Quitar selección previa
        document.querySelectorAll(".shipping-option")
            .forEach(o => o.classList.remove("selected"));

        // Seleccionar el clickeado
        op.classList.add("selected");

        // Actualizar hidden input
        document.getElementById("shippingMethod").value =
            op.getAttribute("data-value");
    });
});



function removeItem(index) {
    cart.splice(index, 1);
    cartCountSpan.textContent = cart.length;
    renderCart();
}


// =========================
// WHATSAPP EFECTIVO
// =========================
payCashBtn.onclick = pagarEfectivo;

function pagarEfectivo() {
    if (cart.length === 0) {
        alert("Tu carrito está vacío.");
        return;
    }

    const pedidoId = generarPedidoId();
    const envio = document.getElementById("shippingMethod")?.value || "retiro";
    const items = agruparItems();
    const subtotal = calcularSubtotal();
    const totalFinal = Math.round(subtotal * (1 - discount));

    let texto = `Hola! 👋✨  
Quiero hacer el siguiente pedido en *Mystère Fragancias* 🧴  
🧾 *Pedido:* ${pedidoId}\n\n`;

    items.forEach(i => {
        texto += `• ${i.title} x${i.quantity} → $${i.total.toLocaleString("es-AR")}\n`;
    });

    texto += `\n💵 *Subtotal:* $${subtotal.toLocaleString("es-AR")}`;

    if (discount > 0) {
        texto += `\n🏷 *Cupón aplicado:* ${appliedCode} (-${discount * 100}%)`;
    }

    texto += `\n💰 *Total final:* $${totalFinal.toLocaleString("es-AR")}\n`;

    if (envio === "envio") {
        texto += `\n🚚 *Entrega:* Envío a domicilio  
Quedo atento/a al costo según zona.`;
    } else {
        texto += `\n📍 *Entrega:* Retiro en punto de entrega  
Coordinamos por WhatsApp.`;
    }

    texto += `\n\n¡Gracias! 😊`;
   guardarPedidoLocal(pedidoId, totalFinal);

cart = [];
cartCountSpan.textContent = 0;
renderCart();


    window.open(
        `https://wa.me/54${PHONE}?text=${encodeURIComponent(texto)}`,
        "_blank"
    );
}


// =========================
// MERCADO PAGO
// =========================


/*
// ====== MERCADO PAGO MOBILE SAFE ======

let pagando = false;

async function pagar() {
    if (cart.length === 0) {
        alert("Tu carrito está vacío.");
        return;
    }

    if (pagando) return;
    pagando = true;

    payMpBtn.disabled = true;
    payMpBtn.textContent = "Redirigiendo...";

    try {
        const items = agruparItems();

        const body = {
            items: items.map(i => ({
                title: i.title,
                quantity: i.quantity,
                price: i.price
            })),
            codigoDescuento: discountInput?.value?.trim() || ""
        };

        const res = await fetch(`${API_URL}/api/pay/create`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body)
        });

        if (!res.ok) throw new Error("Error HTTP");

       const data = await res.json();

console.log("RESPUESTA PAY:", data);

const url = data.initPoint || data.init_point || data.punto_de_inicio;

if (!url) {
    console.error("Respuesta backend sin initPoint:", data);
    throw new Error("Sin initPoint");
}

window.location.href = url;


    } catch (err) {
        console.error(err);
        alert("No se pudo iniciar el pago.");

        pagando = false;
        payMpBtn.disabled = false;
        payMpBtn.textContent = "Pagar con Mercado Pago";
    }
}

payMpBtn.onclick = pagar;

*/

// =========================
// UTILIDADES
// =========================
function agruparItems() {
    const map = {};
    cart.forEach(p => {
        if (!map[p.nombre]) map[p.nombre] = { title: p.nombre, quantity: 0, price: p.precio, total: 0 };
        map[p.nombre].quantity++;
        map[p.nombre].total = map[p.nombre].quantity * p.precio;
    });
    return Object.values(map);
}

function calcularSubtotal() {
    return cart.reduce((acc, p) => acc + p.precio, 0);
}
function generarPedidoId() {
    const fecha = new Date();
    const y = fecha.getFullYear();
    const m = String(fecha.getMonth() + 1).padStart(2, "0");
    const d = String(fecha.getDate()).padStart(2, "0");
    const rand = Math.floor(1000 + Math.random() * 9000);
    return `MYS-${y}${m}${d}-${rand}`;
}
function guardarPedidoLocal(id, total) {
    const pedidos = JSON.parse(localStorage.getItem("pedidos_mystere") || "[]");

    pedidos.push({
        id,
        fecha: new Date().toISOString(),
        total,
        items: agruparItems(),
        descuento: discount,
    });

    localStorage.setItem("pedidos_mystere", JSON.stringify(pedidos));
}


// =======================
// SLIDER PRO
// =======================
let slideIndex = 0;
const slides = document.querySelectorAll(".slide");

function showSlidePro(n) {
    slideIndex = (n + slides.length) % slides.length;
    document.querySelector(".slides").style.transform = `translateX(-${slideIndex * 100}%)`;
}

// Botones
function nextSlide() { showSlidePro(slideIndex + 1); }
function prevSlide() { showSlidePro(slideIndex - 1); }

// Auto-play
setInterval(() => nextSlide(), 6000);

// Swipe en celular
let startX = 0;
document.querySelector(".hero-slider").addEventListener("touchstart", e => {
    startX = e.touches[0].clientX;
});

document.querySelector(".hero-slider").addEventListener("touchend", e => {
    let endX = e.changedTouches[0].clientX;
    if (startX - endX > 60) nextSlide();
    if (endX - startX > 60) prevSlide();
});


  

function appendBotMessage(who, text) {
    const chat = document.getElementById("botChat");
    const div = document.createElement("div");
    div.style.margin = "8px 0";
    div.innerHTML = `<strong>${who === "bot" ? "Mystère Bot" : "Vos"}:</strong> ${text}`;
    chat.appendChild(div);
    chat.scrollTop = chat.scrollHeight;
}
if (localStorage.getItem("forceCart") === "1") {
    localStorage.removeItem("forceCart");
    document.getElementById("cartModal").style.display = "block";
}
// === CHATBOT ===

const chatOpen = document.getElementById("chatbot-open");
const chatWin  = document.getElementById("chatbot-window");
const chatClose = document.getElementById("chatbot-close");
const chatMessages = document.getElementById("chatbot-messages");
const chatInput = document.getElementById("chatbot-input");
const chatSend = document.getElementById("chatbot-send");

chatOpen.onclick = () => chatWin.style.display = "flex";
chatClose.onclick = () => chatWin.style.display = "none";

function addMessage(text, sender="bot") {
    const div = document.createElement("div");
    div.className = sender === "user" ? "user-msg" : "bot-msg";
    div.textContent = text;
    chatMessages.appendChild(div);
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

async function enviarPregunta() {
    const pregunta = chatInput.value.trim();
    if (!pregunta) return;

    addMessage(pregunta, "user");
    chatInput.value = "";

    addMessage("Escribiendo...", "bot");

    const res = await fetch(`${API_URL}/api/bot/consultar`, {

        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ pregunta })
    });

    const data = await res.json();

    // borra "Escribiendo..."
    document.querySelectorAll(".bot-msg").forEach(m => {
        if (m.textContent === "Escribiendo...") m.remove();
    });

    addMessage(data.respuesta, "bot");
}

chatSend.onclick = enviarPregunta;
chatInput.addEventListener("keypress", e => {
    if (e.key === "Enter") enviarPregunta();
});
function cambiarOrden(campo) {
    if (ordenCampo === campo) {
        ordenAsc = !ordenAsc;    // invierte asc/desc
    } else {
        ordenCampo = campo;
        ordenAsc = true;
    }
    aplicarFiltros();
}
function ayudaWhatsApp() {
  const msg = encodeURIComponent(
    "Hola! No encontré lo que buscaba en la web, ¿me ayudás?"
  );
  window.open(`https://wa.me/5492615161952?text=${msg}`, "_blank");
}



window.filtrar = filtrar;
window.changeQty = changeQty;
window.addToCart = addToCart;
window.consultarEncargo = consultarEncargo;
window.nextSlide = nextSlide;
window.prevSlide = prevSlide;
// === EXPORTAR FUNCIONES PARA HTML INLINE ===
window.removeItem = removeItem;
window.pagarEfectivo = pagarEfectivo;
window.changeQty = changeQty;
window.addToCart = addToCart;
window.consultarEncargo = consultarEncargo;
window.nextSlide = nextSlide;
window.prevSlide = prevSlide;
window.filtrar = filtrar;
// =========================
// INICIO
// =========================
document.addEventListener("DOMContentLoaded", () => {
    cargarProductos();
});
function setStockFilter(type) {
  stockFilter = type;

  document.querySelectorAll(".stock-filters button")
    .forEach(b => b.classList.remove("active"));

  event.target.classList.add("active");

  aplicarFiltros();
}

function aplicarFiltros() {
  searchText = document.getElementById("searchInput").value.toLowerCase();
  renderProducts();
}


