// =========================
// CONFIGURACIÓN
// =========================
const PHONE = "2615161952";
console.log("SCRIPT CARGADO OK");

// =========================
// REFERENCIAS AL DOM
// =========================
const API_URL = "https://cuitibackmystere.onrender.com";

const container      = document.getElementById("productsContainer");
const cartModal      = document.getElementById("cartModal");
const openCartBtn    = document.getElementById("openCart");
const closeCartBtn   = document.getElementById("closeCart");
const cartItems      = document.getElementById("cartItems");
const cartTotal      = document.getElementById("cartTotal");
const payCashBtn     = document.getElementById("payCash");
const payMpBtn       = document.getElementById("payMP");
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

        if (filtro !== "todos") {
            if (filtro === "sale" && !p.sale) return;
            if (p.genero !== filtro && filtro !== "sale") return;
        }

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

    }); // ← ESTA LLAVE FALTABA
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
    if (cart.length === 0) return alert("Tu carrito está vacío.");

const envio = document.getElementById("shippingMethod")?.value || "retiro";


    const items = agruparItems();
    const subtotal = calcularSubtotal();
    const final = subtotal * (1 - discount);

    let texto = "Hola! Quiero pagar en EFECTIVO:\n\n";

    items.forEach(i => texto += `• ${i.title} x${i.quantity} → $${i.total}\n`);

    texto += `\nTotal final: $${final.toLocaleString("es-AR")}`;
    texto += `\nEntrega: ${envio}`;

    window.open(`https://wa.me/54${PHONE}?text=${encodeURIComponent(texto)}`, "_blank");
}

// =========================
// MERCADO PAGO
// =========================


payMpBtn.onclick = pagar;

async function pagar() {
    if (cart.length === 0) {
        alert("Tu carrito está vacío.");
        return;
    }

    // 🔒 Bloquear botón para evitar doble click
    payMpBtn.disabled = true;
    payMpBtn.textContent = "Generando pago...";

    try {
        const items = agruparItems();

        const envio = document.getElementById("shippingMethod")?.value || "retiro";
        const subtotal = calcularSubtotal();
        const totalFinal = Math.round(subtotal * (1 - discount));

        const body = {
            items: items.map(i => ({
                title: i.title,
                quantity: i.quantity,
                price: Math.round(i.price * (1 - discount))
            })),
            codigoDescuento: discount > 0
                ? discountInput.value.trim().toUpperCase()
                : null
        };

        const res = await fetch(`${API_URL}/api/pay/create`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body)
        });

        if (!res.ok) {
            throw new Error("Error HTTP " + res.status);
        }

        const data = await res.json();

        const url = data.init_point || data.initPoint;
        if (!url) {
            console.error("Respuesta MP inválida:", data);
            throw new Error("No se recibió init_point");
        }

        // 🚀 Redirección REAL
        window.location.href = url;

    } catch (e) {
        console.error("ERROR PAGO:", e);
        alert("No se pudo generar el pago. Probá de nuevo.");

        // 🔓 Rehabilitar botón si falla
        payMpBtn.disabled = false;
        payMpBtn.textContent = "Pagar con Mercado Pago";
    }
}


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
window.filtrar = filtrar;
window.changeQty = changeQty;
window.addToCart = addToCart;
window.consultarEncargo = consultarEncargo;
window.nextSlide = nextSlide;
window.prevSlide = prevSlide;
// === EXPORTAR FUNCIONES PARA HTML INLINE ===
window.removeItem = removeItem;
window.pagar = pagar;
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


