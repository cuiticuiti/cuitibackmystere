const API_URL = "https://cuitibackmystere.onrender.com";

let productos = [];
let categoriaActual = "TODOS";

document.addEventListener("DOMContentLoaded", () => {

    cargarProductos();

    const buscador = document.querySelector(".search-box input");

    if (buscador) {

        buscador.addEventListener("input", renderProductos);

    }

    document.querySelectorAll(".catalog-filters button").forEach(btn => {

        btn.addEventListener("click", () => {

            document.querySelectorAll(".catalog-filters button")
                .forEach(b => b.classList.remove("active"));

            btn.classList.add("active");

            categoriaActual = btn.textContent.trim().toUpperCase();

            renderProductos();

        });

    });

});

async function cargarProductos() {

    try {

        const res = await fetch(`${API_URL}/api/productos`);

        const datos = await res.json();

        productos = datos.filter(p => p.categoria === "IPHONE");

        renderProductos();

    } catch (e) {

        console.error(e);

    }

}

function renderProductos() {

    const grid = document.querySelector(".products-grid");

    if (!grid) return;

    grid.innerHTML = "";

    const texto = document.querySelector(".search-box input")?.value.toLowerCase() || "";

    let lista = productos.filter(p =>
        p.nombre.toLowerCase().includes(texto)
    );

    if (categoriaActual === "SELLADOS") {

    lista = lista.filter(p => p.estado === "SELLADO");

}
else if (categoriaActual === "USADOS PREMIUM") {

    lista = lista.filter(p => p.estado === "USADO");

}
else if (categoriaActual === "ACCESORIOS") {

    lista = lista.filter(p => p.estado === "ACCESORIO");

}
lista.sort((a,b)=>a.nombre.localeCompare(b.nombre));

    lista.forEach(p => {

        grid.innerHTML += `

        <div class="iphone-card">

            <img src="${p.imagen}" alt="${p.nombre}">

            <div class="iphone-info">

                <h3>${p.nombre}</h3>

                <p>${p.capacidad ?? ""}</p>

                <p>${p.color ?? ""}</p>

                ${p.bateria ? `<p>Batería ${p.bateria}%</p>` : ""}

                <h2>$${p.precio.toLocaleString()}</h2>
               ${p.stock == 0 ?

`
<span class="sin-stock">❌ Sin stock</span>

<button onclick="encargar('${p.nombre}')">

    Encargar

</button>
`

:

`
<span class="en-stock">✅ Disponible</span>

<button onclick="consultar('${p.nombre}')">

    Consultar

</button>
`
}
            </div>

        </div>

        `;

    });

}

function consultar(nombre){

    const mensaje =
`Hola! Me interesa el ${nombre}. ¿Sigue disponible?`;

    window.open(
        `https://wa.me/542615161952?text=${encodeURIComponent(mensaje)}`,
        "_blank"
    );

}
function encargar(nombre){

    const mensaje =
`Hola! Quisiera encargar el ${nombre}. ¿Cuánto demora y cuál sería el precio?`;

    window.open(
        `https://wa.me/542615161952?text=${encodeURIComponent(mensaje)}`,
        "_blank"
    );

}