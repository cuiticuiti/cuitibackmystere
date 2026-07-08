const API_URL = "https://cuitibackmystere.onrender.com";

let productos = [];
let tipoActual = "TODOS";

document.addEventListener("DOMContentLoaded", () => {

    cargarProductos();

    document.querySelectorAll(".catalog-filters button").forEach(btn => {

        btn.addEventListener("click", () => {

            document.querySelectorAll(".catalog-filters button")
                .forEach(b => b.classList.remove("active"));

            btn.classList.add("active");

            tipoActual = btn.textContent.trim().toUpperCase();

            renderProductos();

        });

    });

});

async function cargarProductos(){

    try{

        const res = await fetch(`${API_URL}/api/productos`);

        const datos = await res.json();

        productos = datos.filter(p => p.categoria === "IMPORTACION");

        renderProductos();

    }catch(e){

        console.error(e);

    }

}

function renderProductos(){

    const contenedor = document.querySelector(".productos-grid");

    if(!contenedor) return;

    contenedor.innerHTML = "";

    let lista = productos;

    if(tipoActual !== "TODOS"){

        lista = productos.filter(p =>
            (p.tipo || "").trim().toUpperCase() === tipoActual
        );

    }

    lista.forEach(p => {

        contenedor.innerHTML += `
        <div class="producto-card">

            <img src="${p.imagen}" alt="${p.nombre}">

            <h3>${p.nombre}</h3>

            <p>${p.tipo ?? ""}</p>

            <h2>$${p.precio.toLocaleString()}</h2>

            <button onclick="consultar('${p.nombre}')">

                Solicitar

            </button>

        </div>
        `;

    });

}

function consultar(nombre){

    const mensaje = `Hola! Me interesa ${nombre}. ¿Sigue disponible?`;

    window.open(
        `https://wa.me/542615161952?text=${encodeURIComponent(mensaje)}`,
        "_blank"
    );

}